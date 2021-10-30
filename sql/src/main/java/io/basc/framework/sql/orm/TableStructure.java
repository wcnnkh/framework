package io.basc.framework.sql.orm;

import io.basc.framework.orm.EntityStructure;

public interface TableStructure extends TableDescriptor, EntityStructure<Column> {

	default TableStructure rename(String name) {
		return new TableStructureWrapper<TableStructure>(this) {
			@Override
			public String getName() {
				return name;
			}
		};
	}
}
