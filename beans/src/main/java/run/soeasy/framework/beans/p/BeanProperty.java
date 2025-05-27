package run.soeasy.framework.beans.p;

import java.beans.PropertyDescriptor;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.transform.property.Property;

@RequiredArgsConstructor
public class BeanProperty implements Property {
	@NonNull
	private final PropertyDescriptor propertyDescriptor;

	@Override
	public String getName() {
		return propertyDescriptor.getName();
	}

	@Override
	public TypeDescriptor getReturnTypeDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeDescriptor getRequiredTypeDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isReadable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isWriteable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object readFrom(Object target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void writeTo(Object value, Object target) {
		// TODO Auto-generated method stub

	}

}
