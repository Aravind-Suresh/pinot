package org.apache.pinot.segment.spi.index.creator;

import java.io.IOException;
import javax.annotation.Nonnull;
import org.apache.pinot.segment.spi.index.IndexCreator;
import org.apache.pinot.segment.spi.index.reader.PinotVector;


public interface VectorIndexCreator extends IndexCreator {

  @Override
  default void add(@Nonnull Object value, int dictId) throws IOException {
    add((PinotVector) value);
  }

  void add(@Nonnull PinotVector value) throws IOException;

  @Override
  void seal()
      throws IOException;
}
