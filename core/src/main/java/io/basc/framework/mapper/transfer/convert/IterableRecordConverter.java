package io.basc.framework.mapper.transfer.convert;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ReversibleConverter;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.Parameter;
import io.basc.framework.execution.Parameters;
import io.basc.framework.util.element.Elements;

public interface IterableRecordConverter extends ReversibleConverter<Parameters, Iterable<?>, ConversionException> {
	ParameterConverter getParameterConverter();
	
	@Override
	default Iterable<?> convert(Parameters source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	default Parameters reverseConvert(Iterable<?> source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		Elements<?> elements = Elements.of(source);
		TypeDescriptor elementTypeDescriptor = sourceType.getElementTypeDescriptor();
		Elements<Parameter> parameters = elements.map((e) -> {
			return getParameterConverter().reverseConvert(e, elementTypeDescriptor);
		});
		return new Parameters(parameters);
	}
}
