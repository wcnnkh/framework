package run.soeasy.framework.beans;

import java.beans.PropertyDescriptor;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.invoke.reflect.ReflectionProperty;

@Getter
public class BeanProperty extends ReflectionProperty {
	private static final long serialVersionUID = 1L;
	private transient volatile PropertyDescriptor propertyDescriptor;

	public BeanProperty(@NonNull Class<?> beanClass, @NonNull PropertyDescriptor propertyDescriptor) {
		super(beanClass, propertyDescriptor.getName());
		setPropertyDescriptor(propertyDescriptor);
	}

	public synchronized void setPropertyDescriptor(@NonNull PropertyDescriptor propertyDescriptor) {
		this.propertyDescriptor = propertyDescriptor;
		this.setName(propertyDescriptor.getName());
		if (propertyDescriptor.getReadMethod() != null) {
			setReadMethod(propertyDescriptor.getReadMethod());
		}

		if (propertyDescriptor.getWriteMethod() != null) {
			setWriteMethod(propertyDescriptor.getWriteMethod());
		}
	}
}
