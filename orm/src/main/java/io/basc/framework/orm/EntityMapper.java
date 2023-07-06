package io.basc.framework.orm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.core.annotation.MultiAnnotatedElement;
import io.basc.framework.data.repository.Condition;
import io.basc.framework.data.repository.DeleteOperation;
import io.basc.framework.data.repository.DeleteOperationSymbol;
import io.basc.framework.data.repository.Expression;
import io.basc.framework.data.repository.InsertOperation;
import io.basc.framework.data.repository.InsertOperationSymbol;
import io.basc.framework.data.repository.Operation;
import io.basc.framework.data.repository.OperationSymbol;
import io.basc.framework.data.repository.QueryOperation;
import io.basc.framework.data.repository.QueryOperationSymbol;
import io.basc.framework.data.repository.Repository;
import io.basc.framework.data.repository.Sort;
import io.basc.framework.data.repository.UpdateOperation;
import io.basc.framework.data.repository.UpdateOperationSymbol;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.Getter;
import io.basc.framework.mapper.Mapping;
import io.basc.framework.mapper.ObjectMapper;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.Elements;

public interface EntityMapper extends ObjectMapper, EntityMappingResolver {

	@Override
	default boolean isEntity(TypeDescriptor source, ParameterDescriptor parameterDescriptor) {
		return ObjectMapper.super.isEntity(source, parameterDescriptor);
	}

	default <T> Operation getOperation(OperationSymbol operationSymbol, Class<? extends T> entityClass,
			EntityMapping<? extends Property> entityMapping, T entity) {
		Repository repository = getRepository(operationSymbol, entityClass, entityMapping, entity);
		Elements<? extends Parameter> columnParameters = getParameters(entity, entityMapping.columns().iterator());
		if (operationSymbol instanceof QueryOperationSymbol) {
			QueryOperation queryOperation = new QueryOperation((QueryOperationSymbol) operationSymbol, repository);

			Elements<? extends Expression> columns = toColumns(operationSymbol, repository, entityClass, entityMapping,
					columnParameters);
			queryOperation.setColumns(columns);

			Elements<? extends Condition> conditions = toConditions(operationSymbol, repository, entityClass,
					entityMapping, columnParameters);
			queryOperation.setConditions(conditions);

			Elements<? extends Sort> sorts = toSorts(operationSymbol, repository, entityClass, entityMapping,
					columnParameters);
			queryOperation.setSorts(sorts);
			return queryOperation;
		} else if (operationSymbol instanceof InsertOperationSymbol) {
			Elements<? extends Expression> columns = toColumns(operationSymbol, repository, entityClass, entityMapping,
					columnParameters);
			return new InsertOperation((InsertOperationSymbol) operationSymbol, repository, columns);
		} else if (operationSymbol instanceof DeleteOperationSymbol) {
			DeleteOperation deleteOperation = new DeleteOperation((DeleteOperationSymbol) operationSymbol, repository);
			Elements<? extends Condition> conditions = toConditions(operationSymbol, repository, entityClass,
					entityMapping, columnParameters);
			deleteOperation.setConditions(conditions);
			deleteOperation.setConditions(conditions);
			return deleteOperation;
		} else if (operationSymbol instanceof UpdateOperationSymbol) {
			Elements<? extends Expression> columns = toColumns(operationSymbol, repository, entityClass, entityMapping,
					columnParameters);
			UpdateOperation updateOperation = new UpdateOperation((UpdateOperationSymbol) operationSymbol, repository,
					columns);
			Elements<? extends Condition> conditions = toConditions(operationSymbol, repository, entityClass,
					entityMapping, columnParameters);
			updateOperation.setConditions(conditions);
			return updateOperation;
		}
		throw new UnsupportedException(operationSymbol.toString());
	}

	@Override
	default EntityMapping<? extends Property> getMapping(Class<?> entityClass) {
		Mapping<? extends Field> mapping = ObjectMapper.super.getMapping(entityClass);
		return new DefaultEntityMapping<>(mapping, (e) -> new DefaultProperty(e, entityClass, this), entityClass, this);
	}

	default Elements<Parameter> toParameters(Iterator<? extends Property> properties, Iterator<? extends Object> args) {
		List<Parameter> parameters = new ArrayList<>(4);
		while (properties.hasNext() && args.hasNext()) {
			Property property = properties.next();
			Object value = args.next();
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
			parameters.add(parameter);
		}
		return Elements.of(parameters);
	}

	default Elements<Parameter> getParameters(Object entity, Iterator<? extends Property> propertyIterator) {
		List<Parameter> parameters = new ArrayList<>(4);
		while (propertyIterator.hasNext()) {
			Property property = propertyIterator.next();
			Getter getter = property.getGetters().first();
			Object value = getter.get(entity);
			MultiAnnotatedElement annotatedElement = new MultiAnnotatedElement(
					property.getGetters().map((e) -> e.getTypeDescriptor()));
			TypeDescriptor typeDescriptor = new TypeDescriptor(getter.getTypeDescriptor().getResolvableType(),
					value.getClass(), annotatedElement);
			Parameter parameter = new Parameter(property.getName(), value, typeDescriptor);
			parameters.add(parameter);
		}
		return Elements.of(parameters);
	}

	default Elements<? extends Expression> toColumns(OperationSymbol operationSymbol, Repository repository,
			Class<?> entityClass, EntityMapping<?> entityMapping, Elements<? extends Parameter> parameters) {
		return parameters.map((e) -> toColumn(operationSymbol, repository, entityClass, entityMapping, e));
	}

	default Elements<? extends Condition> toConditions(OperationSymbol operationSymbol, Repository repository,
			Class<?> entityClass, EntityMapping<?> entityMapping, Elements<? extends Parameter> parameters) {
		return parameters.map((e) -> toCondition(operationSymbol, repository, entityClass, entityMapping, e));
	}

	default Elements<? extends Sort> toSorts(OperationSymbol operationSymbol, Repository repository,
			Class<?> entityClass, EntityMapping<?> entityMapping, Elements<? extends Parameter> parameters) {
		return parameters.map((e) -> toSort(operationSymbol, repository, entityClass, entityMapping, e));
	}

	/**
	 * 是否存在有效值
	 * 
	 * @param entity
	 * @param field
	 * @return
	 */
	default boolean hasEffectiveValue(Object entity, Field field) {
		if (!field.isSupportGetter()) {
			return false;
		}

		for (Getter getter : field.getGetters()) {
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
}
