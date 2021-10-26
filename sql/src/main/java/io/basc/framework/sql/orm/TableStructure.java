package io.basc.framework.sql.orm;

import io.basc.framework.orm.EntityStructure;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
	
	default boolean indexExists(Column column){
		if(column.isPrimaryKey() || column.isUnique()){
			return true;
		}

		Map<String, List<Column>> indexGroup = getIndexGroup();
		if(indexGroup != null){
			for(Entry<String, List<Column>> entry : indexGroup.entrySet()){
				for(Column col : entry.getValue()){
					if(col.getName().equals(col.getName())){
						return true;
					}
				}
			}
		}
		return false;
	}
}
