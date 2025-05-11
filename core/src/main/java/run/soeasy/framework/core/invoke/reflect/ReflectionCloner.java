package run.soeasy.framework.core.invoke.reflect;

import java.lang.reflect.Array;
import java.util.EnumSet;

import run.soeasy.framework.core.convert.ConversionService;
import run.soeasy.framework.core.invoke.field.FieldAccessor;
import run.soeasy.framework.core.invoke.field.FieldDescriptor;
import run.soeasy.framework.core.transform.service.MappingService;
import run.soeasy.framework.core.transform.service.SupportedInstanceFactory;
import run.soeasy.framework.core.type.ClassUtils;

public class ReflectionCloner extends MappingService<FieldAccessor<FieldDescriptor>> {

	public ReflectionCloner() {
		getObjectMappingRegistry().setMappingProvider(new ReflectionMappingProvider());
		getConfigurableInstanceFactory().setExtendFactoryTypes(EnumSet.of(SupportedInstanceFactory.ALLOCATE));
	}

	public static <T> T clone(Class<? extends T> requiredType, T source, ConversionService conversionService) {
		if (ClassUtils.isPrimitiveWrapper(requiredType)) {
			return source;
		}

		if (requiredType.isArray()) {
			return cloneArray(source, conversionService);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> T cloneArray(T array, ConversionService conversionService) {
		if (array == null) {
			return null;
		}

		if (array instanceof Object[]) {
			int len = Array.getLength(array);
			Class<?> componentType = array.getClass().getComponentType();
			Object clone = Array.newInstance(array.getClass().getComponentType(), len);
			for (int i = 0; i < len; i++) {
				Array.set(clone, i, clone(componentType, Array.get(array, i), conversionService));
			}
			return (T) clone;
		} else if (array instanceof byte[]) {
			return (T) ((byte[]) array).clone();
		} else if (array instanceof short[]) {
			return (T) ((short[]) array).clone();
		} else if (array instanceof int[]) {
			return (T) ((int[]) array).clone();
		} else if (array instanceof long[]) {
			return (T) ((long[]) array).clone();
		} else if (array instanceof char[]) {
			return (T) ((char[]) array).clone();
		} else if (array instanceof float[]) {
			return (T) ((float[]) array).clone();
		} else if (array instanceof double[]) {
			return (T) ((double[]) array).clone();
		} else if (array instanceof boolean[]) {
			return (T) ((boolean[]) array).clone();
		}
		throw new IllegalArgumentException("Must be array type");
	}
}
