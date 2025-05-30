package run.soeasy.framework.core.invoke.reflect;

import java.lang.reflect.Array;
import java.util.EnumSet;

import run.soeasy.framework.core.ResolvableType;
import run.soeasy.framework.core.SupportedInstanceFactory;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.transform.property.PropertyMappingService;

public class ReflectionCloner extends PropertyMappingService {
	private ReflectionCloner() {
		getMappingRegistry().setMappingProvider(new ReflectionFieldTemplateFactory());
		getConfigurableInstanceFactory().setExtendFactoryTypes(EnumSet.of(SupportedInstanceFactory.ALLOCATE));
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		if (sourceType.isArray() && targetType.isArray()) {
			return cloneArray(source, sourceType, targetType);
		}
		return super.convert(source, sourceType, targetType);
	}

	public Object cloneArray(Object array, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (array instanceof Object[]) {
			TypeDescriptor sourceElementTypeDescriptor = sourceType.getElementTypeDescriptor();
			TypeDescriptor targetElementTypeDescriptor = targetType.getElementTypeDescriptor();
			int len = Array.getLength(array);
			Object clone = Array.newInstance(targetElementTypeDescriptor.getType(), len);
			for (int i = 0; i < len; i++) {
				Object value = Array.get(array, i);
				value = getMapper().getValueMapper().getConversionService().convert(value, sourceElementTypeDescriptor,
						targetElementTypeDescriptor);
				Array.set(clone, i, value);
			}
			return clone;
		} else if (array instanceof byte[]) {
			return ((byte[]) array).clone();
		} else if (array instanceof short[]) {
			return ((short[]) array).clone();
		} else if (array instanceof int[]) {
			return ((int[]) array).clone();
		} else if (array instanceof long[]) {
			return ((long[]) array).clone();
		} else if (array instanceof char[]) {
			return ((char[]) array).clone();
		} else if (array instanceof float[]) {
			return ((float[]) array).clone();
		} else if (array instanceof double[]) {
			return ((double[]) array).clone();
		} else if (array instanceof boolean[]) {
			return ((boolean[]) array).clone();
		}
		throw new IllegalArgumentException("Must be array type");
	}

	@SuppressWarnings("unchecked")
	public static <T> T clone(T source, boolean deep) {
		ReflectionCloner cloner = new ReflectionCloner();
		if (deep) {
			cloner.getMapper().getValueMapper().setConversionService(cloner);
		}

		Class<?> requiredClass = source.getClass();
		Object target = cloner.newInstance(ResolvableType.forType(requiredClass));
		clone(source, target, requiredClass, cloner);
		return (T) target;
	}

	private static void clone(Object source, Object target, Class<?> requiredClass, ReflectionCloner cloner) {
		if (requiredClass == null || requiredClass == Object.class) {
			return;
		}
		cloner.transform(source, requiredClass, target, requiredClass);
		// java新版本已经支持在接口中定义私有变量
		for (Class<?> interfaceClass : requiredClass.getInterfaces()) {
			clone(source, target, interfaceClass, cloner);
		}

		// 递归
		clone(source, target, requiredClass.getSuperclass(), cloner);
	}
}
