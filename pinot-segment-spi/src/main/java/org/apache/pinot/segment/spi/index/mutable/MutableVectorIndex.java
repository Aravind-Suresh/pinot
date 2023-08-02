package org.apache.pinot.segment.spi.index.mutable;

import java.io.IOException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.pinot.segment.spi.index.reader.PinotVector;
import org.apache.pinot.segment.spi.index.reader.VectorIndexReader;


public interface MutableVectorIndex extends VectorIndexReader, MutableIndex {
  @Override
  default void add(@Nonnull Object value, int dictId, int docId) {
    throw new UnsupportedOperationException("Mutable vector indexes are not supported for single-valued columns");
  }

  @Override
  default void add(@Nonnull Object[] values, @Nullable int[] dictIds, int docId) {
    throw new UnsupportedOperationException("Mutable vector indexes are not supported for multi-valued columns");
  }

  void add(PinotVector vector)
      throws IOException;
}
