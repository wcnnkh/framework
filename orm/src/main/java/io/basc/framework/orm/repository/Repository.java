package io.basc.framework.orm.repository;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.domain.PageRequest;
import io.basc.framework.lang.Nullable;
import io.basc.framework.orm.EntityOperations;
import io.basc.framework.orm.EntityStructure;
import io.basc.framework.orm.OrmException;
import io.basc.framework.orm.Property;
import io.basc.framework.util.Pair;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.page.Paginations;
import io.basc.framework.util.stream.Cursor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 存储库
 * 
 * @author wcnnkh
 *
 */
public interface Repository extends EntityOperations {
	default <T> boolean delete(Class<? extends T> entityClass, T entity) throws OrmException {
		Conditions conditionsToUse = getMapper().parseConditions(entityClass,
				getMapper().getStructure(entityClass).getPrimaryKeys().iterator(), null,
				(e) -> e.getField().get(entity), null);
		return delete(entityClass, conditionsToUse) > 0;
	}

	long delete(Class<?> entityClass, @Nullable Conditions conditions) throws OrmException;

	default <T> long deleteAll(Class<? extends T> entityClass, T conditions) {
		Conditions conditionsToUse = getMapper().parseConditions(entityClass,
				getMapper().getStructure(entityClass).columns().iterator(), null, (e) -> e.getField().get(conditions),
				(e) -> !StringUtils.isEmpty(e));
		return delete(entityClass, conditionsToUse);
	}

	default long deleteAll(Class<?> entityClass) {
		return delete(entityClass, null);
	}

	@Override
	default boolean deleteById(Class<?> entityClass, Object... entityIds) throws OrmException {
		EntityStructure<? extends Property> entityStructure = getMapper().getStructure(entityClass);
		List<? extends Property> list = entityStructure.getPrimaryKeys();
		if (list.size() != entityIds.length) {
			throw new OrmException("Inconsistent number of primary keys");
		}

		AtomicInteger index = new AtomicInteger();
		Conditions conditionsToUse = getMapper().parseConditions(entityClass, list.iterator(), null,
				(e) -> entityIds[index.getAndIncrement()], null);
		return delete(entityClass, conditionsToUse) > 0;
	}

	@Override
	default <T> T getById(Class<? extends T> entityClass, Object... ids) {
		return getById(TypeDescriptor.valueOf(entityClass), entityClass, ids);
	}

	@SuppressWarnings("unchecked")
	default <T> T getById(TypeDescriptor resultsTypeDescriptor, Class<?> entityClass, Object... entityIds)
			throws OrmException {
		EntityStructure<? extends Property> structure = getMapper().getStructure(entityClass);
		List<? extends Property> list = structure.getPrimaryKeys();
		if (list.size() != entityIds.length) {
			throw new OrmException("Inconsistent number of primary keys");
		}

		AtomicInteger index = new AtomicInteger();
		Conditions conditions = getMapper().parseConditions(entityClass, list.iterator(), null,
				(e) -> entityIds[index.getAndIncrement()], null);
		return (T) query(resultsTypeDescriptor, entityClass, conditions, null).first();
	}

	@SuppressWarnings("unchecked")
	default <T> List<T> getInIds(TypeDescriptor resultsTypeDescriptor, Class<?> entityClass, List<?> entityInIds,
			Object... entityIds) throws OrmException {
		EntityStructure<? extends Property> entityStructure = getMapper().getStructure(entityClass);
		List<? extends Property> list = entityStructure.getPrimaryKeys();
		RelationshipKeywords relationshipKeywords = getMapper().getRelationshipKeywords();
		ConditionKeywords conditionKeywords = getMapper().getConditionKeywords();
		Iterator<? extends Property> iterator = list.iterator();
		List<Pair<String, Condition>> pairs = new ArrayList<Pair<String, Condition>>();
		int i = 0;
		while (iterator.hasNext()) {
			Property property = iterator.next();
			Object value = entityIds[i++];
			RepositoryColumn column = getMapper().parseColumn(entityClass, property, value);
			Condition condition = new Condition(conditionKeywords.getEqualKeywords().getFirst(), column);
			pairs.add(new Pair<String, Condition>(relationshipKeywords.getAndKeywords().getFirst(), condition));
		}

		Property property = iterator.next();
		RepositoryColumn column = getMapper().parseColumn(entityClass, property, entityInIds);
		pairs.add(new Pair<String, Condition>(relationshipKeywords.getAndKeywords().getFirst(),
				new Condition(conditionKeywords.getInKeywords().getFirst(), column)));
		Conditions conditions = Conditions.build(pairs);
		return (List<T>) query(resultsTypeDescriptor, entityClass, conditions, null).collect(Collectors.toList());
	}

	default RepositoryMapper getMapper() {
		return DefaultRepositoryMapper.DEFAULT;
	}

	@Override
	default <T> boolean isPresent(Class<? extends T> entityClass, T conditions) {
		return query(TypeDescriptor.valueOf(entityClass), entityClass,
				getMapper().parseConditions(entityClass,
						getMapper().getStructure(entityClass).getPrimaryKeys().iterator(), null,
						(e) -> e.getField().get(conditions), null),
				null, new PageRequest(1, 1)).findAny().isPresent();
	}

	@Override
	default boolean isPresent(Class<?> entityClass, Object... ids) {
		AtomicInteger index = new AtomicInteger();
		return query(TypeDescriptor.valueOf(entityClass), entityClass,
				getMapper().parseConditions(entityClass,
						getMapper().getStructure(entityClass).getPrimaryKeys().iterator(), null,
						(e) -> ids[index.getAndIncrement()], null),
				null, new PageRequest(1, 1)).findAny().isPresent();
	}

