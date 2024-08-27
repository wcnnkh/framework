package io.basc.framework.orm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.domain.Entry;
import io.basc.framework.data.domain.Query;
import io.basc.framework.data.repository.Condition;
import io.basc.framework.data.repository.ConditionSymbol;
import io.basc.framework.data.repository.DeleteOperation;
import io.basc.framework.data.repository.DeleteOperationSymbol;
import io.basc.framework.data.repository.Expression;
import io.basc.framework.data.repository.InsertOperation;
import io.basc.framework.data.repository.InsertOperationSymbol;
import io.basc.framework.data.repository.QueryOperation;
import io.basc.framework.data.repository.QueryOperationSymbol;
import io.basc.framework.data.repository.RepositoryOperations;
import io.basc.framework.data.repository.Sort;
import io.basc.framework.data.repository.UpdateOperation;
import io.basc.framework.data.repository.UpdateOperationSymbol;
import io.basc.framework.execution.param.Parameter;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Range;

public interface EntityOperations extends RepositoryOperations {
	default <T> long delete(Class<? extends T> entityClass, T entity) {
		return delete(DeleteOperationSymbol.DELETE, entityClass, entity);
	}

	default <T> long delete(DeleteOperationSymbol deleteOperationSymbol, Class<? extends T> entityClass, T entity) {
		EntityRepository<T> repository = getMapper().getRepository(deleteOperationSymbol, entityClass, entity);
		Elements<? extends Condition> conditions = getMapper().getConditions(deleteOperationSymbol, repository);
		DeleteOperation operation = new DeleteOperation(deleteOperationSymbol, repository);
		operation.setConditions(conditions);
		return delete(operation);
	}

	default <T> long delete(DeleteOperationSymbol deleteOperationSymbol, Object entity) {
		Assert.requiredArgument(entity != null, "entity");
		return delete(deleteOperationSymbol, entity.getClass(), entity);
	}

	default long delete(Object entity) {
		return delete(DeleteOperationSymbol.DELETE, entity);
	}

	default long deleteAll(Class<?> entityClass) {
		EntityRepository<?> repository = getMapper().getRepository(DeleteOperationSymbol.DELETE, entityClass, null);
		DeleteOperation operation = new DeleteOperation(repository);
		return delete(operation);
	}

	default boolean deleteById(Class<?> entityClass, Object... ids) {
		EntityRepository<?> repository = getMapper().getRepository(DeleteOperationSymbol.DELETE, entityClass, null);
		Assert.isTrue(repository.getEntityMapping().getPrimaryKeys().count() != ids.length,
				"Inconsistent number of primary keys and parameters");
		return deleteByPrimaryKeys(DeleteOperationSymbol.DELETE, repository, ids) > 0;
	}

	default <T> boolean deleteById(Class<T> entityClass, T entity) {
		return deleteById(DeleteOperationSymbol.DELETE, entityClass, entity);
	}

	default <T> boolean deleteById(DeleteOperationSymbol deleteOperationSymbol, Class<? extends T> entityClass,
			T entity) {
		EntityRepository<?> repository = getMapper().getRepository(deleteOperationSymbol, entityClass, entity);
		List<Entry<ColumnDescriptor, Parameter>> conditionEntries = getMapper().getEntries(entity,
				repository.getEntityMapping().getPrimaryKeys().iterator());
		List<Condition> conditions = conditionEntries.stream().map((e) -> e.getValue()).map((parameter) -> {
			return new Condition(parameter.getName(), ConditionSymbol.EQU, parameter.getValue(),
					parameter.getTypeDescriptor());
		}).collect(Collectors.toList());

		DeleteOperation deleteOperation = new DeleteOperation(deleteOperationSymbol, repository);
		deleteOperation.setConditions(Elements.of(conditions));
		return delete(deleteOperation) > 0;
	}

	default boolean deleteById(Object entity) {
		Assert.requiredArgument(entity != null, "entity");
		return deleteById(entity.getClass(), entity);
	}

