package io.basc.framework.sql.orm;

import io.basc.framework.orm.EntityDescriptorWrapper;

public class TableDescriptorWrapper<W extends TableDescriptor> extends EntityDescriptorWrapper<W, Column>
		implements TableDescriptor {

	public TableDescriptorWrapper(W wrappedTarget) {
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
