package io.basc.framework.sql.orm;

import io.basc.framework.orm.EntityDescriptor;
import io.basc.framework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface TableDescriptor extends EntityDescriptor<Column>, TableMetadata {
	
	default Map<IndexInfo, List<Column>> getIndexGroups() {
		Map<IndexInfo, List<Column>> groups = new LinkedHashMap<>();
		columns().forEach((column) -> {
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
		});
		return groups;
	}
}
