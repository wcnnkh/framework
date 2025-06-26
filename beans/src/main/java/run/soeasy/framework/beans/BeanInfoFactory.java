package run.soeasy.framework.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;

import lombok.NonNull;
import run.soeasy.framework.core.transform.property.ObjectTemplateFactory;

public interface BeanInfoFactory extends ObjectTemplateFactory<BeanProperty> {
	BeanInfo getBeanInfo(@NonNull Class<?> beanClass) throws IntrospectionException;

	@Override
	default BeanPropertyTemplate getObjectTemplate(Class<?> objectClass) {
		return new BeanPropertyTemplate(objectClass, this);
	}
}
