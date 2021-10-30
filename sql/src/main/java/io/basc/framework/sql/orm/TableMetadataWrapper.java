package io.basc.framework.sql.orm;

import io.basc.framework.orm.EntityMetadataWrapper;

public class TableMetadataWrapper<W extends TableMetadata> extends EntityMetadataWrapper<W> implements TableMetadata {

	public TableMetadataWrapper(W wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public String getEngine() {
		return wrappedTarget.getEngine();
	}

	@Override
	public String getRowFormat() {
		return wrappedTarget.getRowFormat();
	}

}
