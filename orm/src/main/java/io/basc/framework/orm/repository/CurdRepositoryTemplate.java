package io.basc.framework.orm.repository;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.domain.PageRequest;
import io.basc.framework.orm.OrmException;
import io.basc.framework.util.Assert;
import io.basc.framework.util.page.Paginations;
import io.basc.framework.util.stream.Cursor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurdRepositoryTemplate implements CurdRepository {
	private final Map<Class<?>, Curd<?>> repositoryMap = new HashMap<Class<?>, Curd<?>>();
	private final CurdRepository repository;

	public CurdRepositoryTemplate(CurdRepository repository) {
		Assert.requiredArgument(repository != null, "repository");
		this.repository = repository;
	}

	public <T> void register(Class<? extends T> entityClass,
			Curd<? extends T> repository) {
		synchronized (repositoryMap) {
			repositoryMap.put(entityClass, repository);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> Curd<T> getCurd(Class<? extends T> entityClass) {
		Assert.requiredArgument(entityClass != null, "entityClass");
		Curd<T> curd = (Curd<T>) repositoryMap.get(entityClass);
		if (curd == null) {
			synchronized (repositoryMap) {
				curd = (Curd<T>) repositoryMap.get(entityClass);
				if (curd == null) {
					curd = new DefaultCurd<T>(entityClass, repository);
					repositoryMap.put(entityClass, curd);
				}
			}
		}
		return curd;
	}

	public CurdRepository getRepository() {
		return repository;
	}

	@Override
	public <T> boolean delete(Class<? extends T> entityClass, T entity) {
		Curd<T> curd = getCurd(entityClass);
		return curd.delete(entity);
	}

	@Override
	public <T> boolean deleteById(Class<? extends T> entityClass, Object... ids) {
		Curd<T> curd = getCurd(entityClass);
		return curd.deleteById(ids);
	}

	@Override
	public <T> T getById(Class<? extends T> entityClass, Object... ids) {
		Curd<T> curd = getCurd(entityClass);
		return curd.getById(ids);
	}

	@Override
	public <T> boolean isPresent(Class<? extends T> entityClass, T entity) {
		Curd<T> curd = getCurd(entityClass);
		return curd.isPresent(entity);
	}

	@Override
	public <T> boolean isPresentById(Class<? extends T> entityClass,
			Object... ids) {
		Curd<T> curd = getCurd(entityClass);
		return curd.isPresentById(ids);
	}

	@Override
	public <T> void save(Class<? extends T> entityClass, T entity) {
		Curd<T> curd = getCurd(entityClass);
		curd.save(entity);
	}

	@Override
	public <T> boolean update(Class<? extends T> entityClass, T entity) {
		Curd<T> curd = getCurd(entityClass);
		return curd.update(entity);
	}

	@Override
	public <T> long deleteAll(Class<? extends T> entityClass, T conditions) {
		Curd<T> curd = getCurd(entityClass);
		return curd.deleteAll(conditions);
	}

	@Override
	public <T> long deleteAll(Class<? extends T> entityClass) {
		Curd<T> curd = getCurd(entityClass);
		return curd.deleteAll();
	}

	@Override
	public <T, E> T getById(TypeDescriptor resultsTypeDescriptor,
			Class<? extends E> entityClass, Object... entityIds)
			throws OrmException {
		Curd<E> curd = getCurd(entityClass);
		return curd.getById(resultsTypeDescriptor, entityIds);
	}

	@Override
	public <T, E> List<T> getInIds(TypeDescriptor resultsTypeDescriptor,
			Class<? extends E> entityClass, List<?> entityInIds,
			Object... entityIds) throws OrmException {
		Curd<E> curd = getCurd(entityClass);
		return curd.getInIds(resultsTypeDescriptor, entityInIds, entityIds);
	}

	@Override
	public <T> boolean isPresentAny(Class<? extends T> entityClass, T conditions) {
		Curd<T> curd = getCurd(entityClass);
		return curd.isPresentAny(conditions);
	}

	@Override
	public <T, E> Paginations<T> pagingQuery(
			TypeDescriptor resultsTypeDescriptor,
			Class<? extends E> entityClass, E conditions, PageRequest request)
			throws OrmException {
		Curd<E> curd = getCurd(entityClass);
		return curd.pagingQuery(resultsTypeDescriptor, conditions, request);
	}

	@Override
	public <T, E> Cursor<T> query(TypeDescriptor resultsTypeDescriptor,
			Class<? extends E> entityClass, E conditions, PageRequest request)
			throws OrmException {
		Curd<E> curd = getCurd(entityClass);
		return curd.query(resultsTypeDescriptor, conditions, request);
	}

	@Override
	public <T, E> Cursor<T> queryAll(TypeDescriptor resultsTypeDescriptor,
			Class<? extends E> entityClass, E conditions) throws OrmException {
		Curd<E> curd = getCurd(entityClass);
		return curd.queryAll(resultsTypeDescriptor, conditions);
	}

	@Override
	public <T, E> Cursor<T> queryAll(TypeDescriptor resultsTypeDescriptor,
			Class<? extends E> entityClass) throws OrmException {
		Curd<E> curd = getCurd(entityClass);
		return curd.queryAll(resultsTypeDescriptor);
	}

	@Override
	public <T> long updateAll(Class<? extends T> entityClass, T entity,
			T conditions) {
		Curd<T> curd = getCurd(entityClass);
		return curd.updateAll(entity, conditions);
	}
}
