package run.soeasy.framework.core.transform.object;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.execute.reflect.ReflectionField;
import run.soeasy.framework.core.type.ClassMembersLoader;
import run.soeasy.framework.core.type.ClassUtils;
import run.soeasy.framework.core.type.InstanceFactorySupporteds;
import run.soeasy.framework.core.type.MultiableInstanceFactory;
import run.soeasy.framework.core.type.ReflectionUtils;

public class Cloner extends ObjectMapper<ReflectionField> {
	// 用来防止死循环
	private static final ThreadLocal<IdentityHashMap<Object, Object>> IDENTITY_MAP_CONTEXT = new ThreadLocal<>();

	public Cloner() {
		setInstanceFactory(new MultiableInstanceFactory(InstanceFactorySupporteds.ALLOCATE,
				InstanceFactorySupporteds.SERIALIZATION));
	}

	@Override
	public ClassMembersLoader<ReflectionField> getClassPropertyTemplate(Class<?> requiredClass) {
		ClassMembersLoader<ReflectionField> classMembersLoader = super.getClassPropertyTemplate(requiredClass);
		if (classMembersLoader == null) {
			return new ClassMembersLoader<>(requiredClass, (clazz) -> {
				return ReflectionUtils.getDeclaredFields(clazz).filter((e) -> !Modifier.isStatic(e.getModifiers()))
						.map((field) -> new ReflectionField(field));
			}).withAll();// 这里使用全部的原因是最新版本的java可以在接口中定义私有变量
		}
		return classMembersLoader;
	}

	@Override
	public boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return sourceTypeDescriptor.isAssignableTo(targetTypeDescriptor);
	}

	@Override
	public final Object convert(Object source, TypeDescriptor sourceTypeDescriptor, TypeDescriptor targetTypeDescriptor)
			throws ConversionException {
		if (source == null) {
			return null;
		}

		IdentityHashMap<Object, Object> identityMap = IDENTITY_MAP_CONTEXT.get();
		boolean root = false;
		if (identityMap == null) {
			root = true;
			identityMap = new IdentityHashMap<>();
			identityMap.put(source, source);
			IDENTITY_MAP_CONTEXT.set(identityMap);
		}

		try {
			Object target = identityMap.get(source);
			if (target != null) {
				return target;
			}

			targetTypeDescriptor = targetTypeDescriptor.narrow(source);
			return convert(source, sourceTypeDescriptor, targetTypeDescriptor, identityMap);
		} finally {
			if (root) {
				IDENTITY_MAP_CONTEXT.remove();
			}
		}
	}

	public Object convert(@NonNull Object source, TypeDescriptor sourceTypeDescriptor,
			TypeDescriptor targetTypeDescriptor, IdentityHashMap<Object, Object> identityMap)
			throws ConversionException {
		if (ClassUtils.isPrimitiveOrWrapper(source.getClass())) {
			return source;
		}

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

		if (source.getClass().isArray()) {
			int len = Array.getLength(source);
			Object target = Array.newInstance(targetTypeDescriptor.getElementTypeDescriptor().getType(), len);
			transform(source, sourceTypeDescriptor, target, targetTypeDescriptor);
			return target;
		}
		return super.convert(source, sourceTypeDescriptor, targetTypeDescriptor);
	}

	@Override
	public boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return (sourceTypeDescriptor.isAssignableTo(targetTypeDescriptor)
				|| targetTypeDescriptor.isAssignableTo(sourceTypeDescriptor))
				&& super.canTransform(sourceTypeDescriptor, targetTypeDescriptor);
	}

	@Override
	public boolean transform(@NonNull Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull Object target, @NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
		if (source.getClass().isArray()) {
			int cloneSize = Math.min(Array.getLength(source), Array.getLength(target));
			for (int i = 0; i < cloneSize; i++) {
				Object sourceElement = Array.getLength(source);
				Object targetElement = getMapper().getConverter().convert(sourceElement,
						sourceTypeDescriptor.elementTypeDescriptor(sourceElement),
						targetTypeDescriptor.elementTypeDescriptor(sourceElement));
				Array.set(target, i, targetElement);
			}
			return cloneSize > 0;
		}
		return super.transform(source, sourceTypeDescriptor, target, targetTypeDescriptor);
	}

	/**
	 * 克隆
	 * 
	 * @param <T>
	 * @param source 来源
	 * @param deep   是否深拷贝
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T clone(@NonNull T source, boolean deep) {
		Cloner cloner = new Cloner();
		if (deep) {
			cloner.getMapper().setConverter(cloner);
		}
		return (T) cloner.convert(source, source.getClass());
	}
}
