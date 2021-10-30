package io.basc.framework.sql.orm;

import java.util.Collection;

import io.basc.framework.orm.PropertyMetadataWrapper;

public class ColumnMetadataWrapper<W extends ColumnMetadata> extends PropertyMetadataWrapper<W> implements ColumnMetadata {

	public ColumnMetadataWrapper(W wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public Collection<IndexInfo> getIndexs() {
		return wrappedTarget.getIndexs();
	}

}
