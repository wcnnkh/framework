package io.basc.framework.orm.support;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.w3c.dom.NodeList;

import io.basc.framework.beans.factory.ServiceLoaderFactory;
import io.basc.framework.beans.factory.config.Configurable;
import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.convert.ConverterNotFoundException;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.repository.Column;
import io.basc.framework.data.repository.Condition;
import io.basc.framework.data.repository.Expression;
import io.basc.framework.data.repository.OperationSymbol;
import io.basc.framework.data.repository.Repository;
import io.basc.framework.dom.NodeListAccess;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.Mapping;
import io.basc.framework.mapper.MappingStrategy;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.mapper.support.DefaultObjectMapper;
import io.basc.framework.orm.DefaultEntityMapping;
import io.basc.framework.orm.DefaultProperty;
import io.basc.framework.orm.EntityMapper;
import io.basc.framework.orm.EntityMapping;
import io.basc.framework.orm.ForeignKey;
import io.basc.framework.orm.Property;
import io.basc.framework.orm.annotation.AnnotationObjectRelationalResolverExtend;
import io.basc.framework.orm.annotation.IgnoreConfigurationProperty;
import io.basc.framework.orm.filter.EntityOperationFilter;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Range;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.placeholder.PlaceholderFormat;
import io.basc.framework.util.placeholder.PlaceholderFormatAware;

