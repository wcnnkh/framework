package io.basc.framework.orm;

import java.util.OptionalLong;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.data.domain.Query;
import io.basc.framework.data.repository.DeleteOperation;
import io.basc.framework.data.repository.DeleteOperationSymbol;
import io.basc.framework.data.repository.InsertOperation;
import io.basc.framework.data.repository.InsertOperationSymbol;
import io.basc.framework.data.repository.RepositoryOperations;
import io.basc.framework.data.repository.SelectOperation;
import io.basc.framework.data.repository.SelectOperationSymbol;
import io.basc.framework.data.repository.UpdateOperation;
import io.basc.framework.data.repository.UpdateOperationSymbol;
import io.basc.framework.orm.support.DefaultObjectKeyFormat;
import io.basc.framework.util.Elements;
import io.basc.framework.value.Value;

public interface EntityOperations extends RepositoryOperations {
	EntityMapper getMapper();

	default <T> OptionalLong insert(Class<T> entityClass, T entity) throws OrmException {
		return batchInsert(entityClass, Elements.singleton(entity)).first();
	}

	default <T> Elements<OptionalLong> batchInsert(Class<? extends T> entityClass, Elements<? extends T> entitys) {
		TypeDescriptor entityTypeDescriptor = TypeDescriptor.valueOf(entityClass);
		EntityMapping<? extends Property> entityMapping = getMapper().getMapping(entityClass);
		Elements<InsertOperation> operations = entitys.map((e) -> getMapper()
				.getInsertOperation(InsertOperationSymbol.INSERT, entityMapping, Value.of(e, entityTypeDescriptor)));
		return batchInsert(operations);
	}

	default <T> OptionalLong insertIfAbsent(Class<T> entityClass, T entity) {
		return batchInsertIfAbsent(entityClass, Elements.singleton(entity)).first();
	}

	default <T> Elements<OptionalLong> batchInsertIfAbsent(Class<T> entityClass, Elements<? extends T> entitys) {
		TypeDescriptor entityTypeDescriptor = TypeDescriptor.valueOf(entityClass);
		EntityMapping<? extends Property> entityMapping = getMapper().getMapping(entityClass);
		Elements<InsertOperation> operations = entitys
				.map((e) -> getMapper().getInsertOperation(InsertOperationSymbol.INSERT_IF_ABSENT, entityMapping,
						Value.of(e, entityTypeDescriptor)));
		return batchInsertIfAbsent(operations);
	}

	default <T> OptionalLong insertOrUpdate(Class<T> entityClass, T entity) {
		return batchInsertOrUpdate(entityClass, Elements.singleton(entity)).first();
	}

	default <T> Elements<OptionalLong> batchInsertOrUpdate(Class<? extends T> entityClass,
			Elements<? extends T> entitys) {
		TypeDescriptor entityTypeDescriptor = TypeDescriptor.valueOf(entityClass);
		EntityMapping<? extends Property> entityMapping = getMapper().getMapping(entityClass);
		Elements<InsertOperation> operations = entitys
				.map((e) -> getMapper().getInsertOperation(InsertOperationSymbol.INSERT_OR_UPDATE, entityMapping,
						Value.of(e, entityTypeDescriptor)));
		return batchInsertOrUpdate(operations);
	}

	default <T> OptionalLong delete(Class<T> entityClass, T entity) {
		return batchDelete(entityClass, Elements.singleton(entity)).first();
	}

	default <T> Elements<OptionalLong> batchDelete(Class<T> entityClass, Elements<? extends T> entitys) {
		TypeDescriptor entityTypeDescriptor = TypeDescriptor.valueOf(entityClass);
		EntityMapping<? extends Property> entityMapping = getMapper().getMapping(entityClass);
		Elements<DeleteOperation> operations = entitys.map((e) -> getMapper()
				.getDeleteOperation(DeleteOperationSymbol.DELETE, Value.of(e, entityTypeDescriptor), entityMapping));
		return batchDelete(operations);
	}

