package run.soeasy.framework.beans;

import java.util.Arrays;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.transform.property.PropertyMapper;
import run.soeasy.framework.core.transform.property.PropertyMappingFilter;

@Getter
public class BeanMapper extends PropertyMapper<BeanProperty> {
	private static final ConfigurableBeanInfoFactory BEAN_INFO_FACTORY = new ConfigurableBeanInfoFactory();
	public static volatile BeanMapper instance;

	static {
		BEAN_INFO_FACTORY.configure();
	}

	public static <S, T> boolean copyProperties(S source, @NonNull Class<? extends S> sourceClass, T target,
			@NonNull Class<? extends T> targetClass, @NonNull PropertyMappingFilter... filters) {
		return getInstane().transform(source, TypeDescriptor.valueOf(sourceClass), target,
				TypeDescriptor.valueOf(targetClass), Arrays.asList(filters));
	}

	public static <S, T> boolean copyProperties(@NonNull S source, @NonNull T target,
			@NonNull PropertyMappingFilter... filters) {
		return copyProperties(source, source.getClass(), target, target.getClass(), filters);
	}

	public static BeanMapper getInstane() {
		if (instance == null) {
			synchronized (BeanMapper.class) {
				if (instance == null) {
					instance = new BeanMapper();
				}
			}
		}
		return instance;
	}

	public BeanMapper() {
		getObjectTemplateRegistry().setObjectTemplateFactory(BEAN_INFO_FACTORY);
	}
}
