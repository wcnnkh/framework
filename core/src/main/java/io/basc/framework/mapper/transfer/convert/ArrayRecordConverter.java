package io.basc.framework.mapper.transfer.convert;

import java.lang.reflect.Array;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ReversibleConverter;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.Parameter;
import io.basc.framework.execution.Parameters;

public class ArrayRecordConverter extends ReversibleConverter<Parameters, Object, ConversionException> {
	ParameterConverter getParameterConverter();
	
	@Override
	default Object convert(Parameters source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		TypeDescriptor elementTypeDescriptor = targetType.getElementTypeDescriptor();
		Parameter[] parameters = source.getElements().toArray(Parameter[]::new);
		Object array = Array.newInstance(elementTypeDescriptor.getType(), parameters.length);
		for (int i = 0; i < parameters.length; i++) {
			Object value = getParameterConverter().convert(parameters[i], elementTypeDescriptor);
			Array.set(array, i, value);
		}
		return array;
	}

	@Override
	default Parameters reverseConvert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		Parameter[] parameters = new Parameter[Array.getLength(source)];
		TypeDescriptor elementTypeDescriptor = sourceType.getElementTypeDescriptor();
		for (int len = parameters.length, i = 0; i < len; i++) {
			Object value = Array.get(source, i);
			Parameter parameter = getParameterConverter().reverseConvert(value, elementTypeDescriptor);
			parameter.setPositionIndex(i);
			parameters[i] = parameter;
		}
		return new Parameters(parameters);
	}
}
