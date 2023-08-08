package org.apache.pinot.segment.spi.index.reader;

import org.apache.pinot.segment.spi.index.IndexReader;
import org.apache.pinot.spi.data.readers.Vector;
import org.roaringbitmap.buffer.MutableRoaringBitmap;


public interface VectorIndexReader extends IndexReader {
  MutableRoaringBitmap getMatchingDocIds(Vector input, int numMatchingDocs);
}
