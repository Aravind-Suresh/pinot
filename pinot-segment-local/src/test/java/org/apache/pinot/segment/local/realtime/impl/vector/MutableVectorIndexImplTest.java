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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


public class MutableVectorIndexImplTest {
  private static final int MAX_NEIGHBORS = 10;
  private static final int DIMENSION = 32;
  private VectorIndexConfig _vectorIndexConfig;
  private MutableVectorIndexImpl _vectorIndex;

  @BeforeMethod
  public void setup() {
    _vectorIndexConfig = new VectorIndexConfig(false, Vector.VectorType.FLOAT, DIMENSION, VectorSimilarity.COSINE,
        VectorIndexingTechniqueType.HNSW, MAX_NEIGHBORS, new HashMap<>());
    _vectorIndex = new MutableVectorIndexImpl(_vectorIndexConfig);
  }

  @Test
  public void testSmallIndex()
      throws IOException {
    float[][] data = generateTestData();
    for (float[] vector: data) {
      _vectorIndex.add(new Vector(DIMENSION, vector));
    }
    float[] query = {1, 1, 1};
    MutableRoaringBitmap result = _vectorIndex.getMatchingDocIds(new Vector(DIMENSION, query), 2);
    Assert.assertEquals(new int[]{0, 2}, result.toArray());
  }

  @Test
  public void testLargeIndex()
      throws IOException {
    final int len = 1_00_000;
    final int n = 3;
    final float[] pos = {-1, 0, 1};
    final float[][] arr = new float[len][];
    for (int i = 0; i < len; ++i) {
      float[] val = new float[DIMENSION];
      for (int j = 0; j < DIMENSION; ++j) {
        val[j] = pos[Math.abs(ThreadLocalRandom.current().nextInt()) % n];
      }
      arr[i] = val;
      _vectorIndex.add(new Vector(DIMENSION, val));
    }
    int q = 10;
    int k = 3;
    while (q-- > 0) {
      int i = Math.abs(ThreadLocalRandom.current().nextInt()) % n;
      MutableRoaringBitmap result = _vectorIndex.getMatchingDocIds(new Vector(DIMENSION, arr[i]), k);
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
      int[] expected = ArrayUtils.subarray(expectedArr, 0, k);
      Arrays.sort(expected);
      Arrays.sort(actual);
      // TODO change this to actual in 2*k of expected
      Assert.assertTrue(Arrays.equals(expected, actual));
    }
  }

  private float[][] generateTestData() {
    return new float[][]{
        {1, 1, 1},
        {-1, -1, -1},
        {1, 0, 1}
    };
  }
}