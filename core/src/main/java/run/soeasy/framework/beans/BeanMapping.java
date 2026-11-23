package run.soeasy.framework.beans;

import java.beans.BeanInfo;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.mapping.property.PropertyMapping;
import run.soeasy.framework.core.mapping.property.StreamablePropertyMapping;
import run.soeasy.framework.core.streaming.Streamable;
import run.soeasy.framework.core.type.ReflectionUtils;

@Getter
public class BeanMapping extends StreamablePropertyMapping<BeanProperty> {
	@NonNull
	private final Class<?> beanClass;
	@NonNull
	private final BeanInfo beanInfo;

	public BeanMapping(@NonNull Class<?> beanClass, @NonNull BeanInfo beanInfo,
			@NonNull Streamable<BeanProperty> elements) {
		super(elements);
		this.beanClass = beanClass;
		this.beanInfo = beanInfo;
	}

	public BeanMapping(@NonNull Class<?> beanClass, @NonNull BeanInfo beanInfo) {
		this(beanClass, beanInfo, Streamable.array(beanInfo.getPropertyDescriptors())
				.map((descriptor) -> new BeanProperty(beanClass, descriptor)).cached());
	}

	public PropertyMapping<BeanProperty> standard() {
		return new BeanMapping(beanClass, beanInfo, this.elements().filter((property) -> {
			return property.getReadMethod() != null && property.getReadMethod().getSource() != null
					&& !ReflectionUtils.isObjectMethod(property.getReadMethod().getSource());
		})).toMultiMapped();
	}
}
