package run.soeasy.framework.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.util.stream.Stream;

import lombok.NonNull;
import run.soeasy.framework.core.transform.property.MapPropertyTemplate;
import run.soeasy.framework.core.transform.property.PropertyTemplate;
import run.soeasy.framework.core.type.ReflectionUtils;

public class BeanPropertyTemplate extends MapPropertyTemplate<BeanProperty, PropertyTemplate<BeanProperty>>
		implements PropertyTemplate<BeanProperty> {

	public BeanPropertyTemplate(@NonNull Class<?> beanClass, BeanInfoFactory beanInfoFactory) {
		super(() -> {
			BeanInfo beanInfo;
			try {
				beanInfo = beanInfoFactory.getBeanInfo(beanClass);
			} catch (IntrospectionException e) {
				throw new FatalBeanException("Failed to obtain BeanInfo for class [" + beanClass + "]", e);
			}
			return Stream.of(beanInfo.getPropertyDescriptors()).map((e) -> new BeanProperty(beanClass, e))
					.filter((e) -> !isIgnoreProperty(e)).iterator();
		}, false);
	}

	private static boolean isIgnoreProperty(BeanProperty property) {
		if (property.getReadMethod() != null && property.getReadMethod().getSource() != null
				&& ReflectionUtils.isObjectMethod(property.getReadMethod().getSource())) {
			return true;
		}
		return false;
	}
}
