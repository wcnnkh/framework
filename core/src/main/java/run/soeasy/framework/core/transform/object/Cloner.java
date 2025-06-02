package run.soeasy.framework.core.transform.object;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.invoke.reflect.ReflectionField;
import run.soeasy.framework.core.type.ClassMembersLoader;
import run.soeasy.framework.core.type.ReflectionUtils;

public class Cloner extends ObjectMapper<ReflectionField> {

	public boolean canClone(TypeDescriptor typeDescriptor) {
		return canInstantiated(typeDescriptor.getResolvableType()) || String.class == typeDescriptor.getType();
	}

	@Override
	public ClassMembersLoader<ReflectionField> getClassPropertyTemplate(Class<?> requiredClass) {
		ClassMembersLoader<ReflectionField> classMembersLoader = super.getClassPropertyTemplate(requiredClass);
		if (classMembersLoader == null && canClone(TypeDescriptor.valueOf(requiredClass))) {
			return new ClassMembersLoader<>(requiredClass, (clazz) -> {
				return ReflectionUtils.getDeclaredFields(clazz).filter((e) -> !Modifier.isStatic(e.getModifiers()))
						.map((field) -> new ReflectionField(field));
			}).withAll();
		}
		return classMembersLoader;
	}

	@Override
	public boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return sourceTypeDescriptor.getType() == targetTypeDescriptor.getType()
				&& super.canTransform(sourceTypeDescriptor, targetTypeDescriptor);
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceTypeDescriptor, TypeDescriptor targetTypeDescriptor)
			throws ConversionException {
		if (source instanceof byte[]) {
			return ((byte[]) source).clone();
		} else if (source instanceof short[]) {
			return ((short[]) source).clone();
		} else if (source instanceof int[]) {
			return ((int[]) source).clone();
		} else if (source instanceof long[]) {
			return ((long[]) source).clone();
		} else if (source instanceof char[]) {
			return ((char[]) source).clone();
		} else if (source instanceof float[]) {
			return ((float[]) source).clone();
		} else if (source instanceof double[]) {
			return ((double[]) source).clone();
		} else if (source instanceof boolean[]) {
			return ((boolean[]) source).clone();
		}

		if (sourceTypeDescriptor.isArray()) {
			int len = Array.getLength(source);
			Object target = Array.newInstance(targetTypeDescriptor.getElementTypeDescriptor().getType(), len);
			transform(source, sourceTypeDescriptor, target, targetTypeDescriptor);
			return target;
		}

		return canClone(targetTypeDescriptor) ? super.convert(source, sourceTypeDescriptor, targetTypeDescriptor)
				: source;
	}

	@Override
	public boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return sourceTypeDescriptor.getType() == targetTypeDescriptor.getType()
				&& super.canTransform(sourceTypeDescriptor, targetTypeDescriptor);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean transform(@NonNull Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull Object target, @NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
		if (sourceTypeDescriptor.isArray()) {
			TypeDescriptor sourceElementTypeDescriptor = sourceTypeDescriptor.getElementTypeDescriptor();
			TypeDescriptor targetElementTypeDescriptor = targetTypeDescriptor.getElementTypeDescriptor();
			int cloneSize = Math.min(Array.getLength(source), Array.getLength(target));
			for (int i = 0; i < cloneSize; i++) {
				Object sourceElement = Array.getLength(source);
				Object targetElement = getMapper().getConversionService().convert(sourceElement,
						sourceElementTypeDescriptor, targetElementTypeDescriptor);
				Array.set(target, i, targetElement);
			}
			return cloneSize > 0;
		}

		if (sourceTypeDescriptor.isMap() && sourceTypeDescriptor.getName().startsWith("java.")) {
			TypeDescriptor sourceKeyTypeDescriptor = sourceTypeDescriptor.getMapKeyTypeDescriptor();
			TypeDescriptor targetKeyTypeDescriptor = targetTypeDescriptor.getMapKeyTypeDescriptor();
			TypeDescriptor sourceValueTypeDescriptor = sourceTypeDescriptor.getMapValueTypeDescriptor();
			TypeDescriptor targetValueTypeDescriptor = targetTypeDescriptor.getMapValueTypeDescriptor();
			Map<Object, Object> sourceMap = (Map<Object, Object>) source;
			Map<Object, Object> targetMap = (Map<Object, Object>) target;
			for (Entry<Object, Object> entry : sourceMap.entrySet()) {
				Object key = getMapper().getConversionService().convert(entry.getKey(), sourceKeyTypeDescriptor,
						targetKeyTypeDescriptor);
				Object value = getMapper().getConversionService().convert(entry.getValue(), sourceValueTypeDescriptor,
						targetValueTypeDescriptor);
				targetMap.put(key, value);
			}
			return !sourceMap.isEmpty();
		}

		if (sourceTypeDescriptor.isCollection()) {
			TypeDescriptor sourceElementTypeDescriptor = sourceTypeDescriptor.getElementTypeDescriptor();
			TypeDescriptor targetElementTypeDescriptor = targetTypeDescriptor.getElementTypeDescriptor();
			Collection<Object> sourceCollection = (Collection<Object>) source;
			Collection<Object> targetCollection = (Collection<Object>) target;
			for (Object sourceElement : sourceCollection) {
				Object targetElement = getMapper().getConversionService().convert(sourceElement,
						sourceElementTypeDescriptor, targetElementTypeDescriptor);
				targetCollection.add(targetElement);
			}
			return !sourceCollection.isEmpty();
		}
		return super.transform(source, sourceTypeDescriptor, target, targetTypeDescriptor);
	}

	@SuppressWarnings("unchecked")
	public static <T> T clone(@NonNull T source, boolean deep) {
		Cloner cloner = new Cloner();
		if (deep) {
			cloner.getMapper().setConversionService(cloner);
		}
		return (T) cloner.convert(source, source.getClass());
	}
}
