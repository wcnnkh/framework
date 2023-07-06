package io.basc.framework.orm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalLong;
import java.util.stream.Collectors;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.domain.Query;
import io.basc.framework.data.repository.Condition;
import io.basc.framework.data.repository.ConditionSymbol;
import io.basc.framework.data.repository.DeleteOperation;
import io.basc.framework.data.repository.DeleteOperationSymbol;
import io.basc.framework.data.repository.Expression;
import io.basc.framework.data.repository.InsertOperationSymbol;
import io.basc.framework.data.repository.Operation;
import io.basc.framework.data.repository.OperationSymbol;
import io.basc.framework.data.repository.QueryOperation;
import io.basc.framework.data.repository.QueryOperationSymbol;
import io.basc.framework.data.repository.Repository;
import io.basc.framework.data.repository.RepositoryOperations;
import io.basc.framework.data.repository.Sort;
import io.basc.framework.data.repository.UpdateOperation;
import io.basc.framework.data.repository.UpdateOperationSymbol;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.orm.support.DefaultObjectKeyFormat;
import io.basc.framework.util.Elements;

public interface EntityOperations extends RepositoryOperations {
	EntityMapper getMapper();

	default EntityOperation getEntityOperation(Class<?> entityClass, EntityOperation groundEntityOperation) {
		return groundEntityOperation;
	}

	default <T> OptionalLong execute(OperationSymbol operationSymbol, Class<T> entityClass, T entity)
			throws OrmException {
		return batchExecute(operationSymbol, entityClass, Arrays.asList(entity)).get(0);
	}

	default <T> List<OptionalLong> batchExecute(OperationSymbol operationSymbol, Class<T> entityClass,
			List<? extends T> entitys) throws OrmException {
		EntityOperation groundEntityOperation = (mapper, os, clazz, mapping, es) -> {
			List<Operation> operations = es.stream().map((e) -> getMapper().getOperation(os, clazz, mapping, e))
					.collect(Collectors.toList());
			return batchExecute(operations);
		};
		EntityMapping<? extends Property> entityMapping = getMapper().getMapping(entityClass);
		return groundEntityOperation.execute(getMapper(), operationSymbol, entityClass, entityMapping, entitys);
	}

	default <T> OptionalLong insert(Class<T> entityClass, T entity) throws OrmException {
		return batchInsert(entityClass, Arrays.asList(entity)).get(0);
	}

	default <T> List<OptionalLong> batchInsert(Class<T> entityClass, List<? extends T> entitys) {
		return batchExecute(InsertOperationSymbol.INSERT, entityClass, entitys);
	}

	default <T> OptionalLong delete(Class<T> entityClass, T entity) {
		return batchDelete(entityClass, Arrays.asList(entity)).get(0);
	}

	default <T> List<OptionalLong> batchDelete(Class<T> entityClass, List<? extends T> entitys) {
		return batchExecute(DeleteOperationSymbol.DELETE, entityClass, entitys);
	}

	default <T> OptionalLong deleteByPrimaryKeys(Class<T> entityClass, T entity) throws OrmException {
		return batchDeleteByPrimaryKeys(entityClass, Arrays.asList(entity)).get(0);
	}

	default <T> List<OptionalLong> batchDeleteByPrimaryKeys(Class<T> entityClass, List<? extends T> entitys)
			throws OrmException {
		EntityOperation groundEntityOperation = (mapper, os, clazz, mapping, es) -> {
			List<DeleteOperation> operations = es.stream().map((e) -> {
				Repository repository = getMapper().getRepository(os, clazz, mapping, e);
				DeleteOperation deleteOperation = new DeleteOperation(repository);
				Elements<? extends Parameter> primaryKeys = getMapper().getParameters(e,
						mapping.getPrimaryKeys().iterator());
				Elements<? extends Condition> conditions = primaryKeys.map((parameter) -> {
					return new Condition(parameter.getName(), ConditionSymbol.EQU, parameter.getSource(),
							parameter.getTypeDescriptor());
				});
				deleteOperation.setConditions(conditions);
				return deleteOperation;
			}).collect(Collectors.toList());
			return batchExecute(operations);
		};
		EntityMapping<? extends Property> entityMapping = getMapper().getMapping(entityClass);
		return groundEntityOperation.execute(getMapper(), DeleteOperationSymbol.DELETE, entityClass, entityMapping,
				entitys);
	}

