package io.basc.framework.orm.repository;

import io.basc.framework.data.domain.PageRequest;
import io.basc.framework.util.page.Paginations;
import io.basc.framework.util.stream.Cursor;

import java.util.List;

public interface CurdRepository<V> {

	V getById(Object... ids);

	List<V> queryList(V conditions);

	boolean isPresent(V conditions);

	boolean isPresentAny(V conditions);

	Cursor<V> query(V conditions, PageRequest request);

	Cursor<V> queryAll(V conditions);

	Cursor<V> queryAll();

	List<V> getInIds(List<?> inIds, Object... ids);

	Paginations<V> pagingQuery(V conditions, PageRequest request);

	void save(V entity);

	long updateAll(V entity, V conditions);

	boolean update(V entity);

	long deleteAll();

	long deleteAll(V conditions);

	boolean deleteById(Object... ids);

	boolean delete(V entity);

	boolean saveIfAbsent(V entity);
}
