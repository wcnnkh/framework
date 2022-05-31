package io.basc.framework.mapper;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import io.basc.framework.core.Members;
import io.basc.framework.core.MembersDecorator;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.LinkedMultiValueMap;
import io.basc.framework.util.MultiValueMap;
import io.basc.framework.util.StringUtils;

/**
 * 结构
 * 
 * @author wcnnkh
 *
 * @param <T>
 */
public class Structure<T extends Field> extends MembersDecorator<T, Structure<T>> {
	protected String name;
	protected Collection<String> aliasNames;
	private T parent;

	public Structure(Members<T> members) {
		super(members);
	}

	public Structure(Class<?> sourceClass, Function<Class<?>, ? extends Stream<T>> processor) {
		super(sourceClass, processor);
	}

	public Structure(Structure<? extends T> members) {
		super(members);
		this.parent = members.parent;
		this.aliasNames = members.aliasNames;
		this.name = members.name;
	}

	@Override
	protected Structure<T> decorate(Members<T> members) {
		Structure<T> structure = new Structure<T>(members);
		structure.parent = this.parent;
		structure.aliasNames = this.aliasNames;
		structure.parent = this.parent;
		return structure;
	}

	public T getParent() {
		return this.parent;
	}

	protected T clone(T source) {
		T target = ReflectionUtils.invokeCloneMethod(source);
		if (target == null) {
			target = source;
		}
		return target;
	}

	public Structure<T> setParent(T parent) {
		if (this.parent == parent) {
			return decorate(this);
		}

		Members<T> members = map((e) -> {
			T t = clone(e);
			t.setParent(parent);
			return t;
		});
		return decorate(members);
	}

	public Structure<T> setParentField(Field parent) {
		if (this.parent == parent) {
			return decorate(this);
		}

		Members<T> members = map((e) -> {
			T t = clone(e);
			t.setParent(parent);
			return t;
		});
		return decorate(members);
	}

	public String getName() {
		if (StringUtils.isEmpty(name)) {
			return StringUtils.toLowerCase(getSourceClass().getSimpleName(), 0, 1);
		}
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<String> getAliasNames() {
		if (aliasNames == null) {
			return Arrays.asList(StringUtils
					.humpNamingReplacement(StringUtils.toLowerCase(getSourceClass().getSimpleName(), 0, 1), "_"));
		}
		return Collections.unmodifiableCollection(aliasNames);
	}

	public void setAliasNames(Collection<String> aliasNames) {
		this.aliasNames = aliasNames;
	}

	public Structure<T> byGetterName(String name, @Nullable Type type) {
		return getters(new AcceptFieldDescriptor(name, type));
	}

	public Structure<T> byName(String name) {
		AcceptFieldDescriptor acceptFieldDescriptor = new AcceptFieldDescriptor(name, null);
		return filter((e) -> (e.isSupportGetter() && acceptFieldDescriptor.accept(e.getGetter()))
				|| (e.isSupportSetter() && acceptFieldDescriptor.accept(e.getSetter())));
	}

	public Structure<T> byName(String name, @Nullable Type type) {
		AcceptFieldDescriptor acceptFieldDescriptor = new AcceptFieldDescriptor(name, type);
		return filter((e) -> (e.isSupportGetter() && acceptFieldDescriptor.accept(e.getGetter()))
				|| (e.isSupportSetter() && acceptFieldDescriptor.accept(e.getSetter())));
	}

	public Structure<T> bySetterName(String name, @Nullable Type type) {
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
	public Structure<T> entity() {
		return ignoreStatic().ignoreTransient().strict();
	}

	/**
	 * 排除一些字段
	 * 
	 * @param names
	 * @return
	 */
	public Structure<T> exclude(Collection<String> names) {
		return exclude((e) -> (e.isSupportGetter() && names.contains(e.getGetter().getName()))
				|| (e.isSupportSetter() && names.contains(e.getSetter().getName())));
	}

	@Nullable
	public T getByGetterName(String name, @Nullable Type type) {
		return byGetterName(name, type).first();
	}

	@Nullable
	public T getByName(String name, @Nullable Type type) {
		return byName(name, type).first();
	}

	public T getByName(String name) {
		return getByName(name, null);
	}

	@Nullable
	public T getBySetterName(String name, @Nullable Type type) {
		return bySetterName(name, type).first();
	}

	public T getBySetterName(String name) {
		return getBySetterName(name, null);
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

	/**
	 * 支持getter的
	 * 
	 * @return
	 */
	public Structure<T> getters() {
		return filter(FieldFeature.SUPPORT_GETTER);
	}

	public Structure<T> getters(Predicate<? super FieldDescriptor> accept) {
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
	public Structure<T> ignoreFinal() {
		return filter(FieldFeature.IGNORE_FINAL);
	}

	public Structure<T> ignoreStatic() {
		return filter(FieldFeature.IGNORE_STATIC);
	}

	/**
	 * 忽略transient描述的字段
	 * 
	 * @return
	 */
	public Structure<T> ignoreTransient() {
		return filter(FieldFeature.IGNORE_TRANSIENT);
	}

	/**
	 * 支持setter的
	 * 
	 * @return
	 */
	public Structure<T> setters() {
		return filter(FieldFeature.SUPPORT_SETTER);
	}

	public Structure<T> setters(Predicate<? super FieldDescriptor> accept) {
		if (accept == null) {
			return this;
		}

		return filter((e) -> e.isSupportSetter() && accept.test(e.getSetter()));
	}

	/**
	 * 严格的，必须包含getter和setter
	 * 
	 * @return
	 */
	public Structure<T> strict() {
		return filter(FieldFeature.STRICT);
	}

	public Structure<T> rename(String name) {
		Structure<T> structure = clone();
		structure.name = name;
		return structure;
	}
}