	/**
	 * 根据主键进行删除
	 * 
	 * @param entityClass
	 * @param primaryKey  如果存在多主键就使用多个数据
	 * @return
	 */
	default long deleteByPrimaryKeys(Class<?> entityClass, Object... primaryKeys) {
		EntityRepository<?> repository = getMapper().getRepository(DeleteOperationSymbol.DELETE, entityClass, null);
		return deleteByPrimaryKeys(DeleteOperationSymbol.DELETE, repository, primaryKeys);
	}

	default long deleteByPrimaryKeys(DeleteOperationSymbol operationSymbol, EntityRepository<?> repository,
			Object... primaryKeys) {
		List<Entry<ColumnDescriptor, Parameter>> conditionEntries = getMapper().combineEntries(
				repository.getEntityMapping().getPrimaryKeys().iterator(), Arrays.asList(primaryKeys).iterator());
		List<Condition> conditions = conditionEntries.stream().map((e) -> e.getValue()).map((parameter) -> {
			return new Condition(parameter.getName(), ConditionSymbol.EQU, parameter.getValue(),
					parameter.getTypeDescriptor());
		}).collect(Collectors.toList());
		DeleteOperation deleteOperation = new DeleteOperation(operationSymbol, repository);
		deleteOperation.setConditions(Elements.of(conditions));
		return delete(deleteOperation);
	}

	@SuppressWarnings("unchecked")
	default <T> T getById(Class<T> entityClass, Object... ids) {
		EntityRepository<?> repository = getMapper().getRepository(QueryOperationSymbol.QUERY, entityClass, null);
		Assert.isTrue(repository.getEntityMapping().getPrimaryKeys().count() != ids.length,
				"Inconsistent number of primary keys and parameters");
		return (T) queryByPrimaryKeys(TypeDescriptor.valueOf(entityClass), QueryOperationSymbol.QUERY, repository, ids)
				.getElements().first();
	}

	default <T> T getById(Class<T> entityClass, T entity) {
		return getById(QueryOperationSymbol.QUERY, entityClass, entity);
	}

	default <T> T getById(QueryOperationSymbol queryOperationSymbol, Class<? extends T> entityClass, T entity) {
		EntityRepository<T> repository = getMapper().getRepository(queryOperationSymbol, entityClass, entity);
		Elements<? extends Expression> columns = getMapper().getColumns(queryOperationSymbol, repository);

		List<Entry<ColumnDescriptor, Parameter>> conditionEntries = getMapper().getEntries(entity,
				repository.getEntityMapping().getPrimaryKeys().iterator());
		List<Condition> conditions = conditionEntries.stream().map((e) -> e.getValue()).map((parameter) -> {
			return new Condition(parameter.getName(), ConditionSymbol.EQU, parameter.getValue(),
					parameter.getTypeDescriptor());
		}).collect(Collectors.toList());

		QueryOperation queryOperation = new QueryOperation(queryOperationSymbol, columns, repository);
		queryOperation.setConditions(Elements.of(conditions));
		Query<T> query = query(TypeDescriptor.valueOf(entityClass), queryOperation);
		return query.getElements().first();
	}

	@SuppressWarnings("unchecked")
	default <T> T getById(T entity) {
		Assert.requiredArgument(entity != null, "entity");
		return (T) getById(entity.getClass(), entity);
	}

	EntityMapper getMapper();

	default <T> boolean insert(Class<? extends T> entityClass, T entity) {
		return insert(InsertOperationSymbol.INSERT, entityClass, entity) > 0;
	}

	default <T> boolean saveOrUpdate(Class<? extends T> entityClass, T entity) {
		return insert(InsertOperationSymbol.SAVE_OR_UPDATE, entityClass, entity) > 0;
	}

	default <T> boolean saveIfAbsent(Class<? extends T> entityClass, T entity) {
		return insert(InsertOperationSymbol.SAVE_IF_ABSENT, entityClass, entity) > 0;
	}

	default boolean saveOrUpdate(Object entity) {
		Assert.requiredArgument(entity != null, "entity");
		return saveOrUpdate(entity.getClass(), entity);
	}

	default boolean saveIfAbsent(Object entity) {
		Assert.requiredArgument(entity != null, "entity");
		return saveIfAbsent(entity.getClass(), entity);
	}

