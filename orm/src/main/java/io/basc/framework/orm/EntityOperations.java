package io.basc.framework.orm;

import java.util.OptionalLong;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.data.domain.Query;
import io.basc.framework.data.repository.DeleteOperation;
import io.basc.framework.data.repository.DeleteOperationSymbol;
import io.basc.framework.data.repository.InsertOperationSymbol;
import io.basc.framework.data.repository.Operation;
import io.basc.framework.data.repository.OperationSymbol;
import io.basc.framework.data.repository.RepositoryOperations;
import io.basc.framework.data.repository.QueryOperation;
import io.basc.framework.data.repository.QueryOperationSymbol;
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
			Elements<Operation> operations = es.map((e) -> getMapper().getOperation(os, e, mapping));
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

	default <T> OptionalLong insertIfAbsent(Class<T> entityClass, T entity) {
		return batchInsertIfAbsent(entityClass, Elements.singleton(entity)).first();
	}

	default <T> Elements<OptionalLong> batchInsertIfAbsent(Class<T> entityClass, Elements<? extends T> entitys) {
		return batchExecute(InsertOperationSymbol.INSERT_IF_ABSENT, entityClass, entitys);
	}

	default <T> OptionalLong insertOrUpdate(Class<T> entityClass, T entity) {
		return batchInsertOrUpdate(entityClass, Elements.singleton(entity)).first();
	}

	default <T> Elements<OptionalLong> batchInsertOrUpdate(Class<T> entityClass, Elements<? extends T> entitys) {
		return batchExecute(InsertOperationSymbol.INSERT_OR_UPDATE, entityClass, entitys);
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
		DeleteOperation deleteOperation = new DeleteOperation();
		deleteOperation.setRepositorys(getMapper().getRepositorys(deleteOperation.getOperationSymbol(),
				TypeDescriptor.valueOf(entityClass), entityMapping));
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
		return query(selectOperation, resultTypeDescriptor);
	}

	default <R> Query<R> selectAll(TypeDescriptor resultTypeDescriptor, Class<?> entityClass) {
		EntityMapping<? extends Property> entityMapping = getMapper().getMapping(entityClass);
		TypeDescriptor entityTypeDescriptor = new TypeDescriptor(ResolvableType.forClass(entityClass), entityClass,
				resultTypeDescriptor);
		QueryOperation selectOperation = new QueryOperation();
		selectOperation.setRepositorys(
				getMapper().getRepositorys(selectOperation.getOperationSymbol(), entityTypeDescriptor, entityMapping));
		return query(selectOperation, resultTypeDescriptor);
	}

	default <R> Query<R> selectByPrimaryKeys(TypeDescriptor resultTypeDescriptor, Class<?> entityClass,
			Object... primaryKeys) {
		TypeDescriptor entityTypeDescriptor = new TypeDescriptor(ResolvableType.forClass(entityClass), entityClass,
				resultTypeDescriptor);
		EntityMapping<? extends Property> entityMapping = getMapper().getMapping(entityClass);
		QueryOperation selectOperation = getMapper().getSelectOperationByPrimaryKeys(QueryOperationSymbol.QUERY,
				entityMapping, entityTypeDescriptor, Elements.forArray(primaryKeys).map((e) -> Value.of(e)));
		return query(selectOperation, resultTypeDescriptor);
	}

	default <T, R> Query<R> selectByPrimaryKeys(TypeDescriptor resultTypeDescriptor, Class<? extends T> entityClass,
			T entity) {
		TypeDescriptor entityTypeDescriptor = new TypeDescriptor(ResolvableType.forClass(entityClass), entityClass,
				resultTypeDescriptor);
		EntityMapping<? extends Property> entityMapping = getMapper().getMapping(entityClass);
		QueryOperation selectOperation = getMapper().getSelectOperationByPrimaryKeys(QueryOperationSymbol.QUERY,
				Value.of(entity, entityTypeDescriptor), entityMapping);
		return query(selectOperation, resultTypeDescriptor);
	}

	default <K, R> PrimaryKeyQuery<K, R> selectInPrimaryKeys(TypeDescriptor resultTypeDescriptor, Class<?> entityClass,
			Elements<? extends K> inPrimaryKeys, Object... primaryKeys) {
		EntityMapping<? extends Property> entityMapping = getMapper().getMapping(entityClass);
		QueryOperation selectOperation = getMapper().getSelectOperationInPrimaryKeys(QueryOperationSymbol.QUERY,
				entityMapping, resultTypeDescriptor, Elements.forArray(primaryKeys).map((e) -> Value.of(e)),
				inPrimaryKeys.map((e) -> Value.of(e)));
		Query<R> query = query(selectOperation, resultTypeDescriptor);
		ObjectKeyFormat objectKeyFormat = new DefaultObjectKeyFormat();
		return new PrimaryKeyQuery<>(query, entityMapping, objectKeyFormat, inPrimaryKeys, primaryKeys);
	}
}
