package io.basc.framework.orm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalLong;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.data.domain.Query;
import io.basc.framework.data.repository.Condition;
import io.basc.framework.data.repository.ConditionSymbol;
import io.basc.framework.data.repository.DeleteOperation;
import io.basc.framework.data.repository.DeleteOperationSymbol;
import io.basc.framework.data.repository.InsertOperationSymbol;
import io.basc.framework.data.repository.Operation;
import io.basc.framework.data.repository.OperationSymbol;
import io.basc.framework.data.repository.QueryOperation;
import io.basc.framework.data.repository.QueryOperationSymbol;
import io.basc.framework.data.repository.Repository;
import io.basc.framework.data.repository.RepositoryOperations;
import io.basc.framework.data.repository.UpdateOperation;
import io.basc.framework.data.repository.UpdateOperationSymbol;
import io.basc.framework.orm.support.DefaultObjectKeyFormat;
import io.basc.framework.util.Elements;
import io.basc.framework.value.Value;

public interface EntityOperations extends RepositoryOperations {
	EntityMapper getMapper();

	default EntityOperation getEntityOperation(Class<?> entityClass, EntityOperation groundEntityOperation) {
		return groundEntityOperation;
	}

	default <T> OptionalLong execute(OperationSymbol operationSymbol, Class<T> entityClass, T entity)
			throws OrmException {
		return batchExecute(operationSymbol, entityClass, Elements.singleton(entity)).first();
	}

	default <T> Elements<OptionalLong> batchExecute(OperationSymbol operationSymbol, Class<T> entityClass,
			Elements<? extends T> entitys) throws OrmException {
		TypeDescriptor entityTypeDescriptor = TypeDescriptor.valueOf(entityClass);
		EntityMapping<? extends Property> entityMapping = getMapper().getMapping(entityClass);
		Elements<Value> sources = entitys.map((e) -> Value.of(e, entityTypeDescriptor));
		EntityOperation groundEntityOperation = (os, mapping, es) -> {
			Elements<Operation> operations = es.map((e) -> getMapper().getOperation(os, mapping, entityClass, e));
			return batchExecute(operations);
		};
		return groundEntityOperation.execute(operationSymbol, entityMapping, sources);
	}

	default <T> OptionalLong insert(Class<T> entityClass, T entity) throws OrmException {
		return batchInsert(entityClass, Elements.singleton(entity)).first();
	}

	default <T> Elements<OptionalLong> batchInsert(Class<T> entityClass, Elements<? extends T> entitys) {
		return batchExecute(InsertOperationSymbol.INSERT, entityClass, entitys);
	}

	default <T> OptionalLong delete(Class<T> entityClass, T entity) {
		return batchDelete(entityClass, Elements.singleton(entity)).first();
	}

	default <T> Elements<OptionalLong> batchDelete(Class<T> entityClass, Elements<? extends T> entitys) {
		return batchExecute(DeleteOperationSymbol.DELETE, entityClass, entitys);
	}

	default <T> OptionalLong deleteByPrimaryKeys(Class<T> entityClass, T entity) throws OrmException {
		return batchDeleteByPrimaryKeys(entityClass, Elements.singleton(entity)).first();
	}

	default <T> Elements<OptionalLong> batchDeleteByPrimaryKeys(Class<T> entityClass, Elements<? extends T> entitys)
			throws OrmException {
		TypeDescriptor entityTypeDescriptor = TypeDescriptor.valueOf(entityClass);
		EntityMapping<? extends Property> entityMapping = getMapper().getMapping(entityClass);
		EntityOperation groundEntityOperation = (os, mapping, es) -> {
			Elements<DeleteOperation> operations = es.map((e) -> {
				return getMapper().getDeleteOperationByPrimaryKeys(os, e, mapping);
			});
			return batchExecute(operations);
		};
		return groundEntityOperation.execute(DeleteOperationSymbol.DELETE, entityMapping,
				entitys.map((e) -> Value.of(e, entityTypeDescriptor)));
	}

	/**
	 * 根据主键进行删除
	 * 
	 * @param entityClass
	 * @param primaryKey  如果存在多主键就使用多个数据
	 * @return
	 */
	default OptionalLong deleteByPrimaryKeys(Class<?> entityClass, Object... primaryKey) {
		EntityMapping<? extends Property> entityMapping = getMapper().getMapping(entityClass);
		DeleteOperation deleteOperation = getMapper().getDeleteOperationByPrimaryKeys(DeleteOperationSymbol.DELETE,
				TypeDescriptor.valueOf(entityClass), entityMapping,
				Elements.forArray(primaryKey).map((e) -> Value.of(e)));
		return delete(deleteOperation);
	}

	default OptionalLong deleteAll(Class<?> entityClass) {
		EntityMapping<? extends Property> entityMapping = getMapper().getMapping(entityClass);
		DeleteOperation deleteOperation = new DeleteOperation(new Repository(entityMapping.getName()));
		return delete(deleteOperation);
	}

	default <T> Elements<OptionalLong> batchUpdate(Class<T> entityClass, T matchEntity, Elements<? extends T> entitys) {
		TypeDescriptor entityTypeDescriptor = TypeDescriptor.valueOf(entityClass);
		EntityMapping<? extends Property> entityMapping = getMapper().getMapping(entityClass);
		Value matchValue = Value.of(matchEntity, entityTypeDescriptor);
		EntityOperation groundEntityOperation = (os, mapping, es) -> {
			Elements<UpdateOperation> operations = es.map((e) -> {
				return getMapper().getUpdateOperation(os, matchValue, entityMapping, e, entityMapping);
			});
			return batchExecute(operations);
		};
		return groundEntityOperation.execute(UpdateOperationSymbol.UPDATE, entityMapping,
				entitys.map((e) -> Value.of(e, entityTypeDescriptor)));
	}

