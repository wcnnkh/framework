package io.basc.framework.orm.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.MapperUtils;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.mapper.Structure;
import io.basc.framework.orm.ObjectRelationalMapper;
import io.basc.framework.orm.OrmException;
import io.basc.framework.orm.Property;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.Pair;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.XUtils;
import io.basc.framework.util.comparator.Sort;
import io.basc.framework.util.stream.Processor;

public interface RepositoryMapper extends ObjectRelationalMapper {
	default ConditionKeywords getConditionKeywords() {
		return ConditionKeywords.DEFAULT;
	}

	default RelationshipKeywords getRelationshipKeywords() {
		return RelationshipKeywords.DEFAULT;
	}

	default ConditionsBuilder conditionsBuilder() {
		return new ConditionsBuilder(getRelationshipKeywords(), getConditionKeywords());
	}

	default ConditionsBuilder conditionsBuilder(Condition condition) {
		return new ConditionsBuilder(getRelationshipKeywords(), getConditionKeywords(), condition);
	}

	default <E extends Throwable> ConditionsBuilder conditionsBuilder(
			Processor<ConditionBuilder, Condition, E> conditionBuilder) throws E {
		return new ConditionsBuilder(getRelationshipKeywords(), getConditionKeywords(), conditionBuilder);
	}

	/**
	 * 将参数依据orm规则展开
	 * 
	 * @param entityClass
	 * @param columns
	 * @param appendableOrders 追加
	 * @return
	 */
	default List<Parameter> open(Class<?> entityClass, Collection<? extends Parameter> columns,
			List<OrderColumn> appendableOrders) {
		if (CollectionUtils.isEmpty(columns)) {
			return Collections.emptyList();
		}

		List<Parameter> list = new ArrayList<Parameter>();
		for (Parameter column : columns) {
			if (column.getValue() == null || !isEntity(column.getType(), column)) {
				list.add(column);
				continue;
			}

			// 如果是entity将对象内容展开
			getStructure(column.getType()).columns().filter((e) -> {
				resolveOrders(column.getType(), e.getGetter(), appendableOrders);
				return true;
			}).filter((e) -> !e.isAutoIncrement() || MapperUtils.isExistValue(e, column.getValue())).forEach((c) -> {
				Parameter repositoryColumn = c.getParameter(column.getValue());
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
	 * @param appendableOrders 追加
	 * @return
	 */
	default Conditions open(Class<?> entityClass, Conditions conditions, List<OrderColumn> appendableOrders) {
		if (conditions == null) {
			return null;
		}

		Condition condition = conditions.getCondition();
		List<WithCondition> withConditions = new ArrayList<WithCondition>(conditions.getWiths());
		if (isEntity(entityClass, condition.getParameter())) {
			// 如果是entity将对象内容展开
			Iterator<? extends Property> iterator = getStructure(condition.getParameter().getType()).columns()
					.iterator();
			while (iterator.hasNext()) {
				Property property = iterator.next();
				resolveOrders(entityClass, property.getGetter(), appendableOrders);
				Parameter repositoryColumn = property.getParameter(condition.getParameter().get());
				Condition newCondition = new Condition(condition.getCondition(), repositoryColumn);
				withConditions.add(new WithCondition("and", new Conditions(newCondition, null)));
			}
		}

		withConditions = withConditions.stream()
				.map((e) -> new WithCondition(e.getWith(), open(entityClass, e.getCondition(), appendableOrders)))
				.collect(Collectors.toList());
		return new Conditions(condition, withConditions);
	}

	default Parameter parseParameter(Class<?> entityClass, Property property, @Nullable Object value) {
		TypeDescriptor valueTypeDescriptor = new TypeDescriptor(property.getGetter());
		if (value != null && !ClassUtils.isAssignableValue(valueTypeDescriptor.getType(), value)) {
			valueTypeDescriptor = valueTypeDescriptor.narrow(value);
		}
		return new Parameter(property.getName(), value, valueTypeDescriptor);
	}

	default void resolveOrders(Class<?> entityClass, ParameterDescriptor descriptor, List<OrderColumn> appendable) {
		if (appendable == null) {
			return;
		}

		Sort sort = getSort(entityClass, descriptor);
		if (sort == null) {
			return;
		}

		appendable.add(new OrderColumn(descriptor.getName(), sort, null));
	}

	default <T, P extends Property> Stream<Parameter> parseParameters(Class<?> entityClass,
			Iterator<? extends P> properties, @Nullable List<OrderColumn> orders,
			Processor<P, Object, OrmException> valueProcessor, @Nullable Predicate<Pair<P, Object>> predicate)
			throws OrmException {
		return XUtils.stream(properties).filter((e) -> {
			resolveOrders(entityClass, e.getGetter(), orders);
			return true;
		}).map((e) -> new Pair<P, Object>(e, valueProcessor.process(e)))
				.filter((e) -> predicate == null || predicate.test(e))
				.map((e) -> parseParameter(entityClass, e.getKey(), e.getValue()));
	}

	default Conditions parseConditions(Class<?> entityClass, Iterator<? extends Parameter> iterator) {
		RelationshipKeywords relationshipKeywords = getRelationshipKeywords();
		ConditionKeywords conditionKeywords = getConditionKeywords();
		return ConditionsBuilder.build(XUtils.stream(iterator).map((column) -> {
			String relationship = getRelationship(entityClass, column);
			if (StringUtils.isEmpty(relationship)) {
				relationship = relationshipKeywords.getAndKeywords().getFirst();
			}

			String condition = getCondition(entityClass, column);
			if (StringUtils.isEmpty(condition)) {
				condition = conditionKeywords.getEqualKeywords().getFirst();
			}
			return new Pair<String, Condition>(relationship, new Condition(condition, column));
		}).iterator());
	}

	default <T, P extends Property> Conditions parseConditions(Class<?> entityClass, Iterator<? extends P> properties,
			@Nullable List<OrderColumn> orders, Processor<P, Object, OrmException> valueProcessor,
			@Nullable Predicate<Pair<P, Object>> predicate) throws OrmException {
		Stream<Parameter> stream = parseParameters(entityClass, properties, orders, valueProcessor, predicate);
		try {
			return parseConditions(entityClass, stream.iterator());
		} finally {
			stream.close();
		}
	}

	default <T> List<Parameter> parseValues(Class<? extends T> entityClass, T entity,
			Structure<? extends Property> structure) {
		return parseParameters(entityClass, structure.stream().filter((e) -> !e.isEntity()).iterator(), null,
				(e) -> e.get(entity), (e) -> e.getKey().isNullable() || StringUtils.isNotEmpty(e.getValue()))
						.collect(Collectors.toList());
	}
}
