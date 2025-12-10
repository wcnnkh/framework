package run.soeasy.framework.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.util.stream.Stream;

import lombok.NonNull;
import run.soeasy.framework.core.mapping.property.PropertyMapping;
import run.soeasy.framework.core.mapping.property.StreamablePropertyMapping;
import run.soeasy.framework.core.streaming.Streamable;
import run.soeasy.framework.core.type.ReflectionUtils;

public class BeanInfoMapping extends StreamablePropertyMapping<BeanProperty> {

	public BeanInfoMapping(@NonNull Streamable<BeanProperty> elements) {
		super(elements);
	}

	public BeanInfoMapping(@NonNull Class<?> beanClass, @NonNull BeanInfo beanInfo) {
		this(Streamable.array(beanInfo.getPropertyDescriptors())
				.map((descriptor) -> new BeanProperty(beanClass, descriptor)));
	}

	protected BeanInfoMapping(@NonNull Class<?> beanClass, @NonNull BeanInfoFactory beanInfoFactory) {
		this(Streamable.of(() -> {
			BeanInfo beanInfo;
			try {
				beanInfo = beanInfoFactory.getBeanInfo(beanClass);
			} catch (IntrospectionException e) {
				throw new FatalBeanException("Failed to obtain BeanInfo for class [" + beanClass + "]", e);
			}
			// 将属性描述符转换为BeanProperty，并过滤忽略的属性
			return Stream.of(beanInfo.getPropertyDescriptors())
					.map((descriptor) -> new BeanProperty(beanClass, descriptor)).iterator();
		}));
	}

	public PropertyMapping<BeanProperty> standard() {
		return new BeanInfoMapping(this.elements().filter((property) -> {
			return property.getReadMethod() != null && property.getReadMethod().getSource() != null
					&& ReflectionUtils.isObjectMethod(property.getReadMethod().getSource());
		})).toMultiMapped();
	}
}
