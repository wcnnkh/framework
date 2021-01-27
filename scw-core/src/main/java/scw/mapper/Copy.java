package scw.mapper;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import scw.core.reflect.ReflectionUtils;
import scw.instance.InstanceUtils;
import scw.instance.NoArgsInstanceFactory;

@SuppressWarnings("unchecked")
public class Copy {
	static final Method CLONE_METOHD = ReflectionUtils.getMethod(Object.class, "clone");
	private NoArgsInstanceFactory instanceFactory = InstanceUtils.INSTANCE_FACTORY;
	private Mapper mapper = MapperUtils.getMapper();
	private final EditableFieldFilters filters = new EditableFieldFilters();

	/**
	 * 如果对象实现了java.lang.Cloneable 接口，是否反射调用clone方法
	 */
	private boolean invokeCloneableMethod = true;
	/**
	 * 当使用克隆的时候默认不克隆transient修辞符字段
	 */
	private boolean cloneTransientField = false;

	/**
	 * 默认忽略静态字段
	 */
	private boolean ignoreStaticField = true;

	/**
	 * 是否要求字段泛型相同
	 */
	private boolean genericTypeEqual = true;

	/**
	 * 是否深拷贝
	 */
	private boolean deepCopy = false;

	public boolean isDeepCopy() {
		return deepCopy;
	}

	public Copy setDeepCopy(boolean deepCopy) {
		this.deepCopy = deepCopy;
		return this;
	}

	public final Mapper getMapper() {
		return mapper;
	}

	public Copy setMapper(Mapper mapper) {
		this.mapper = mapper;
		return this;
	}

	public boolean isIgnoreStaticField() {
		return ignoreStaticField;
	}

	public Copy setIgnoreStaticField(boolean ignoreStaticField) {
		this.ignoreStaticField = ignoreStaticField;
		return this;
	}

	public final EditableFieldFilters getFilters() {
		return filters;
	}

	/**
	 * 如果对象实现了java.lang.Cloneable 接口，是否反射调用clone方法
	 * 
	 * @return
	 */
	public boolean isInvokeCloneableMethod() {
		return invokeCloneableMethod;
	}

	/**
	 * 如果对象实现了java.lang.Cloneable 接口，是否反射调用clone方法
	 * 
	 * @param invokeCloneableMethod
	 */
	public Copy setInvokeCloneableMethod(boolean invokeCloneableMethod) {
		this.invokeCloneableMethod = invokeCloneableMethod;
		return this;
	}

	public final NoArgsInstanceFactory getInstanceFactory() {
		return instanceFactory;
	}

	public Copy setInstanceFactory(NoArgsInstanceFactory instanceFactory) {
		this.instanceFactory = instanceFactory;
		return this;
	}

	/**
	 * 克隆transient修辞符字段
	 * 
	 * @return
	 */
	public boolean isCloneTransientField() {
		return cloneTransientField;
	}

	/**
	 * 克隆transient修辞符字段
	 * 
	 * @param cloneTransientField
	 */
	public Copy setCloneTransientField(boolean cloneTransientField) {
		this.cloneTransientField = cloneTransientField;
		return this;
	}

	/**
	 * 是否要求字段泛型相同
	 * 
	 * @return
	 */
	public boolean isGenericTypeEqual() {
		return genericTypeEqual;
	}

	/**
	 * 是否要求字段泛型相同
	 * 
	 * @param genericTypeEqual
	 */
	public Copy setGenericTypeEqual(boolean genericTypeEqual) {
		this.genericTypeEqual = genericTypeEqual;
		return this;
	}

	protected <T> T cloneArray(Class<T> targetClass, Object array, Field parentField) {
		int size = Array.getLength(array);
		Object newArr = Array.newInstance(targetClass.getComponentType(), size);
		for (int i = 0; i < size; i++) {
			Array.set(newArr, i, clone(Array.get(array, i), parentField));
		}
		return (T) newArr;
	}

	/**
	 * 获取对应的数据来源字段
	 * 
	 * @param sourceClass
	 *            数据来源
	 * @param targetField
	 *            要插入的字段
	 * @return
	 */
	protected Field getSourceField(Class<?> sourceClass, Fields sourceFields, final Field targetField) {
		return sourceFields.find(new FieldFilter() {

			public boolean accept(Field field) {
				if (!field.isSupportGetter()) {
					return false;
				}

				if (!targetField.getSetter().getName().equals(field.getGetter().getName())) {
					return false;
				}

				if (isGenericTypeEqual()) {
					if (!targetField.getSetter().getGenericType().equals(field.getGetter().getGenericType())) {
						return false;
					}
				} else {
					if (!targetField.getSetter().getType().equals(field.getGetter().getType())) {
						return false;
					}
				}

				// 使用异或，只有两个都是静态或都不是静态时才通过
				return !(Modifier.isStatic(targetField.getSetter().getModifiers())
						^ Modifier.isStatic(field.getGetter().getModifiers()));
			}
		});
	}