	/**
	 * 根据主键进行删除
	 * 
	 * @param entityClass
	 * @param primaryKey  如果存在多主键就使用多个数据
	 * @return
	 */
	default OptionalLong deleteByPrimaryKeys(Class<?> entityClass, Object... primaryKeys) {
		EntityMapping<? extends Property> entityMapping = getMapper().getMapping(entityClass);
		Repository repository = getMapper().getRepository(DeleteOperationSymbol.DELETE, entityClass, entityMapping,
				null);
		DeleteOperation deleteOperation = new DeleteOperation(repository);
		Elements<? extends Parameter> parameters = getMapper().toParameters(entityMapping.getPrimaryKeys().iterator(),
				Arrays.asList(primaryKeys).iterator());
		Elements<? extends Condition> conditions = parameters.map((parameter) -> {
			return new Condition(parameter.getName(), ConditionSymbol.EQU, parameter.getSource(),
					parameter.getTypeDescriptor());
		});
		deleteOperation.setConditions(conditions);
		return delete(deleteOperation);
	}

	default OptionalLong deleteAll(Class<?> entityClass) {
		EntityMapping<? extends Property> entityMapping = getMapper().getMapping(entityClass);
		Repository repository = getMapper().getRepository(DeleteOperationSymbol.DELETE, entityClass, entityMapping,
				null);
		DeleteOperation deleteOperation = new DeleteOperation(repository);
		return delete(deleteOperation);
	}

	default <T> List<OptionalLong> batchUpdate(Class<T> entityClass, T matchEntity, List<? extends T> entitys) {
		EntityMapping<? extends Property> entityMapping = getMapper().getMapping(entityClass);
		Repository matchRepository = getMapper().getRepository(DeleteOperationSymbol.DELETE, entityClass, entityMapping,
				matchEntity);
		Elements<? extends Parameter> conditionParams = getMapper().getParameters(matchEntity,
				entityMapping.columns().iterator());
		Elements<? extends Condition> conditions = getMapper().toConditions(UpdateOperationSymbol.UPDATE,
				matchRepository, entityClass, entityMapping, conditionParams);
		EntityOperation groundEntityOperation = (mapper, os, clazz, mapping, es) -> {
			List<UpdateOperation> operations = es.stream().map((e) -> {
				Repository repository = mapper.getRepository(os, clazz, mapping, e);
				Elements<? extends Parameter> columnParams = mapper.getParameters(e, mapping.columns().iterator());
				Elements<? extends Expression> columns = mapper.toColumns(os, repository, clazz, mapping, columnParams);
				UpdateOperation updateOperation = new UpdateOperation(repository, columns);
				updateOperation.setConditions(conditions);
				return updateOperation;
			}).collect(Collectors.toList());
			return batchExecute(operations);
		};
		return groundEntityOperation.execute(getMapper(), UpdateOperationSymbol.UPDATE, entityClass, entityMapping,
				entitys);
	}

	default <T> OptionalLong update(Class<T> entityClass, T matchEntity, T newEntity) {
		return batchUpdate(entityClass, matchEntity, Arrays.asList(newEntity)).get(0);
	}

	default <T> OptionalLong updateByPrimaryKeys(Class<T> entityClass, T entity) {
		return batchUpdateByPrimaryKeys(entityClass, Arrays.asList(entity)).get(0);
	}

	default <T> List<OptionalLong> batchUpdateByPrimaryKeys(Class<T> entityClass, List<? extends T> entitys) {
		EntityOperation groundEntityOperation = (mapper, os, clazz, mapping, es) -> {
			List<UpdateOperation> operations = es.stream().map((e) -> {
				Repository repository = getMapper().getRepository(os, clazz, mapping, e);
				Elements<? extends Parameter> columnParams = mapper.getParameters(e,
						mapping.getNotPrimaryKeys().iterator());
				Elements<? extends Expression> columns = mapper.toColumns(os, repository, clazz, mapping, columnParams);
				UpdateOperation updateOperation = new UpdateOperation(repository, columns);
				Elements<? extends Parameter> conditionParams = mapper.getParameters(e,
						mapping.getPrimaryKeys().iterator());
				Elements<? extends Condition> conditions = conditionParams.map((parameter) -> {
					return new Condition(parameter.getName(), ConditionSymbol.EQU, parameter.getSource(),
							parameter.getTypeDescriptor());
				});
				updateOperation.setConditions(conditions);
				return updateOperation;
			}).collect(Collectors.toList());
			return batchExecute(operations);
		};
		EntityMapping<? extends Property> entityMapping = getMapper().getMapping(entityClass);
		return groundEntityOperation.execute(getMapper(), UpdateOperationSymbol.UPDATE, entityClass, entityMapping,
				entitys);
	}

