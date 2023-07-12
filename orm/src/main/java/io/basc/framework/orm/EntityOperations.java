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
import io.basc.framework.data.repository.DeleteOperationSymbol;
import io.basc.framework.data.repository.Expression;
import io.basc.framework.data.repository.InsertOperationSymbol;
import io.basc.framework.data.repository.QueryOperation;
import io.basc.framework.data.repository.QueryOperationSymbol;
import io.basc.framework.data.repository.RepositoryOperations;
import io.basc.framework.data.repository.Sort;
import io.basc.framework.data.repository.UpdateOperationSymbol;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Range;

public interface EntityOperations extends RepositoryOperations {
	default long delete(Object entity) {
		return delete(DeleteOperationSymbol.DELETE, entity);
	}

	default <T> long delete(Class<? extends T> entityClass, T entity) {
		return delete(DeleteOperationSymbol.DELETE, entityClass, entity);
	}

	default <T> long delete(DeleteOperationSymbol deleteOperationSymbol, Class<? extends T> entityClass, T entity) {
		EntityRepository<T> repository = getMapper().getRepository(deleteOperationSymbol, entityClass, entity);
		Elements<? extends Condition> conditions = getMapper().getConditions(deleteOperationSymbol, repository);
		return delete(deleteOperationSymbol, repository, conditions);
	}

	default <T> long delete(DeleteOperationSymbol deleteOperationSymbol, Object entity) {
		Assert.requiredArgument(entity != null, "entity");
		return delete(deleteOperationSymbol, entity.getClass(), entity);
	}

	default long deleteAll(Class<?> entityClass) {
		EntityRepository<?> repository = getMapper().getRepository(DeleteOperationSymbol.DELETE, entityClass, null);
		return delete(DeleteOperationSymbol.DELETE, repository, null);
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
		List<Entry<Property, Parameter>> conditionEntries = getMapper().combineEntries(
				repository.getEntityMapping().getPrimaryKeys().iterator(), Arrays.asList(primaryKeys).iterator());
		List<Condition> conditions = conditionEntries.stream().map((e) -> e.getValue()).map((parameter) -> {
			return new Condition(parameter.getName(), ConditionSymbol.EQU, parameter.getSource(),
					parameter.getTypeDescriptor());
		}).collect(Collectors.toList());
		return delete(DeleteOperationSymbol.DELETE, repository, Elements.of(conditions));
	}

	default <T> long deleteByPrimaryKeys(Class<T> entityClass, T entity) throws OrmException {
		EntityRepository<T> repository = getMapper().getRepository(DeleteOperationSymbol.DELETE, entityClass, entity);
		List<Entry<Property, Parameter>> conditionEntries = getMapper().getEntries(entity,
				repository.getEntityMapping().getPrimaryKeys().iterator());
		List<Condition> conditions = conditionEntries.stream().map((e) -> e.getValue()).map((parameter) -> {
			return new Condition(parameter.getName(), ConditionSymbol.EQU, parameter.getSource(),
					parameter.getTypeDescriptor());
		}).collect(Collectors.toList());
		return delete(DeleteOperationSymbol.DELETE, repository, Elements.of(conditions));
	}

	EntityMapper getMapper();

	default long insert(Object entity) {
		return insert(InsertOperationSymbol.INSERT, entity);
	}

	default <T> long insert(Class<? extends T> entityClass, T entity) {
		return insert(InsertOperationSymbol.INSERT, entityClass, entity);
	}

	default <T> long insert(InsertOperationSymbol insertOperationSymbol, Class<? extends T> entityClass, T entity) {
		EntityRepository<T> repository = getMapper().getRepository(insertOperationSymbol, entityClass, entity);
		Elements<? extends Expression> columns = getMapper().getColumns(insertOperationSymbol, repository);
		return insert(insertOperationSymbol, columns, repository);
	}

	default <T> long insert(InsertOperationSymbol insertOperationSymbol, Object entity) {
		Assert.requiredArgument(entity != null, "entity");
		return insert(insertOperationSymbol, entity.getClass(), entity);
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
		QueryOperation operation = new QueryOperation(queryOperationSymbol, repository);
		operation.setColumns(columns);
		operation.setConditions(conditions);
		operation.setOrders(orders);
		operation.setLimit(limit);
		return query(resultTypeDescriptor, operation);
	}

	default <T> Query<T> queryAll(TypeDescriptor resultTypeDescriptor, Class<?> entityClass) {
		return queryAll(resultTypeDescriptor, QueryOperationSymbol.QUERY, entityClass);
	}

	default <T> Query<T> queryAll(TypeDescriptor resultTypeDescriptor, QueryOperationSymbol queryOperationSymbol,
			Class<?> entityClass) {
		EntityRepository<?> repository = getMapper().getRepository(queryOperationSymbol, entityClass, null);
		QueryOperation queryOperation = new QueryOperation(queryOperationSymbol, repository);
		Elements<? extends Expression> columns = getMapper().getColumns(queryOperationSymbol, null);
		queryOperation.setColumns(columns);

		Elements<? extends Sort> orders = getMapper().getOrders(queryOperationSymbol, repository);
		Range<Long> limit = getMapper().getLimit(queryOperationSymbol, repository);
		queryOperation.setOrders(orders);
		queryOperation.setLimit(limit);
		return query(resultTypeDescriptor, queryOperation);
	}

