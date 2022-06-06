package io.basc.framework.orm.support;

import java.util.Collection;
import java.util.LinkedHashSet;

import io.basc.framework.aop.support.ProxyUtils;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.data.domain.Range;
import io.basc.framework.factory.Configurable;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.mapper.AbstractObjectMapper;
import io.basc.framework.mapper.ObjectMapper;
import io.basc.framework.orm.ForeignKey;
import io.basc.framework.orm.ObjectRelational;
import io.basc.framework.orm.ObjectRelationalMapper;
import io.basc.framework.orm.Property;
import io.basc.framework.orm.annotation.AnnotationObjectRelationalResolverExtend;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.comparator.Sort;

public abstract class AbstractObjectRelationalMapper<S, E extends Throwable> extends AbstractObjectMapper<S, E>
		implements ObjectRelationalMapper, ObjectMapper<S, E>, Configurable {
	private final ConfigurableServices<ObjectRelationalResolverExtend> objectRelationalResolverExtendServices = new ConfigurableServices<ObjectRelationalResolverExtend>(
			ObjectRelationalResolverExtend.class);

	public AbstractObjectRelationalMapper() {
		objectRelationalResolverExtendServices.addService(new AnnotationObjectRelationalResolverExtend());
	}

	@Override
	public ObjectRelational<? extends Property> getStructure(Class<?> entityClass) {
		return ObjectRelationalMapper.super.getStructure(entityClass);
	}

	public final ConfigurableServices<ObjectRelationalResolverExtend> getObjectRelationalResolverExtendServices() {
		return objectRelationalResolverExtendServices;
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		this.objectRelationalResolverExtendServices.configure(serviceLoaderFactory);
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
	public Boolean isIgnore(Class<?> entityClass, ParameterDescriptor descriptor) {
		Boolean v = ObjectRelationalResolverExtendChain.build(objectRelationalResolverExtendServices.iterator())
				.isIgnore(entityClass, descriptor);
		return v == null ? false : v;
	}

	private String getDefaultName(ParameterDescriptor descriptor) {
		return humpNamingReplacement ? StringUtils.humpNamingReplacement(descriptor.getName(), "_")
				: descriptor.getName();
	}

	@Override
	public String getName(Class<?> entityClass, ParameterDescriptor descriptor) {
		String name = ObjectRelationalResolverExtendChain.build(objectRelationalResolverExtendServices.iterator())
				.getName(entityClass, descriptor);
		return StringUtils.isEmpty(name) ? getDefaultName(descriptor) : name;
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
	public Collection<String> getAliasNames(Class<?> entityClass, ParameterDescriptor descriptor) {
		Collection<String> names = ObjectRelationalResolverExtendChain
				.build(objectRelationalResolverExtendServices.iterator()).getAliasNames(entityClass, descriptor);
		if (CollectionUtils.isEmpty(names)) {
			names = new LinkedHashSet<String>();
			String defaultName = getName(entityClass, descriptor);
			names.add(defaultName);
			appendDefaultAliasNames(names, defaultName);
		}

		if (isEntity(entityClass, descriptor)) {
			names.addAll(getAliasNames(descriptor.getType()));
		}
		return names;
	}

	private String getDefaultEntityName(Class<?> entityClass) {
		String className = ProxyUtils.getFactory().getUserClass(entityClass).getSimpleName();
		return StringUtils.humpNamingReplacement(className, "_");
	}

	@Override
	public String getName(Class<?> entityClass) {
		String name = ObjectRelationalResolverExtendChain.build(objectRelationalResolverExtendServices.iterator())
				.getName(entityClass);
		if (StringUtils.isEmpty(name)) {
			name = getDefaultEntityName(entityClass);
		}
		return name;
	}

	@Override
	public Collection<String> getAliasNames(Class<?> entityClass) {
		Collection<String> names = ObjectRelationalResolverExtendChain
				.build(objectRelationalResolverExtendServices.iterator()).getAliasNames(entityClass);
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
	public Boolean isPrimaryKey(Class<?> entityClass, ParameterDescriptor descriptor) {
		Boolean v = ObjectRelationalResolverExtendChain.build(objectRelationalResolverExtendServices.iterator())
				.isPrimaryKey(entityClass, descriptor);
		return v == null ? false : v;
	}

	@Override
	public Boolean isNullable(Class<?> entityClass, ParameterDescriptor descriptor) {
		Boolean v = ObjectRelationalResolverExtendChain.build(objectRelationalResolverExtendServices.iterator())
				.isNullable(entityClass, descriptor);
		return v == null ? !isPrimaryKey(entityClass, descriptor) : v;
	}

	@Override
	public Boolean isEntity(Class<?> entityClass, ParameterDescriptor descriptor) {
		Boolean v = ObjectRelationalResolverExtendChain.build(objectRelationalResolverExtendServices.iterator())
				.isEntity(entityClass, descriptor);
		return v == null ? isEntity(descriptor.getType()) : v;
	}

	@Override
	public Boolean isEntity(Class<?> entityClass) {
		Boolean v = ObjectRelationalResolverExtendChain.build(objectRelationalResolverExtendServices.iterator())
				.isEntity(entityClass);
		return v == null ? false : v;
	}

	@Override
	public Boolean isVersionField(Class<?> entityClass, ParameterDescriptor descriptor) {
		Boolean v = ObjectRelationalResolverExtendChain.build(objectRelationalResolverExtendServices.iterator())
				.isVersionField(entityClass, descriptor);
		return v == null ? false : v;
	}

	@Override
	public Collection<Range<Double>> getNumberRanges(Class<?> entityClass, ParameterDescriptor descriptor) {
		return ObjectRelationalResolverExtendChain.build(objectRelationalResolverExtendServices.iterator())
				.getNumberRanges(entityClass, descriptor);
	}

	@Override
	public Boolean isAutoIncrement(Class<?> entityClass, ParameterDescriptor descriptor) {
		Boolean v = ObjectRelationalResolverExtendChain.build(objectRelationalResolverExtendServices.iterator())
				.isAutoIncrement(entityClass, descriptor);
		return v == null ? false : v;
	}

	@Override
	public Boolean isIgnore(Class<?> entityClass) {
		Boolean v = ObjectRelationalResolverExtendChain.build(objectRelationalResolverExtendServices.iterator())
				.isIgnore(entityClass);
		return v == null ? false : v;
	}

	@Override
	public String getComment(Class<?> entityClass) {
		return ObjectRelationalResolverExtendChain.build(objectRelationalResolverExtendServices.iterator())
				.getComment(entityClass);
	}

	@Override
	public String getComment(Class<?> entityClass, ParameterDescriptor descriptor) {
		return ObjectRelationalResolverExtendChain.build(objectRelationalResolverExtendServices.iterator())
				.getComment(entityClass, descriptor);
	}

	@Override
	public String getCharsetName(Class<?> entityClass) {
		return ObjectRelationalResolverExtendChain.build(objectRelationalResolverExtendServices.iterator())
				.getCharsetName(entityClass);
	}

	@Override
	public String getCharsetName(Class<?> entityClass, ParameterDescriptor descriptor) {
		return ObjectRelationalResolverExtendChain.build(objectRelationalResolverExtendServices.iterator())
				.getCharsetName(entityClass, descriptor);
	}

	@Override
	public Boolean isUnique(Class<?> entityClass, ParameterDescriptor descriptor) {
		Boolean v = ObjectRelationalResolverExtendChain.build(objectRelationalResolverExtendServices.iterator())
				.isUnique(entityClass, descriptor);
		return v == null ? false : v;
	}

	@Override
	public Boolean isIncrement(Class<?> entityClass, ParameterDescriptor descriptor) {
		Boolean v = ObjectRelationalResolverExtendChain.build(objectRelationalResolverExtendServices.iterator())
				.isIncrement(entityClass, descriptor);
		return v == null ? false : v;
	}

	@Override
	public Sort getSort(Class<?> entityClass, ParameterDescriptor descriptor) {
		return ObjectRelationalResolverExtendChain.build(objectRelationalResolverExtendServices.iterator())
				.getSort(entityClass, descriptor);
	}

	@Override
	public String getCondition(Class<?> entityClass, ParameterDescriptor descriptor) {
		return ObjectRelationalResolverExtendChain.build(objectRelationalResolverExtendServices.iterator())
				.getCondition(entityClass, descriptor);
	}

	@Override
	public String getRelationship(Class<?> entityClass, ParameterDescriptor descriptor) {
		return ObjectRelationalResolverExtendChain.build(objectRelationalResolverExtendServices.iterator())
				.getRelationship(entityClass, descriptor);
	}

	@Override
	public ForeignKey getForeignKey(Class<?> entityClass, ParameterDescriptor descriptor) {
		return ObjectRelationalResolverExtendChain.build(objectRelationalResolverExtendServices.iterator())
				.getForeignKey(entityClass, descriptor);
	}

	@Override
	public boolean isDisplay(Class<?> entityClass, ParameterDescriptor descriptor) {
		return ObjectRelationalResolverExtendChain.build(objectRelationalResolverExtendServices.iterator())
				.isDisplay(entityClass, descriptor);
	}

}