	default <T> OptionalLong deleteByPrimaryKeys(Class<T> entityClass, T entity) throws OrmException {
		EntityMapping<? extends Property> entityMapping = getMapper().getMapping(entityClass);
		DeleteOperation deleteOperation = getMapper().getDeleteOperationByPrimaryKeys(DeleteOperationSymbol.DELETE,
				Value.of(entity, TypeDescriptor.valueOf(entityClass)), entityMapping);
		return delete(deleteOperation);
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

	default <T> OptionalLong update(Class<T> entityClass, T oldEntity, T newEntity) {
		TypeDescriptor entityTypeDescriptor = TypeDescriptor.valueOf(entityClass);
		EntityMapping<? extends Property> entityMapping = getMapper().getMapping(entityClass);
		UpdateOperation updateOperation = getMapper().getUpdateOperation(UpdateOperationSymbol.UPDATE,
				Value.of(oldEntity, entityTypeDescriptor), entityMapping, Value.of(newEntity, entityTypeDescriptor),
				entityMapping);
		return update(updateOperation);
	}

	default <T> OptionalLong updateByPrimaryKeys(Class<T> entityClass, T entity) {
		return batchUpdateByPrimaryKeys(entityClass, Elements.singleton(entity)).first();
	}

	default <T> Elements<OptionalLong> batchUpdateByPrimaryKeys(Class<T> entityClass, Elements<? extends T> entitys) {
		TypeDescriptor entityTypeDescriptor = TypeDescriptor.valueOf(entityClass);
		EntityMapping<? extends Property> entityMapping = getMapper().getMapping(entityClass);
		Elements<UpdateOperation> operations = entitys
				.map((e) -> getMapper().getUpdateOperationByPrimaryKeys(UpdateOperationSymbol.UPDATE,
						Value.of(e, entityTypeDescriptor), entityMapping));
		return batchUpdate(operations);
	}

	default <T, R> Query<R> select(TypeDescriptor resultTypeDescriptor, Class<? extends T> entityClass, T entity) {
		TypeDescriptor entityTypeDescriptor = new TypeDescriptor(ResolvableType.forClass(entityClass), entityClass,
				resultTypeDescriptor);
		EntityMapping<? extends Property> entityMapping = getMapper().getMapping(entityClass);
		SelectOperation selectOperation = getMapper().getSelectOperation(SelectOperationSymbol.SELECT,
				Value.of(entity, entityTypeDescriptor), entityMapping);
		return select(resultTypeDescriptor, selectOperation);
	}

	default <R> Query<R> selectAll(TypeDescriptor resultTypeDescriptor, Class<?> entityClass) {
		EntityMapping<? extends Property> entityMapping = getMapper().getMapping(entityClass);
		TypeDescriptor entityTypeDescriptor = new TypeDescriptor(ResolvableType.forClass(entityClass), entityClass,
				resultTypeDescriptor);
		SelectOperation selectOperation = new SelectOperation();
		selectOperation.setRepositorys(
				getMapper().getRepositorys(selectOperation.getOperationSymbol(), entityTypeDescriptor, entityMapping));
		return select(resultTypeDescriptor, selectOperation);
	}

	default <R> Query<R> selectByPrimaryKeys(TypeDescriptor resultTypeDescriptor, Class<?> entityClass,
			Object... primaryKeys) {
		TypeDescriptor entityTypeDescriptor = new TypeDescriptor(ResolvableType.forClass(entityClass), entityClass,
				resultTypeDescriptor);
		EntityMapping<? extends Property> entityMapping = getMapper().getMapping(entityClass);
		SelectOperation selectOperation = getMapper().getSelectOperationByPrimaryKeys(SelectOperationSymbol.SELECT,
				entityMapping, entityTypeDescriptor, Elements.forArray(primaryKeys).map((e) -> Value.of(e)));
		return select(resultTypeDescriptor, selectOperation);
	}

	default <T, R> Query<R> selectByPrimaryKeys(TypeDescriptor resultTypeDescriptor, Class<? extends T> entityClass,
			T entity) {
		TypeDescriptor entityTypeDescriptor = new TypeDescriptor(ResolvableType.forClass(entityClass), entityClass,
				resultTypeDescriptor);
		EntityMapping<? extends Property> entityMapping = getMapper().getMapping(entityClass);
		SelectOperation selectOperation = getMapper().getSelectOperationByPrimaryKeys(SelectOperationSymbol.SELECT,
				Value.of(entity, entityTypeDescriptor), entityMapping);
		return select(resultTypeDescriptor, selectOperation);
	}

	default <K, R> PrimaryKeyQuery<K, R> selectInPrimaryKeys(TypeDescriptor resultTypeDescriptor, Class<?> entityClass,
			Elements<? extends K> inPrimaryKeys, Object... primaryKeys) {
		EntityMapping<? extends Property> entityMapping = getMapper().getMapping(entityClass);
		SelectOperation selectOperation = getMapper().getSelectOperationInPrimaryKeys(SelectOperationSymbol.SELECT,
				entityMapping, resultTypeDescriptor, Elements.forArray(primaryKeys).map((e) -> Value.of(e)),
				inPrimaryKeys.map((e) -> Value.of(e)));
		Query<R> query = select(resultTypeDescriptor, selectOperation);
		ObjectKeyFormat objectKeyFormat = new DefaultObjectKeyFormat();
		return new PrimaryKeyQuery<>(query, entityMapping, objectKeyFormat, inPrimaryKeys, primaryKeys);
	}
}