public class DefaultEntityMapper extends DefaultObjectMapper
		implements EntityMapper, Configurable, PlaceholderFormatAware {
	private final AnnotationObjectRelationalResolverExtend annotationObjectRelationalResolverExtend = new AnnotationObjectRelationalResolverExtend();
	/**
	 * 是否将驼峰命名转换为下划线的名称 myAbc-> my_abc
	 */
	private boolean humpNamingReplacement = false;

	private final ConfigurableServices<EntityMappingResolverExtend> objectRelationalResolverExtendServices = new ConfigurableServices<EntityMappingResolverExtend>(
			EntityMappingResolverExtend.class);

	private final ConfigurableServices<EntityOperationFilter> entityOperationFilters = new ConfigurableServices<>(
			EntityOperationFilter.class);

	private boolean configured;

	public ConfigurableServices<EntityOperationFilter> getEntityOperationFilters() {
		return entityOperationFilters;
	}

	public DefaultEntityMapper() {
		registerObjectAccessFactory(NodeList.class, (s, e) -> new NodeListAccess(s, e));
		getMappingStrategy().getPredicateRegistry().and((parameter) -> {
			if (parameter.getTypeDescriptor().isAnnotationPresent(IgnoreConfigurationProperty.class)) {
				return false;
			}
			return true;
		});
		objectRelationalResolverExtendServices.register(annotationObjectRelationalResolverExtend);
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

	public void configurationProperties(Object source, TypeDescriptor sourceType, Object target,
			TypeDescriptor targetType) throws ConverterNotFoundException {
		if (source == null || target == null) {
			return;
		}

		if (!isConfigurable(targetType)) {
			return;
		}

		transform(source, sourceType, target, targetType);
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		this.objectRelationalResolverExtendServices.configure(serviceLoaderFactory);
		configured = true;
	}

	@Override
	public Elements<String> getAliasNames(Class<?> entityClass) {
		Elements<String> parentNames = EntityMappingResolverExtendChain
				.build(objectRelationalResolverExtendServices.getServices().iterator()).getAliasNames(entityClass);
		Set<String> names = new LinkedHashSet<>(8);
		// 如果没有使用过别名，那就设置默认名称
		String defaultName = getName(entityClass);
		names.add(defaultName);
		appendDefaultAliasNames(names, defaultName);
		return Elements.concat(parentNames, Elements.of(names));
	}

	@Override
	public Elements<String> getAliasNames(Class<?> entityClass, ParameterDescriptor descriptor) {
		Elements<String> parentNames = EntityMappingResolverExtendChain
				.build(objectRelationalResolverExtendServices.getServices().iterator())
				.getAliasNames(entityClass, descriptor);
		Set<String> names = new LinkedHashSet<>(8);
		String defaultName = getName(entityClass, descriptor);
		names.add(defaultName);
		appendDefaultAliasNames(names, defaultName);

		if (isEntity(entityClass, descriptor)) {
			getAliasNames(descriptor.getTypeDescriptor().getType()).forEach(names::add);
		}
		return Elements.concat(parentNames, Elements.of(names));
	}

	@Override
	public String getCharsetName(Class<?> entityClass) {
		return EntityMappingResolverExtendChain.build(objectRelationalResolverExtendServices.getServices().iterator())
				.getCharsetName(entityClass);
	}

	@Override
	public String getCharsetName(Class<?> entityClass, ParameterDescriptor descriptor) {
		return EntityMappingResolverExtendChain.build(objectRelationalResolverExtendServices.getServices().iterator())
				.getCharsetName(entityClass, descriptor);
	}

	@Override
	public Elements<? extends Column> getColumns(OperationSymbol operationSymbol, TypeDescriptor source,
			Parameter parameter) {
		return EntityMappingResolverExtendChain.build(objectRelationalResolverExtendServices.getServices().iterator())
				.getColumns(operationSymbol, source, parameter);
	}

	@Override
	public String getComment(Class<?> entityClass) {
		return EntityMappingResolverExtendChain.build(objectRelationalResolverExtendServices.getServices().iterator())
				.getComment(entityClass);
	}

	@Override
	public String getComment(Class<?> entityClass, ParameterDescriptor descriptor) {
		return EntityMappingResolverExtendChain.build(objectRelationalResolverExtendServices.getServices().iterator())
				.getComment(entityClass, descriptor);
	}

	@Override
	public Elements<? extends Condition> getConditions(OperationSymbol operationSymbol, TypeDescriptor source,
			Parameter parameter) {
		return EntityMappingResolverExtendChain.build(objectRelationalResolverExtendServices.getServices().iterator())
				.getConditions(operationSymbol, source, parameter);
	}

	private String getDefaultEntityName(Class<?> entityClass) {
		String className = entityClass.getSimpleName();
		return StringUtils.humpNamingReplacement(className, "_");
	}

	private String getDefaultName(ParameterDescriptor descriptor) {
		return humpNamingReplacement ? StringUtils.humpNamingReplacement(descriptor.getName(), "_")
				: descriptor.getName();
	}

	@Override
	public Elements<? extends Expression> getExpressions(OperationSymbol operationSymbol, TypeDescriptor source,
			Parameter parameter) {
		return EntityMappingResolverExtendChain.build(objectRelationalResolverExtendServices.getServices().iterator())
				.getExpressions(operationSymbol, source, parameter);
	}

	@Override
	public ForeignKey getForeignKey(Class<?> entityClass, ParameterDescriptor descriptor) {
		return EntityMappingResolverExtendChain.build(objectRelationalResolverExtendServices.getServices().iterator())
				.getForeignKey(entityClass, descriptor);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EntityMapping<? extends Property> getMapping(Class<?> entityClass) {
		Mapping<? extends Field> mapping = super.getMapping(entityClass);
		if (mapping == null) {
			synchronized (this) {
				mapping = super.getMapping(entityClass);
				if (mapping == null) {
					EntityMapping<? extends Property> entityMapping = EntityMapper.super.getMapping(entityClass);
					registerMapping(entityClass, entityMapping);
					return entityMapping;
				}
			}
		}

		if (mapping instanceof EntityMapping) {
			return (EntityMapping<? extends Property>) mapping;
		}

		if (isMappingRegistred(entityClass)) {
			// 递归
			return getMapping(entityClass);
		}

		synchronized (this) {
			if (isMappingRegistred(entityClass)) {
				// 递归
				return getMapping(entityClass);
			}

			EntityMapping<? extends Property> entityMapping = new DefaultEntityMapping<>(mapping,
					(e) -> new DefaultProperty(e, entityClass, this), entityClass, this);
			registerMapping(entityClass, entityMapping);
			return entityMapping;
		}
	}

	@Override
	public MappingStrategy getMappingStrategy(TypeDescriptor typeDescriptor) {
		MappingStrategy dottomlessMappingStrategy = super.getMappingStrategy(typeDescriptor);
		return getMappingStrategy(typeDescriptor, dottomlessMappingStrategy);
	}

	@Override
	public MappingStrategy getMappingStrategy(TypeDescriptor source, MappingStrategy dottomlessMappingStrategy) {
		return EntityMappingResolverExtendChain.build(objectRelationalResolverExtendServices.getServices().iterator())
				.getMappingStrategy(source, dottomlessMappingStrategy);
	}

	@Override
	public String getName(Class<?> entityClass) {
		String name = EntityMappingResolverExtendChain
				.build(objectRelationalResolverExtendServices.getServices().iterator()).getName(entityClass);
		if (StringUtils.isEmpty(name)) {
			name = getDefaultEntityName(entityClass);
		}
		return name;
	}

	@Override
	public String getName(Class<?> entityClass, ParameterDescriptor descriptor) {
		String name = EntityMappingResolverExtendChain
				.build(objectRelationalResolverExtendServices.getServices().iterator())
				.getName(entityClass, descriptor);
		return StringUtils.isEmpty(name) ? getDefaultName(descriptor) : name;
	}

	@Override
	public Elements<Range<Double>> getNumberRanges(Class<?> entityClass, ParameterDescriptor descriptor) {
		return EntityMappingResolverExtendChain.build(objectRelationalResolverExtendServices.getServices().iterator())
				.getNumberRanges(entityClass, descriptor);
	}

	public final ConfigurableServices<EntityMappingResolverExtend> getObjectRelationalResolverExtendServices() {
		return objectRelationalResolverExtendServices;
	}

	@Nullable
	public PlaceholderFormat getPlaceholderFormat() {
		return annotationObjectRelationalResolverExtend.getPlaceholderFormat();
	}

	@Override
	public Elements<? extends Repository> getRepositorys(OperationSymbol operationSymbol,
			TypeDescriptor entityTypeDescriptor, EntityMapping<? extends Property> entityMapping) {
		return EntityMappingResolverExtendChain.build(objectRelationalResolverExtendServices.getServices().iterator())
				.getRepositorys(operationSymbol, entityTypeDescriptor, entityMapping);
	}

	@Override
	public Elements<? extends io.basc.framework.data.repository.Sort> getSorts(OperationSymbol operationSymbol,
			TypeDescriptor source, ParameterDescriptor descriptor) {
		return EntityMappingResolverExtendChain.build(objectRelationalResolverExtendServices.getServices().iterator())
				.getSorts(operationSymbol, source, descriptor);
	}

	@Override
	public boolean hasEffectiveValue(TypeDescriptor source, Parameter parameter) {
		return EntityMappingResolverExtendChain.build(objectRelationalResolverExtendServices.getServices().iterator())
				.hasEffectiveValue(source, parameter);
	}

	@Override
	public boolean isAutoIncrement(Class<?> entityClass, ParameterDescriptor descriptor) {
		return EntityMappingResolverExtendChain.build(objectRelationalResolverExtendServices.getServices().iterator())
				.isAutoIncrement(entityClass, descriptor);
	}

	@Override
	public boolean isConfigurable(TypeDescriptor sourceType) {
		return EntityMappingResolverExtendChain.build(objectRelationalResolverExtendServices.getServices().iterator())
				.isConfigurable(sourceType);
	}

	public boolean isConfigured() {
		return configured;
	}

	@Override
	public boolean isDisplay(Class<?> entityClass, ParameterDescriptor descriptor) {
		return EntityMappingResolverExtendChain.build(objectRelationalResolverExtendServices.getServices().iterator())
				.isDisplay(entityClass, descriptor);
	}

	@Override
	public boolean isEntity(Class<?> entityClass, ParameterDescriptor descriptor) {
		return isEntity(descriptor.getTypeDescriptor()) || EntityMappingResolverExtendChain
				.build(objectRelationalResolverExtendServices.getServices().iterator())
				.isEntity(entityClass, descriptor);
	}

	@Override
	public boolean isEntity(TypeDescriptor source) {
		return super.isEntity(source) || EntityMappingResolverExtendChain
				.build(objectRelationalResolverExtendServices.getServices().iterator()).isEntity(source);
	}

	public boolean isHumpNamingReplacement() {
		return humpNamingReplacement;
	}

	@Override
	public boolean isIgnore(Class<?> entityClass) {
		return EntityMappingResolverExtendChain.build(objectRelationalResolverExtendServices.getServices().iterator())
				.isIgnore(entityClass);
	}

	@Override
	public boolean isIgnore(Class<?> entityClass, ParameterDescriptor descriptor) {
		return EntityMappingResolverExtendChain.build(objectRelationalResolverExtendServices.getServices().iterator())
				.isIgnore(entityClass, descriptor);
	}

	@Override
	public boolean isIncrement(Class<?> entityClass, ParameterDescriptor descriptor) {
		return EntityMappingResolverExtendChain.build(objectRelationalResolverExtendServices.getServices().iterator())
				.isIncrement(entityClass, descriptor);
	}

	@Override
	public boolean isNullable(Class<?> entityClass, ParameterDescriptor descriptor) {
		return !isPrimaryKey(entityClass, descriptor) && EntityMappingResolverExtendChain
				.build(objectRelationalResolverExtendServices.getServices().iterator())
				.isNullable(entityClass, descriptor);
	}

	@Override
	public boolean isPrimaryKey(Class<?> entityClass, ParameterDescriptor descriptor) {
		return EntityMappingResolverExtendChain.build(objectRelationalResolverExtendServices.getServices().iterator())
				.isPrimaryKey(entityClass, descriptor);
	}

	@Override
	public boolean isUnique(Class<?> entityClass, ParameterDescriptor descriptor) {
		return EntityMappingResolverExtendChain.build(objectRelationalResolverExtendServices.getServices().iterator())
				.isUnique(entityClass, descriptor);
	}

	@Override
	public boolean isVersion(Class<?> entityClass, ParameterDescriptor descriptor) {
		return EntityMappingResolverExtendChain.build(objectRelationalResolverExtendServices.getServices().iterator())
				.isVersion(entityClass, descriptor);
	}

	public void setHumpNamingReplacement(boolean humpNamingReplacement) {
		this.humpNamingReplacement = humpNamingReplacement;
	}

	@Override
	public void setPlaceholderFormat(@Nullable PlaceholderFormat placeholderFormat) {
		this.annotationObjectRelationalResolverExtend.setPlaceholderFormat(placeholderFormat);
	}
}