	default <T> long insert(InsertOperationSymbol insertOperationSymbol, Class<? extends T> entityClass, T entity) {
		EntityRepository<T> repository = getMapper().getRepository(insertOperationSymbol, entityClass, entity);
		Elements<? extends Expression> columns = getMapper().getColumns(insertOperationSymbol, repository);
		InsertOperation operation = new InsertOperation(insertOperationSymbol, repository, columns);

		if (insertOperationSymbol.isIncludeConditions()) {
			List<Entry<ColumnDescriptor, Parameter>> conditionEntries = getMapper().getEntries(entity,
					repository.getEntityMapping().getPrimaryKeys().iterator());
			List<Condition> conditions = conditionEntries.stream().map((e) -> e.getValue()).map((parameter) -> {
				return new Condition(parameter.getName(), ConditionSymbol.EQU, parameter.getValue(),
						parameter.getTypeDescriptor());
			}).collect(Collectors.toList());
			operation.setConditions(Elements.of(conditions));
		}
		return insert(operation);
	}

	default <T> long insert(InsertOperationSymbol insertOperationSymbol, Object entity) {
		Assert.requiredArgument(entity != null, "entity");
		return insert(insertOperationSymbol, entity.getClass(), entity);
	}

	default long insert(Object entity) {
		return insert(InsertOperationSymbol.INSERT, entity);
	}

	default <T> Query<T> query(Class<? extends T> entityClass, T entity) {
		return query(TypeDescriptor.valueOf(entityClass), entityClass, entity);
	}

	@SuppressWarnings("unchecked")
	default <T> Query<T> query(T entity) {
		return (Query<T>) query(entity.getClass(), entity);
	}

	default <T, R> Query<R> query(TypeDescriptor resultTypeDescriptor, Class<? extends T> entityClass, T entity) {
		return query(resultTypeDescriptor, QueryOperationSymbol.QUERY, entityClass, entity);
	}

	default <T, R> Query<R> query(TypeDescriptor resultTypeDescriptor, QueryOperationSymbol queryOperationSymbol,
			Class<? extends T> entityClass, T entity) {
		EntityRepository<T> repository = getMapper().getRepository(queryOperationSymbol, entityClass, entity);
		Elements<? extends Expression> columns = getMapper().getColumns(queryOperationSymbol, repository);
		Elements<? extends Condition> conditions = getMapper().getConditions(queryOperationSymbol, repository);
		Elements<? extends Sort> orders = getMapper().getOrders(queryOperationSymbol, repository);
		Range<Long> limit = getMapper().getLimit(queryOperationSymbol, repository);

		QueryOperation operation = new QueryOperation(queryOperationSymbol, columns, repository);
		operation.setConditions(conditions);
		operation.setOrders(orders);
		operation.setLimit(limit);
		return query(resultTypeDescriptor, operation);
	}

	default <T> Query<T> queryAll(Class<T> entityClass) {
		return queryAll(TypeDescriptor.valueOf(entityClass), entityClass);
	}

	default <T> Query<T> queryAll(TypeDescriptor resultTypeDescriptor, Class<?> entityClass) {
		return queryAll(resultTypeDescriptor, QueryOperationSymbol.QUERY, entityClass);
	}

	default <T> Query<T> queryAll(TypeDescriptor resultTypeDescriptor, QueryOperationSymbol queryOperationSymbol,
			Class<?> entityClass) {
		EntityRepository<?> repository = getMapper().getRepository(queryOperationSymbol, entityClass, null);
		Elements<? extends Expression> columns = getMapper().getColumns(queryOperationSymbol, null);
		QueryOperation queryOperation = new QueryOperation(queryOperationSymbol, columns, repository);

		Elements<? extends Sort> orders = getMapper().getOrders(queryOperationSymbol, repository);
		Range<Long> limit = getMapper().getLimit(queryOperationSymbol, repository);
		queryOperation.setOrders(orders);
		queryOperation.setLimit(limit);
		return query(resultTypeDescriptor, queryOperation);
	}

	default <T> Query<T> queryByPrimaryKeys(Class<T> entityClass, Object... primaryKeys) {
		return queryByPrimaryKeys(TypeDescriptor.valueOf(entityClass), entityClass, primaryKeys);
	}

