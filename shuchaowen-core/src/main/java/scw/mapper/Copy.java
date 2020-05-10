package scw.mapper;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import scw.core.instance.InstanceUtils;
import scw.core.instance.NoArgsInstanceFactory;
import scw.core.reflect.ReflectionUtils;

@SuppressWarnings("unchecked")
public class Copy {
	static final Method CLONE_METOHD = ReflectionUtils.getMethod(Object.class, "clone");
	private NoArgsInstanceFactory instanceFactory = InstanceUtils.INSTANCE_FACTORY;
	private Mapper mapper = MapperUtils.getMapper();
	/**
	 * 如果对象实现了java.lang.Cloneable 接口，是否反射调用clone方法
	 */
	private boolean invokeCloneableMethod = true;
	/**
	 * 默认不克隆transient修辞符字段
	 */
	private boolean cloneTransientField = false;

	/**
	 * 是否使用clone方式复制
	 */
	private boolean clone = false;

	public final boolean isClone() {
		return clone;
	}

	public void setClone(boolean clone) {
		this.clone = clone;
	}

	public final Mapper getMapper() {
		return mapper;
	}

	public void setMapper(Mapper mapper) {
		this.mapper = mapper;
	}

	public final boolean isInvokeCloneableMethod() {
		return invokeCloneableMethod;
	}

	public void setInvokeCloneableMethod(boolean invokeCloneableMethod) {
		this.invokeCloneableMethod = invokeCloneableMethod;
	}

	public final NoArgsInstanceFactory getInstanceFactory() {
		return instanceFactory;
	}

	public void setInstanceFactory(NoArgsInstanceFactory instanceFactory) {
		this.instanceFactory = instanceFactory;
	}

	public boolean isCloneTransientField() {
		return cloneTransientField;
	}

	public void setCloneTransientField(boolean cloneTransientField) {
		this.cloneTransientField = cloneTransientField;
	}

	protected Object cloneArray(Class<?> sourceClass, Object array, Field parentField,
			FieldFilter filter, FilterFeature... filterFeatures) {
		int size = Array.getLength(array);
		Object newArr = Array.newInstance(sourceClass.getComponentType(), size);
		for (int i = 0; i < size; i++) {
			Array.set(newArr, i, copy(Array.get(array, i), parentField, filter, filterFeatures));
		}
		return newArr;
	}

	protected Field getSourceField(Class<?> sourceClass, final Field targetField) {
		if (!targetField.isSupportGetter()) {
			return null;
		}

		return mapper.getField(sourceClass, new FieldFilter() {

			public boolean accept(Field field) {
				if (!field.isSupportSetter()) {
					return false;
				}

				if (targetField.getGetter().getGenericType() != field.getSetter()
						.getGenericType()) {
					return false;
				}

				if (!targetField.getGetter().getName()
						.equals(field.getSetter().getName())) {
					return false;
				}

				if (Modifier.isStatic(targetField.getGetter().getModifiers())) {
					return Modifier.isStatic(field.getSetter().getModifiers());
				} else {
					return !Modifier.isStatic(field.getSetter().getModifiers());
				}
			}
		});
	}

	public <T, S> void copy(Class<? extends T> targetClass, T target, Class<? extends S> sourceClass, S source,
			Field parentField, FieldFilter filter, FilterFeature... filterFeatures) {
		for (Field field : mapper.getFields(targetClass, parentField, filter,
				filterFeatures)) {
			if (!field.isSupportSetter()) {
				continue;
			}

			Field sourceField = getSourceField(sourceClass, field);
			if (sourceField == null) {
				continue;
			}

			if (!sourceField.isSupportGetter()) {
				continue;
			}

			Object value = sourceField.getGetter().get(source);
			if (value == null) {
				continue;
			}

			if (isClone()) {
				if (Modifier.isTransient(field.getSetter().getModifiers())) {
					if (isCloneTransientField()) {
						value = copy(value, field, filter, filterFeatures);
					}
				} else {
					value = copy(value, field, filter, filterFeatures);
				}
			}

			field.getSetter().set(target, value);
		}
	}

	public <T, S> T copy(Class<? extends T> targetClass, Class<? extends S> sourceClass, S source,
			Field parentField, FieldFilter filter, FilterFeature... filterFeatures) {
		if (!getInstanceFactory().isInstance(targetClass)) {
			return (T) source;
		}

		T target = getInstanceFactory().getInstance(targetClass);
		if (getInstanceFactory().isSingleton(targetClass)) {
			return target;
		}

		copy(targetClass, target, sourceClass, source, parentField, filter, filterFeatures);
		return target;
	}

	public <T> T copy(T source, Field parentField, FieldFilter filter,
			FilterFeature... filterFeatures) {
		if (source == null) {
			return null;
		}

		if (source instanceof scw.mapper.Cloneable) {
			return (T) ((scw.mapper.Cloneable) source).clone();
		}

		Class<T> sourceClass = (Class<T>) source.getClass();
		if (getInstanceFactory().isSingleton(sourceClass)) {
			return source;
		}

		if (sourceClass.isPrimitive() || sourceClass.isEnum()) {
			return source;
		} else if (sourceClass.isArray()) {
			return (T) cloneArray(sourceClass, source, parentField, filter, filterFeatures);
		} else if (isInvokeCloneableMethod() && source instanceof Cloneable) {
			return (T) ReflectionUtils.invokeMethod(CLONE_METOHD, source);
		}
		return copy(sourceClass, sourceClass, source, parentField, filter, filterFeatures);
	}

	private static final Copy DEFAULT_COPY = new Copy();
	private static final Copy CLONE_COPY = new Copy();
	private static final Copy INVOKER_SETTER_COPY = new Copy();

	static {
		CLONE_COPY.setClone(true);
	}

	public static <T> T clone(T source, FieldFilter filter, FilterFeature... filterFeatures) {
		return CLONE_COPY.copy(source, null, filter, filterFeatures);
	}

	public static <T> T clone(T source) {
		return clone(source, null, FilterFeature.GETTER_IGNORE_STATIC, FilterFeature.SETTER_IGNORE_STATIC);
	}

	public static <T> T copy(Class<? extends T> targetClass, Object source, FieldFilter filter,
			FilterFeature... filterFeatures) {
		return DEFAULT_COPY.copy(targetClass, source.getClass(), source, null, filter, filterFeatures);
	}

	public static <T> T copy(Class<? extends T> targetClass, Object source) {
		return copy(targetClass, source, null, FilterFeature.GETTER_IGNORE_STATIC, FilterFeature.SETTER_IGNORE_STATIC);
	}

	public static void copy(Object target, Object source, FieldFilter filter,
			FilterFeature... filterFeatures) {
		INVOKER_SETTER_COPY.copy(target.getClass(), target, source.getClass(), source, null, filter, filterFeatures);
	}

	/**
	 * 推荐使用此方法
	 * 
	 * @param target
	 * @param source
	 */
	public static void copy(Object target, Object source) {
		copy(target, source, null, FilterFeature.GETTER_IGNORE_STATIC, FilterFeature.SETTER_IGNORE_STATIC);
	}
}
