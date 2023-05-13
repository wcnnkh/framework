package io.basc.framework.orm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.core.annotation.MultiAnnotatedElement;
import io.basc.framework.data.repository.Column;
import io.basc.framework.data.repository.Condition;
import io.basc.framework.data.repository.ConditionSymbol;
import io.basc.framework.data.repository.DeleteOperation;
import io.basc.framework.data.repository.DeleteOperationSymbol;
import io.basc.framework.data.repository.Expression;
import io.basc.framework.data.repository.InsertOperation;
import io.basc.framework.data.repository.InsertOperationSymbol;
import io.basc.framework.data.repository.OperationSymbol;
import io.basc.framework.data.repository.SelectOperation;
import io.basc.framework.data.repository.SelectOperationSymbol;
import io.basc.framework.data.repository.Sort;
import io.basc.framework.data.repository.UpdateOperation;
import io.basc.framework.data.repository.UpdateOperationSymbol;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.Getter;
import io.basc.framework.mapper.Mapping;
import io.basc.framework.mapper.ObjectMapper;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.util.Elements;
import io.basc.framework.value.Value;

public interface EntityMapper extends ObjectMapper, EntityMappingResolver {
	default Elements<? extends Column> getColumns(OperationSymbol operationSymbol, Value entity,
			EntityMapping<? extends Property> entityMapping) {
		return resolveParameters(operationSymbol, entity, entityMapping,
				(parameter) -> getColumns(operationSymbol, entity.getTypeDescriptor(), parameter));
	}

	default Elements<? extends Condition> getConditions(OperationSymbol operationSymbol, Value entity,
			EntityMapping<? extends Property> entityMapping) {
		return resolveParameters(operationSymbol, entity, entityMapping,
				(parameter) -> getConditions(operationSymbol, entity.getTypeDescriptor(), parameter));
	}

	default Elements<Condition> getConditionsByPrimaryKeys(OperationSymbol operationSymbol, TypeDescriptor source,
			EntityMapping<? extends Property> entityMapping, Elements<? extends Value> primaryKeys) {
		Elements<Parameter> parameters = getParameters(entityMapping.getPrimaryKeys().iterator(),
				primaryKeys.iterator());
		return parameters.map((parameter) -> {
			Elements<? extends Condition> conditions = getConditions(operationSymbol, source, parameter);
			for (Condition condition : conditions) {
				if (condition.getConditionSymbol().getName().equals(ConditionSymbol.EQU.getName())) {
					return condition;
				}
			}
			return new Condition(new Expression(parameter), ConditionSymbol.EQU, new Expression(parameter));
		});
	}

	default Elements<? extends Condition> getConditionsByPrimaryKeys(OperationSymbol operationSymbol, Value entity,
			EntityMapping<? extends Property> entityMapping) {
		return resolveParameters(operationSymbol, entity, entityMapping.getPrimaryKeys(),
				(parameter) -> getConditions(operationSymbol, entity.getTypeDescriptor(), parameter));
	}

	default DeleteOperation getDeleteOperation(DeleteOperationSymbol deleteOperationSymbol, Value entity,
			EntityMapping<? extends Property> entityMapping) {
		DeleteOperation deleteOperation = new DeleteOperation(deleteOperationSymbol);
		deleteOperation
				.setRepositorys(getRepositorys(deleteOperationSymbol, entity.getTypeDescriptor(), entityMapping));
		deleteOperation.setConditions(getConditions(deleteOperationSymbol, entity, entityMapping));
		return deleteOperation;
	}

	default DeleteOperation getDeleteOperationByPrimaryKeys(DeleteOperationSymbol deleteOperationSymbol,
			TypeDescriptor source, EntityMapping<? extends Property> entityMapping,
			Elements<? extends Value> primaryKeys) {
		DeleteOperation deleteOperation = new DeleteOperation(deleteOperationSymbol);
		deleteOperation
				.setConditions(getConditionsByPrimaryKeys(deleteOperationSymbol, source, entityMapping, primaryKeys));
		deleteOperation.setRepositorys(getRepositorys(deleteOperationSymbol, source, entityMapping));
		return deleteOperation;
	}

