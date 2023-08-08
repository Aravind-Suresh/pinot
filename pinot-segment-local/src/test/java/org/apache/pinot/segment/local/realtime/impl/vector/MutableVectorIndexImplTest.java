package org.apache.pinot.segment.local.realtime.impl.vector;

import it.unimi.dsi.fastutil.ints.IntArrays;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.commons.lang.ArrayUtils;
import org.apache.pinot.segment.spi.index.VectorIndexConfig;
import org.apache.pinot.spi.config.table.VectorIndexingTechniqueType;
import org.apache.pinot.spi.config.table.VectorSimilarity;
import org.apache.pinot.spi.data.readers.Vector;
import org.roaringbitmap.buffer.MutableRoaringBitmap;
import org.testng.Assert;
import org.testng.annotations.Test;


public class MutableVectorIndexImplTest {
  private static final int MAX_NEIGHBORS = 10;
  private static final int SMALL_DIMENSION = 3;
  private static final int LARGE_DIMENSION = 32;
  private VectorIndexConfig _vectorIndexConfig;
  private MutableVectorIndexImpl _vectorIndex;

  @Test
  public void testSmallIndex()
      throws IOException {
    _vectorIndexConfig = new VectorIndexConfig(false, Vector.VectorType.FLOAT, SMALL_DIMENSION, VectorSimilarity.COSINE,
        VectorIndexingTechniqueType.HNSW, MAX_NEIGHBORS, new HashMap<>());
    _vectorIndex = new MutableVectorIndexImpl(_vectorIndexConfig);
    float[][] data = generateSmallTestData();
    for (float[] vector: data) {
      _vectorIndex.add(new Vector(SMALL_DIMENSION, vector));
    }
    float[] query = {1, 1, 1};
    MutableRoaringBitmap result = _vectorIndex.getMatchingDocIds(new Vector(SMALL_DIMENSION, query), 2);
    Assert.assertEquals(new int[]{0, 2}, result.toArray());
  }

  @Test
  public void testLargeIndex()
      throws IOException {
    _vectorIndexConfig = new VectorIndexConfig(false, Vector.VectorType.FLOAT, LARGE_DIMENSION, VectorSimilarity.COSINE,
        VectorIndexingTechniqueType.HNSW, MAX_NEIGHBORS, new HashMap<>());
    _vectorIndex = new MutableVectorIndexImpl(_vectorIndexConfig);
    final int len = 1_000;
    final float[] pos = {-1, 0, 1};
    final int n = pos.length;
    final float[][] arr = new float[len][];
    for (int i = 0; i < len; ++i) {
      float[] val = new float[LARGE_DIMENSION];
      for (int j = 0; j < LARGE_DIMENSION; ++j) {
        val[j] = pos[Math.abs(ThreadLocalRandom.current().nextInt()) % n];
      }
      arr[i] = val;
      _vectorIndex.add(new Vector(LARGE_DIMENSION, val));
    }
    int q = 10;
    int k = 3;
    while (q-- > 0) {
      int i = Math.abs(ThreadLocalRandom.current().nextInt()) % n;
      MutableRoaringBitmap result = _vectorIndex.getMatchingDocIds(new Vector(LARGE_DIMENSION, arr[i]), k);
      int[] actual = result.toArray();
      int[] expectedArr = new int[len];
      for (int j = 0; j < len; ++j) {
        expectedArr[j] = j;
      }
      IntArrays.quickSort(expectedArr, (k1, k2) -> {
        float s1 = VectorUtils.vectorSimilarityFunction(VectorSimilarity.COSINE).compare(arr[k1], arr[i]);
        float s2 = VectorUtils.vectorSimilarityFunction(VectorSimilarity.COSINE).compare(arr[k2], arr[i]);
        return Float.compare(s2, s1);
      });
      // as hnsw is approximate knn search, we are checking if the top-k actuals are
      // contained within the top-5k expected values
      int[] expected = ArrayUtils.subarray(expectedArr, 0, 5*k);
      Arrays.sort(expected);
      Arrays.sort(actual);
      boolean valid = true;
      for (int u: actual) {
        valid = valid && ArrayUtils.contains(expected, u);
      }
      Assert.assertTrue(valid);
    }
  }

  private float[][] generateSmallTestData() {
    return new float[][]{
        {1, 1, 1},
        {-1, -1, -1},
        {1, 0, 1}
    };
  }
}