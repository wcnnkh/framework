package io.basc.framework.mapper.transfer.convert;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ReversibleConverter;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.Parameter;
import io.basc.framework.execution.Parameters;
import io.basc.framework.execution.Setter;
import io.basc.framework.mapper.InstanceFactory;
import io.basc.framework.mapper.entity.FieldDescriptor;
import io.basc.framework.mapper.entity.Mapping;
import io.basc.framework.mapper.entity.factory.MappingFactory;
import io.basc.framework.util.element.Elements;

public interface EntityRecordConverter extends ReversibleConverter<Parameters, Object, ConversionException> {
	MappingFactory getMappingFactory();

	InstanceFactory getInstanceFactory();

	ParameterConverter getParameterConverter();

	@Override
	default Object convert(Parameters source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		Mapping<?> mapping = getMappingFactory().getMapping(targetType.getType());
		Object entity = getInstanceFactory().newInstance(targetType);
		List<Parameter> parameters = new ArrayList<>(source.getElements().toList());
		for (FieldDescriptor fieldDescriptor : mapping.getElements()) {
			if (!fieldDescriptor.isSupportSetter()) {
				continue;
			}
			Setter setter = fieldDescriptor.setter();
			Iterator<Parameter> iterator = parameters.iterator();

			while (iterator.hasNext()) {
				Parameter parameter = iterator.next();
				if (parameter.getName().equals(fieldDescriptor.getName())
						|| parameter.getAliasNames().contains(fieldDescriptor.getName())
						|| fieldDescriptor.getAliasNames().contains(parameter.getName())
						|| fieldDescriptor.getAliasNames().anyMatch(parameter.getAliasNames(), String::equals)) {
					Object value = getParameterConverter().convert(parameter, setter.getTypeDescriptor());
					setter.set(entity, value);
					iterator.remove();
					break;
				}
			}
		}
		return entity;
	}

	@Override
	default Parameters reverseConvert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		Mapping<?> mapping = getMappingFactory().getMapping(targetType.getType());
		Elements<Parameter> elements = mapping.getElements().filter((e) -> e.isSupportGetter())
				.map((fieldDescriptor) -> {
					Object value = fieldDescriptor.getter().get(source);
					Parameter parameter = new Parameter(fieldDescriptor.getPositionIndex(), fieldDescriptor.getName(),
							value, fieldDescriptor.getter().getTypeDescriptor());
					parameter.setAliasNames(fieldDescriptor.getAliasNames());
					return parameter;
				});
		return new Parameters(elements);
	}
}
