package org.apache.pinot.segment.local.segment.index.vector;

import java.util.Map;
import javax.annotation.Nullable;
import org.apache.pinot.segment.spi.ColumnMetadata;
import org.apache.pinot.segment.spi.creator.IndexCreationContext;
import org.apache.pinot.segment.spi.index.FieldIndexConfigs;
import org.apache.pinot.segment.spi.index.IndexHandler;
import org.apache.pinot.segment.spi.index.IndexReaderFactory;
import org.apache.pinot.segment.spi.index.IndexType;
import org.apache.pinot.segment.spi.index.VectorIndexConfig;
import org.apache.pinot.segment.spi.index.creator.VectorIndexCreator;
import org.apache.pinot.segment.spi.index.reader.VectorIndexReader;
import org.apache.pinot.segment.spi.store.SegmentDirectory;
import org.apache.pinot.spi.config.table.TableConfig;
import org.apache.pinot.spi.data.Schema;


public class VectorIndexType implements IndexType<VectorIndexConfig, VectorIndexReader, VectorIndexCreator> {
  @Override
  public String getId() {
    return null;
  }

  @Override
  public Class<VectorIndexConfig> getIndexConfigClass() {
    return null;
  }

  @Override
  public VectorIndexConfig getDefaultConfig() {
    return null;
  }

  @Override
  public Map<String, VectorIndexConfig> getConfig(TableConfig tableConfig, Schema schema) {
    return null;
  }

  @Override
  public VectorIndexCreator createIndexCreator(IndexCreationContext context, VectorIndexConfig indexConfig)
      throws Exception {
    return null;
  }

  @Override
  public IndexReaderFactory<VectorIndexReader> getReaderFactory() {
    return null;
  }

  @Override
  public String getFileExtension(ColumnMetadata columnMetadata) {
    return null;
  }

  @Override
  public IndexHandler createIndexHandler(SegmentDirectory segmentDirectory, Map<String, FieldIndexConfigs> configsByCol,
      @Nullable Schema schema, @Nullable TableConfig tableConfig) {
    return null;
  }

  @Override
  public void convertToNewFormat(TableConfig tableConfig, Schema schema) {

  }
}
