package io.basc.framework.mapper;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.core.reflect.ReflectionApi;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.ParentDiscover;
import io.basc.framework.util.Processor;
import io.basc.framework.util.ReversibleIterator;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.alias.AliasRegistry;
import io.basc.framework.value.Value;

public class Field extends AccessibleField implements Member, ParentDiscover<Field>, Predicate<Field> {
	protected Field parent;
	protected Class<?> declaringClass;
	protected Collection<String> aliasNames;
	protected Integer modifiers;
	protected Boolean synthetic;
	protected String name;
	protected int nameNestingDepth = -1;
	protected String nameNestingConnector = "_";

	public Field() {
	}

	public Field(Field parent, Class<?> declaringClass, String name, java.lang.reflect.Field field, Method getter,
			Method setter) {
		this(parent, declaringClass,
				(field == null && getter == null) ? null : new DefaultGetter(declaringClass, name, field, getter),
				(field == null && setter == null) ? null : new DefaultSetter(declaringClass, name, field, setter));
	}

	public Field(Field parent, Class<?> declaringClass, Getter getter, Setter setter) {
		super(getter, setter);
		this.declaringClass = declaringClass;
		this.parent = parent;
	}

	public Field(Field parent, Class<?> declaringClass, AccessibleField metadata) {
		super(metadata);
		this.parent = parent;
		this.declaringClass = declaringClass;
	}

	public Field(Field field) {
		super(field);
		if (field != null) {
			this.parent = field.parent;
			this.declaringClass = field.declaringClass;
			this.aliasNames = field.aliasNames;
			this.modifiers = field.modifiers;
			this.synthetic = field.synthetic;
			this.name = field.name;
			this.nameNestingDepth = field.nameNestingDepth;
			this.nameNestingConnector = field.nameNestingConnector;
		}
	}

	public final int getNameNestingDepth() {
		return nameNestingDepth;
	}

	public void setNameNestingDepth(int nameNestingDepth) {
		this.nameNestingDepth = nameNestingDepth;
	}

	public final String getNameNestingConnector() {
		return nameNestingConnector;
	}

	public void setNameNestingConnector(String nameNestingConnector) {
		this.nameNestingConnector = nameNestingConnector;
	}

	public Field clone() {
		return new Field(this);
	}

	public Field getParent() {
		return parent;
	}

	public void setParent(Field parent) {
		this.parent = parent;
	}

	@Override
	public Object get(Object instance) {
		if (parent == null) {
			return getGetter().get(instance);
		}

		Object parentValue = instance;
		ReversibleIterator<Field> enumeration = parents().invert();
		while (enumeration.hasNext()) {
			Field parentField = enumeration.next();
			if (!parentField.isSupportGetter()) {
				return Value.EMPTY.getAsObject(getGetter().getType());
			}

			boolean isStatic = Modifier.isStatic(parentField.getGetter().getModifiers());
			if (isStatic) {
				// 如果是静态方法
				parentValue = null;
			} else {
				if (!ClassUtils.isAssignableValue(parentField.getDeclaringClass(), parentValue)) {
					break;
				}

				parentValue = parentField.getGetter().get(parentValue);
				// 如果不是静态的，但获取到的是空就不用再向下获取了
				if (parentValue == null) {
					return Value.EMPTY.getAsObject(getGetter().getType());
				}
			}
		}

		if (ClassUtils.isAssignableValue(getDeclaringClass(), parentValue)) {
			return getGetter().get(parentValue);
		} else {
			return getGetter().get(instance);
		}
	}

	@Override
	public void set(Object instance, Object value) {
		if (parent == null) {
			getSetter().set(instance, value);
			return;
		}

		if (ClassUtils.isAssignableValue(getDeclaringClass(), instance)) {
			getSetter().set(instance, value);
			return;
		}

		Object parentValue = instance;
		ReversibleIterator<Field> enumeration = parents().invert();
		while (enumeration.hasNext()) {
			Field parentField = enumeration.next();
			boolean isStatic = Modifier.isStatic(parentField.getGetter().getModifiers());
			if (isStatic) {
				// 如果是静态方法
				parentValue = null;
			} else {
				Object target = parentField.isSupportGetter() ? parentField.getGetter().get(parentValue) : null;
				if (target == null) {
					if (value == null) {
						return;
					}

					// 不可以不支持Setter
					target = ReflectionApi.newInstance(parentField.getSetter().getType());
					parentField.getSetter().set(parentValue, target);
				}
				parentValue = target;
			}
		}

		if (ClassUtils.isAssignableValue(getDeclaringClass(), parentValue)) {
			getSetter().set(parentValue, value);
		} else {
			getSetter().set(instance, value);
		}
	}

	public <V, E extends Throwable> V getValueByNames(Processor<? super String, ? extends V, ? extends E> processor)
			throws E {
		if (isSupportSetter()) {
			V value = processor.process(getSetter().getName());
			if (value != null) {
				return value;
			}
		}

		if (isSupportGetter()) {
			V value = processor.process(getGetter().getName());
			if (value != null) {
				return value;
			}
		}
		return null;
	}

	@Override
	public Class<?> getDeclaringClass() {
		return this.declaringClass;
	}

	private String resolveName() {
		if (isSupportGetter()) {
			return getGetter().getName();
		}

		if (isSupportSetter()) {
			return getSetter().getName();
		}
		return null;
	}

