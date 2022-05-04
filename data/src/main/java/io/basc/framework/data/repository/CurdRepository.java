package io.basc.framework.data.repository;

import io.basc.framework.util.page.Pagination;

public interface CurdRepository<K, V> {

	V getById(K key);

	boolean deleteById(K key);

	boolean save(V entity);

	boolean update(V entity);

	boolean delete(V entity);

	Pagination<V> select(V condition, long pageNum, int pageSize);
}
