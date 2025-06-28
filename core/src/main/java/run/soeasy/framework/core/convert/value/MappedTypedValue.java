package run.soeasy.framework.core.convert.value;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.support.SystemConversionService;

@Getter
@RequiredArgsConstructor
class MappedTypedValue<W extends TypedValue> implements TypedValueWrapper<W> {
	private final W source;
	private final TypeDescriptor typeDescriptor;
	private final Converter converter;

	@Override
	public TypeDescriptor getReturnTypeDescriptor() {
		return typeDescriptor;
	}

	@Override
	public Object get() {
		TypeDescriptor sourceTypeDescriptor = source.getReturnTypeDescriptor();
		if (converter.canConvert(sourceTypeDescriptor, typeDescriptor)) {
			return converter.convert(source.get(), sourceTypeDescriptor, typeDescriptor);
		}
		return SystemConversionService.getInstance().convert(source.get(), sourceTypeDescriptor, typeDescriptor);
	}
}
