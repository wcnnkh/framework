package io.basc.framework.beans;

import io.basc.framework.convert.IdentityConversionService;
import io.basc.framework.convert.RecursiveConversionService;
import io.basc.framework.mapper.entity.MappingStrategy;
import io.basc.framework.mapper.support.DefaultMappingStrategy;
import io.basc.framework.util.Assert;

public class BeanUtils {
	private static final BeanMapper MAPPER = new BeanMapper();

	public static <T> T clone(Object source, Class<? extends T> targetType) {
		return MAPPER.convert(source, targetType);
	}

	public static <T> T clone(Object source, Class<? extends T> targetType, boolean deep) {
		DefaultMappingStrategy mappingStrategy = new DefaultMappingStrategy();
		if (deep) {
			mappingStrategy.setConversionService(new RecursiveConversionService(MAPPER));
		} else {
			mappingStrategy.setConversionService(new IdentityConversionService());
		}
		return clone(source, targetType, mappingStrategy);
	}

	public static <T> T clone(Object source, Class<? extends T> targetType, MappingStrategy mappingStrategy) {
		Assert.requiredArgument(targetType != null, "targetType");
		T target = MAPPER.newInstance(targetType);
		return copy(source, target, mappingStrategy);
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
		DefaultMappingStrategy mappingStrategy = new DefaultMappingStrategy();
		if (deep) {
			mappingStrategy.setConversionService(new RecursiveConversionService(MAPPER));
		} else {
			mappingStrategy.setConversionService(new IdentityConversionService());
		}
		return copy(source, target, mappingStrategy);
	}

	public static <T> T copy(Object source, T target, MappingStrategy mappingStrategy) {
		if (source == null || target == null) {
			return target;
		}
		MAPPER.transform(source, target, mappingStrategy);
		return target;
	}

	public static BeanMapper getMapper() {
		return MAPPER;
	}
}
