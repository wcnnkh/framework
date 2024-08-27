package io.basc.framework.orm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.domain.Entry;
import io.basc.framework.data.repository.Condition;
import io.basc.framework.data.repository.Expression;
import io.basc.framework.data.repository.OperationSymbol;
import io.basc.framework.data.repository.Sort;
import io.basc.framework.execution.param.Parameter;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.stereotype.Field;
import io.basc.framework.mapper.stereotype.ObjectMapper;
import io.basc.framework.mapper.stereotype.OffLineField;
import io.basc.framework.orm.config.Analyzer;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Range;

public interface EntityMapper extends ObjectMapper, EntityKeyGenerator, Analyzer {

	default <T> EntityRepository<T> getRepository(OperationSymbol operationSymbol, Class<? extends T> entityClass,
			@Nullable T entity) {
		EntityMapping<?> entityMapping = getMapping(entityClass);
		String repositoryName = getRepositoryName(operationSymbol, entityMapping, entityClass, entity);
		return new EntityRepository<T>(repositoryName, entityMapping, entityClass, entity);
	}

	@Override
	EntityMapping<? extends ColumnDescriptor> getMapping(Class<?> entityClass);

	default <T> Elements<? extends Condition> getConditions(OperationSymbol operationSymbol,
			EntityRepository<T> repository) {
		if (repository.getEntity() == null) {
			return Elements.empty();
		}

		List<Entry<ColumnDescriptor, Parameter>> entries = getEntries(repository.getEntity(),
				repository.getEntityMapping().columns().iterator());
		return toConditions(operationSymbol, repository, Elements.of(entries));
	}

	default <T> Elements<? extends Sort> getOrders(OperationSymbol operationSymbol, EntityRepository<T> repository) {
		Elements<Entry<ColumnDescriptor, Parameter>> entries;
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

		List<Entry<ColumnDescriptor, Parameter>> entries = getEntries(repository.getEntity(),
				repository.getEntityMapping().columns().iterator());
		return toColumns(operationSymbol, repository, Elements.of(entries));
	}

	default <T> Range<Long> getLimit(OperationSymbol operationSymbol, EntityRepository<T> repository) {
		// TODO 实现待定
		return Range.unbounded();
	}

	default Parameter createParameter(ColumnDescriptor property, Object value) {
		OffLineField field = new OffLineField(property);
		field.setValue(value);
		return field;
	}

	default <F extends ColumnDescriptor> List<Entry<F, Parameter>> combineEntries(Iterator<? extends F> properties,
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

	default <F extends ColumnDescriptor> List<Entry<F, Parameter>> getEntries(Object entity,
			Iterator<? extends F> propertyIterator) {
		List<Entry<F, Parameter>> entries = new ArrayList<>();
		while (propertyIterator.hasNext()) {
			F property = propertyIterator.next();
			Field parameter = new Field(property, entity);
			if (!hasEffectiveValue(parameter)) {
				continue;
			}

			Entry<F, Parameter> entry = new Entry<>(property, parameter);
			entries.add(entry);
		}
		return entries;
	}

	default boolean hasEffectiveValue(Object entity, ColumnDescriptor property) {
		if (!property.isSupportGetter()) {
			return false;
		}

		Field field = new Field(property, entity);
		if (!field.isPresent()) {
			return false;
		}

		return hasEffectiveValue(field);
	}

	@Override
	default boolean isEntity(TypeDescriptor source) {
		return ObjectMapper.super.isEntity(source);
	}

	default <T> Elements<? extends Expression> toColumns(OperationSymbol operationSymbol,
			EntityRepository<T> repository, Elements<? extends Entry<ColumnDescriptor, Parameter>> elements) {
		return elements.filter((e) -> hasEffectiveValue(e.getValue()))
				.map((e) -> getColumn(operationSymbol, repository, e.getValue(), e.getKey()));
	}

	default <T> Elements<? extends Condition> toConditions(OperationSymbol operationSymbol,
			EntityRepository<T> repository, Elements<? extends Entry<ColumnDescriptor, Parameter>> elements) {
		return elements.filter((e) -> hasEffectiveValue(e.getValue()))
				.map((e) -> getCondition(operationSymbol, repository, e.getValue(), e.getKey()));
	}
}
