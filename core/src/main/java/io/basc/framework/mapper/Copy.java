package io.basc.framework.mapper;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.env.Sys;
import io.basc.framework.factory.NoArgsInstanceFactory;
import io.basc.framework.lang.NestedExceptionUtils;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.CollectionFactory;

@SuppressWarnings("unchecked")
public class Copy implements Cloneable {
	private NoArgsInstanceFactory instanceFactory;
	private FieldFactory fieldFactory;

	/**
	 * 如果对象实现了java.lang.Cloneable 接口，是否反射调用clone方法
	 */
	private boolean invokeCloneableMethod = true;

	/**
	 * 是否深拷贝
	 */
	private boolean deepCopy = false;

	/**
	 * 是否可以为空时也复制
	 */
	private boolean nullable = false;

	/**
	 * 是否忽略静态的字段
	 */
	private boolean ignoreStatic = true;

	@Override
	public Copy clone() {
		return copy(this, Copy.class);
	}

	public boolean isDeepCopy() {
		return deepCopy;
	}

	public Copy setDeepCopy(boolean deepCopy) {
		if (this.deepCopy == deepCopy) {
			return this;
		}

		Copy copy = clone();
		copy.deepCopy = deepCopy;
		return copy;
	}

	public FieldFactory getFieldFactory() {
		return fieldFactory == null ? MapperUtils.getFieldFactory() : fieldFactory;
	}

	public Copy setFieldFactory(FieldFactory fieldFactory) {
		if (this.fieldFactory == fieldFactory) {
			return this;
		}

		Copy copy = clone();
		copy.fieldFactory = fieldFactory;
		return copy;
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
		if (this.invokeCloneableMethod == invokeCloneableMethod) {
			return this;
		}

		Copy copy = clone();
		copy.invokeCloneableMethod = invokeCloneableMethod;
		return copy;
	}

	public final NoArgsInstanceFactory getInstanceFactory() {
		return instanceFactory == null ? Sys.env : instanceFactory;
	}

	public Copy setInstanceFactory(NoArgsInstanceFactory instanceFactory) {
		if (this.instanceFactory == instanceFactory) {
			return this;
		}

		Copy copy = new Copy();
		copy.instanceFactory = instanceFactory;
		return copy;
	}

	public boolean isNullable() {
		return nullable;
	}

	public Copy setNullable(boolean nullable) {
		if (this.nullable == nullable) {
			return this;
		}

		Copy copy = new Copy();
		copy.nullable = nullable;
		return copy;
	}

	protected boolean checkModifiers(Getter source, Setter target) {
		int sourceModifiers = source.getField() == null ? source.getModifiers() : source.getField().getModifiers();
		int targetModifiers = target.getField() == null ? target.getModifiers() : target.getField().getModifiers();
		if (ignoreStatic && (Modifier.isStatic(sourceModifiers) || Modifier.isStatic(targetModifiers))) {
			return false;
		}

		if (Modifier.isStatic(sourceModifiers) ^ Modifier.isStatic(targetModifiers)) {
			return false;
		}
		return true;
	}

	protected boolean checkName(Getter source, Setter target) {
		String leftName = source.getField() == null ? source.getName() : source.getField().getName();
		String rightName = target.getField() == null ? target.getName() : target.getField().getName();
		return leftName.equals(rightName);
	}

	protected boolean accept(Getter source, Setter target) {
		return checkModifiers(source, target) && checkName(source, target);
	}

	protected Object getValue(Getter getter, Object instance) {
		java.lang.reflect.Field field = getter.getField();
		if (field != null) {
			return getValue(getter, field, instance);
		}
		return getter.get(instance);
	}

	protected Object getValue(Getter getter, java.lang.reflect.Field field, Object instance) {
		try {
			ReflectionUtils.makeAccessible(field);
			return field.get(instance);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(getter + " instance [" + instance + "]",
					NestedExceptionUtils.excludeInvalidNestedExcpetion(e));
		}
	}