	default <T> OptionalLong update(Class<T> entityClass, T matchEntity, T newEntity) {
		return batchUpdate(entityClass, matchEntity, Elements.singleton(newEntity)).first();
	}

	default <T> OptionalLong updateByPrimaryKeys(Class<T> entityClass, T entity) {
		return batchUpdateByPrimaryKeys(entityClass, Elements.singleton(entity)).first();
	}

	default <T> Elements<OptionalLong> batchUpdateByPrimaryKeys(Class<T> entityClass, Elements<? extends T> entitys) {
		TypeDescriptor entityTypeDescriptor = TypeDescriptor.valueOf(entityClass);
		EntityMapping<? extends Property> entityMapping = getMapper().getMapping(entityClass);
		EntityOperation groundEntityOperation = (os, mapping, es) -> {
			Elements<UpdateOperation> operations = es.map((e) -> {
				return getMapper().getUpdateOperationByPrimaryKeys(os, e, mapping);
			});
			return batchExecute(operations);
		};
		return groundEntityOperation.execute(UpdateOperationSymbol.UPDATE, entityMapping,
				entitys.map((e) -> Value.of(e, entityTypeDescriptor)));
	}

	default <T, R> Query<R> query(TypeDescriptor resultTypeDescriptor, Class<? extends T> entityClass, T entity) {
		TypeDescriptor entityTypeDescriptor = new TypeDescriptor(ResolvableType.forClass(entityClass), entityClass,
				resultTypeDescriptor);
		EntityMapping<? extends Property> entityMapping = getMapper().getMapping(entityClass);
		QueryOperation selectOperation = getMapper().getSelectOperation(QueryOperationSymbol.QUERY,
				Value.of(entity, entityTypeDescriptor), entityMapping);
		return query(resultTypeDescriptor, selectOperation);
	}

	default <R> Query<R> selectAll(TypeDescriptor resultTypeDescriptor, Class<?> entityClass) {
		EntityMapping<? extends Property> entityMapping = getMapper().getMapping(entityClass);
		QueryOperation selectOperation = new QueryOperation(new Repository(entityMapping.getName()));
		return query(resultTypeDescriptor, selectOperation);
	}

	default <R> Query<R> selectByPrimaryKeys(TypeDescriptor resultTypeDescriptor, Class<?> entityClass,
			Object... primaryKeys) {
		EntityMapping<? extends Property> entityMapping = getMapper().getMapping(entityClass);
		Repository repository = getMapper().getRepository(entityMapping, null);
		QueryOperation queryOperation = new QueryOperation(repository);
		List<Condition> conditions = new ArrayList<>(primaryKeys.length);
		Iterator<? extends Property> primaryProperties = entityMapping.getPrimaryKeys().iterator();
		Iterator<Object> valueIterator = Arrays.asList(primaryKeys).iterator();
		while (primaryProperties.hasNext() && valueIterator.hasNext()) {
			Property property = primaryProperties.next();
			Object value = valueIterator.next();
			conditions.add(new Condition(property.getName(), ConditionSymbol.EQU, value, null));
		}
		queryOperation.setConditions(Elements.of(conditions));
		return query(resultTypeDescriptor, queryOperation);
	}

	default <T, R> Query<R> selectByPrimaryKeys(TypeDescriptor resultTypeDescriptor, Class<? extends T> entityClass,
			T entity) {
		EntityMapping<? extends Property> entityMapping = getMapper().getMapping(entityClass);
		Repository repository = getMapper().getRepository(entityMapping, entity);
		QueryOperation queryOperation = new QueryOperation(repository);
		List<? extends Property> primeryProperties = entityMapping.getPrimaryKeys().toList();
		List<Condition> conditions = new ArrayList<>(primeryProperties.size());
		for (Property property : primeryProperties) {
			Object value = property.getGetters().first().get(entity);
			conditions.add(new Condition(property.getName(), ConditionSymbol.EQU, value,
					property.getGetters().first().getTypeDescriptor()));
		}
		queryOperation.setConditions(Elements.of(conditions));
		return query(resultTypeDescriptor, queryOperation);
	}

	default <K, R> PrimaryKeyQuery<K, R> selectInPrimaryKeys(TypeDescriptor resultTypeDescriptor, Class<?> entityClass,
			Elements<? extends K> inPrimaryKeys, Object... primaryKeys) {
		EntityMapping<? extends Property> entityMapping = getMapper().getMapping(entityClass);
		Repository repository = getMapper().getRepository(entityMapping, null);
		QueryOperation queryOperation = new QueryOperation(repository);
		List<Condition> conditions = new ArrayList<>(primaryKeys.length);
		Iterator<? extends Property> primaryProperties = entityMapping.getPrimaryKeys().iterator();
		Iterator<Object> valueIterator = Arrays.asList(primaryKeys).iterator();
		while (primaryProperties.hasNext() && valueIterator.hasNext()) {
			Property property = primaryProperties.next();
			Object value = valueIterator.next();
			conditions.add(new Condition(property.getName(), ConditionSymbol.EQU, value, null));
		}

		if (!primaryProperties.hasNext()) {
			// TODO 主键数量不对，无法构造
		}

		Property property = primaryProperties.next();
		conditions.add(new Condition(property.getName(), ConditionSymbol.EQU, primaryKeys, null));
		queryOperation.setConditions(Elements.of(conditions));
		Query<R> query = query(resultTypeDescriptor, queryOperation);
		ObjectKeyFormat objectKeyFormat = new DefaultObjectKeyFormat();
		return new PrimaryKeyQuery<>(query, entityMapping, objectKeyFormat, inPrimaryKeys, primaryKeys);
	}
}
