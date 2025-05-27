package run.soeasy.framework.core.invoke.reflect;

import java.lang.reflect.Array;
import java.util.EnumSet;

import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.lang.SupportedInstanceFactory;
import run.soeasy.framework.core.transform.property.ObjectPropertyAccessor;
import run.soeasy.framework.core.transform.property.PropertyMappingService;

public class ReflectionCloner extends PropertyMappingService<ObjectPropertyAccessor<ReflectionField>> {

	public ReflectionCloner() {
		getMappingRegistry().setMappingProvider(new ReflectionCloneMappingProvider());
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
		return (T) cloner.convert(source, TypeDescriptor.forObject(source));
	}
}
