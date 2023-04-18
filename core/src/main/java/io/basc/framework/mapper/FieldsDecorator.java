package io.basc.framework.mapper;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import io.basc.framework.core.Members;
import io.basc.framework.core.MembersDecorator;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Elements;
import io.basc.framework.util.LinkedMultiValueMap;
import io.basc.framework.util.MultiValueMap;

public abstract class FieldsDecorator<S extends Field, T extends FieldsDecorator<S, T>> extends MembersDecorator<S, T> {
	protected S parent;

	public FieldsDecorator(S parent, Class<?> sourceClass, Function<Class<?>, ? extends Elements<S>> processor) {
		super(sourceClass, processor);
		this.parent = parent;
	}

	public FieldsDecorator(Members<S> members) {
		super(members);
	}

	public FieldsDecorator(FieldsDecorator<S, T> members) {
		super(members);
		this.parent = members.parent;
	}

	public S getParent() {
		return this.parent;
	}

	/**
	 * 返回一个新的
	 * 
	 * @param parent
	 * @return
	 */
	public T setParent(S parent) {
		Members<S> members = map((e) -> {
			S t = ReflectionUtils.invokeCloneMethod(e);
			if (t == null) {
				t = e;
			}
			t.setParent(parent);
			return t;
		});
		T fields = decorate(members);
		fields.parent = parent;
		return fields;
	}

	public T byGetterName(String name, @Nullable Type type) {
		return getters(new PredicateFieldDescriptor(name, type));
	}

	public T byName(String name) {
		PredicateFieldDescriptor acceptFieldDescriptor = new PredicateFieldDescriptor(name, null);
		return filter((e) -> (e.isSupportGetter() && acceptFieldDescriptor.test(e.getGetter()))
				|| (e.isSupportSetter() && acceptFieldDescriptor.test(e.getSetter())));
	}

	public T byName(String name, @Nullable Type type) {
		PredicateFieldDescriptor acceptFieldDescriptor = new PredicateFieldDescriptor(name, type);
		return filter((e) -> (e.isSupportGetter() && acceptFieldDescriptor.test(e.getGetter()))
				|| (e.isSupportSetter() && acceptFieldDescriptor.test(e.getSetter())));
	}

	public T bySetterName(String name, @Nullable Type type) {
		return setters(new PredicateFieldDescriptor(name, type));
	}

	/**
	 * 获取实体类字段(抽象的字段，不一定存在{@link java.lang.reflect.Field})
	 * 
	 * @see #ignoreStatic()
	 * @see #ignoreTransient()
	 * @see #strict()
	 * @return
	 */
	public T entity() {
		return ignoreStatic().ignoreTransient().strict();
	}

	/**
	 * 排除一些字段
	 * 
	 * @param names
	 * @return
	 */
	public T exclude(Collection<String> names) {
		return exclude((e) -> (e.isSupportGetter() && names.contains(e.getGetter().getName()))
				|| (e.isSupportSetter() && names.contains(e.getSetter().getName())));
	}

	@Nullable
	public S getByGetterName(String name, @Nullable Type type) {
		return byGetterName(name, type).getElements().first();
	}

	@Nullable
	public S getByName(String name, @Nullable Type type) {
		return byName(name, type).getElements().first();
	}

	@Nullable
	public S getBySetterName(String name, @Nullable Type type) {
		return bySetterName(name, type).getElements().first();
	}

	/**
	 * 获取字段的值
	 * 
	 * @param instance
	 * @return 子类和父类可能存在相同的字段名
	 */
	public MultiValueMap<String, Object> getMultiValueMap(Object instance) {
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		for (Field field : this.getElements()) {
			if (!field.isSupportGetter()) {
				continue;
			}

			Object value = field.get(instance);
			if (value == null) {
				continue;
			}

			map.add(field.getGetter().getName(), value);
		}
		return map;
	}

	/**
	 * 支持getter的
	 * 
	 * @return
	 */
	public T getters() {
		return filter(FieldFeature.SUPPORT_GETTER);
	}

	public T getters(Predicate<? super FieldDescriptor> accept) {
		if (accept == null) {
			return decorate(this);
		}
		return filter((e) -> e.isSupportGetter() && accept.test(e.getGetter()));
	}

	public Map<String, Object> getValueMap(Object instance) {
		return getMultiValueMap(instance).toSingleValueMap();
	}

	/**
	 * 忽略常量
	 * 
	 * @return
	 */
	public T ignoreFinal() {
		return filter(FieldFeature.IGNORE_FINAL);
	}

	public T ignoreStatic() {
		return filter(FieldFeature.IGNORE_STATIC);
	}

	/**
	 * 忽略transient描述的字段
	 * 
	 * @return
	 */
	public T ignoreTransient() {
		return filter(FieldFeature.IGNORE_TRANSIENT);
	}

	/**
	 * 支持setter的
	 * 
	 * @return
	 */
	public T setters() {
		return filter(FieldFeature.SUPPORT_SETTER);
	}

	public T setters(Predicate<? super FieldDescriptor> accept) {
		if (accept == null) {
			return decorate(this);
		}

		return filter((e) -> e.isSupportSetter() && accept.test(e.getSetter()));
	}

	/**
	 * 严格的，必须包含getter和setter
	 * 
	 * @return
	 */
	public T strict() {
		return filter(FieldFeature.STRICT);
	}

	public T jumpTo(Class<?> cursorId, S parent) {
		return super.jumpTo(cursorId).setParent(parent);
	}
}