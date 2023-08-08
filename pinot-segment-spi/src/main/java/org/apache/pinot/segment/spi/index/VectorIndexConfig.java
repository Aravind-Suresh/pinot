package org.apache.pinot.segment.spi.index;

import java.util.Collections;
import java.util.Map;
import org.apache.pinot.spi.config.table.IndexConfig;
import org.apache.pinot.spi.config.table.VectorIndexingTechniqueType;
import org.apache.pinot.spi.config.table.VectorSimilarity;
import org.apache.pinot.spi.data.readers.Vector;


public class VectorIndexConfig extends IndexConfig {

  public static final String HNSW_GRAPH_FANOUT = "hnsw.graph.fanout";
  public static final String HNSW_BEAM_WIDTH = "hnsw.beam.width";
  public static final String HNSW_SEARCH_MAX_NODES_VISITED = "hnsw.search.max.nodes.visited";
  public static final String DEFAULT_HNSW_GRAPH_FANOUT = "10";
  public static final String DEFAULT_HNSW_BEAM_WIDTH = "20";
  public static final String DEFAULT_HNSW_SEARCH_MAX_NODES_VISITED = "1000";

  private Vector.VectorType _vectorType;
  private int _dimension;

  private VectorSimilarity _vectorSimilarity;

  // determines the indexing mechanism type (HNSW / AKNN)
  private VectorIndexingTechniqueType _indexType;

  // common top-level fields
  // max value of nearest neighbours search
  private int _maxNeighbors;

  private Map<String, String> _parameters;

  // index specific fields can be defined here as a map / json node here

  public VectorIndexConfig(Boolean disabled) {
    super(disabled);
  }

  public VectorIndexConfig(Boolean disabled, Vector.VectorType vectorType, int dimension,
      VectorSimilarity vectorSimilarity, VectorIndexingTechniqueType indexType, int maxNeighbors,
      Map<String, String> parameters) {
    super(disabled);
    _vectorType = vectorType;
    _dimension = dimension;
    _vectorSimilarity = vectorSimilarity;
    _indexType = indexType;
    _maxNeighbors = maxNeighbors;
    _parameters = parameters;
  }

  public Vector.VectorType getVectorType() {
    return _vectorType;
  }

  public VectorSimilarity getVectorSimilarity() {
    return _vectorSimilarity;
  }

  public VectorIndexingTechniqueType getIndexType() {
    return _indexType;
  }

  public int getDimension() {
    return _dimension;
  }

  public int getMaxNeighbors() {
    return _maxNeighbors;
  }

  public Map<String, String> getParameters() {
    return Collections.unmodifiableMap(_parameters);
  }
}
