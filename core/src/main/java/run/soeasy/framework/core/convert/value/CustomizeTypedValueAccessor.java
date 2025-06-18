package run.soeasy.framework.core.convert.value;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.TypeDescriptor;

@Getter
@Setter
public class CustomizeTypedValueAccessor extends CustomizeTypedDataAccessor<Object> implements TypedValueAccessor {
	@NonNull
	private Converter converter = Converter.assignable();

	@Override
	public Object get() {
		Object value = super.get();
		Object source;
		TypeDescriptor sourceTypeDescriptor;
		if (value instanceof TypedData) {
			TypedData<?> typedData = (TypedData<?>) value;
			source = typedData.get();
			sourceTypeDescriptor = typedData.getReturnTypeDescriptor();
		} else {
			source = value;
			sourceTypeDescriptor = TypeDescriptor.forObject(source);
		}
		return converter.convert(source, sourceTypeDescriptor, getTypeDescriptor());
	}

	public TypedValue value() {
		return this;
	}
}
