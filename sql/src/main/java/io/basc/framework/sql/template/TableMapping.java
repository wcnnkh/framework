package io.basc.framework.sql.template;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.basc.framework.orm.EntityMapping;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.Elements;

public interface TableMapping<T extends Column> extends EntityMapping<T> {
	String getEngine();

	String getRowFormat();

	boolean isAutoCreate();

	default Map<IndexInfo, List<Column>> getIndexGroups() {
		Map<IndexInfo, List<Column>> groups = new LinkedHashMap<>();
		columns().forEach((column) -> {
			Elements<IndexInfo> indexs = column.getIndexs();
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
