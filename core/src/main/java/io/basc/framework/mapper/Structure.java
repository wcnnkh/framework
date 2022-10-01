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
import io.basc.framework.util.XUtils;
import io.basc.framework.util.page.PageablesIterator;

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
	private int nameNestingDepth = -1;
	private String nameNestingConnector = "_";

	/**
	 * @param members 支持Structure入参
	 */
	public Structure(Members<T> members) {
		super(members);
		if (members instanceof Structure) {
			this.name = ((Structure<?>) members).name;
			this.aliasNames = ((Structure<?>) members).aliasNames;
			this.parent = ((Structure<T>) members).parent;
			this.nameNestingConnector = ((Structure<T>) members).nameNestingConnector;
			this.nameNestingDepth = ((Structure<T>) members).nameNestingDepth;
		}
	}

	public Structure(Class<?> sourceClass, T parent, Function<Class<?>, ? extends Stream<T>> processor) {
		super(sourceClass, processor);
		this.parent = parent;
	}

	/**
	 * @param members 支持Structure入参
	 * @param map
	 */
	public Structure(Members<? extends Field> members, Function<? super Field, ? extends T> map) {
		super(members.map(map));
		if (members instanceof Structure) {
			this.name = ((Structure<?>) members).name;
			this.aliasNames = ((Structure<?>) members).aliasNames;
			this.parent = map(map.apply(((Structure<?>) members).parent));
			this.nameNestingConnector = ((Structure<?>) members).nameNestingConnector;
			this.nameNestingDepth = ((Structure<?>) members).nameNestingDepth;
		}
	}

	protected T map(T source) {
		if (this.parent == null) {
			return source;
		}

		if (source == null) {
			return source;
		}

		if (source.hasParent()) {
			return source;
		}

		T t = clone(source);
		t.setParent(this.parent);
		return t;
	}

	@Override
	public <S> Members<S> mapProcessor(Function<Stream<T>, ? extends Stream<S>> processor) {
		return super.mapProcessor((s) -> processor.apply(s.map((e) -> map(e))));
	}

	@Override
	protected Structure<T> decorate(Members<T> members) {
		Structure<T> structure = new Structure<T>(members);
		if (!(members instanceof Structure)) {
			structure.parent = this.parent;
			structure.aliasNames = this.aliasNames;
			structure.parent = this.parent;
			structure.nameNestingConnector = this.nameNestingConnector;
			structure.nameNestingDepth = this.nameNestingDepth;
		}
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
		Structure<T> structure = decorate(members);
		structure.parent = parent;
		return structure;
	}

	@Override
	public Stream<? extends Structure<T>> pages() {
		return XUtils.stream(new PageablesIterator<>(this, (e) -> e.next()));
	}

	public Structure<T> setNameNestingDepth(int nameNestingDepth) {
		if (this.nameNestingDepth == nameNestingDepth) {
			return decorate(this);
		}

		Members<T> members = map((e) -> {
			T t = clone(e);
			t.setNameNestingDepth(nameNestingDepth);
			return t;
		});
		Structure<T> structure = decorate(members);
		structure.nameNestingDepth = nameNestingDepth;
		return structure;
	}

	public int getNameNestingDepth() {
		return nameNestingDepth;
	}

	public String getNameNestingConnector() {
		return nameNestingConnector;
	}

	public Structure<T> setNameNestingConnector(String nameNestingConnector) {
		if (StringUtils.equals(this.nameNestingConnector, nameNestingConnector)) {
			return decorate(this);
		}

		Members<T> members = map((e) -> {
			T t = clone(e);
			t.setNameNestingDepth(nameNestingDepth);
			return t;
		});
		Structure<T> structure = decorate(members);
		structure.nameNestingConnector = nameNestingConnector;
		return structure;
	}

	/**
	 * 默认的实现会出现getParent()为空
	 * 
	 * @param parent
	 * @return
	 */
	public Structure<T> setParentField(Field parent) {
		if (this.parent == parent) {
			return decorate(this);
		}

		Members<T> members = map((e) -> {
			T t = clone(e);
			t.setParent(parent);
			return t;
		});
		Structure<T> structure = decorate(members);
		return structure;
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
		return getters(new PredicateFieldDescriptor(name, type));
	}

	public Structure<T> byName(String name) {
		PredicateFieldDescriptor acceptFieldDescriptor = new PredicateFieldDescriptor(name, null);
		return filter((e) -> (e.isSupportGetter() && acceptFieldDescriptor.test(e.getGetter()))
				|| (e.isSupportSetter() && acceptFieldDescriptor.test(e.getSetter())));
	}

	public Structure<T> byName(String name, @Nullable Type type) {
		PredicateFieldDescriptor acceptFieldDescriptor = new PredicateFieldDescriptor(name, type);
		return filter((e) -> (e.isSupportGetter() && acceptFieldDescriptor.test(e.getGetter()))
				|| (e.isSupportSetter() && acceptFieldDescriptor.test(e.getSetter())));
	}

	public Structure<T> bySetterName(String name, @Nullable Type type) {
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
