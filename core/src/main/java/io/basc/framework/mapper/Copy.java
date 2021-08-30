package io.basc.framework.mapper;

import io.basc.framework.core.ResolvableType;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.env.Sys;
import io.basc.framework.instance.NoArgsInstanceFactory;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Accept;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ConfigurableAccept;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.Comparator;

@SuppressWarnings("unchecked")
public class Copy {
	public static final Comparator<FieldDescriptor> COMPARATOR = new Comparator<FieldDescriptor>() {

		@Override
		public int compare(FieldDescriptor target, FieldDescriptor source) {
			if (!target.getName().equals(source.getName())) {
				return -1;
			}

			ResolvableType targetType = ResolvableType.forType(target.getGenericType());
			ResolvableType sourceType = ResolvableType.forType(source.getGenericType());
			return targetType.isAssignableFrom(sourceType) ? 0 : -1;
		}
	};

	private NoArgsInstanceFactory instanceFactory;
	private FieldFactory fieldFactory = MapperUtils.getFieldFactory();
	private final ConfigurableAccept<Field> filter = new ConfigurableAccept<Field>();
	private Comparator<FieldDescriptor> fieldMatcher;

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

	/**
	 * 是否校验字段的Modifiers
	 */
	private boolean checkModifiers = true;

	public Comparator<FieldDescriptor> getFieldMatcher() {
		return fieldMatcher == null ? COMPARATOR : fieldMatcher;
	}

	public void setFieldMatcher(Comparator<FieldDescriptor> fieldMatcher) {
		this.fieldMatcher = fieldMatcher;
	}

	/**
	 * 是否校验字段的Modifiers
	 * 
	 * @return
	 */
	public final boolean isCheckModifiers() {
		return checkModifiers;
	}

	/**
	 * 是否校验字段的Modifiers
	 * 
	 * @param checkModifiers
	 */
	public void setCheckModifiers(boolean checkModifiers) {
		this.checkModifiers = checkModifiers;
	}

	public final boolean isDeepCopy() {
		return deepCopy;
	}

	public void setDeepCopy(boolean deepCopy) {
		this.deepCopy = deepCopy;
	}

	public final FieldFactory getFieldFactory() {
		return fieldFactory;
	}

	public void setFieldFactory(FieldFactory fieldFactory) {
		this.fieldFactory = fieldFactory;
	}

	/**
	 * 是否忽略静态字段
	 * 
	 * @return
	 */
	public final boolean isIgnoreStaticField() {
		return ignoreStaticField;
	}

	public void setIgnoreStaticField(boolean ignoreStaticField) {
		this.ignoreStaticField = ignoreStaticField;
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
	public void setInvokeCloneableMethod(boolean invokeCloneableMethod) {
		this.invokeCloneableMethod = invokeCloneableMethod;
	}

	public final NoArgsInstanceFactory getInstanceFactory() {
		return instanceFactory == null ? Sys.env : instanceFactory;
	}

	public void setInstanceFactory(NoArgsInstanceFactory instanceFactory) {
		this.instanceFactory = instanceFactory;
	}

	/**
	 * 在里深拷贝时是否克隆transient修辞符字段
	 * 
	 * @return
	 */
	public final boolean isCloneTransientField() {
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

	private Field getSourceField(Fields sourceFields, final Field targetField,
			Comparator<FieldDescriptor> fieldMatcher) {
		return sourceFields.accept(new Accept<Field>() {

			public boolean accept(Field sourceField) {
				if (!sourceField.isSupportGetter()) {
					return false;
				}

				// 使用异或，只有两个都是静态或都不是静态时才通过
				// 异或 true ^ false = true true ^ true = false false ^ false = false
				if (checkModifiers && (Modifier.isStatic(targetField.getSetter().getModifiers())
						^ Modifier.isStatic(sourceField.getGetter().getModifiers()))) {
					return false;
				}
				return fieldMatcher.compare(targetField.getSetter(), sourceField.getGetter()) == 0;
			}
		}).first();
	}

	/**
	 * 拷贝
	 * 
	 * @param <T>
	 * @param <S>
	 * @param targetFields 目标字段
	 * @param target       目标对象
	 * @param sourceFields 来源字段
	 * @param source       来源
	 */
	public <T, S> void copy(Fields targetFields, T target, Fields sourceFields, S source) {
		if (source == null) {
			return;
		}

		targetFields.accept(FieldFeature.SUPPORT_SETTER).accept(FieldFeature.EXISTING_SETTER_FIELD)
				.accept((targetField) -> {
					// 是否忽略静态字段
					if (ignoreStaticField && Modifier.isStatic(targetField.getSetter().getModifiers())) {
						return false;
					}
					return true;
				}).accept(getFilter()).forEach((targetField) -> {
					Field sourceField = getSourceField(sourceFields, targetField, getFieldMatcher());
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

	public <T, S> void copy(Class<? extends T> targetClass, T target, @Nullable Field targetParentField,
			Class<? extends S> sourceClass, S source, @Nullable Field sourceParentField) {
		if (source == null) {
			return;
		}

		copy(getFieldFactory().getFields(targetClass, targetParentField).all(), target,
				getFieldFactory().getFields(sourceClass, sourceParentField).all(), source);
	}

	private Object copyValue(Field field, Object value) {
		Object valueToUse = value;
		if (deepCopy) {
			if (Modifier.isTransient(field.getSetter().getModifiers())) {
				if (cloneTransientField) {
					valueToUse = clone(valueToUse, field);
				}
			} else {
				valueToUse = clone(valueToUse, field);
			}
		}
		return valueToUse;
	}

	public <T, S> T copy(Class<? extends T> targetClass, @Nullable Field targetParentField,
			Class<? extends S> sourceClass, S source, @Nullable Field sourceParentField) {
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

	public <T> T clone(T source, @Nullable Field parentField) {
		if (source == null) {
			return null;
		}

		return (T) clone(source.getClass(), source, parentField);
	}

	public <T> T clone(Class<? extends T> sourceClass, T source, @Nullable Field parentField) {
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
			if (ignoreStaticField && Modifier.isStatic(field.getSetter().getModifiers())) {
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

	/**
	 * 浅拷贝
	 * 
	 * @param <T>
	 * @param targetClass
	 * @param source
	 * @return
	 */
	public static <T> T copy(Class<? extends T> targetClass, Object source) {
		Assert.requiredArgument(targetClass != null, "targetClass");
		Assert.requiredArgument(source != null, "source");
		return DEFAULT_COPY.copy(targetClass, null, source.getClass(), source, null);
	}

	/**
	 * 浅拷贝
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