	default <R> Query<R> queryByPrimaryKeys(TypeDescriptor resultTypeDescriptor, Class<?> entityClass,
			Object... primaryKeys) {
		EntityRepository<?> repository = getMapper().getRepository(QueryOperationSymbol.QUERY, entityClass, null);
		return queryByPrimaryKeys(resultTypeDescriptor, QueryOperationSymbol.QUERY, repository, primaryKeys);
	}

	default <R> Query<R> queryByPrimaryKeys(TypeDescriptor resultTypeDescriptor, QueryOperationSymbol operationSymbol,
			EntityRepository<?> repository, Object... primaryKeys) {
		Elements<? extends Expression> columns = getMapper().getColumns(operationSymbol, repository);
		QueryOperation queryOperation = new QueryOperation(operationSymbol, columns, repository);

		List<Entry<ColumnDescriptor, Parameter>> conditionEntries = getMapper().combineEntries(
				repository.getEntityMapping().getPrimaryKeys().iterator(), Arrays.asList(primaryKeys).iterator());
		List<Condition> conditions = conditionEntries.stream().map((e) -> e.getValue()).map((parameter) -> {
			return new Condition(parameter.getName(), ConditionSymbol.EQU, parameter.getValue(),
					parameter.getTypeDescriptor());
		}).collect(Collectors.toList());
		queryOperation.setConditions(Elements.of(conditions));
		return query(resultTypeDescriptor, queryOperation);
	}

	default <K, R> PrimaryKeyQuery<K, R> queryInPrimaryKeys(TypeDescriptor resultTypeDescriptor, Class<?> entityClass,
			Elements<? extends K> inPrimaryKeys, Object... primaryKeys) {
		EntityRepository<?> repository = getMapper().getRepository(QueryOperationSymbol.QUERY, entityClass, null);
		Elements<? extends Expression> columns = getMapper().getColumns(QueryOperationSymbol.QUERY, repository);
		QueryOperation queryOperation = new QueryOperation(QueryOperationSymbol.QUERY, columns, repository);

		Iterator<? extends ColumnDescriptor> propertyIterator = repository.getEntityMapping().getPrimaryKeys().iterator();
		List<Entry<ColumnDescriptor, Parameter>> conditionEntries = getMapper().combineEntries(propertyIterator,
				Arrays.asList(primaryKeys).iterator());
		List<Condition> conditions = new ArrayList<>(conditionEntries.size() + 1);
		for (Entry<ColumnDescriptor, Parameter> entry : conditionEntries) {
			Parameter parameter = entry.getValue();
			conditions.add(new Condition(parameter.getName(), ConditionSymbol.EQU, parameter.getValue(),
					parameter.getTypeDescriptor()));
		}

		if (!propertyIterator.hasNext()) {
			throw new OrmException("The number of primary key parameters must be less than the primary key");
		}

		ColumnDescriptor property = propertyIterator.next();
		conditions.add(new Condition(property.getName(), ConditionSymbol.EQU, inPrimaryKeys.toArray(), null));
		queryOperation.setConditions(Elements.of(conditions));
		Query<R> query = query(resultTypeDescriptor, queryOperation);
		return new PrimaryKeyQuery<>(query, repository, getMapper(), inPrimaryKeys, primaryKeys);
	}

	default <T> long update(Class<? extends T> entityClass, T entity, T conditionEntity) {
		return update(UpdateOperationSymbol.UPDATE, entityClass, entity, conditionEntity);
	}

	default <T> long update(UpdateOperationSymbol updateOperationSymbol, Class<? extends T> entityClass, T entity,
			T conditionEntity) {
		EntityRepository<T> conditionRepository = getMapper().getRepository(updateOperationSymbol, entityClass,
				conditionEntity);
		EntityRepository<T> repository = conditionRepository.clone();
		repository.setEntity(entity);
		Elements<? extends Expression> columns = getMapper().getColumns(updateOperationSymbol, repository);
		Elements<? extends Condition> conditions = getMapper().getConditions(updateOperationSymbol,
				conditionRepository);
		UpdateOperation updateOperation = new UpdateOperation(updateOperationSymbol, repository, columns);
		updateOperation.setConditions(conditions);
		return update(updateOperation);
	}

