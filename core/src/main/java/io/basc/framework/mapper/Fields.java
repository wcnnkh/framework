package io.basc.framework.mapper;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import io.basc.framework.core.Members;
import io.basc.framework.core.MembersMapper;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Accept;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.LinkedMultiValueMap;
import io.basc.framework.util.MultiValueMap;

public final class Fields extends MembersMapper<Field, Fields> {
	public static final Function<Class<?>, Stream<FieldMetadata>> DEFAULT = new FieldFunction();

	public static Fields getFields(Class<?> sourceClass) {
		return new Fields(sourceClass);
	}

	public static Fields getFields(Class<?> sourceClass, Field parentField) {
		return new Fields(sourceClass, parentField);
	}

	private Field parentField;

	public Fields(Class<?> sourceClass) {
		this(sourceClass, null);
	}

	public Fields(Class<?> sourceClass, Field parentField) {
		this(sourceClass, parentField, DEFAULT);
	}

	public Fields(Class<?> sourceClass, Field parentField,
			Function<Class<?>, ? extends Stream<? extends FieldMetadata>> processor) {
		super(sourceClass, processor.andThen((e) -> e.map((o) -> new Field(parentField, o))));
		this.parentField = parentField;
	}

	protected Fields(Field parentField, Members<Field> members) {
		super(members);
		this.parentField = parentField;
	}

	public Fields byGetterName(String name, @Nullable Type type) {
		return getters(new AcceptFieldDescriptor(name, type));
	}

	public Fields byName(String name) {
		AcceptFieldDescriptor acceptFieldDescriptor = new AcceptFieldDescriptor(name, null);
		return filter((e) -> (e.isSupportGetter() && acceptFieldDescriptor.accept(e.getGetter()))
				|| (e.isSupportSetter() && acceptFieldDescriptor.accept(e.getSetter())));
	}

	public Fields byName(String name, @Nullable Type type) {
		AcceptFieldDescriptor acceptFieldDescriptor = new AcceptFieldDescriptor(name, type);
		return filter((e) -> (e.isSupportGetter() && acceptFieldDescriptor.accept(e.getGetter()))
				|| (e.isSupportSetter() && acceptFieldDescriptor.accept(e.getSetter())));
	}

	public Fields bySetterName(String name, @Nullable Type type) {
		return setters(new AcceptFieldDescriptor(name, type));
	}

	/**
	 * 获取实体类字段(抽象的字段，不一定存在{@link java.lang.reflect.Field})
	 * 
	 * @see #ignoreStatic()
	 * @see #ignoreTransient()
	 * @see #strict()
	 * @return
	 */
	public Fields entity() {
		return ignoreStatic().ignoreTransient().strict();
	}

	/**
	 * 排除一些字段
	 * 
	 * @param accept
	 * @return
	 */
	public Fields exclude(Accept<Field> accept) {
		if (accept == null) {
			return this;
		}

		return filter(accept.negate());
	}

	/**
	 * 排除一些字段
	 * 
	 * @param names
	 * @return
	 */
	public Fields exclude(Collection<String> names) {
		if (CollectionUtils.isEmpty(names)) {
			return this;
		}

		return exclude((e) -> (e.isSupportGetter() && names.contains(e.getGetter().getName()))
				|| (e.isSupportSetter() && names.contains(e.getSetter().getName())));
	}

	@Nullable
	public Field getByGetterName(String name, @Nullable Type type) {
		return byGetterName(name, type).first();
	}

	@Nullable
	public Field getByName(String name, @Nullable Type type) {
		return byName(name, type).first();
	}

	@Nullable
	public Field getBySetterName(String name, @Nullable Type type) {
		return bySetterName(name, type).first();
	}

	/**
	 * 获取字段的值
	 * 
	 * @param instance
	 * @return 子类和父类可能存在相同的字段名
	 */
	public MultiValueMap<String, Object> getMultiValueMap(Object instance) {
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		for (Field field : this) {
			if (!field.isSupportGetter()) {
				continue;
			}

			Object value = field.getValue(instance);
			if (value == null) {
				continue;
			}

			map.add(field.getGetter().getName(), value);
		}
		return map;
	}

	public Field getParentField() {
		return parentField;
	}

	/**
	 * 支持getter的
	 * 
	 * @return
	 */
	public Fields getters() {
		return filter(FieldFeature.SUPPORT_GETTER);
	}

	public Fields getters(Accept<FieldDescriptor> accept) {
		if (accept == null) {
			return this;
		}

		return filter((e) -> e.isSupportGetter() && accept.accept(e.getGetter()));
	}

	public Map<String, Object> getValueMap(Object instance) {
		return getMultiValueMap(instance).toSingleValueMap();
	}

	/**
	 * 忽略常量
	 * 
	 * @return
	 */
	public Fields ignoreFinal() {
		return filter(FieldFeature.IGNORE_FINAL);
	}

	public Fields ignoreStatic() {
		return filter(FieldFeature.IGNORE_STATIC);
	}

	/**
	 * 忽略transient描述的字段
	 * 
	 * @return
	 */
	public Fields ignoreTransient() {
		return filter(FieldFeature.IGNORE_TRANSIENT);
	}

	@Override
	protected Fields map(Members<Field> members) {
		if (members instanceof Fields) {
			return (Fields) members;
		}
		return new Fields(parentField, members);
	}

	/**
	 * 返回一个新的
	 * 
	 * @param parentField
	 * @return
	 */
	public Fields setParentField(Field parentField) {
		Members<Field> members = map((e) -> e.setParentField(parentField));
		return new Fields(parentField, members);
	}

	public Fields jumpTo(Class<?> cursorId, Field parentField) {
		return jumpTo(cursorId).setParentField(parentField);
	}

	/**
	 * 支持setter的
	 * 
	 * @return
	 */
	public Fields setters() {
		return filter(FieldFeature.SUPPORT_SETTER);
	}

	public Fields setters(Accept<FieldDescriptor> accept) {
		if (accept == null) {
			return this;
		}

		return filter((e) -> e.isSupportSetter() && accept.accept(e.getSetter()));
	}

	/**
	 * 严格的，必须包含getter和setter
	 * 
	 * @return
	 */
	public Fields strict() {
		return filter(FieldFeature.STRICT);
	}
}
