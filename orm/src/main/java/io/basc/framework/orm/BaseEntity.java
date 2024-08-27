package io.basc.framework.orm;

import java.util.OptionalLong;

import io.basc.framework.data.domain.Query;
import io.basc.framework.util.Elements;

public interface BaseEntity<T> {
	OptionalLong insert();

	OptionalLong insertOrUpdate();

	OptionalLong insertIfAbsent();

	OptionalLong update(T oldEntity);

	OptionalLong updateByPrimaryKeys();

	OptionalLong delete();

	OptionalLong deleteByPrimaryKeys();

	Query<T> select();

	Query<T> selectByPrimaryKeys();

	<K> PrimaryKeyQuery<K, T> selectInPrimaryKeys(Elements<? extends K> inPrimaryKeys);
}
