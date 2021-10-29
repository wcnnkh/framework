package io.basc.framework.sql.orm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.basc.framework.orm.EntityDescriptor;
import io.basc.framework.util.CollectionUtils;

public interface TableDescriptor extends EntityDescriptor<Column>, TableMetadata {
	
	/**
	 * 获取所有的列(排除实体字段)
	 * @return
	 */
	default List<Column> getColumns(){
		return stream().filter((c) -> !c.isEntity()).collect(Collectors.toList());
	}
	
	default List<Column> getPrimaryKeys() {
		return stream().filter((column) -> column.isPrimaryKey() && !column.isEntity()).collect(Collectors.toList());
	}

	default List<Column> getNotPrimaryKeys() {
		return stream().filter((column) -> !column.isPrimaryKey() && !column.isEntity()).collect(Collectors.toList());
	}

	default Map<IndexInfo, List<Column>> getIndexGroups() {
		Map<IndexInfo, List<Column>> groups = new LinkedHashMap<>();
		for (Column column : this) {
			Collection<IndexInfo> indexs = column.getIndexs();
			if (!CollectionUtils.isEmpty(indexs)) {
				for (IndexInfo indexInfo : indexs) {
					List<Column> columns = groups.get(indexInfo);
					if (columns == null) {
						columns = new ArrayList<>(8);
					}
					columns.add(column);
					groups.put(indexInfo, columns);
				}
			}
		}
		return groups;
	}
	
	
}
