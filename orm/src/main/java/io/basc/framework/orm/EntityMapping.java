package io.basc.framework.orm;

import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.Mapping;
import io.basc.framework.util.Elements;

/**
 * 实体映射
 * 
 * @author wcnnkh
 *
 * @param <T>
 */
public interface EntityMapping<T extends Property> extends Mapping<T> {

	default Elements<T> getPrimaryKeys() {
		return getElements().filter((e) -> e.isPrimaryKey());
	}

	default Elements<T> getNotPrimaryKeys() {
		return getElements().filter((e) -> !e.isPrimaryKey());
	}

	@Nullable
	String getComment();

	@Nullable
	String getCharsetName();
}