	default <T, R> R getByPrimaryKeys(TypeDescriptor resultTypeDescriptor, Class<? extends T> entityClass, T entity) {
		EntityRepository<?> repository = getMapper().getRepository(QueryOperationSymbol.QUERY, entityClass, null);
		QueryOperation queryOperation = new QueryOperation(QueryOperationSymbol.QUERY, repository);
		List<Entry<Property, Parameter>> conditionEntries = getMapper().getEntries(entity,
				repository.getEntityMapping().getPrimaryKeys().iterator());
		List<Condition> conditions = conditionEntries.stream().map((e) -> e.getValue()).map((parameter) -> {
			return new Condition(parameter.getName(), ConditionSymbol.EQU, parameter.getSource(),
					parameter.getTypeDescriptor());
		}).collect(Collectors.toList());
		queryOperation.setColumns(Elements.of(conditions));
		Query<R> query = query(resultTypeDescriptor, queryOperation);
		return query.getElements().first();
	}

	default <R> Query<R> queryByPrimaryKeys(TypeDescriptor resultTypeDescriptor, Class<?> entityClass,
			Object... primaryKeys) {
		EntityRepository<?> repository = getMapper().getRepository(QueryOperationSymbol.QUERY, entityClass, null);
		QueryOperation queryOperation = new QueryOperation(QueryOperationSymbol.QUERY, repository);
		Elements<? extends Expression> columns = getMapper().getColumns(QueryOperationSymbol.QUERY, repository);
		queryOperation.setColumns(columns);

		List<Entry<Property, Parameter>> conditionEntries = getMapper().combineEntries(
				repository.getEntityMapping().getPrimaryKeys().iterator(), Arrays.asList(primaryKeys).iterator());
		List<Condition> conditions = conditionEntries.stream().map((e) -> e.getValue()).map((parameter) -> {
			return new Condition(parameter.getName(), ConditionSymbol.EQU, parameter.getSource(),
					parameter.getTypeDescriptor());
		}).collect(Collectors.toList());
		queryOperation.setConditions(Elements.of(conditions));
		return query(resultTypeDescriptor, queryOperation);
	}

	default <K, R> PrimaryKeyQuery<K, R> queryInPrimaryKeys(TypeDescriptor resultTypeDescriptor, Class<?> entityClass,
			Elements<? extends K> inPrimaryKeys, Object... primaryKeys) {
		EntityRepository<?> repository = getMapper().getRepository(QueryOperationSymbol.QUERY, entityClass, null);
		QueryOperation queryOperation = new QueryOperation(QueryOperationSymbol.QUERY, repository);
		Elements<? extends Expression> columns = getMapper().getColumns(QueryOperationSymbol.QUERY, repository);
		queryOperation.setColumns(columns);

		Iterator<? extends Property> propertyIterator = repository.getEntityMapping().getPrimaryKeys().iterator();
		List<Entry<Property, Parameter>> conditionEntries = getMapper().combineEntries(propertyIterator,
				Arrays.asList(primaryKeys).iterator());
		List<Condition> conditions = new ArrayList<>(conditionEntries.size() + 1);
		for (Entry<Property, Parameter> entry : conditionEntries) {
			Parameter parameter = entry.getValue();
			conditions.add(new Condition(parameter.getName(), ConditionSymbol.EQU, parameter.getSource(),
					parameter.getTypeDescriptor()));
		}

		if (!propertyIterator.hasNext()) {
			throw new OrmException("The number of primary key parameters must be less than the primary key");
		}

		Property property = propertyIterator.next();
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
		return update(updateOperationSymbol, repository, columns, conditions);
	}

	default <T> long updateByPrimaryKeys(Class<? extends T> entityClass, T entity) {
		EntityRepository<T> repository = getMapper().getRepository(UpdateOperationSymbol.UPDATE, entityClass, entity);
		List<Entry<Property, Parameter>> columnEntries = getMapper().getEntries(entity,
				repository.getEntityMapping().getNotPrimaryKeys().iterator());
		Elements<? extends Expression> columns = getMapper().toColumns(UpdateOperationSymbol.UPDATE, repository,
				Elements.of(columnEntries));
		List<Entry<Property, Parameter>> conditionEntries = getMapper().getEntries(entity,
				repository.getEntityMapping().getPrimaryKeys().iterator());
		List<Condition> conditions = conditionEntries.stream().map((e) -> e.getValue()).map((parameter) -> {
			return new Condition(parameter.getName(), ConditionSymbol.EQU, parameter.getSource(),
					parameter.getTypeDescriptor());
		}).collect(Collectors.toList());
		return update(repository, columns, Elements.of(conditions));
	}

	default long updateByEntityPrimaryKeys(Object entity) {
		Assert.requiredArgument(entity != null, "entity");
		return updateByPrimaryKeys(entity.getClass(), entity);
	}
}
