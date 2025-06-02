package run.soeasy.framework.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.util.stream.Stream;

import lombok.NonNull;
import run.soeasy.framework.core.transform.property.MapPropertyTemplate;
import run.soeasy.framework.core.transform.property.PropertyTemplate;

public class BeanTemplate extends MapPropertyTemplate<BeanProperty, PropertyTemplate<BeanProperty>>
		implements PropertyTemplate<BeanProperty> {

	public BeanTemplate(@NonNull Class<?> beanClass, BeanInfoFactory beanInfoFactory) {
		super(() -> {
			BeanInfo beanInfo;
			try {
				beanInfo = beanInfoFactory.getBeanInfo(beanClass);
			} catch (IntrospectionException e) {
				throw new FatalBeanException("Failed to obtain BeanInfo for class [" + beanClass + "]", e);
			}

			return Stream.of(beanInfo.getPropertyDescriptors()).map((e) -> new BeanProperty(beanClass, e)).iterator();
		}, false);
	}
}
