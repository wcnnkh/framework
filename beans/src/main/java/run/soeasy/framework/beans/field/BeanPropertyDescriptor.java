package run.soeasy.framework.beans.field;

import java.beans.PropertyDescriptor;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.transform.field.FieldDescriptor;

@RequiredArgsConstructor
@Getter
public class BeanPropertyDescriptor implements FieldDescriptor {
	@NonNull
	private final PropertyDescriptor propertyDescriptor;

	@Override
	public Object getIndex() {
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
	public Object readFrom(Object target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void writeTo(Object value, Object target) {
		// TODO Auto-generated method stub

	}
}
