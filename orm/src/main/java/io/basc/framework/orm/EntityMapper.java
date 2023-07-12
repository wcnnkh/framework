package io.basc.framework.orm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.core.annotation.MultiAnnotatedElement;
import io.basc.framework.data.domain.Entry;
import io.basc.framework.data.repository.Condition;
import io.basc.framework.data.repository.Expression;
import io.basc.framework.data.repository.OperationSymbol;
import io.basc.framework.data.repository.Sort;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.Getter;
import io.basc.framework.mapper.Mapping;
import io.basc.framework.mapper.ObjectMapper;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Range;

public interface EntityMapper extends ObjectMapper, EntityKeyGenerator, EntityResolver {
	default <T> EntityRepository<T> getRepository(OperationSymbol operationSymbol, Class<? extends T> entityClass,
			@Nullable T entity) {
		EntityMapping<?> entityMapping = getMapping(entityClass);
		String repositoryName = getRepositoryName(operationSymbol, entityMapping, entityClass, entity);
		return new EntityRepository<T>(repositoryName, entityMapping, entityClass, entity);
	}

	default <T> Elements<? extends Condition> getConditions(OperationSymbol operationSymbol,
			EntityRepository<T> repository) {
		if (repository.getEntity() == null) {
			return Elements.empty();
		}

		List<Entry<Property, Parameter>> entries = getEntries(repository.getEntity(),
				repository.getEntityMapping().columns().iterator());
		return toConditions(operationSymbol, repository, Elements.of(entries));
	}

	default <T> Elements<? extends Sort> getOrders(OperationSymbol operationSymbol, EntityRepository<T> repository) {
		Elements<Entry<Property, Parameter>> entries;
		if (repository.getEntity() == null) {
			entries = repository.getEntityMapping().columns().map((property) -> {
				Parameter parameter = createParameter(property, null);
				return new Entry<>(property, parameter);
			});
		} else {
			entries = Elements
					.of(getEntries(repository.getEntity(), repository.getEntityMapping().columns().iterator()));
		}
		return entries.map((e) -> getSort(operationSymbol, repository, e.getValue(), e.getKey()));
	}

	default <T> Elements<? extends Expression> getColumns(OperationSymbol operationSymbol,
			EntityRepository<T> repository) {
		if (repository.getEntity() == null) {
			return repository.getEntityMapping().columns().map((e) -> new Expression(e.getName()));
		}

		List<Entry<Property, Parameter>> entries = getEntries(repository.getEntity(),
				repository.getEntityMapping().columns().iterator());
		return toColumns(operationSymbol, repository, Elements.of(entries));
	}

	default <T> Range<Long> getLimit(OperationSymbol operationSymbol, EntityRepository<T> repository) {
		// TODO 实现待定
		return Range.unbounded();
	}

	@Override
	default boolean isEntity(TypeDescriptor source, ParameterDescriptor parameterDescriptor) {
		return ObjectMapper.super.isEntity(source, parameterDescriptor);
	}

	@Override
	default EntityMapping<? extends Property> getMapping(Class<?> entityClass) {
		Mapping<? extends Field> mapping = ObjectMapper.super.getMapping(entityClass);
		return new DefaultEntityMapping<>(mapping, (e) -> new DefaultProperty(e, entityClass, this), entityClass, this);
	}

	default Parameter createParameter(Property property, Object value) {
		Parameter parameter;
		if (value instanceof Parameter) {
			parameter = (Parameter) value;
			parameter = parameter.rename(property.getName());
		} else {
			MultiAnnotatedElement annotatedElement = new MultiAnnotatedElement(
					property.getGetters().map((e) -> e.getTypeDescriptor()));
			TypeDescriptor typeDescriptor = new TypeDescriptor(ResolvableType.forClass(value.getClass()),
					value.getClass(), annotatedElement);
			parameter = new Parameter(property.getName(), value, typeDescriptor);
		}
		return parameter;
	}

	default <F extends Property> List<Entry<F, Parameter>> combineEntries(Iterator<? extends F> properties,
			Iterator<? extends Object> args) {
		List<Entry<F, Parameter>> entries = new ArrayList<>(8);
		while (properties.hasNext() && args.hasNext()) {
			F property = properties.next();
			Object value = args.next();
			Parameter parameter = createParameter(property, value);
			Entry<F, Parameter> entry = new Entry<>(property, parameter);
			entries.add(entry);
		}
		return entries;
	}

	default <F extends Property> List<Entry<F, Parameter>> getEntries(Object entity,
			Iterator<? extends F> propertyIterator) {
		List<Entry<F, Parameter>> entries = new ArrayList<>();
		while (propertyIterator.hasNext()) {
			F property = propertyIterator.next();
			Getter getter = property.getGetters().first();
			Object value = getter.get(entity);
			MultiAnnotatedElement annotatedElement = new MultiAnnotatedElement(
					property.getGetters().map((e) -> e.getTypeDescriptor()));
			TypeDescriptor typeDescriptor = new TypeDescriptor(getter.getTypeDescriptor().getResolvableType(),
					value.getClass(), annotatedElement);
			Parameter parameter = new Parameter(property.getName(), value, typeDescriptor);
			if (!hasEffectiveValue(parameter)) {
				continue;
			}

			Entry<F, Parameter> entry = new Entry<>(property, parameter);
			entries.add(entry);
		}
		return entries;
	}

	default boolean hasEffectiveValue(Object entity, Property property) {
		if (!property.isSupportGetter()) {
			return false;
		}

		for (Getter getter : property.getGetters()) {
			Object value = getter.get(entity);
			if (value == null) {
				continue;
			}

			Parameter parameter = new Parameter(getter.getName(), value, getter.getTypeDescriptor());
			if (!parameter.isPresent()) {
				continue;
			}

			if (hasEffectiveValue(parameter)) {
				return true;
			}
		}
		return false;
	}

	@Override
	default boolean isEntity(TypeDescriptor source) {
		return ObjectMapper.super.isEntity(source);
	}

	default <T> Elements<? extends Expression> toColumns(OperationSymbol operationSymbol,
			EntityRepository<T> repository, Elements<? extends Entry<Property, Parameter>> elements) {
		return elements.filter((e) -> hasEffectiveValue(e.getValue()))
				.map((e) -> getColumn(operationSymbol, repository, e.getValue(), e.getKey()));
	}

	default <T> Elements<? extends Condition> toConditions(OperationSymbol operationSymbol,
			EntityRepository<T> repository, Elements<? extends Entry<Property, Parameter>> elements) {
		return elements.filter((e) -> hasEffectiveValue(e.getValue()))
				.map((e) -> getCondition(operationSymbol, repository, e.getValue(), e.getKey()));
	}
}
