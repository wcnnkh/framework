package io.basc.framework.mapper.transfer.convert;

import io.basc.framework.convert.ReversibleConverter;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.Parameter;
import io.basc.framework.execution.Parameters;
import io.basc.framework.mapper.property.Item;
import io.basc.framework.mapper.property.Items;
import io.basc.framework.mapper.support.ItemRegistry;
import io.basc.framework.util.element.Elements;

public interface ItemsRecordConverter extends ReversibleConverter<Parameters, Items<?>, Throwable> {
	ParameterConverter getParameterConverter();

	@Override
	default Items<?> convert(Parameters source, TypeDescriptor sourceType, TypeDescriptor targetType) throws Throwable {
		TypeDescriptor elementTypeDescriptor = targetType.getElementTypeDescriptor();
		Class<?> elementType = elementTypeDescriptor.getType();
		if (elementType == null || elementType == Object.class || Parameter.class.isAssignableFrom(elementType)) {
			return source;
		}

		ItemRegistry<Item> itemRegistry = new ItemRegistry<>();
		for (Parameter parameter : source.getElements()) {
			Item item = (Item) getParameterConverter().convert(parameter, elementTypeDescriptor);
			itemRegistry.register(item);
		}
		return itemRegistry;
	}

	@Override
	default Parameters reverseConvert(Items<?> source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws Throwable {
		TypeDescriptor elementTypeDescriptor = sourceType.getElementTypeDescriptor();
		Elements<Parameter> elements = source.getElements()
				.map((e) -> getParameterConverter().reverseConvert(e, elementTypeDescriptor));
		return new Parameters(elements);
	}
}
