package io.basc.framework.orm.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.orm.ObjectKeyFormat;
import io.basc.framework.orm.ObjectRelational;
import io.basc.framework.orm.OrmException;
import io.basc.framework.orm.PrimaryKeyElements;
import io.basc.framework.orm.Property;
import io.basc.framework.util.Elements;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.Pair;
import io.basc.framework.util.page.Paginations;

/**
 * 存储库
 * 
 * @author wcnnkh
 *
 */
public interface Repository extends CurdOperations {
	<E> long delete(Class<? extends E> entityClass, @Nullable Conditions conditions) throws OrmException;

	@Override
	default <T> boolean delete(Class<? extends T> entityClass, T entity) throws OrmException {
		Conditions conditionsToUse = getMapper().parseConditions(entityClass,
				getMapper().getStructure(entityClass).getPrimaryKeys(), null, (e) -> e.get(entity), null);
		return delete(entityClass, conditionsToUse) > 0;
	}

	@Override
	default <E> long deleteAll(Class<? extends E> entityClass) {
		return delete(entityClass, null);
	}

	@Override
	default <T> long deleteAll(Class<? extends T> entityClass, T conditions) {
		Conditions conditionsToUse = getMapper().parseConditions(entityClass,
				getMapper().getStructure(entityClass).columns(), null, (e) -> e.get(conditions),
				(e) -> ObjectUtils.isNotEmpty(e));
		return delete(entityClass, conditionsToUse);
	}

