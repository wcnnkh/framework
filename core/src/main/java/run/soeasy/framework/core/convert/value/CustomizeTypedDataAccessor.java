package run.soeasy.framework.core.convert.value;

import lombok.Data;
import run.soeasy.framework.core.convert.TypeDescriptor;

@Data
public class CustomizeTypedDataAccessor<T> implements TypedDataAccessor<T> {
	private T value;
	private TypeDescriptor typeDescriptor;
	
	@Override
	public final TypeDescriptor getReturnTypeDescriptor() {
		return getTypeDescriptor();
	}

	public TypeDescriptor getTypeDescriptor() {
		return typeDescriptor == null ? TypeDescriptor.forObject(value) : typeDescriptor;
	}

	@Override
	public T get() {
		return value;
	}

	@Override
	public final TypeDescriptor getRequiredTypeDescriptor() {
		return getTypeDescriptor();
	}

	@Override
	public void set(T value) {
		this.value = value;
	}

}
