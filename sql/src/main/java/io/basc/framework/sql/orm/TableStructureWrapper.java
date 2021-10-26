package io.basc.framework.sql.orm;

import io.basc.framework.orm.EntityStructureWrapper;

import java.util.List;
import java.util.Map;

public class TableStructureWrapper<M extends TableStructure> extends
		EntityStructureWrapper<M, Column> implements TableStructure {

	public TableStructureWrapper(M tableStructure) {
		super(tableStructure);
	}

	@Override
	public String getName() {
		return wrappedTarget.getName();
	}

	@Override
	public Map<String, List<Column>> getIndexGroup() {
		return wrappedTarget.getIndexGroup();
	}
}
