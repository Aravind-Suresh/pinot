package org.apache.pinot.segment.local.realtime.impl.vector;

import com.google.common.base.Preconditions;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.lucene.index.VectorEncoding;
import org.apache.lucene.index.VectorSimilarityFunction;
import org.apache.lucene.util.hnsw.HnswGraphBuilder;
import org.apache.lucene.util.hnsw.HnswGraphSearcher;
import org.apache.lucene.util.hnsw.NeighborQueue;
import org.apache.pinot.segment.spi.index.VectorIndexConfig;
import org.apache.pinot.segment.spi.index.mutable.MutableVectorIndex;
import org.apache.pinot.spi.data.readers.Vector;
import org.roaringbitmap.buffer.MutableRoaringBitmap;


public class MutableVectorIndexImpl implements MutableVectorIndex {
  // updates are not thread safe
  private final HnswGraphBuilder<?> _hnswGraph;
  private final VectorUtils.ListBasedRandomAccessVectorValues _vectors;
  private final VectorEncoding _vectorEncoding;
  private final VectorSimilarityFunction _vectorSimilarityFunction;
  private final int _maxNodesAllowedToVisit;
  private int _nextDocId;
  private final int _maxNeighbors;

  public MutableVectorIndexImpl(VectorIndexConfig config) {
    _nextDocId = 0;
    _vectors = new VectorUtils.ListBasedRandomAccessVectorValues(config.getDimension());
    _maxNeighbors = config.getMaxNeighbors();
    _vectorEncoding = VectorUtils.vectorEncoding(config.getVectorType());
    _vectorSimilarityFunction = VectorUtils.vectorSimilarityFunction(config.getVectorSimilarity());
    _maxNodesAllowedToVisit = Integer.parseInt(
        config.getParameters().getOrDefault(VectorIndexConfig.HNSW_SEARCH_MAX_NODES_VISITED,
            VectorIndexConfig.DEFAULT_HNSW_SEARCH_MAX_NODES_VISITED));

    int M = Integer.parseInt(config.getParameters().getOrDefault(VectorIndexConfig.HNSW_GRAPH_FANOUT,
        VectorIndexConfig.DEFAULT_HNSW_GRAPH_FANOUT));
    int beamWidth = Integer.parseInt(config.getParameters().getOrDefault(VectorIndexConfig.HNSW_BEAM_WIDTH,
        VectorIndexConfig.DEFAULT_HNSW_BEAM_WIDTH));
    int seed = ThreadLocalRandom.current().nextInt();
    try {
      _hnswGraph = HnswGraphBuilder.create(_vectors,
          _vectorEncoding, _vectorSimilarityFunction,
          M, beamWidth, seed);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void add(Vector vector)
      throws IOException {
    _vectors.add(vector);
    _hnswGraph.addGraphNode(_nextDocId, _vectors);
    _nextDocId++;
  }

  @Override
  public MutableRoaringBitmap getMatchingDocIds(Vector input, int numMatchingDocs) {
    Preconditions.checkArgument(numMatchingDocs <= _maxNeighbors);
    try {
      NeighborQueue queue = HnswGraphSearcher.search(input.getFloatValues(), numMatchingDocs, _vectors, _vectorEncoding,
          _vectorSimilarityFunction, _hnswGraph.getGraph(), null, _maxNodesAllowedToVisit);
      return MutableRoaringBitmap.bitmapOf(queue.nodes());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void close()
      throws IOException {
  }
}
