package io.basc.framework.orm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.core.annotation.MultiAnnotatedElement;
import io.basc.framework.data.repository.DeleteOperation;
import io.basc.framework.data.repository.DeleteOperationSymbol;
import io.basc.framework.data.repository.InsertOperation;
import io.basc.framework.data.repository.InsertOperationSymbol;
import io.basc.framework.data.repository.Operation;
import io.basc.framework.data.repository.OperationSymbol;
import io.basc.framework.data.repository.QueryOperation;
import io.basc.framework.data.repository.QueryOperationSymbol;
import io.basc.framework.data.repository.Repository;
import io.basc.framework.data.repository.UpdateOperation;
import io.basc.framework.data.repository.UpdateOperationSymbol;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.Getter;
import io.basc.framework.mapper.Mapping;
import io.basc.framework.mapper.ObjectMapper;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.util.Elements;
import io.basc.framework.value.Value;

public interface EntityMapper extends ObjectMapper, EntityMappingResolver {

	default <T> Operation getOperation(OperationSymbol operationSymbol, EntityMapping<? extends Property> entityMapping,
			Class<? extends T> entityType, T entity) {
		Repository repository = getRepository(entityMapping, entity);
		if (operationSymbol instanceof QueryOperationSymbol) {
			QueryOperation queryOperation = new QueryOperation((QueryOperationSymbol) operationSymbol, repository);
			queryOperation.setColumns(getColumns(operationSymbol, entityMapping, entity));
			queryOperation.setConditions(getCondition(operationSymbol, entityMapping, entity));
			queryOperation.setSorts(getSorts(operationSymbol, entityMapping, entity));
			return queryOperation;
		} else if (operationSymbol instanceof InsertOperationSymbol) {
			return new InsertOperation((InsertOperationSymbol) operationSymbol, repository,
					getColumns(operationSymbol, entityMapping, entity));
		} else if (operationSymbol instanceof DeleteOperationSymbol) {
			DeleteOperation deleteOperation = new DeleteOperation((DeleteOperationSymbol) operationSymbol, repository);
			deleteOperation.setConditions(getCondition(operationSymbol, entityMapping, entity));
			return deleteOperation;
		} else if (operationSymbol instanceof UpdateOperationSymbol) {
			UpdateOperation updateOperation = new UpdateOperation((UpdateOperationSymbol) operationSymbol, repository,
					getColumns(operationSymbol, entityMapping, entity));
			updateOperation.setConditions(getCondition(operationSymbol, entityMapping, entity));
			return updateOperation;
		}
		throw new UnsupportedException(operationSymbol.toString());
	}

	@Override
	default EntityMapping<? extends Property> getMapping(Class<?> entityClass) {
		Mapping<? extends Field> mapping = ObjectMapper.super.getMapping(entityClass);
		return new DefaultEntityMapping<>(mapping, (e) -> new DefaultProperty(e, entityClass, this), entityClass, this);
	}

	default Elements<Parameter> getParameters(Iterator<? extends Property> properties, Iterator<? extends Value> args) {
		List<Parameter> parameters = new ArrayList<>(8);
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

	/**
	 * 是否存在有效值
	 * 
	 * @param entity
	 * @param field
	 * @return
	 */
	default boolean hasEffectiveValue(Value source, Field field) {
		if (!field.isSupportGetter()) {
			return false;
		}

		for (Getter getter : field.getGetters()) {
			Object value = getter.get(source);
			if (value == null) {
				continue;
			}

			Parameter parameter = new Parameter(getter.getName(), value, getter.getTypeDescriptor());
			if (!parameter.isPresent()) {
				continue;
			}

			if (hasEffectiveValue(source.getTypeDescriptor(), parameter)) {
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
