package scw.orm.sql;

import java.util.List;
import java.util.Map;

import scw.orm.EntityStructure;

public interface TableStructure extends EntityStructure<Column> {
	Map<String, List<Column>> getIndexGroup();

	default TableStructure rename(String name) {
		return new TableStructureWrapper<TableStructure>(this) {
			@Override
			public String getName() {
				return name;
			}
		};
	}
}
