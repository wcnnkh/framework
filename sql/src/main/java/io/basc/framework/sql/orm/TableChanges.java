package io.basc.framework.sql.orm;

import java.util.Collection;
import java.util.Collections;

import io.basc.framework.mapper.Field;
import io.basc.framework.util.CollectionUtils;

/**
 * 获取表变更
 * 
 * @author wcnnkh
 *
 */
public class TableChanges {
	private Collection<String> deleteColumns;
	private Collection<Field> addColumns;

	@SuppressWarnings("unchecked")
	public TableChanges(Collection<String> deleteColumns, Collection<Field> addColumns) {
		this.deleteColumns = CollectionUtils.isEmpty(deleteColumns) ? Collections.EMPTY_LIST
				: Collections.unmodifiableCollection(deleteColumns);
		this.addColumns = CollectionUtils.isEmpty(addColumns) ? Collections.EMPTY_LIST
				: Collections.unmodifiableCollection(addColumns);
	}

	public Collection<String> getDeleteColumns() {
		return deleteColumns;
	}

	public Collection<Field> getAddColumnss() {
		return addColumns;
	}
}
