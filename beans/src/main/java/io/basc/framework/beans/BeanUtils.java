package io.basc.framework.beans;

import io.basc.framework.convert.IdentityConversionService;
import io.basc.framework.convert.RecursiveConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.transform.strategy.DefaultPropertiesTransformStrategy;
import io.basc.framework.transform.strategy.PropertiesTransformStrategy;
import io.basc.framework.util.Assert;

public class BeanUtils {
	private static final BeanMapper MAPPER = new BeanMapper();

	public static <T> T clone(Object source, Class<? extends T> targetType) {
		return MAPPER.convert(source, targetType);
	}

	public static <T> T clone(Object source, Class<? extends T> targetType, boolean deep) {
		DefaultPropertiesTransformStrategy strategy = new DefaultPropertiesTransformStrategy();
		if (deep) {
			strategy.setConversionService(new RecursiveConversionService(MAPPER));
		} else {
			strategy.setConversionService(new IdentityConversionService());
		}
		return clone(source, targetType, strategy);
	}

	public static <T> T clone(Object source, Class<? extends T> targetType, PropertiesTransformStrategy strategy) {
		Assert.requiredArgument(targetType != null, "targetType");
		T target = MAPPER.newInstance(targetType);
		return copy(source, target, strategy);
	}

	@SuppressWarnings("unchecked")
	public static <T> T clone(T source) {
		Assert.requiredArgument(source != null, "source");
		return (T) clone(source, source.getClass());
	}

	@SuppressWarnings("unchecked")
	public static <T> T clone(T source, boolean deep) {
		return (T) clone(source, source.getClass(), deep);
	}

	public static <T> T copy(Object source, T target) {
		if (source == null || target == null) {
			return target;
		}
		MAPPER.transform(source, target);
		return target;
	}

	public static <T> T copy(Object source, T target, boolean deep) {
		DefaultPropertiesTransformStrategy strategy = new DefaultPropertiesTransformStrategy();
		if (deep) {
			strategy.setConversionService(new RecursiveConversionService(MAPPER));
		} else {
			strategy.setConversionService(new IdentityConversionService());
		}
		return copy(source, target, strategy);
	}

	public static <T> T copy(Object source, T target, PropertiesTransformStrategy strategy) {
		if (source == null || target == null) {
			return target;
		}
		MAPPER.transform(source, TypeDescriptor.forObject(source), target, TypeDescriptor.forObject(target), strategy);
		return target;
	}

	public static BeanMapper getMapper() {
		return MAPPER;
	}
}
