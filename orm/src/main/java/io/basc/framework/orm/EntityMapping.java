package io.basc.framework.orm;

import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.stereotype.Mapping;
import io.basc.framework.util.collections.Elements;

/**
 * 实体映射
 * 
 * @author wcnnkh
 *
 * @param <T>
 */
public interface EntityMapping<T extends ColumnDescriptor> extends Mapping<T> {

	default Elements<T> getPrimaryKeys() {
		return columns().filter((e) -> e.isPrimaryKey());
	}

	default Elements<T> getNotPrimaryKeys() {
		return columns().filter((e) -> !e.isPrimaryKey());
	}

	@Nullable
	String getComment();

	@Nullable
	String getCharsetName();

	/**
	 * 所有列，默认不包含实体字段
	 * 
	 * @return
	 */
	default Elements<T> columns() {
		return getElements().filter((e) -> !e.isEntity());
	}
}