	protected void setValue(Getter getter, Setter setter, java.lang.reflect.Field field, Object instance,
			Object value) {
		try {
			ReflectionUtils.makeAccessible(field);
			field.set(instance, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(setter + " instance [" + instance + "] value [" + value + "]",
					NestedExceptionUtils.excludeInvalidNestedExcpetion(e));
		}
	}

	protected void setValue(Getter getter, Setter setter, Object instance, Object value) {
		java.lang.reflect.Field field = setter.getField();
		if (field != null) {
			setValue(getter, setter, field, instance, value);
			return;
		}
		setter.set(instance, value);
	}

	public void copy(Field sourceField, Object source, Field targetField, Object target) {
		Getter getter = sourceField.getGetter();
		Setter setter = targetField.getSetter();
		if (getter == null || setter == null) {
			return;
		}

		if (!accept(getter, setter)) {
			return;
		}

		ResolvableType targetType = ResolvableType
				.forType(setter.getField() == null ? setter.getGenericType() : setter.getField().getGenericType());
		ResolvableType sourceType = ResolvableType
				.forType(getter.getField() == null ? getter.getGenericType() : getter.getField().getGenericType());
		if (!targetType.isAssignableFrom(sourceType)) {
			return;
		}

		Object value = getValue(getter, source);
		if (value == null && !isNullable()) {
			return;
		}

		if (value != null && isDeepCopy()) {
			// 深拷贝
			value = clone(new TypeDescriptor(sourceType, null, getter), value, sourceField);
		}
		setValue(getter, setter, target, value);
	}

	public <T, S> void copy(Fields sourceFields, S source, Fields targetFields, T target) {
		Assert.requiredArgument(sourceFields != null, "sourceFields");
		Assert.requiredArgument(targetFields != null, "targetFields");
		if (source == null || target == null) {
			return;
		}

		Fields sourceFilterFields = sourceFields.getters().shared();
		targetFields.setters().stream().forEach((targetField) -> sourceFilterFields
				.forEach((sourceField) -> copy(sourceField, source, targetField, target)));
	}

	public <T> void copy(Fields fields, T source, T target) {
		Assert.requiredArgument(fields != null, "fields");
		if (source == null || target == null) {
			return;
		}

		fields.strict().forEach((field) -> copy(field, source, field, target));
	}

	/**
	 * 复制，要求get和set都存在字段{@link java.lang.reflect.Field}
	 * 
	 * @see FieldFeature#EXISTING_FIELD
	 * @param <T>
	 * @param entityClass
	 * @param parentField
	 * @param source
	 * @param target
	 */
	public <T> void copy(Class<? extends T> entityClass, Field parentField, T source, T target) {
		Assert.requiredArgument(entityClass != null, "entityClass");
		if (source == null || target == null) {
			return;
		}
		copy(getFieldFactory().getFields(entityClass, parentField).accept(FieldFeature.EXISTING_FIELD).all(), source,
				target);
	}

	/**
	 * 复制,要求sourceClass字段满足{@link FieldFeature#EXISTING_GETTER_FIELD }和targetClass字段满足{@link FieldFeature#EXISTING_SETTER_FIELD }
	 * 
	 * @param <T>
	 * @param <S>
	 * @param sourceClass
	 * @param source
	 * @param sourceParentField
	 * @param targetClass
	 * @param target
	 * @param targetParentField
	 */
	public <T, S> void copy(Class<? extends S> sourceClass, S source, @Nullable Field sourceParentField,
			Class<? extends T> targetClass, T target, @Nullable Field targetParentField) {
		Assert.requiredArgument(sourceClass != null, "sourceClass");
		Assert.requiredArgument(targetClass != null, "targetClass");
		if (source == null || target == null) {
			return;
		}

		if (sourceParentField == null && targetParentField == null && targetClass.isAssignableFrom(sourceClass)) {
			copy(targetClass, sourceParentField == targetParentField ? targetParentField : null, source, target);
		} else {
			copy(getFieldFactory().getFields(sourceClass, sourceParentField).accept(FieldFeature.EXISTING_GETTER_FIELD)
					.all(), source,
					getFieldFactory().getFields(targetClass, targetParentField)
							.accept(FieldFeature.EXISTING_SETTER_FIELD).all(),
					target);
		}
	}

	/**
	 * 创建实例并复制到对应的属性
	 * 
	 * @see #copy(Class, Object, Field, Class, Object, Field)
	 * @param <T>
	 * @param <S>
	 * @param sourceType
	 * @param source
	 * @param sourceParentField
	 * @param targetType
	 * @param targetParentField
	 * @return
	 */
	public <T, S> T copy(TypeDescriptor sourceType, S source, @Nullable Field sourceParentField,
			TypeDescriptor targetType, @Nullable Field targetParentField) {
		Assert.requiredArgument(sourceType != null, "sourceType");
		Assert.requiredArgument(targetType != null, "targetType");

		if (source == null) {
			return null;
		}

		if (targetType.isArray() && sourceType.isArray() && targetType.isAssignableTo(sourceType)) {
			return cloneArray(source, sourceParentField, sourceType);
		}

		if (targetType.isCollection() && sourceType.isCollection() && targetType.isAssignableTo(sourceType)) {
			return cloneCollection((Collection<?>) source, sourceParentField, targetType);
		}

		if (targetType.isMap() && sourceType.isMap() && targetType.isAssignableTo(sourceType)) {
			return cloneMap((Map<?, ?>) source, sourceParentField, targetType);
		}

		Class<?> targetClass = targetType.getType();
		if (!getInstanceFactory().isInstance(targetClass)) {
			// 如果无法实例化
			if (targetClass.isInstance(source)) {
				return (T) targetClass.cast(source);
			}
			throw new IllegalStateException("Unable to copy " + sourceType + " -> " + targetType);
		}

		T target = (T) getInstanceFactory().getInstance(targetClass);
		copy(sourceType.getType(), source, sourceParentField, targetClass, target, targetParentField);
		return target;
	}

	private <T> T cloneArray(Object sourceArray, Field sourceParentField, TypeDescriptor targetType) {
		int size = Array.getLength(sourceArray);
		TypeDescriptor elementType = targetType.getElementTypeDescriptor();
		Object newArr = Array.newInstance(elementType.getType(), size);
		for (int i = 0; i < size; i++) {
			Array.set(newArr, i, clone(elementType, Array.get(sourceArray, i), sourceParentField));
		}
		return (T) newArr;
	}

	private <T> T cloneCollection(Collection<?> sources, Field sourceParentField, TypeDescriptor targetType) {
		TypeDescriptor elementType = targetType.getElementTypeDescriptor();
		Collection<Object> targets;
		try {
			targets = CollectionFactory.createCollection(targetType.getType(), elementType.getType(), sources.size());
		} catch (IllegalArgumentException e) {
			// 无法构造
			if (sources instanceof Cloneable) {
				return ReflectionUtils.clone((Cloneable) sources);
			}
			return (T) sources;
		}

		for (Object source : sources) {
			targets.add(clone(elementType, source, sourceParentField));
		}
		return (T) targets;
	}

	private <T> T cloneMap(Map<?, ?> sourceMap, Field sourceParentField, TypeDescriptor targetType) {
		TypeDescriptor keyType = targetType.getMapKeyTypeDescriptor();
		TypeDescriptor valueType = targetType.getMapValueTypeDescriptor();
		Map<Object, Object> targetMap;
		try {
			targetMap = CollectionFactory.createMap(keyType.getType(), valueType.getType(), sourceMap.size());
		} catch (IllegalArgumentException e) {
			// 无法构造
			if (sourceMap instanceof Cloneable) {
				return ReflectionUtils.clone((Cloneable) sourceMap);
			}
			return (T) sourceMap;
		}

		for (Entry<?, ?> entry : sourceMap.entrySet()) {
			targetMap.put(clone(keyType, entry.getKey(), sourceParentField),
					clone(valueType, entry.getValue(), sourceParentField));
		}
		return (T) targetMap;
	}

	/**
	 * 克隆一个实例
	 * 
	 * @param <T>
	 * @param source
	 * @param parentField
	 * @return
	 */
	public <T> T clone(T source, @Nullable Field parentField) {
		if (source == null) {
			return null;
		}

		return (T) clone(TypeDescriptor.forObject(source), source, parentField);
	}

	/**
	 * 克隆一个实体，
	 * 
	 * @param <T>
	 * @param sourceType
	 * @param source
	 * @param parentField
	 * @return
	 */
	public <T> T clone(TypeDescriptor sourceType, T source, @Nullable Field parentField) {
		Assert.requiredArgument(sourceType != null, "sourceType");
		if (source == null) {
			return null;
		}

		if (sourceType.isPrimitive() || sourceType.isEnum()) {
			return source;
		}

		if (sourceType.isArray()) {
			return cloneArray(source, parentField, sourceType);
		}

		if (sourceType.isCollection()) {
			return cloneCollection((Collection<?>) source, parentField, sourceType);
		}

		if (sourceType.isMap()) {
			return cloneMap((Map<?, ?>) source, parentField, sourceType);
		}

		if (isInvokeCloneableMethod() && source instanceof Cloneable) {
			return ReflectionUtils.clone((Cloneable) source);
		}

		Class<?> sourceClass = sourceType.getType();
		if (!getInstanceFactory().isInstance(sourceClass)) {
			return source;
		}

		Object target = getInstanceFactory().getInstance(sourceClass);
		copy(sourceClass, parentField, source, target);
		return (T) target;
	}

	/**
	 * 浅拷贝
	 */
	public static final Copy SHALLOW = new Copy();
	/**
	 * 深拷贝
	 */
	public static final Copy DEEP = SHALLOW.setDeepCopy(true);

	/**
	 * 深拷贝
	 * 
	 * @see #DEEP
	 * @param source
	 * @return
	 */
	public static <T> T clone(T source) {
		return DEEP.clone(source, null);
	}

	/**
	 * 浅拷贝
	 * 
	 * @see #SHALLOW
	 * @param <T>
	 * @param targetClass
	 * @param source
	 * @return
	 */
	public static <T> T copy(Object source, Class<? extends T> targetClass) {
		Assert.requiredArgument(source != null, "source");
		Assert.requiredArgument(targetClass != null, "targetClass");
		return SHALLOW.copy(TypeDescriptor.forObject(source), source, null, TypeDescriptor.valueOf(targetClass), null);
	}

	/**
	 * 浅拷贝
	 * 
	 * @see #SHALLOW
	 * @param <T>
	 * @param source
	 * @param target
	 * @return
	 */
	public static <T> T copy(Object source, T target) {
		Assert.requiredArgument(source != null, "source");
		Assert.requiredArgument(target != null, "target");
		SHALLOW.copy(source.getClass(), source, null, target.getClass(), target, null);
		return target;
	}

	/**
	 * 拷贝(浅拷贝)一个对象并对对应的字段插入值
	 * 
	 * @see #copy(Object, Class)
	 * @param <T>
	 * @param sourceClass
	 * @param source
	 * @param field
	 * @param value
	 * @return
	 */
	public static <T> T cloneAndSetValue(Class<? extends T> sourceClass, Object source, java.lang.reflect.Field field,
			Object value) {
		T target = copy(source, sourceClass);
		ReflectionUtils.setField(field, target, value);
		return target;
	}
}
