package io.basc.framework.mapper.transfer.convert;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.config.support.DefaultReversibleConverterRegistry;
import io.basc.framework.convert.lang.Value;
import io.basc.framework.execution.Parameter;
import io.basc.framework.util.Item;
import io.basc.framework.util.Named;

public class ParameterConverter extends DefaultReversibleConverterRegistry<Parameter, ConversionException> {

	@Override
	public Object convert(Parameter source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		if (super.canConvert(sourceType, targetType)) {
			return super.convert(source, sourceType, targetType);
		}
		return source.getAsObject(targetType);
	}

	@Override
	public Parameter reverseConvert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		if (source instanceof Parameter) {
			return (Parameter) source;
		}

		if (super.canConvert(sourceType, targetType)) {
			return super.reverseConvert(source, sourceType, targetType);
		}

		Value value;
		if (source instanceof Value) {
			value = (Value) source;
		} else {
			value = Value.of(source, sourceType);
		}

		Parameter parameter;
		if (source instanceof Item) {
			Item item = (Item) source;
			parameter = new Parameter(item.getPositionIndex(), item.getName(), value);
			parameter.setAliasNames(item.getAliasNames());
		} else if (source instanceof Named) {
			Named named = (Named) source;
			parameter = new Parameter(-1, named.getName(), value);
			parameter.setAliasNames(named.getAliasNames());
		} else {
			parameter = new Parameter(-1, null, value);
		}
		return parameter;
	}
}
