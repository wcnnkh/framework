package io.basc.framework.orm.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.repository.Condition;
import io.basc.framework.data.repository.ConditionSymbol;
import io.basc.framework.data.repository.Conditions;
import io.basc.framework.data.repository.RelationshipSymbol;
import io.basc.framework.data.repository.WithCondition;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.Getter;
import io.basc.framework.mapper.Mapping;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.orm.EntityMapping;
import io.basc.framework.orm.OrmException;
import io.basc.framework.orm.Property;
import io.basc.framework.orm.support.DefaultEntityMapper;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Pair;
import io.basc.framework.util.Processor;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.comparator.Sort;

public abstract class DefaultRepositoryMapper<S, E extends Throwable> extends DefaultEntityMapper
		implements RepositoryMapper {

	@Override
	public String getRelationship(Class<?> entityClass, ParameterDescriptor descriptor) {
		String relationship = RepositorySetting.getLocalRelationship().get(descriptor.getName());
		if (StringUtils.isNotEmpty(relationship)) {
			return relationship;
		}
		return super.getRelationship(entityClass, descriptor);
	}

	@Override
	public String getCondition(Class<?> entityClass, ParameterDescriptor descriptor) {
		String condition = RepositorySetting.getLocalConditions().get(descriptor.getName());
		if (StringUtils.isNotEmpty(condition)) {
			return condition;
		}
		return super.getCondition(entityClass, descriptor);
	}

	public List<Parameter> open(Class<?> entityClass, Collection<? extends Parameter> columns,
			List<OrderColumn> appendableOrders) {
		if (CollectionUtils.isEmpty(columns)) {
			return Collections.emptyList();
		}

		List<Parameter> list = new ArrayList<Parameter>();
		for (Parameter column : columns) {
			if (column == null || !column.isPresent()) {
				continue;
			}

			if (!isEntity(column.getTypeDescriptor().getType(), column)) {
				list.add(column);
				continue;
			}

			// 如果是entity将对象内容展开
			EntityMapping<? extends Property> mapping = getMapping(column.getTypeDescriptor().getType());
			mapping.getElements().filter((e) -> {
				resolveOrders(column.getTypeDescriptor().getType(), e, appendableOrders);
				return true;
			}).filter((e) -> !e.isAutoIncrement() || hasEffectiveValue(column, e)).forEach((c) -> {
				Object value = c.getGetters().first().get(column);
				list.add(new Parameter(c.getName(), value, c.getGetters().first().getTypeDescriptor()));
			});

		}
		return list;
	}

	public Conditions open(Class<?> entityClass, Conditions conditions, List<OrderColumn> appendableOrders) {
		if (conditions == null) {
			return null;
		}

		Condition condition = conditions.getCondition();
		List<WithCondition> withConditions = new ArrayList<WithCondition>(conditions.getWiths());
		if (isEntity(entityClass, condition.getParameter())) {
			// 如果是entity将对象内容展开
			EntityMapping<? extends Property> mapping = getMapping(
					condition.getParameter().getTypeDescriptor().getType());
			Iterator<? extends Property> iterator = mapping.getElements().iterator();
			while (iterator.hasNext()) {
				Property property = iterator.next();
				resolveOrders(entityClass, property, appendableOrders);
				Getter getter = property.getGetters().first();
				Object value = getter.get(condition.getParameter());
				Parameter repositoryColumn = new Parameter(property.getName(), value, getter.getTypeDescriptor());
				Condition newCondition = new Condition(condition.getSymbol(), repositoryColumn);
				withConditions.add(new WithCondition(RelationshipSymbol.AND, new Conditions(newCondition, null)));
			}
		}

		withConditions = withConditions.stream()
				.map((e) -> new WithCondition(e.getSymbol(), open(entityClass, e.getCondition(), appendableOrders)))
				.collect(Collectors.toList());
		return new Conditions(condition, withConditions);
	}

	public Parameter parseParameter(Class<?> entityClass, Property property, @Nullable Object value) {
		TypeDescriptor valueTypeDescriptor = new TypeDescriptor(property.getGetter());
		if (value != null && !ClassUtils.isAssignableValue(valueTypeDescriptor.getType(), value)) {
			valueTypeDescriptor = valueTypeDescriptor.narrow(value);
		}
		return new Parameter(property.getName(), value, valueTypeDescriptor);
	}

	public void resolveOrders(Class<?> entityClass, Property property, List<OrderColumn> appendable) {
		if (appendable == null) {
			return;
		}

		Sort sort = property.getGetters().map((e) -> getSort(entityClass, e)).first();
		if (sort == null) {
			return;
		}

		appendable.add(new OrderColumn(property.getName(), sort, null));
	}

	public <T, P extends Property> Elements<Parameter> parseParameters(Class<?> entityClass,
			Elements<? extends P> parameters, @Nullable List<OrderColumn> orders,
			Processor<P, Object, OrmException> valueProcessor, @Nullable Predicate<Pair<P, Object>> predicate)
			throws OrmException {
		return parameters.filter((e) -> {
			resolveOrders(entityClass, e.getGetter(), orders);
			return true;
		}).map((e) -> new Pair<P, Object>(e, valueProcessor.process(e)))
				.filter((e) -> predicate == null || predicate.test(e))
				.map((e) -> parseParameter(entityClass, e.getKey(), e.getValue()));
	}

	public Conditions parseConditions(Class<?> entityClass, Elements<? extends Parameter> parameters) {
		return ConditionsBuilder.build(parameters.map((column) -> {
			RelationshipSymbol relationship = getRelationship(entityClass, column);
			if (StringUtils.isEmpty(relationship)) {
				relationship = RelationshipSymbol.AND;
			}

			String condition = getCondition(entityClass, column);
			if (StringUtils.isEmpty(condition)) {
				condition = conditionKeywords.getEqualKeywords().getFirst();
			}
			return new Pair<String, Condition>(relationship,
					new Condition(ConditionSymbol.getConditionSymbols(condition).first(), column));
		}));
	}

	public <T, P extends Property> Conditions parseConditions(Class<?> entityClass, Elements<? extends P> parameters,
			@Nullable List<OrderColumn> orders, Processor<P, Object, OrmException> valueProcessor,
			@Nullable Predicate<Pair<P, Object>> predicate) throws OrmException {
		Elements<Parameter> ps = parseParameters(entityClass, parameters, orders, valueProcessor, predicate);
		return parseConditions(entityClass, ps);
	}

	public <T> List<Parameter> parseValues(Class<? extends T> entityClass, T entity,
			Mapping<? extends Property> structure) {
		return parseParameters(entityClass, structure.filter((e) -> !e.isEntity()).getElements(), null,
				(e) -> e.get(entity), (e) -> e.getKey().isNullable() || ObjectUtils.isNotEmpty(e.getValue()))
				.collect(Collectors.toList());
	}
}