	default DeleteOperation getDeleteOperationByPrimaryKeys(DeleteOperationSymbol deleteOperationSymbol, Value entity,
			EntityMapping<? extends Property> entityMapping) {
		DeleteOperation deleteOperation = new DeleteOperation(deleteOperationSymbol);
		deleteOperation
				.setRepositorys(getRepositorys(deleteOperationSymbol, entity.getTypeDescriptor(), entityMapping));
		deleteOperation.setConditions(getConditionsByPrimaryKeys(deleteOperationSymbol, entity, entityMapping));
		return deleteOperation;
	}

	default Elements<? extends Expression> getExpressions(OperationSymbol operationSymbol, Value entity,
			EntityMapping<? extends Property> entityMapping) {
		return resolveParameters(operationSymbol, entity, entityMapping,
				(parameter) -> getExpressions(operationSymbol, entity.getTypeDescriptor(), parameter));
	}

	default InsertOperation getInsertOperation(InsertOperationSymbol insertOperationSymbol,
			EntityMapping<? extends Property> entityMapping, Value entity) {
		InsertOperation insertOperation = new InsertOperation(insertOperationSymbol);
		insertOperation
				.setRepositorys(getRepositorys(insertOperationSymbol, entity.getTypeDescriptor(), entityMapping));
		insertOperation.setConditions(getConditions(insertOperationSymbol, entity, entityMapping));
		insertOperation.setColumns(getColumns(insertOperationSymbol, entity, entityMapping));
		return insertOperation;
	}

