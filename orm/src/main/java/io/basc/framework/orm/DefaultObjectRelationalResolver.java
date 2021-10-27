package io.basc.framework.orm;

import io.basc.framework.aop.support.ProxyUtils;
import io.basc.framework.data.domain.Range;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.mapper.FieldDescriptor;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.StringUtils;

import java.util.Collection;
import java.util.LinkedHashSet;

public class DefaultObjectRelationalResolver extends
		ConfigurableServices<ObjectRelationalExtend> implements
		ObjectRelationalResolver {

	public DefaultObjectRelationalResolver() {
		super(ObjectRelationalExtend.class);
	}

	/**
	 * 是否将驼峰命名转换为下划线的名称 myAbc-> my_abc
	 */
	private boolean humpNamingReplacement = false;

	public boolean isHumpNamingReplacement() {
		return humpNamingReplacement;
	}

	public void setHumpNamingReplacement(boolean humpNamingReplacement) {
		this.humpNamingReplacement = humpNamingReplacement;
	}

	@Override
	public Boolean ignore(Class<?> entityClass, FieldDescriptor fieldDescriptor) {
		Boolean v = ObjectRelationalResolverChain.build(iterator()).ignore(
				entityClass, fieldDescriptor);
		return v == null ? false : true;
	}

	private String getDefaultName(FieldDescriptor fieldDescriptor) {
		return humpNamingReplacement ? StringUtils.humpNamingReplacement(
				fieldDescriptor.getName(), "_") : fieldDescriptor.getName();
	}

	@Override
	public String getName(Class<?> entityClass, FieldDescriptor fieldDescriptor) {
		String name = ObjectRelationalResolverChain.build(iterator()).getName(
				entityClass, fieldDescriptor);
		return StringUtils.isEmpty(name) ? getDefaultName(fieldDescriptor)
				: name;
	}

	private void appendDefaultAliasNames(Collection<String> names, String name) {
		String humpName = StringUtils.humpNamingReplacement(name, "_");
		if (!humpName.equals(name)) {
			names.add(humpName);
		}

		String humpName2 = StringUtils.humpNamingReplacement(name, "-");
		if (!humpName2.equals(name)) {
			names.add(humpName2);
		}
	}

	@Override
	public Collection<String> getAliasNames(Class<?> entityClass,
			FieldDescriptor fieldDescriptor) {
		Collection<String> names = ObjectRelationalResolverChain.build(
				iterator()).getAliasNames(entityClass, fieldDescriptor);
		if (CollectionUtils.isEmpty(names)) {
			names = new LinkedHashSet<String>();
			String defaultName = getName(entityClass, fieldDescriptor);
			names.add(defaultName);
			appendDefaultAliasNames(names, defaultName);
		}

		if (isEntity(entityClass, fieldDescriptor)) {
			names.addAll(getAliasNames(fieldDescriptor.getType()));
		}
		return names;
	}

	private String getDefaultEntityName(Class<?> entityClass) {
		String className = ProxyUtils.getFactory().getUserClass(entityClass)
				.getSimpleName();
		return StringUtils.humpNamingReplacement(className, "_");
	}

	@Override
	public String getName(Class<?> entityClass) {
		String name = ObjectRelationalResolverChain.build(iterator()).getName(
				entityClass);
		if (StringUtils.isEmpty(name)) {
			name = getDefaultEntityName(entityClass);
		}
		return name;
	}

	@Override
	public Collection<String> getAliasNames(Class<?> entityClass) {
		Collection<String> names = ObjectRelationalResolverChain.build(
				iterator()).getAliasNames(entityClass);
		if (CollectionUtils.isEmpty(names)) {
			names = new LinkedHashSet<String>(8);
			// 如果没有使用过别名，那就设置默认名称
			String defaultName = getName(entityClass);
			names.add(defaultName);
			appendDefaultAliasNames(names, defaultName);
		}
		return names;
	}

	@Override
	public Boolean isPrimaryKey(Class<?> entityClass,
			FieldDescriptor fieldDescriptor) {
		Boolean v = ObjectRelationalResolverChain.build(iterator())
				.isPrimaryKey(entityClass, fieldDescriptor);
		return v == null ? false : v;
	}

	@Override
	public Boolean isNullable(Class<?> entityClass,
			FieldDescriptor fieldDescriptor) {
		Boolean v = ObjectRelationalResolverChain.build(iterator()).isNullable(
				entityClass, fieldDescriptor);
		return v == null ? !isPrimaryKey(entityClass, fieldDescriptor) : v;
	}

	@Override
	public Boolean isEntity(Class<?> entityClass,
			FieldDescriptor fieldDescriptor) {
		Boolean v = ObjectRelationalResolverChain.build(iterator()).isEntity(
				entityClass, fieldDescriptor);
		return v == null ? isEntity(fieldDescriptor.getType()) : v;
	}

	@Override
	public Boolean isEntity(Class<?> entityClass) {
		Boolean v = ObjectRelationalResolverChain.build(iterator()).isEntity(
				entityClass);
		return v == null ? false : v;
	}

	@Override
	public Boolean isVersionField(Class<?> entityClass,
			FieldDescriptor fieldDescriptor) {
		Boolean v = ObjectRelationalResolverChain.build(iterator())
				.isVersionField(entityClass, fieldDescriptor);
		return v == null ? false : v;
	}

	@Override
	public Range<Double> getNumberRange(Class<?> entityClass,
			FieldDescriptor fieldDescriptor) {
		return ObjectRelationalResolverChain.build(iterator()).getNumberRange(entityClass, fieldDescriptor);
	}
}
