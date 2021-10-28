package io.basc.framework.sql.orm;

import java.util.Collection;

public class TableStructureWrapper<W extends TableStructure> extends TableDescriptorWrapper<W>
		implements TableStructure {

	public TableStructureWrapper(W tableStructure) {
		super(tableStructure);
	}

	@Override
	public TableStructure rename(String name) {
		return wrappedTarget.rename(name);
	}

	@Override
	public Class<?> getEntityClass() {
		return wrappedTarget.getEntityClass();
	}

	@Override
	public Collection<String> getAliasNames() {
		return wrappedTarget.getAliasNames();
	}

}
