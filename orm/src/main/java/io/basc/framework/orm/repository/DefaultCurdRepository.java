package io.basc.framework.orm.repository;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.domain.PageRequest;
import io.basc.framework.util.page.Paginations;
import io.basc.framework.util.stream.Cursor;

import java.util.List;

public class DefaultCurdRepository<V> implements CurdRepository<V> {
	private final Class<? extends V> entityClass;
	private final Repository repository;

	public DefaultCurdRepository(Class<? extends V> entityClass,
			Repository repository) {
		this.entityClass = entityClass;
		this.repository = repository;
	}

	@Override
	public V getById(Object... ids) {
		return repository.getById(entityClass, ids);
	}

	@Override
	public List<V> queryList(V conditions) {
		return repository.queryList(TypeDescriptor.valueOf(entityClass),
				entityClass, conditions);
	}

	@Override
	public boolean isPresent(V conditions) {
		return repository.isPresent(entityClass, conditions);
	}

	@Override
	public boolean isPresentAny(V conditions) {
		return repository.isPresentAny(entityClass, conditions);
	}

	@Override
	public Cursor<V> query(V conditions, PageRequest request) {
		return repository.query(TypeDescriptor.valueOf(entityClass),
				entityClass, conditions, request);
	}

	@Override
	public Cursor<V> queryAll(V conditions) {
		return repository.queryAll(TypeDescriptor.valueOf(entityClass),
				entityClass, conditions);
	}

	@Override
	public Cursor<V> queryAll() {
		return repository.queryAll(TypeDescriptor.valueOf(entityClass),
				entityClass);
	}

	@Override
	public List<V> getInIds(List<?> inIds, Object... ids) {
		return repository.getInIds(TypeDescriptor.valueOf(entityClass),
				entityClass, inIds, ids);
	}

	@Override
	public Paginations<V> pagingQuery(V conditions, PageRequest request) {
		return repository.pagingQuery(TypeDescriptor.valueOf(entityClass),
				entityClass, conditions, request);
	}

	@Override
	public void save(V entity) {
		repository.save(entityClass, entity);
	}

	@Override
	public long updateAll(V entity, V conditions) {
		return repository.updateAll(entityClass, entity, conditions);
	}

	@Override
	public boolean update(V entity) {
		return repository.update(entityClass, entity);
	}

	@Override
	public long deleteAll() {
		return repository.deleteAll(entityClass);
	}

	@Override
	public long deleteAll(V conditions) {
		return repository.deleteAll(entityClass, conditions);
	}

	@Override
	public boolean deleteById(Object... ids) {
		return repository.deleteById(entityClass, ids);
	}

	@Override
	public boolean delete(V entity) {
		return repository.delete(entityClass, entity);
	}

	@Override
	public boolean saveIfAbsent(V entity) {
		return repository.saveIfAbsent(entityClass, entity);
	}
}
