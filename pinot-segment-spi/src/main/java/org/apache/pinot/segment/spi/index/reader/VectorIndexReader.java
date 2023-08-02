package org.apache.pinot.segment.spi.index.reader;

import org.apache.pinot.segment.spi.index.IndexReader;
import org.roaringbitmap.buffer.MutableRoaringBitmap;


public interface VectorIndexReader extends IndexReader {
  MutableRoaringBitmap getMatchingDocIds(PinotVector input);
}