	default <T, R> Query<R> query(TypeDescriptor resultTypeDescriptor, Class<? extends T> entityClass, T entity) {
		EntityMapping<? extends Property> entityMapping = getMapper().getMapping(entityClass);
		Repository repository = getMapper().getRepository(QueryOperationSymbol.QUERY, entityClass, entityMapping,
				entity);
		QueryOperation queryOperation = new QueryOperation(repository);
		Elements<? extends Parameter> params = getMapper().getParameters(entity, entityMapping.columns().iterator());

		// 组装查询列表
		Elements<? extends Expression> columns = getMapper().toColumns(queryOperation.getOperationSymbol(), repository,
				entityClass, entityMapping, params);
		queryOperation.setColumns(columns);

		// 组装查询条件
		Elements<? extends Condition> conditions = getMapper().toConditions(queryOperation.getOperationSymbol(),
				repository, entityClass, entityMapping, params);
		queryOperation.setConditions(conditions);

		// 组装排序条件
		Elements<? extends Sort> sorts = getMapper().toSorts(queryOperation.getOperationSymbol(), repository,
				entityClass, entityMapping, params);
		queryOperation.setSorts(sorts);
		return query(resultTypeDescriptor, queryOperation);
	}

	default <R> Query<R> selectAll(TypeDescriptor resultTypeDescriptor, Class<?> entityClass) {
		EntityMapping<? extends Property> entityMapping = getMapper().getMapping(entityClass);
		QueryOperation selectOperation = new QueryOperation(new Repository(entityMapping.getName()));
		return query(resultTypeDescriptor, selectOperation);
	}

	default <R> Query<R> selectByPrimaryKeys(TypeDescriptor resultTypeDescriptor, Class<?> entityClass,
			Object... primaryKeys) {
		EntityMapping<? extends Property> entityMapping = getMapper().getMapping(entityClass);
		Repository repository = getMapper().getRepository(QueryOperationSymbol.QUERY, entityClass, entityMapping, null);
		QueryOperation queryOperation = new QueryOperation(repository);
		Elements<? extends Parameter> conditionParams = getMapper()
				.toParameters(entityMapping.getPrimaryKeys().iterator(), Arrays.asList(primaryKeys).iterator());
		Elements<? extends Condition> conditions = conditionParams.map((parameter) -> {
			return new Condition(parameter.getName(), ConditionSymbol.EQU, parameter.getSource(),
					parameter.getTypeDescriptor());
		});
		queryOperation.setConditions(conditions);
		return query(resultTypeDescriptor, queryOperation);
	}

	default <T, R> Query<R> selectByPrimaryKeys(TypeDescriptor resultTypeDescriptor, Class<? extends T> entityClass,
			T entity) {
		EntityMapping<? extends Property> entityMapping = getMapper().getMapping(entityClass);
		Repository repository = getMapper().getRepository(QueryOperationSymbol.QUERY, entityClass, entityMapping, null);
		QueryOperation queryOperation = new QueryOperation(repository);
		Elements<? extends Parameter> conditionParams = getMapper().getParameters(entity,
				entityMapping.getPrimaryKeys().iterator());
		Elements<? extends Condition> conditions = conditionParams.map((parameter) -> {
			return new Condition(parameter.getName(), ConditionSymbol.EQU, parameter.getSource(),
					parameter.getTypeDescriptor());
		});
		queryOperation.setConditions(conditions);
		return query(resultTypeDescriptor, queryOperation);
	}

	default <K, R> PrimaryKeyQuery<K, R> selectInPrimaryKeys(TypeDescriptor resultTypeDescriptor, Class<?> entityClass,
			Elements<? extends K> inPrimaryKeys, Object... primaryKeys) {
		EntityMapping<? extends Property> entityMapping = getMapper().getMapping(entityClass);
		Repository repository = getMapper().getRepository(QueryOperationSymbol.QUERY, entityClass, entityMapping, null);
		QueryOperation queryOperation = new QueryOperation(repository);
		List<Condition> conditions = new ArrayList<>(primaryKeys.length);
		Iterator<? extends Property> primaryProperties = entityMapping.getPrimaryKeys().iterator();
		Iterator<Object> valueIterator = Arrays.asList(primaryKeys).iterator();
		Elements<? extends Parameter> conditionParams = getMapper().toParameters(primaryProperties, valueIterator);
		for (Parameter parameter : conditionParams) {
			conditions.add(new Condition(parameter.getName(), ConditionSymbol.EQU, parameter.getSource(),
					parameter.getTypeDescriptor()));
		}

		if (!primaryProperties.hasNext()) {
			throw new OrmException("The number of primary key parameters must be less than the primary key");
		}

		Property property = primaryProperties.next();
		conditions.add(new Condition(property.getName(), ConditionSymbol.EQU, primaryKeys, null));
		queryOperation.setConditions(Elements.of(conditions));
		Query<R> query = query(resultTypeDescriptor, queryOperation);
		ObjectKeyFormat objectKeyFormat = new DefaultObjectKeyFormat();
		return new PrimaryKeyQuery<>(query, entityMapping, objectKeyFormat, inPrimaryKeys, primaryKeys);
	}
}
