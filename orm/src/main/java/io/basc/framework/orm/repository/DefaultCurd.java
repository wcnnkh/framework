package io.basc.framework.orm.repository;

import java.util.List;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.orm.OrmException;
import io.basc.framework.util.Elements;
import io.basc.framework.util.page.Paginations;

public class DefaultCurd<V> implements Curd<V> {
	private final Class<? extends V> entityClass;
	private final CurdOperations repository;

	public DefaultCurd(Class<? extends V> entityClass, CurdOperations repository) {
		this.entityClass = entityClass;
		this.repository = repository;
	}

	@Override
	public V getById(Object... ids) {
		return repository.getById(entityClass, ids);
	}

	@Override
	public boolean isPresent(V conditions) {
		return repository.isPresent(entityClass, conditions);
	}

	@Override
	public Paginations<V> query(V conditions) {
		return repository.query(TypeDescriptor.valueOf(entityClass), entityClass, conditions);
	}

	@Override
	public Paginations<V> queryAll() {
		return repository.queryAll(TypeDescriptor.valueOf(entityClass), entityClass);
	}

	@Override
	public <K> Elements<V> getInIds(List<? extends K> inIds, Object... ids) {
		return repository.getInIds(TypeDescriptor.valueOf(entityClass), entityClass, inIds, ids);
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

	@Override
	public <T> T getById(TypeDescriptor resultsTypeDescriptor, Object... entityIds) throws OrmException {
		return repository.getById(resultsTypeDescriptor, entityClass, entityIds);
	}

	@Override
	public <K, T> Elements<T> getInIds(TypeDescriptor resultsTypeDescriptor, List<? extends K> entityInIds,
			Object... entityIds) throws OrmException {
		return repository.getInIds(resultsTypeDescriptor, entityClass, entityInIds, entityIds);
	}

	@Override
	public <T> Paginations<T> query(TypeDescriptor resultsTypeDescriptor, V conditions) throws OrmException {
		return repository.query(resultsTypeDescriptor, entityClass, conditions);
	}

	@Override
	public <T> Paginations<T> queryAll(TypeDescriptor resultsTypeDescriptor) throws OrmException {
		return repository.queryAll(resultsTypeDescriptor, entityClass);
	}

	@Override
	public boolean isPresentById(Object... ids) {
		return repository.isPresentById(entityClass, ids);
	}
}