	@Override
	default <E> boolean deleteById(Class<? extends E> entityClass, Object... entityIds) throws OrmException {
		ObjectRelational<? extends Property> entityStructure = getMapper().getStructure(entityClass);
		Elements<? extends Property> list = entityStructure.getPrimaryKeys();
		if (list.count() != entityIds.length) {
			throw new OrmException("Inconsistent number of primary keys");
		}

		AtomicInteger index = new AtomicInteger();
		Conditions conditionsToUse = getMapper().parseConditions(entityClass, list, null,
				(e) -> entityIds[index.getAndIncrement()], null);
		return delete(entityClass, conditionsToUse) > 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	public default <T, E> T getById(TypeDescriptor resultsTypeDescriptor, Class<? extends E> entityClass,
			Object... entityIds) throws OrmException {
		ObjectRelational<? extends Property> structure = getMapper().getStructure(entityClass);
		Elements<? extends Property> list = structure.getPrimaryKeys();
		if (list.count() != entityIds.length) {
			throw new OrmException("Inconsistent number of primary keys");
		}

		AtomicInteger index = new AtomicInteger();
		Conditions conditions = getMapper().parseConditions(entityClass, list, null,
				(e) -> entityIds[index.getAndIncrement()], null);
		return (T) query(resultsTypeDescriptor, entityClass, conditions, null).getElements().first();
	}

	@Override
	default <K, T> PrimaryKeyElements<K, T> getInIds(Class<? extends T> entityClass, List<? extends K> inPrimaryKeys,
			Object... primaryKeys) throws OrmException {
		return getInIds(TypeDescriptor.valueOf(entityClass), entityClass, inPrimaryKeys, primaryKeys);
	}

	@SuppressWarnings("unchecked")
	@Override
	default <K, T> PrimaryKeyElements<K, T> getInIds(TypeDescriptor resultsTypeDescriptor, Class<?> entityClass,
			List<? extends K> inPrimaryKeys, Object... primaryKeys) throws OrmException {
		ObjectRelational<? extends Property> entityStructure = getMapper().getStructure(entityClass);
		Elements<? extends Property> list = entityStructure.getPrimaryKeys();
		RelationshipKeywords relationshipKeywords = getMapper().getRelationshipKeywords();
		ConditionKeywords conditionKeywords = getMapper().getConditionKeywords();
		Iterator<? extends Property> iterator = list.iterator();
		List<Pair<String, Condition>> pairs = new ArrayList<Pair<String, Condition>>();
		int i = 0;
		while (iterator.hasNext()) {
			Property property = iterator.next();
			Object value = primaryKeys[i++];
			Parameter column = getMapper().parseParameter(entityClass, property, value);
			Condition condition = new Condition(conditionKeywords.getEqualKeywords().getFirst(), column);
			pairs.add(new Pair<String, Condition>(relationshipKeywords.getAndKeywords().getFirst(), condition));
		}

		Property property = iterator.next();
		Parameter column = getMapper().parseParameter(entityClass, property, inPrimaryKeys);
		pairs.add(new Pair<String, Condition>(relationshipKeywords.getAndKeywords().getFirst(),
				new Condition(conditionKeywords.getInKeywords().getFirst(), column)));
		Conditions conditions = ConditionsBuilder.build(Elements.of(pairs));
		Elements<T> resultSet = (Elements<T>) query(resultsTypeDescriptor, entityClass, conditions, null);
		return new PrimaryKeyElements<K, T>(resultSet, getObjectKeyFormat(), entityStructure, inPrimaryKeys,
				primaryKeys);
	}

	RepositoryResolver getMapper();

	ObjectKeyFormat getObjectKeyFormat();

	@Override
	default <T> boolean isPresent(Class<? extends T> entityClass, T conditions) {
		return !query(
				TypeDescriptor.valueOf(entityClass), entityClass, getMapper().parseConditions(entityClass,
						getMapper().getStructure(entityClass).getPrimaryKeys(), null, (e) -> e.get(conditions), null),
				null).getElements().isEmpty();
	}

	@Override
	default <E> boolean isPresentById(Class<? extends E> entityClass, Object... ids) {
		AtomicInteger index = new AtomicInteger();
		return !query(TypeDescriptor.valueOf(entityClass), entityClass,
				getMapper().parseConditions(entityClass, getMapper().getStructure(entityClass).getPrimaryKeys(), null,
						(e) -> ids[index.getAndIncrement()], null),
				null).getElements().isEmpty();
	}

	default <T> Paginations<T> query(Class<? extends T> entityClass, @Nullable Conditions conditions,
			List<? extends OrderColumn> orders) throws OrmException {
		return query(TypeDescriptor.valueOf(entityClass), entityClass, conditions, orders);
	}

	<T, E> Paginations<T> query(TypeDescriptor resultsTypeDescriptor, Class<? extends E> entityClass,
			@Nullable Conditions conditions, List<? extends OrderColumn> orders) throws OrmException;

	@Override
	default <T, E> Paginations<T> query(TypeDescriptor resultsTypeDescriptor, Class<? extends E> entityClass,
			E conditions) throws OrmException {
		List<OrderColumn> orderColumns = new ArrayList<OrderColumn>(8);
		return query(resultsTypeDescriptor, entityClass,
				getMapper().parseConditions(entityClass, getMapper().getStructure(entityClass).columns(), orderColumns,
						(e) -> e.get(conditions), (e) -> ObjectUtils.isNotEmpty(e.getValue())),
				orderColumns);
	}

	@Override
	default <T, E> Paginations<T> queryAll(TypeDescriptor resultsTypeDescriptor, Class<? extends E> entityClass)
			throws OrmException {
		return query(resultsTypeDescriptor, entityClass, null, null);
	}

	<E> long saveColumns(Class<? extends E> entityClass, Collection<? extends Parameter> columns) throws OrmException;

	@Override
	default <T> void save(Class<? extends T> entityClass, T entity) throws OrmException {
		List<Parameter> columns = getMapper().parseValues(entityClass, entity, getMapper().getStructure(entityClass));
		saveColumns(entityClass, columns);
	}

	<E> long update(Class<? extends E> entityClass, Collection<? extends Parameter> columns, Conditions conditions)
			throws OrmException;

	@Override
	default <T> boolean update(Class<? extends T> entityClass, T entity) throws OrmException {
		ObjectRelational<? extends Property> entityStructure = getMapper().getStructure(entityClass).all();
		List<Parameter> columns = getMapper().parseValues(entityClass, entity, entityStructure);
		Conditions conditionsToUse = getMapper().parseConditions(entityClass, entityStructure.getPrimaryKeys(), null,
				(e) -> e.get(entity), null);
		return update(entityClass, columns, conditionsToUse) > 0;
	}

	@Override
	default <T> long updateAll(Class<? extends T> entityClass, T entity, T conditions) {
		ObjectRelational<? extends Property> entityStructure = getMapper().getStructure(entityClass);
		List<Parameter> columns = getMapper().parseValues(entityClass, entity, entityStructure);
		Conditions conditionsToUse = getMapper().parseConditions(entityClass, entityStructure.columns(), null,
				(e) -> e.get(conditions), (e) -> e.getKey().isNullable() || ObjectUtils.isNotEmpty(e.getValue()));
		return update(entityClass, columns, conditionsToUse);
	}
}
