package org.apache.pinot.segment.spi.index.creator;

import java.io.IOException;
import javax.annotation.Nonnull;
import org.apache.pinot.segment.spi.index.IndexCreator;
import org.apache.pinot.spi.data.readers.Vector;


public interface VectorIndexCreator extends IndexCreator {

  @Override
  default void add(@Nonnull Object value, int dictId) throws IOException {
    add((Vector) value);
  }

  void add(@Nonnull Vector value) throws IOException;

  @Override
  void seal()
      throws IOException;
}
