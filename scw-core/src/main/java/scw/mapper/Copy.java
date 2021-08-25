package scw.mapper;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;

import scw.core.Assert;
import scw.core.ResolvableType;
import scw.core.reflect.ReflectionUtils;
import scw.env.Sys;
import scw.instance.NoArgsInstanceFactory;
import scw.util.Accept;
import scw.util.ConfigurableAccept;

@SuppressWarnings("unchecked")
public class Copy {
	private NoArgsInstanceFactory instanceFactory;
	private FieldFactory fieldFactory = MapperUtils.getFieldFactory();
	private final ConfigurableAccept<Field> filter = new ConfigurableAccept<Field>();

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

	public final FieldFactory getFieldFactory() {
		return fieldFactory;
	}

	public Copy setFieldFactory(FieldFactory fieldFactory) {
		this.fieldFactory = fieldFactory;
		return this;
	}

	/**
	 * 是否忽略静态字段
	 * 
	 * @return
	 */
	public boolean isIgnoreStaticField() {
		return ignoreStaticField;
	}

	public Copy setIgnoreStaticField(boolean ignoreStaticField) {
		this.ignoreStaticField = ignoreStaticField;
		return this;
	}

	public ConfigurableAccept<Field> getFilter() {
		return filter;
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
		return instanceFactory == null ? Sys.env : instanceFactory;
	}

	public Copy setInstanceFactory(NoArgsInstanceFactory instanceFactory) {
		this.instanceFactory = instanceFactory;
		return this;
	}

	/**
	 * 在里深拷贝时是否克隆transient修辞符字段
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

	protected <T> T cloneArray(Class<T> targetClass, Object sourceArray, Field sourceParentField) {
		int size = Array.getLength(sourceArray);
		Object newArr = Array.newInstance(targetClass.getComponentType(), size);
		for (int i = 0; i < size; i++) {
			Array.set(newArr, i, clone(Array.get(sourceArray, i), sourceParentField));
		}
		return (T) newArr;
	}

	/**
	 * 获取对应的数据来源字段
	 * 
	 * @param sourceClass 数据来源
	 * @param targetField 要插入的字段
	 * @return
	 */
	protected Field getSourceField(Fields sourceFields, final Field targetField) {
		return sourceFields.accept(new Accept<Field>() {

			public boolean accept(Field sourceField) {
				if (!sourceField.isSupportGetter()) {
					return false;
				}

				if (!targetField.getSetter().getName().equals(sourceField.getGetter().getName())) {
					return false;
				}
				
				ResolvableType targetType = ResolvableType.forType(targetField.getSetter().getGenericType());
				ResolvableType sourceType = ResolvableType.forType(sourceField.getGetter().getGenericType());
				if(!targetType.isAssignableFrom(sourceType)) {
					return false;
				}

				// 使用异或，只有两个都是静态或都不是静态时才通过
				// 异或 true ^ false = true true ^ true = false false ^ false = false
				return !(Modifier.isStatic(targetField.getSetter().getModifiers())
						^ Modifier.isStatic(sourceField.getGetter().getModifiers()));
			}
		}).first();
	}

	public <T, S> void copy(Fields targetFields, T target, Fields sourceFields, S source) {
		if (source == null) {
			return;
		}

		targetFields.accept(FieldFeature.SUPPORT_SETTER).accept(FieldFeature.EXISTING_SETTER_FIELD)
				.accept((targetField) -> {
					// 是否忽略静态字段
					if (isIgnoreStaticField() && Modifier.isStatic(targetField.getSetter().getModifiers())) {
						return false;
					}
					return true;
				}).accept(getFilter()).forEach((targetField) -> {
					Field sourceField = getSourceField(sourceFields, targetField);
					if (sourceField == null) {
						return;
					}

					Object value = sourceField.getGetter().get(source);
					if (value == null) {
						return;
					}

					value = copyValue(targetField, value);
					setValue(sourceField, targetField, target, value);
				});
	}

	public <T, S> void copy(Class<? extends T> targetClass, T target, Field targetParentField,
			Class<? extends S> sourceClass, S source, Field sourceParentField) {
		if (source == null) {
			return;
		}

		copy(getFieldFactory().getFields(targetClass, targetParentField).all(), target,
				getFieldFactory().getFields(sourceClass, sourceParentField).all(), source);
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

	public <T, S> T copy(Class<? extends T> targetClass, Field targetParentField, Class<? extends S> sourceClass,
			S source, Field sourceParentField) {
		if (source == null) {
			return null;
		}

		if (targetClass.isArray() && sourceClass.isArray()) {
			return cloneArray(targetClass, source, sourceParentField);
		}

		if (!getInstanceFactory().isInstance(targetClass)) {
			// 如果无法实例化
			if (targetClass.isInstance(source)) {
				return targetClass.cast(source);
			}
			throw new IllegalStateException("Unable to copy " + sourceClass + " -> " + targetClass);
		}

		T target = getInstanceFactory().getInstance(targetClass);
		copy(targetClass, target, targetParentField, sourceClass, source, sourceParentField);
		return target;
	}

	public <T> T clone(T source, Field parentField) {
		if (source == null) {
			return null;
		}

		return (T) clone(source.getClass(), source, parentField);
	}

	public <T> T clone(Class<? extends T> sourceClass, T source, Field parentField) {
		if (source == null) {
			return null;
		}

		if (sourceClass.isPrimitive() || sourceClass.isEnum() || !getInstanceFactory().isInstance(sourceClass)) {
			return source;
		}

		if (sourceClass.isArray()) {
			return (T) cloneArray(sourceClass, source, parentField);
		}

		if (isInvokeCloneableMethod() && source instanceof Cloneable) {
			return ReflectionUtils.clone((Cloneable) source);
		}

		Object target = getInstanceFactory().getInstance(sourceClass);
		getFieldFactory().getFields(sourceClass, parentField).accept(getFilter()).streamAll().forEach((field) -> {
			if (!(field.isSupportGetter() && field.isSupportSetter() && field.getGetter().getField() != null
					&& field.getSetter().getField() != null)) {
				return;
			}

			// 是否忽略静态字段
			if (isIgnoreStaticField() && Modifier.isStatic(field.getSetter().getModifiers())) {
				return;
			}

			Object value = field.getGetter().get(source);
			if (value == null) {
				return;
			}

			value = copyValue(field, value);
			setValue(field, field, target, value);
		});
		return (T) target;
	}

	protected void setValue(Field sourceField, Field targetField, Object target, Object value) {
		targetField.getSetter().set(target, value);
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
		Assert.requiredArgument(targetClass != null, "targetClass");
		Assert.requiredArgument(source != null, "source");
		return DEFAULT_COPY.copy(targetClass, null, source.getClass(), source, null);
	}

	/**
	 * 推荐使用此方法
	 * 
	 * @param target
	 * @param source
	 */
	public static void copy(Object target, Object source) {
		Assert.requiredArgument(target != null, "target");
		Assert.requiredArgument(source != null, "source");
		DEFAULT_COPY.copy(target.getClass(), target, null, source.getClass(), source, null);
	}
}
