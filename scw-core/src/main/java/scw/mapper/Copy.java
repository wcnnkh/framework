package scw.mapper;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import scw.core.instance.InstanceUtils;
import scw.core.instance.NoArgsInstanceFactory;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ArrayUtils;

@SuppressWarnings("unchecked")
public class Copy {
	static final Method CLONE_METOHD = ReflectionUtils.getMethod(Object.class, "clone");
	private NoArgsInstanceFactory instanceFactory = InstanceUtils.INSTANCE_FACTORY;
	private Mapper mapper = MapperUtils.getMapper();
	private FieldFilter[] filters;

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
	 * 是否使用clone方式复制
	 */
	private boolean clone = false;

	public final boolean isClone() {
		return clone;
	}

	public Copy setClone(boolean clone) {
		this.clone = clone;
		return this;
	}

	public final Mapper getMapper() {
		return mapper;
	}

	public Copy setMapper(Mapper mapper) {
		this.mapper = mapper;
		return this;
	}

	public final boolean isIgnoreStaticField() {
		return ignoreStaticField;
	}

	public Copy setIgnoreStaticField(boolean ignoreStaticField) {
		this.ignoreStaticField = ignoreStaticField;
		return this;
	}

	public final FieldFilter[] getFilters() {
		return filters == null ? new FieldFilter[0] : filters.clone();
	}

	public Copy setFilters(FieldFilter... filters) {
		this.filters = filters;
		return this;
	}

	public Copy addFilters(FieldFilter... filters) {
		this.filters = ArrayUtils.merge(this.filters, filters);
		return this;
	}

	/**
	 * 如果对象实现了java.lang.Cloneable 接口，是否反射调用clone方法
	 * 
	 * @return
	 */
	public final boolean isInvokeCloneableMethod() {
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

	protected Object cloneArray(Class<?> sourceClass, Object array, Field parentField) {
		int size = Array.getLength(array);
		Object newArr = Array.newInstance(sourceClass.getComponentType(), size);
		for (int i = 0; i < size; i++) {
			Array.set(newArr, i, copy(Array.get(array, i), parentField));
		}
		return newArr;
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

			if (isClone()) {
				if (Modifier.isTransient(field.getSetter().getModifiers())) {
					if (isCloneTransientField()) {
						value = copy(value, field);
					}
				} else {
					value = copy(value, field);
				}
			}

			field.getSetter().set(target, value);
		}
	}

	public <T, S> T copy(Class<? extends T> targetClass, Class<? extends S> sourceClass, S source, Field parentField) {
		if (!getInstanceFactory().isInstance(targetClass)) {
			return (T) source;
		}

		T target = getInstanceFactory().getInstance(targetClass);
		if (getInstanceFactory().isSingleton(targetClass)) {
			return target;
		}

		copy(targetClass, target, sourceClass, source, parentField);
		return target;
	}

	public <T> T copy(T source, Field parentField) {
		if (source == null) {
			return null;
		}

		Class<T> sourceClass = (Class<T>) source.getClass();
		if (getInstanceFactory().isSingleton(sourceClass)) {
			return source;
		}

		if (sourceClass.isPrimitive() || sourceClass.isEnum()) {
			return source;
		} else if (sourceClass.isArray()) {
			return (T) cloneArray(sourceClass, source, parentField);
		} else if (isInvokeCloneableMethod() && source instanceof Cloneable) {
			return (T) ReflectionUtils.invokeMethod(CLONE_METOHD, source);
		}
		return copy(sourceClass, sourceClass, source, parentField);
	}

	private static final Copy DEFAULT_COPY = new Copy();
	private static final Copy CLONE_COPY = new Copy();

	static {
		CLONE_COPY.setClone(true);
	}

	public static <T> T clone(T source) {
		return CLONE_COPY.copy(source, null);
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