	default <T> boolean updateById(Class<? extends T> entityClass, T entity) {
		return updateById(UpdateOperationSymbol.UPDATE, entityClass, entity);
	}

	default <T> boolean updateById(Class<? extends T> entityClass, T entity, Object... ids) {
		EntityRepository<T> repository = getMapper().getRepository(UpdateOperationSymbol.UPDATE, entityClass, entity);
		Assert.isTrue(repository.getEntityMapping().getPrimaryKeys().count() != ids.length,
				"Inconsistent number of primary keys and parameters");
		return updateByPrimaryKeys(UpdateOperationSymbol.UPDATE, repository, ids) > 0;
	}

	default boolean updateById(Object entity) {
		Assert.requiredArgument(entity != null, "entity");
		return updateById(entity.getClass(), entity);
	}

	default boolean updateById(Object entity, Object... ids) {
		Assert.requiredArgument(entity != null, "entity");
		return updateById(entity.getClass(), entity, ids);
	}

	default <T> boolean updateById(UpdateOperationSymbol updateOperationSymbol, Class<? extends T> entityClass,
			T entity) {
		EntityRepository<T> repository = getMapper().getRepository(updateOperationSymbol, entityClass, entity);
		List<Entry<ColumnDescriptor, Parameter>> columnEntries = getMapper().getEntries(entity,
				repository.getEntityMapping().getNotPrimaryKeys().iterator());
		Elements<? extends Expression> columns = getMapper().toColumns(updateOperationSymbol, repository,
				Elements.of(columnEntries));
		List<Entry<ColumnDescriptor, Parameter>> conditionEntries = getMapper().getEntries(entity,
				repository.getEntityMapping().getPrimaryKeys().iterator());
		List<Condition> conditions = conditionEntries.stream().map((e) -> e.getValue()).map((parameter) -> {
			return new Condition(parameter.getName(), ConditionSymbol.EQU, parameter.getValue(),
					parameter.getTypeDescriptor());
		}).collect(Collectors.toList());

		UpdateOperation updateOperation = new UpdateOperation(updateOperationSymbol, repository, columns);
		updateOperation.setConditions(Elements.of(conditions));
		return update(updateOperation) > 0;
	}

	default <T> long updateByPrimaryKeys(Class<? extends T> entityClass, T entity, Object... primaryKeys) {
		EntityRepository<T> repository = getMapper().getRepository(UpdateOperationSymbol.UPDATE, entityClass, entity);
		return updateByPrimaryKeys(UpdateOperationSymbol.UPDATE, repository, primaryKeys);
	}

	default long updateByPrimaryKeys(Object entity, Object... primaryKeys) {
		Assert.requiredArgument(entity != null, "entity");
		return updateByPrimaryKeys(entity.getClass(), entity, primaryKeys);
	}

	default <T> long updateByPrimaryKeys(UpdateOperationSymbol updateOperationSymbol, EntityRepository<?> repository,
			Object... primaryKeys) {
		Assert.requiredArgument(repository.getEntity() != null, "repository#getEntity()");
		List<Entry<ColumnDescriptor, Parameter>> columnEntries = getMapper().getEntries(repository.getEntity(),
				repository.getEntityMapping().getNotPrimaryKeys().iterator());
		Elements<? extends Expression> columns = getMapper().toColumns(updateOperationSymbol, repository,
				Elements.of(columnEntries));

		List<Entry<ColumnDescriptor, Parameter>> conditionEntries = getMapper().combineEntries(
				repository.getEntityMapping().getPrimaryKeys().iterator(), Arrays.asList(primaryKeys).iterator());
		List<Condition> conditions = conditionEntries.stream().map((e) -> e.getValue()).map((parameter) -> {
			return new Condition(parameter.getName(), ConditionSymbol.EQU, parameter.getValue(),
					parameter.getTypeDescriptor());
		}).collect(Collectors.toList());

		UpdateOperation updateOperation = new UpdateOperation(updateOperationSymbol, repository, columns);
		updateOperation.setConditions(Elements.of(conditions));
		return update(updateOperation);
	}
}
