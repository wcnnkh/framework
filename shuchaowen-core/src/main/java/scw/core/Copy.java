package scw.core;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import scw.core.instance.InstanceUtils;
import scw.core.instance.NoArgsInstanceFactory;
import scw.core.reflect.ReflectionUtils;

@SuppressWarnings("unchecked")
public class Copy {
	static final Method CLONE_METOHD = ReflectionUtils.getMethod(Object.class, "clone");
	private NoArgsInstanceFactory instanceFactory = InstanceUtils.INSTANCE_FACTORY;
	private boolean ignoreStaticField = true;
	private boolean ignoreTransient = false;
	private boolean invokerSetter = false;
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

	public boolean isClone() {
		return clone;
	}

	public void setClone(boolean clone) {
		this.clone = clone;
	}

	public boolean isInvokerSetter() {
		return invokerSetter;
	}

	public void setInvokerSetter(boolean invokerSetter) {
		this.invokerSetter = invokerSetter;
	}

	public boolean isInvokeCloneableMethod() {
		return invokeCloneableMethod;
	}

	public void setInvokeCloneableMethod(boolean invokeCloneableMethod) {
		this.invokeCloneableMethod = invokeCloneableMethod;
	}

	public NoArgsInstanceFactory getInstanceFactory() {
		return instanceFactory;
	}

	public void setInstanceFactory(NoArgsInstanceFactory instanceFactory) {
		this.instanceFactory = instanceFactory;
	}

	public boolean isIgnoreStaticField() {
		return ignoreStaticField;
	}

	public void setIgnoreStaticField(boolean ignoreStaticField) {
		this.ignoreStaticField = ignoreStaticField;
	}

	public boolean isCloneTransientField() {
		return cloneTransientField;
	}

	public void setCloneTransientField(boolean cloneTransientField) {
		this.cloneTransientField = cloneTransientField;
	}

	public boolean isIgnoreTransient() {
		return ignoreTransient;
	}

	public void setIgnoreTransient(boolean ignoreTransient) {
		this.ignoreTransient = ignoreTransient;
	}

	protected Field[] getFields(Class<?> clazz) {
		return clazz.getDeclaredFields();
	}

	protected Object cloneArray(Class<?> sourceType, Object array) throws Exception {
		int size = Array.getLength(array);
		Object newArr = Array.newInstance(sourceType.getComponentType(), size);
		for (int i = 0; i < size; i++) {
			Array.set(newArr, i, clone(Array.get(array, i)));
		}
		return newArr;
	}
	
	protected void copy(Class<?> targetClass, Field targetField, Object source, Object target) throws Exception {
		if (isIgnoreStaticField() && Modifier.isStatic(targetField.getModifiers())) {
			return;
		}

		if (isIgnoreTransient() && Modifier.isTransient(targetField.getModifiers())) {
			return;
		}

		Field sourceField = ReflectionUtils.findField(source.getClass(), targetField.getName());
		if (sourceField == null) {
			return;
		}

		ReflectionUtils.setAccessibleField(sourceField);
		Object value = sourceField.get(source);
		if (value == null) {
			return;
		}

		if (isClone()) {
			if (Modifier.isTransient(targetField.getModifiers())) {
				if (isCloneTransientField()) {
					value = clone(value);
				}
			} else {
				value = clone(value);
			}
		}

		ReflectionUtils.setAccessibleField(targetField);
		if (isInvokerSetter()) {
			ReflectionUtils.setFieldValue(targetClass, targetField, target, value);
		} else {
			targetField.set(target, value);
		}
	}

	public <T> void copy(Class<? extends T> targetClass, T target, Object source) throws Exception {
		Class<?> clazz = targetClass;
		while (clazz != null && clazz != Object.class) {
			for (Field field : getFields(clazz)) {
				copy(targetClass, field, source, target);
			}
			clazz = clazz.getSuperclass();
		}
	}

	public <T> T copy(Class<? extends T> targetClass, Object source) throws Exception {
		if (!getInstanceFactory().isInstance(targetClass)) {
			return (T) source;
		}

		T target = getInstanceFactory().getInstance(targetClass);
		if (getInstanceFactory().isSingleton(targetClass)) {
			return target;
		}

		copy(targetClass, target, source);
		return target;
	}

	public <T> T clone(T source) throws Exception {
		if (source == null) {
			return null;
		}

		if (source instanceof scw.lang.Cloneable) {
			return (T) ((scw.lang.Cloneable) source).clone();
		}

		Class<T> sourceClass = (Class<T>) source.getClass();
		if (getInstanceFactory().isSingleton(sourceClass)) {
			return source;
		}

		if (sourceClass.isPrimitive() || sourceClass.isEnum()) {
			return source;
		} else if (sourceClass.isArray()) {
			return (T) cloneArray(sourceClass, source);
		} else if (isInvokeCloneableMethod() && source instanceof Cloneable) {
			return (T) CLONE_METOHD.invoke(source);
		}
		return copy(sourceClass, source);
	}
}
