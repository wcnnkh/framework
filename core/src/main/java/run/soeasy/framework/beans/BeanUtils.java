package run.soeasy.framework.beans;

import java.util.Arrays;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.transform.property.PropertyMappingFilter;

public class BeanUtils {
	private static final BeanMapper BEAN_MAPPER = new BeanMapper();

	public static BeanMapper getMapper() {
		return BEAN_MAPPER;
	}

	public static <S, T> boolean copyProperties(@NonNull S source, @NonNull T target,
			@NonNull PropertyMappingFilter... filters) {
		return copyProperties(source, source.getClass(), target, target.getClass(), filters);
	}

	public static <S, T> boolean copyProperties(S source, @NonNull Class<? extends S> sourceClass, T target,
			@NonNull Class<? extends T> targetClass, @NonNull PropertyMappingFilter... filters) {
		return getMapper().transform(source, TypeDescriptor.valueOf(sourceClass), targetClass,
				TypeDescriptor.valueOf(targetClass), Arrays.asList(filters));
	}
}
