package org.apache.pinot.segment.local.realtime.impl.vector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.lucene.index.RandomAccessVectorValues;
import org.apache.lucene.index.VectorEncoding;
import org.apache.lucene.index.VectorSimilarityFunction;
import org.apache.lucene.util.BytesRef;
import org.apache.pinot.spi.config.table.VectorSimilarity;
import org.apache.pinot.spi.data.readers.Vector;


public class VectorUtils {
  public static VectorEncoding vectorEncoding(Vector.VectorType vectorType) {
    return VectorEncoding.FLOAT32;
  }

  public static VectorSimilarityFunction vectorSimilarityFunction(@Nonnull VectorSimilarity vectorSimilarity) {
    switch (vectorSimilarity) {
      case COSINE:
        return VectorSimilarityFunction.COSINE;
      case DOT_PRODUCT:
        return VectorSimilarityFunction.DOT_PRODUCT;
      case EUCLIDEAN:
        return VectorSimilarityFunction.EUCLIDEAN;
    }
    throw new IllegalArgumentException("invalid vector similarity: " + vectorSimilarity);
  }

  public static RandomAccessVectorValues emptyRandomAccessVectorValues() {
    return new RandomAccessVectorValues() {
      @Override
      public int size() {
        return 0;
      }

      @Override
      public int dimension() {
        return 0;
      }

      @Override
      public float[] vectorValue(int targetOrd)
          throws IOException {
        return new float[0];
      }

      @Override
      public BytesRef binaryValue(int targetOrd)
          throws IOException {
        return null;
      }

      @Override
      public RandomAccessVectorValues copy()
          throws IOException {
        return null;
      }
    };
  }

  public static class ListBasedRandomAccessVectorValues implements RandomAccessVectorValues {
    private final List<Vector> _vectors;
    private final int _dimension;

    public ListBasedRandomAccessVectorValues(int dimension) {
      this(new ArrayList<>(), dimension);
    }

    public ListBasedRandomAccessVectorValues(List<Vector> vectors, int dimension) {
      _vectors = vectors;
      _dimension = dimension;
    }

    public void add(Vector vector) {
      _vectors.add(vector);
    }

    @Override
    public int size() {
      return _vectors.size();
    }

    @Override
    public int dimension() {
      return _dimension;
    }

    @Override
    public float[] vectorValue(int targetOrd)
        throws IOException {
      return _vectors.get(targetOrd).getFloatValues();
    }

    @Override
    public BytesRef binaryValue(int targetOrd)
        throws IOException {
      return null;
    }

    @Override
    public RandomAccessVectorValues copy()
        throws IOException {
      return new ListBasedRandomAccessVectorValues(_vectors, _dimension);
    }
  }
}
