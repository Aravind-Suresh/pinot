package org.apache.pinot.segment.spi.index;

import org.apache.pinot.spi.config.table.IndexConfig;
import org.apache.pinot.spi.config.table.VectorIndexingTechniqueType;


public class VectorIndexConfig extends IndexConfig {

  // determines the indexing mechanism type (HNSW / AKNN)
  private VectorIndexingTechniqueType _indexType;

  // common top-level fields
  // max value of nearest neighbours search
  private int k;

  // index specific fields can be defined here as a map / json node here

  public VectorIndexConfig(Boolean disabled) {
    super(disabled);
  }
}
