package run.soeasy.framework.core.convert.value;

import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.TypeDescriptor;

class MappedTypedValueAccessor<W extends TypedValueAccessor> extends MappedTypedValue<W>
		implements TypedValueAccessorWrapper<W> {
	public MappedTypedValueAccessor(W source, TypeDescriptor typeDescriptor, Converter converter) {
		super(source, typeDescriptor, converter);
	}
}
