package io.basc.framework.orm.repository;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.orm.ObjectRelationalMapping;
import io.basc.framework.orm.OrmException;
import io.basc.framework.orm.Property;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.Pair;
import io.basc.framework.util.XUtils;
import io.basc.framework.util.comparator.Sort;
import io.basc.framework.util.stream.Processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface RepositoryMapping extends ObjectRelationalMapping {
	default ConditionKeywords getConditionKeywords() {
		return ConditionKeywords.DEFAULT;
	}

	default RelationshipKeywords getRelationshipKeywords() {
		return RelationshipKeywords.DEFAULT;
	}

	/**
	 * 将参数依据orm规则展开
	 * 
	 * @param entityClass
	 * @param columns
	 * @param appendableOrders
	 *            追加
	 * @return
	 */
	default List<RepositoryColumn> open(Class<?> entityClass,
			Collection<? extends RepositoryColumn> columns,
			List<? extends OrderColumn> appendableOrders) {
		if (CollectionUtils.isEmpty(columns)) {
			return Collections.emptyList();
		}

		List<RepositoryColumn> list = new ArrayList<RepositoryColumn>();
		for (RepositoryColumn column : columns) {
			if (column.getValue() == null
					|| !isEntity(column.getType(), column)) {
				list.add(column);
				continue;
			}

			// 如果是entity将对象内容展开
			getStructure(column.getType())
					.columns()
					.forEach(
							(c) -> {
								Object value = c.getField().getGetter()
										.get(column.getValue());
								RepositoryColumn repositoryColumn = new RepositoryColumn(
										c.getName(), value, new TypeDescriptor(
												c.getField().getGetter()));
								list.add(repositoryColumn);
							});

		}
		return list;
	}

	/**
	 * 将参数依据orm规则展开
	 * 
	 * @param entityClass
	 * @param mapping
	 * @param conditions
	 * @param appendableOrders
	 *            追加
	 * @return
	 */
	default Conditions open(Class<?> entityClass, Conditions conditions,
			List<OrderColumn> appendableOrders) {
		if (conditions == null) {
			return null;
		}

		Condition condition = conditions.getCondition();
		List<WithCondition> withConditions = new ArrayList<WithCondition>(
				conditions.getWiths());
		if (isEntity(entityClass, condition.getColumn())) {
			// 如果是entity将对象内容展开
			Iterator<? extends Property> iterator = getStructure(
					condition.getColumn().getType()).columns().iterator();
			while (iterator.hasNext()) {
				Property property = iterator.next();
				Object value = property.getField().getGetter()
						.get(condition.getColumn().getValue());
				RepositoryColumn repositoryColumn = new RepositoryColumn(
						property.getName(), value, new TypeDescriptor(property
								.getField().getGetter()));
				Condition newCondition = new Condition(
						condition.getCondition(), repositoryColumn);
				withConditions.add(new WithCondition("and", new Conditions(
						newCondition, null)));
			}
		}

		withConditions = withConditions
				.stream()
				.map((e) -> new WithCondition(e.getWith(), open(entityClass,
						e.getCondition(), appendableOrders)))
				.collect(Collectors.toList());
		return new Conditions(condition, withConditions);
	}

	default RepositoryColumn parseColumn(Class<?> entityClass,
			Property property, @Nullable Object value) {
		TypeDescriptor valueTypeDescriptor = new TypeDescriptor(property
				.getField().getGetter());
		if (value != null
				&& !ClassUtils.isAssignableValue(valueTypeDescriptor.getType(),
						value)) {
			valueTypeDescriptor = valueTypeDescriptor.narrow(value);
		}
		return new RepositoryColumn(property.getName(), value,
				valueTypeDescriptor);
	}

	default void resolveOrders(Class<?> entityClass,
			ParameterDescriptor descriptor, List<OrderColumn> appendable) {
		if (appendable == null) {
			return;
		}

		Sort sort = getSort(entityClass, descriptor);
		if (sort == null) {
			return;
		}

		appendable.add(new OrderColumn(descriptor.getName(), sort, null));
	}

	default <T, P extends Property> Stream<RepositoryColumn> parseColumns(
			Class<?> entityClass, Iterator<? extends P> properties,
			@Nullable List<OrderColumn> orders,
			Processor<P, Object, OrmException> valueProcessor,
			@Nullable Predicate<Pair<P, Object>> predicate) throws OrmException {
		return XUtils.stream(properties).filter((e) -> {
			resolveOrders(entityClass, e.getField().getGetter(), orders);
			return true;
		}).map((e) -> new Pair<P, Object>(e, valueProcessor.process(e)))
				.filter((e) -> predicate == null || predicate.test(e))
				.map((e) -> parseColumn(entityClass, e.getKey(), e.getValue()));
	}

	default Conditions parseConditions(Class<?> entityClass,
			Iterator<? extends RepositoryColumn> iterator) {
		RelationshipKeywords relationshipKeywords = getRelationshipKeywords();
		ConditionKeywords conditionKeywords = getConditionKeywords();
		return Conditions.build(XUtils
				.stream(iterator)
				.map((column) -> new Pair<String, Condition>(
						relationshipKeywords.getKey(column), new Condition(
								conditionKeywords.getKey(column), column)))
				.iterator());
	}

	default <T, P extends Property> Conditions parseConditions(
			Class<?> entityClass, Iterator<? extends P> properties,
			@Nullable List<OrderColumn> orders,
			Processor<P, Object, OrmException> valueProcessor,
			@Nullable Predicate<Pair<P, Object>> predicate) throws OrmException {
		Stream<RepositoryColumn> stream = parseColumns(entityClass, properties,
				orders, valueProcessor, predicate);
		try {
			return parseConditions(entityClass, stream.iterator());
		} finally {
			stream.close();
		}
	}
}
