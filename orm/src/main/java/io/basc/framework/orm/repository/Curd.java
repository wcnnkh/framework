package io.basc.framework.orm.repository;

import java.util.List;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.orm.OrmException;
import io.basc.framework.util.Elements;
import io.basc.framework.util.page.Paginations;

public interface Curd<V> {
	boolean delete(V entity);

	long deleteAll();

	long deleteAll(V conditions);

	boolean deleteById(Object... primaryKeys);

	V getById(Object... primaryKeys);

	<T> T getById(TypeDescriptor resultsTypeDescriptor, Object... primaryKeys) throws OrmException;

	<K> Elements<V> getInIds(List<? extends K> inPrimaryKeys, Object... primaryKeys);

	<K, T> Elements<T> getInIds(TypeDescriptor resultsTypeDescriptor, List<? extends K> inPrimaryKeys,
			Object... primaryKeys) throws OrmException;

	boolean isPresentById(Object... primaryKeys);

	boolean isPresent(V conditions);

	<T> Paginations<T> query(TypeDescriptor resultsTypeDescriptor, V conditions) throws OrmException;

	Paginations<V> query(V conditions);

	Paginations<V> queryAll();

	<T> Paginations<T> queryAll(TypeDescriptor resultsTypeDescriptor) throws OrmException;

	void save(V entity);

	boolean saveIfAbsent(V entity);

	boolean update(V entity);

	long updateAll(V entity, V conditions);
}
