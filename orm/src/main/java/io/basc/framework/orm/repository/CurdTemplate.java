package io.basc.framework.orm.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.domain.Query;
import io.basc.framework.orm.OrmException;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;

public class CurdTemplate implements CurdOperations {
	private final Map<Class<?>, Curd<?>> repositoryMap = new HashMap<Class<?>, Curd<?>>();
	private final CurdOperations repository;

	public CurdTemplate(CurdOperations repository) {
		Assert.requiredArgument(repository != null, "repository");
		this.repository = repository;
	}

	public <T> void register(Class<? extends T> entityClass, Curd<? extends T> repository) {
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

	public CurdOperations getRepository() {
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
	public <T> T getById(Class<? extends T> entityClass, Object... primaryKeys) {
		Curd<T> curd = getCurd(entityClass);
		return curd.getById(primaryKeys);
	}

	@Override
	public <T> boolean isPresent(Class<? extends T> entityClass, T entity) {
		Curd<T> curd = getCurd(entityClass);
		return curd.isPresent(entity);
	}

	@Override
	public <T> boolean isPresentById(Class<? extends T> entityClass, Object... primaryKeys) {
		Curd<T> curd = getCurd(entityClass);
		return curd.isPresentById(primaryKeys);
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
	public <T, E> T getById(TypeDescriptor resultsTypeDescriptor, Class<? extends E> entityClass, Object... primaryKeys)
			throws OrmException {
		Curd<E> curd = getCurd(entityClass);
		return curd.getById(resultsTypeDescriptor, primaryKeys);
	}

	@Override
	public <K, T> Elements<T> getInIds(TypeDescriptor resultsTypeDescriptor, Class<?> entityClass,
			List<? extends K> inPrimaryKeys, Object... primaryKeys) throws OrmException {
		Curd<?> curd = getCurd(entityClass);
		return curd.getInIds(resultsTypeDescriptor, inPrimaryKeys, primaryKeys);
	}

	@Override
	public <T, E> Query<T> query(TypeDescriptor resultsTypeDescriptor, Class<? extends E> entityClass,
			E conditions) throws OrmException {
		Curd<E> curd = getCurd(entityClass);
		return curd.query(resultsTypeDescriptor, conditions);
	}

	@Override
	public <T, E> Query<T> queryAll(TypeDescriptor resultsTypeDescriptor, Class<? extends E> entityClass)
			throws OrmException {
		Curd<E> curd = getCurd(entityClass);
		return curd.queryAll(resultsTypeDescriptor);
	}

	@Override
	public <T> long updateAll(Class<? extends T> entityClass, T entity, T conditions) {
		Curd<T> curd = getCurd(entityClass);
		return curd.updateAll(entity, conditions);
	}
}