	public <T, S> void copy(Class<? extends T> targetClass, T target, Class<? extends S> sourceClass, S source,
			Field parentField) {
		if (source == null) {
			return;
		}

		Fields sourceFields = mapper.getFields(sourceClass, parentField, filters);
		for (Field field : mapper.getFields(targetClass, parentField, filters)) {
			if (!field.isSupportSetter()) {
				continue;
			}

			/**
			 * 目标字段应该存在实际的java.lang.Field
			 */
			if (field.getSetter().getField() == null) {
				continue;
			}

			// 是否忽略静态字段
			if (isIgnoreStaticField() && Modifier.isStatic(field.getSetter().getModifiers())) {
				continue;
			}

			Field sourceField = getSourceField(sourceClass, sourceFields, field);
			if (sourceField == null) {
				continue;
			}

			Object value = sourceField.getGetter().get(source);
			if (value == null) {
				continue;
			}

			value = copyValue(field, value);
			field.getSetter().set(target, value);
		}
	}

	private Object copyValue(Field field, Object value) {
		Object valueToUse = value;
		if (isDeepCopy()) {
			if (Modifier.isTransient(field.getSetter().getModifiers())) {
				if (isCloneTransientField()) {
					valueToUse = clone(valueToUse, field);
				}
			} else {
				valueToUse = clone(valueToUse, field);
			}
		}
		return valueToUse;
	}

	public <T, S> T copy(Class<? extends T> targetClass, Class<? extends S> sourceClass, S source, Field parentField) {
		if (source == null) {
			return null;
		}

		if (targetClass.isArray() && sourceClass.isArray()) {
			return cloneArray(targetClass, source, parentField);
		}

		if (!getInstanceFactory().isInstance(targetClass)) {
			// 如果无法实例化
			if (targetClass.isInstance(source)) {
				return targetClass.cast(source);
			}
			throw new RuntimeException("Unable to copy " + sourceClass + " -> " + targetClass);
		}

		T target = getInstanceFactory().getInstance(targetClass);
		copy(targetClass, target, sourceClass, source, parentField);
		return target;
	}

	public <T> T clone(T source, Field parentField) {
		if (source == null) {
			return null;
		}

		Class<?> sourceClass = source.getClass();
		if (sourceClass.isPrimitive() || sourceClass.isEnum()
				|| !getInstanceFactory().isInstance(sourceClass)) {
			return source;
		}

		if (sourceClass.isArray()) {
			return (T) cloneArray(sourceClass, source, parentField);
		}

		if (isInvokeCloneableMethod() && source instanceof Cloneable) {
			return (T) ReflectionUtils.invokeMethod(CLONE_METOHD, source);
		}

		Object target = getInstanceFactory().getInstance(sourceClass);
		while (sourceClass != null && sourceClass != Object.class) {
			Fields fields = mapper.getFields(sourceClass, false, parentField, filters);
			for (Field field : fields) {
				if (!(field.isSupportGetter() && field.isSupportSetter() && field.getGetter().getField() != null
						&& field.getSetter().getField() != null)) {
					continue;
				}

				// 是否忽略静态字段
				if (isIgnoreStaticField() && Modifier.isStatic(field.getSetter().getModifiers())) {
					continue;
				}

				Object value = field.getGetter().get(source);
				if (value == null) {
					continue;
				}

				value = copyValue(field, value);
				field.getSetter().set(target, value);
			}
			sourceClass = sourceClass.getSuperclass();
		}
		return (T) target;
	}

	private static final Copy DEFAULT_COPY = new Copy();
	private static final Copy CLONE_COPY = new Copy();

	static {
		CLONE_COPY.setDeepCopy(true);
	}

	/**
	 * 深拷贝
	 * 
	 * @param source
	 * @return
	 */
	public static <T> T clone(T source) {
		return CLONE_COPY.clone(source, null);
	}

	public static <T> T copy(Class<? extends T> targetClass, Object source) {
		return DEFAULT_COPY.copy(targetClass, source.getClass(), source, null);
	}

	/**
	 * 推荐使用此方法
	 * 
	 * @param target
	 * @param source
	 */
	public static void copy(Object target, Object source) {
		DEFAULT_COPY.copy(target.getClass(), target, source.getClass(), source, null);
	}
}