	@Override
	public String getName() {
		if (StringUtils.isNotEmpty(name)) {
			return name;
		}

		String name = resolveName();
		if (StringUtils.isEmpty(name)) {
			return null;
		}

		if (hasParent() && this.nameNestingDepth > 0) {
			StringBuilder sb = new StringBuilder();
			ReversibleIterator<Field> parents = parents().invert();
			int i = 0;
			while (parents.hasNext() && (i++ < this.nameNestingDepth)) {
				Field parent = parents.next();
				sb.append(parent.getName());
				sb.append(this.nameNestingConnector);
			}

			sb.append(name);
			return sb.toString();
		}
		return name;
	}

	public Collection<String> getAliasNames() {
		if (aliasNames != null) {
			return Collections.unmodifiableCollection(aliasNames);
		}

		if (isSupportGetter() && isSupportSetter()) {
			return Arrays.asList(getSetter().getName());
		}
		return Collections.emptyList();
	}

	public void setAliasNames(Collection<String> aliasNames) {
		this.aliasNames = aliasNames;
	}

	@Override
	public int getModifiers() {
		if (modifiers != null) {
			return modifiers;
		}

		if (isSupportGetter() && isSupportSetter()) {
			return getGetter().getModifiers() | getSetter().getModifiers();
		}

		if (isSupportGetter()) {
			return getGetter().getModifiers();
		}

		if (isSupportSetter()) {
			return getSetter().getModifiers();
		}
		return 0;
	}

	@Override
	public boolean isSynthetic() {
		if (synthetic != null) {
			return synthetic;
		}

		if (isSupportGetter() && isSupportSetter()) {
			return getGetter().isSynthetic() || getSetter().isSynthetic();
		}

		if (isSupportGetter()) {
			return getGetter().isSynthetic();
		}

		if (isSupportSetter()) {
			return getSetter().isSynthetic();
		}

		return false;
	}

	public void setModifiers(Integer modifiers) {
		this.modifiers = modifiers;
	}

	public void setSynthetic(Boolean synthetic) {
		this.synthetic = synthetic;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Field rename(String name) {
		Field field = clone();
		field.setName(name);
		return field;
	}

	/**
	 * 需要支持getter
	 * 
	 * @see #isSupportGetter()
	 * @param instance
	 * @return
	 */
	public Parameter getParameter(Object instance) {
		return new Parameter(getName(), get(instance), new TypeDescriptor(getGetter()));
	}

	@Override
	public String toString() {
		if (parent == null) {
			return super.toString();
		}

		StringBuilder sb = new StringBuilder();
		sb.append("parent[").append(parent).append("] ");
		sb.append(super.toString());
		return sb.toString();
	}

	/**
	 * @param structure
	 * @return
	 */
	public <T extends Field, E extends Throwable> Structure<T> withEntityTo(Structure<T> structure,
			@Nullable Processor<Field, Structure<T>, E> processor) throws E {
		if (processor == null) {
			return structure;
		}

		Structure<T> entity = processor.process(this);
		if (entity == null) {
			return structure;
		}

		entity = entity.setParentField(this).filter((e) -> {
			e.nameNestingDepth = 0;
			return true;
		});
		return structure.with(entity);
	}

	private static void appendNames(String prefix, Field field, Collection<String> names, boolean root,
			String nameConnector, boolean nameNesting, AliasRegistry aliasRegistry) {
		Field parent = field.getParent();
		if (parent == null || !root || !nameNesting || field.nameNestingDepth >= 0) {
			names.add(prefix == null ? field.getName() : (prefix + field.getName()));
			Collection<String> aliasNames = field.getAliasNames();
			if (aliasNames != null) {
				for (String name : aliasNames) {
					names.add(prefix == null ? name : (prefix + name));
					if (aliasRegistry != null && aliasRegistry.isAlias(name)) {
						String[] aliasArray = aliasRegistry.getAliases(name);
						if (aliasArray != null) {
							for (String alias : aliasArray) {
								names.add(prefix == null ? alias : (prefix + alias));
							}
						}
					}
				}
			}
		} else {
			for (String name : parent.getNames(nameNesting, aliasRegistry, prefix, nameConnector)) {
				appendNames(nameConnector == null ? name : (name + nameConnector), field, names, false, nameConnector,
						nameNesting, aliasRegistry);
			}
		}
	}

	/**
	 * 获取插入时所有可以使用的名称
	 * 
	 * @param nameNesting   如果存在parent是否进行嵌套
	 * @param prefix        前缀
	 * @param nameConnector 发生嵌套时名称之间的连接符
	 * @return
	 */
	protected Collection<String> getNames(boolean nameNesting, @Nullable AliasRegistry aliasRegistry,
			@Nullable String prefix, @Nullable String nameConnector) {
		Set<String> names = new LinkedHashSet<String>(8);
		appendNames(prefix, this, names, true, nameConnector, nameNesting, aliasRegistry);
		return names;
	}

	/**
	 * 获取所有可以使用的名称
	 * 
	 * @param context
	 * @return
	 */
	public Collection<String> getNames(ObjectMapperContext context) {
		return getNames(context.isNameNesting(), context.getAliasRegistry(), context.getNamePrefix(),
				context.getNameConnector());
	}

	@Override
	public boolean test(Field target) {
		if (target == null) {
			return false;
		}

		if (target.getName().equals(getName()) || target.getAliasNames().contains(getName())) {
			if (isSupportGetter() && target.isSupportSetter()) {
				int sourceModifiers = getGetter().getModifiers();
				int targetModifiers = target.getSetter().getModifiers();
				if (Modifier.isStatic(sourceModifiers) ^ Modifier.isStatic(targetModifiers)) {
					return false;
				}

				ResolvableType type1 = ResolvableType.forType(getGetter().getGenericType());
				ResolvableType type2 = ResolvableType.forType(target.getSetter().getGenericType());
				if (!type2.isAssignableFrom(type1)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
}
