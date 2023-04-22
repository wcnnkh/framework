package io.basc.framework.mapper;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.Members;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.core.Structure;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.util.LinkedMultiValueMap;
import io.basc.framework.util.MultiValueMap;
import io.basc.framework.util.StringUtils;
import lombok.Data;

@Data
public class Fields<T extends Field> extends Structure<T> implements Mapping<T> {
	private String name;
	private Elements<String> aliasNames;
	private T parent;
	private int nameNestingDepth = -1;
	private String nameNestingConnector = "_";
	private final TypeDescriptor typeDescriptor;

	public Fields(TypeDescriptor typeDescriptor, Members<T> members) {
		super(members);
		this.typeDescriptor = typeDescriptor;
	}

	public Fields(TypeDescriptor typeDescriptor, Function<? super ResolvableType, ? extends Members<T>> processor) {
		super(typeDescriptor.getResolvableType(), processor);
		this.typeDescriptor = typeDescriptor;
	}

	public Fields(TypeDescriptor typeDescriptor, Structure<T> structure) {
		super(structure);
		this.typeDescriptor = typeDescriptor;
	}

	@Override
	public String getName() {
		if (StringUtils.isNotEmpty(name)) {
			return name;
		}
		return typeDescriptor.getType().getName();
	}

	public Elements<String> getAliasName() {
		if (aliasNames != null) {
			return aliasNames;
		}

		String name = typeDescriptor.getType().getSimpleName();
		name = StringUtils.toLowerCase(name, 0, 1);
		name = StringUtils.humpNamingReplacement(name, "_");
		return Elements.singleton(name);
	}

	@Override
	public Fields<T> peek(Consumer<? super T> consumer) {
		Structure<T> structure = super.peek(consumer);
		return new Fields<>(typeDescriptor, structure);
	}

	@Override
	public Members<T> getMembers() {
		return super.getMembers();
	}

	public Fields<T> byGetterName(String name, @Nullable Type type) {
		return getters(new PredicateFieldDescriptor(name, type));
	}

	public Fields<T> byName(String name) {
		PredicateFieldDescriptor acceptFieldDescriptor = new PredicateFieldDescriptor(name, null);
		return filter((e) -> (e.isSupportGetter() && acceptFieldDescriptor.test(e.getGetter()))
				|| (e.isSupportSetter() && acceptFieldDescriptor.test(e.getSetter())));
	}

	public Fields<T> byName(String name, @Nullable Type type) {
		PredicateFieldDescriptor acceptFieldDescriptor = new PredicateFieldDescriptor(name, type);
		return filter((e) -> (e.isSupportGetter() && acceptFieldDescriptor.test(e.getGetter()))
				|| (e.isSupportSetter() && acceptFieldDescriptor.test(e.getSetter())));
	}

	public Fields<T> bySetterName(String name, @Nullable Type type) {
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
	public Fields<T> entity() {
		return ignoreStatic().ignoreTransient().strict();
	}

	/**
	 * 排除一些字段
	 * 
	 * @param names
	 * @return
	 */
	public Fields<T> exclude(Collection<String> names) {
		return exclude((e) -> (e.isSupportGetter() && names.contains(e.getGetter().getName()))
				|| (e.isSupportSetter() && names.contains(e.getSetter().getName())));
	}

	public Fields<T> exclude(Predicate<? super T> predicate) {
		Assert.requiredArgument(predicate != null, "predicate");
		return filter(predicate.negate());
	}

	@Nullable
	public T getByGetterName(String name, @Nullable Type type) {
		return byGetterName(name, type).getElements().first();
	}

	public T getByName(String name) {
		return getByName(name, null);
	}

	@Nullable
	public T getByName(String name, @Nullable Type type) {
		return byName(name, type).getElements().first();
	}

	public T getBySetterName(String name) {
		return getBySetterName(name, null);
	}

	@Nullable
	public T getBySetterName(String name, @Nullable Type type) {
		return bySetterName(name, type).getElements().first();
	}

	/**
	 * 获取字段的值
	 * 
	 * @param instance
	 * @return 可能存在相同的字段名
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
	public Fields<T> getters() {
		return filter(FieldFeature.SUPPORT_GETTER);
	}

	public Fields<T> getters(Predicate<? super FieldDescriptor> accept) {
		Assert.requiredArgument(accept != null, "accept");
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
	public Fields<T> ignoreFinal() {
		return filter(FieldFeature.IGNORE_FINAL);
	}

	public Fields<T> ignoreStatic() {
		return filter(FieldFeature.IGNORE_STATIC);
	}

	/**
	 * 忽略transient描述的字段
	 * 
	 * @return
	 */
	public Fields<T> ignoreTransient() {
		return filter(FieldFeature.IGNORE_TRANSIENT);
	}

	/**
	 * 支持setter的
	 * 
	 * @return
	 */
	public Fields<T> setters() {
		return filter(FieldFeature.SUPPORT_SETTER);
	}

	public Fields<T> setters(Predicate<? super FieldDescriptor> accept) {
		Assert.requiredArgument(accept != null, "accept");
		return filter((e) -> e.isSupportSetter() && accept.test(e.getSetter()));
	}

	/**
	 * 严格的，必须包含getter和setter
	 * 
	 * @return
	 */
	public Fields<T> strict() {
		return filter(FieldFeature.STRICT);
	}

	/**
	 * 返回一个新的Members，包含全部元素
	 * 
	 * @return
	 */
	public Fields<T> all() {
		Structure<T> structure = super.clone();
		return new Fields<>(structure);
	}

	@Override
	public Fields<T> clone() {
		Structure<T> structure = super.clone();
		return new Fields<>(structure);
	}

	public Fields<T> concat(Elements<? extends T> elements) {
		Assert.requiredArgument(elements != null, "elements");
		Structure<T> structure = super.concat(elements);
		return new Fields<>(structure);
	}

	@Override
	public Fields<T> filter(Predicate<? super T> predicate) {
		Structure<T> structure = super.filter(predicate);
		return new Fields<>(structure);
	}

	@Override
	public Fields<T> flatConvert(Function<? super Members<T>, ? extends Members<T>> mapper) {
		Structure<T> structure = super.flatConvert(mapper);
		return new Fields<>(structure);
	}
}