	@Override
	default EntityMapping<? extends Property> getMapping(Class<?> entityClass) {
		Mapping<? extends Field> mapping = ObjectMapper.super.getMapping(entityClass);
		return new DefaultEntityMapping<>(entityClass, this, mapping, (e) -> new DefaultProperty(e, entityClass, this));
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

	default SelectOperation getSelectOperation(SelectOperationSymbol selectOperationSymbol, Value entity,
			EntityMapping<? extends Property> entityMapping) {
		SelectOperation selectOperation = new SelectOperation(selectOperationSymbol);
		selectOperation
				.setRepositorys(getRepositorys(selectOperationSymbol, entity.getTypeDescriptor(), entityMapping));
		selectOperation.setExpressions(getExpressions(selectOperationSymbol, entity, entityMapping));
		selectOperation.setConditions(getConditionsByPrimaryKeys(selectOperationSymbol, entity, entityMapping));
		selectOperation.setSorts(getSorts(selectOperationSymbol, entity, entityMapping));
		return selectOperation;
	}

	default SelectOperation getSelectOperationByPrimaryKeys(SelectOperationSymbol selectOperationSymbol,
			EntityMapping<? extends Property> entityMapping, TypeDescriptor source,
			Elements<? extends Value> primaryKeys) {
		SelectOperation selectOperation = new SelectOperation(selectOperationSymbol);
		selectOperation.setRepositorys(getRepositorys(selectOperationSymbol, source, entityMapping));
		selectOperation
				.setConditions(getConditionsByPrimaryKeys(selectOperationSymbol, source, entityMapping, primaryKeys));
		return selectOperation;
	}

	default SelectOperation getSelectOperationByPrimaryKeys(SelectOperationSymbol selectOperationSymbol, Value entity,
			EntityMapping<? extends Property> entityMapping) {
		SelectOperation selectOperation = new SelectOperation(selectOperationSymbol);
		selectOperation
				.setRepositorys(getRepositorys(selectOperationSymbol, entity.getTypeDescriptor(), entityMapping));
		selectOperation.setConditions(getConditionsByPrimaryKeys(selectOperationSymbol, entity, entityMapping));
		return selectOperation;
	}

	default SelectOperation getSelectOperationInPrimaryKeys(SelectOperationSymbol selectOperationSymbol,
			EntityMapping<? extends Property> entityMapping, TypeDescriptor source,
			Elements<? extends Value> primaryKeys, Elements<? extends Value> inPrimaryKeys) {
		Iterator<? extends Property> propertyIterator = entityMapping.getPrimaryKeys().iterator();
		Iterator<? extends Value> primaryKeyIterator = primaryKeys.iterator();
		List<Condition> conditions = new ArrayList<>();
		for (Parameter parameter : getParameters(propertyIterator, primaryKeyIterator)) {
			Elements<? extends Condition> elements = getConditions(selectOperationSymbol, source, parameter);
			boolean find = false;
			for (Condition condition : elements) {
				if (condition.getConditionSymbol().getName().equals(ConditionSymbol.EQU.getName())) {
					conditions.add(condition);
					find = true;
					break;
				}
			}

			if (!find) {
				conditions
						.add(new Condition(new Expression(parameter), ConditionSymbol.EQU, new Expression(parameter)));
			}
		}

		// in字段
		Property property = propertyIterator.next();
		Parameter parameter = new Parameter(property.getName(), inPrimaryKeys);
		SelectOperation selectOperation = new SelectOperation();
		selectOperation.setRepositorys(getRepositorys(selectOperationSymbol, source, entityMapping));
		conditions
				.add(new Condition(new Expression(property.getName()), ConditionSymbol.IN, new Expression(parameter)));
		selectOperation.setConditions(Elements.of(conditions));
		return selectOperation;
	}

	default Elements<? extends Sort> getSorts(OperationSymbol operationSymbol, Value entity,
			EntityMapping<? extends Property> entityMapping) {
		return resolveParameters(operationSymbol, entity, entityMapping,
				(parameter) -> getSorts(operationSymbol, entity.getTypeDescriptor(), parameter));
	}

	default UpdateOperation getUpdateOperation(UpdateOperationSymbol updateOperationSymbol, Value oldEntity,
			EntityMapping<? extends Property> oldEntityMapping, Value newEntity,
			EntityMapping<? extends Property> newEntityMapping) {
		UpdateOperation updateOperation = new UpdateOperation();
		updateOperation
				.setRepositorys(getRepositorys(updateOperationSymbol, oldEntity.getTypeDescriptor(), oldEntityMapping));
		updateOperation.setColumns(getColumns(updateOperationSymbol, newEntity, newEntityMapping));
		updateOperation.setConditions(getConditions(updateOperationSymbol, oldEntity, oldEntityMapping));
		return updateOperation;
	}

	default UpdateOperation getUpdateOperationByPrimaryKeys(UpdateOperationSymbol updateOperationSymbol, Value entity,
			EntityMapping<? extends Property> entityMapping) {
		UpdateOperation updateOperation = new UpdateOperation();
		updateOperation
				.setRepositorys(getRepositorys(updateOperationSymbol, entity.getTypeDescriptor(), entityMapping));
		updateOperation.setColumns(getColumns(updateOperationSymbol, entity, entityMapping));
		updateOperation.setConditions(getConditionsByPrimaryKeys(updateOperationSymbol, entity, entityMapping));
		return updateOperation;
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

	default <T> Elements<T> resolveParameters(OperationSymbol operationSymbol, Value entity,
			Elements<? extends Property> properties,
			Function<? super Parameter, ? extends Elements<T>> resolveProcessor) {
		return properties.flatMap((property) -> {
			MultiAnnotatedElement annotatedElement = new MultiAnnotatedElement(
					property.getGetters().map((e) -> e.getTypeDescriptor()));
			Elements<Parameter> parameters = property.getGetters().map((getter) -> {
				Object value = getter.get(entity);
				TypeDescriptor typeDescriptor = new TypeDescriptor(getter.getTypeDescriptor().getResolvableType(),
						getter.getTypeDescriptor().getType(), annotatedElement);
				return new Parameter(property.getName(), value, typeDescriptor);
			});

			for (Parameter parameter : parameters) {
				Elements<T> elements = resolveProcessor.apply(parameter);
				if (elements.isEmpty()) {
					continue;
				}

				return elements;
			}
			return Elements.empty();
		});
	}

	default <T> Elements<T> resolveParameters(OperationSymbol operationSymbol, Value entity,
			EntityMapping<? extends Property> entityMapping,
			Function<? super Parameter, ? extends Elements<T>> resolveProcessor) {
		return Elements.concat(
				resolveParameters(operationSymbol, entity, entityMapping.getPrimaryKeys(), resolveProcessor),
				resolveParameters(operationSymbol, entity, entityMapping.getNotPrimaryKeys(), resolveProcessor));
	}
}