	default <T> boolean isPresentAny(Class<? extends T> entityClass, T conditions) {
		return query(TypeDescriptor.valueOf(entityClass), entityClass,
				getMapper().parseConditions(entityClass, getMapper().getStructure(entityClass).columns().iterator(),
						null, (e) -> e.getField().get(conditions), (e) -> StringUtils.isNotEmpty(e.getValue())),
				null, new PageRequest(1, 1)).findAny().isPresent();
	}

	default <T, E> Paginations<T> pagingQuery(TypeDescriptor resultsTypeDescriptor, Class<? extends E> entityClass,
			E conditions, PageRequest request) throws OrmException {
		List<OrderColumn> orderColumns = new ArrayList<OrderColumn>(8);
		Conditions conditionsToUse = getMapper().parseConditions(entityClass,
				getMapper().getStructure(entityClass).columns().iterator(), orderColumns,
				(e) -> e.getField().get(conditions), (e) -> StringUtils.isNotEmpty(e.getValue()));
		return pagingQuery(resultsTypeDescriptor, entityClass, conditionsToUse, orderColumns, request);
	}

	<T> Paginations<T> pagingQuery(TypeDescriptor resultsTypeDescriptor, Class<?> entityClass,
			@Nullable Conditions conditions, List<? extends OrderColumn> orders, @Nullable PageRequest pageRequest)
			throws OrmException;

	default <T, E> Cursor<T> query(TypeDescriptor resultsTypeDescriptor, Class<? extends E> entityClass, E conditions,
			PageRequest request) throws OrmException {
		List<OrderColumn> orderColumns = new ArrayList<OrderColumn>(8);
		Conditions conditionsToUse = getMapper().parseConditions(entityClass,
				getMapper().getStructure(entityClass).columns().iterator(), orderColumns,
				(e) -> e.getField().get(conditions), (e) -> StringUtils.isNotEmpty(e.getValue()));
		return query(resultsTypeDescriptor, entityClass, conditionsToUse, orderColumns, request);
	}

	<T> Cursor<T> query(TypeDescriptor resultsTypeDescriptor, Class<?> entityClass, @Nullable Conditions conditions,
			List<? extends OrderColumn> orders, PageRequest pageRequest) throws OrmException;

	default <T, E> Cursor<T> queryAll(TypeDescriptor resultsTypeDescriptor, Class<? extends E> entityClass,
			E conditions) throws OrmException {
		List<OrderColumn> orderColumns = new ArrayList<OrderColumn>(8);
		return queryAll(resultsTypeDescriptor, entityClass,
				getMapper().parseConditions(entityClass, getMapper().getStructure(entityClass).columns().iterator(),
						orderColumns, (e) -> e.getField().get(conditions), (e) -> StringUtils.isNotEmpty(e.getValue())),
				orderColumns);
	}

	default <T> Cursor<T> queryAll(TypeDescriptor resultsTypeDescriptor, Class<?> entityClass) throws OrmException {
		return queryAll(resultsTypeDescriptor, entityClass, null, null);
	}

	<T> Cursor<T> queryAll(TypeDescriptor resultsTypeDescriptor, Class<?> entityClass, @Nullable Conditions conditions,
			List<? extends OrderColumn> orders) throws OrmException;

	@SuppressWarnings("unchecked")
	default <T, E> List<T> queryList(TypeDescriptor resultsTypeDescriptor, Class<? extends E> entityClass, E conditions)
			throws OrmException {
		return (List<T>) queryAll(resultsTypeDescriptor, entityClass, conditions).collect(Collectors.toList());
	}

	default <T> void save(Class<? extends T> entityClass, T entity) throws OrmException {
		List<RepositoryColumn> columns = getMapper().parseColumns(entityClass,
				getMapper().getStructure(entityClass).columns().iterator(), null, (e) -> e.getField().get(entity), null)
				.collect(Collectors.toList());
		save(entityClass, columns);
	}

	long save(Class<?> entityClass, Collection<? extends RepositoryColumn> columns) throws OrmException;

	@Override
	default <T> boolean update(Class<? extends T> entityClass, T entity) throws OrmException {
		EntityStructure<? extends Property> entityStructure = getMapper().getStructure(entityClass);
		List<RepositoryColumn> columns = getMapper().parseColumns(entityClass, entityStructure.columns().iterator(),
				null, (e) -> e.getField().get(entity), (e) -> StringUtils.isNotEmpty(e.getValue())).collect(Collectors.toList());
		Conditions conditionsToUse = getMapper().parseConditions(entityClass,
				entityStructure.getPrimaryKeys().iterator(), null, (e) -> e.getField().get(entity), null);
		return update(entityClass, columns, conditionsToUse) > 0;
	}

	long update(Class<?> entityClass, Collection<? extends RepositoryColumn> columns, Conditions conditions)
			throws OrmException;

	default <T> long updateAll(Class<? extends T> entityClass, T entity, T conditions) {
		EntityStructure<? extends Property> entityStructure = getMapper().getStructure(entityClass);
		List<RepositoryColumn> columns = getMapper().parseColumns(entityClass, entityStructure.columns().iterator(),
				null, (e) -> e.getField().get(entity), (e) -> StringUtils.isNotEmpty(e.getValue())).collect(Collectors.toList());
		Conditions conditionsToUse = getMapper().parseConditions(entityClass, entityStructure.columns().iterator(),
				null, (e) -> e.getField().get(conditions), (e) -> !StringUtils.isNotEmpty(e.getValue()));
		return update(entityClass, columns, conditionsToUse);
	}
}
