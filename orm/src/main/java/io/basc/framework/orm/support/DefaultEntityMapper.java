package io.basc.framework.orm.support;

import java.util.Iterator;

import org.w3c.dom.NodeList;

import io.basc.framework.beans.factory.ServiceLoaderFactory;
import io.basc.framework.beans.factory.config.Configurable;
import io.basc.framework.core.convert.ConverterNotFoundException;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.env.SystemProperties;
import io.basc.framework.core.execution.Parameter;
import io.basc.framework.core.execution.ParameterDescriptor;
import io.basc.framework.data.repository.Condition;
import io.basc.framework.data.repository.Expression;
import io.basc.framework.data.repository.IndexInfo;
import io.basc.framework.data.repository.OperationSymbol;
import io.basc.framework.data.repository.Sort;
import io.basc.framework.dom.NodeListProperties;
import io.basc.framework.mapper.stereotype.DefaultObjectMapper;
import io.basc.framework.mapper.stereotype.FieldDescriptor;
import io.basc.framework.mapper.stereotype.Mapping;
import io.basc.framework.orm.ColumnDescriptor;
import io.basc.framework.orm.DefaultColumnDescriptor;
import io.basc.framework.orm.DefaultEntityMapping;
import io.basc.framework.orm.EntityMapper;
import io.basc.framework.orm.EntityMapping;
import io.basc.framework.orm.EntityRepository;
import io.basc.framework.orm.ForeignKey;
import io.basc.framework.orm.config.ConfigurableAnalyzer;
import io.basc.framework.orm.config.DefaultAnalyzer;
import io.basc.framework.transform.strategy.PropertiesTransformStrategy;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Range;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class DefaultEntityMapper extends DefaultObjectMapper implements EntityMapper, Configurable {
	/**
	 * 默认对象主键的连接符
	 */
	private static final String ENTITY_KEY_CONNECTOR = SystemProperties
			.getProperty("io.basc.framework.orm.key.connector").or(":").getAsString();

	@NonNull
	private String entityKeyConnector = ENTITY_KEY_CONNECTOR;

	private final ConfigurableAnalyzer entityResolver = new DefaultAnalyzer();

	private boolean configured;

	public DefaultEntityMapper() {
		registerPropertiesTransformer(NodeList.class, (s, e) -> new NodeListProperties(s));
		getPropertiesTransformStrategy().and((s, parameter) -> {
			if (isIgnore(s.getType(), parameter)) {
				return false;
			}
			return true;
		});
	}

	public void configurationProperties(Object source, TypeDescriptor sourceType, Object target,
			TypeDescriptor targetType) throws ConverterNotFoundException {
		if (source == null || target == null) {
			return;
		}

		transform(source, sourceType, target, targetType);
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		this.entityResolver.configure(serviceLoaderFactory);
		configured = true;
	}

	@Override
	public Elements<String> getAliasNames(Class<?> entityClass) {
		return entityResolver.getAliasNames(entityClass);
	}

	@Override
	public Elements<String> getAliasNames(Class<?> entityClass, ParameterDescriptor descriptor) {
		return entityResolver.getAliasNames(entityClass, descriptor);
	}

	@Override
	public String getCharsetName(Class<?> entityClass) {
		return entityResolver.getCharsetName(entityClass);
	}

	@Override
	public String getCharsetName(Class<?> entityClass, ParameterDescriptor descriptor) {
		return entityResolver.getCharsetName(entityClass, descriptor);
	}

	@Override
	public <T> String getRepositoryName(OperationSymbol operationSymbol, EntityMapping<?> entityMapping,
			Class<? extends T> entityClass, T entity) {
		return entityResolver.getRepositoryName(operationSymbol, entityMapping, entityClass, entity);
	}

	@Override
	public <T> Expression getColumn(OperationSymbol operationSymbol, EntityRepository<T> repository,
			Parameter parameter, ColumnDescriptor property) {
		return entityResolver.getColumn(operationSymbol, repository, parameter, property);
	}

	@Override
	public <T> Condition getCondition(OperationSymbol operationSymbol, EntityRepository<T> repository,
			Parameter parameter, ColumnDescriptor property) {
		return entityResolver.getCondition(operationSymbol, repository, parameter, property);
	}

	@Override
	public <T> Sort getSort(OperationSymbol operationSymbol, EntityRepository<T> repository, Parameter parameter,
			ColumnDescriptor property) {
		return entityResolver.getSort(operationSymbol, repository, parameter, property);
	}

	@Override
	public String getComment(Class<?> entityClass) {
		return entityResolver.getComment(entityClass);
	}

	@Override
	public String getComment(Class<?> entityClass, ParameterDescriptor descriptor) {
		return entityResolver.getComment(entityClass, descriptor);
	}

	@Override
	public ForeignKey getForeignKey(Class<?> entityClass, ParameterDescriptor descriptor) {
		return entityResolver.getForeignKey(entityClass, descriptor);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EntityMapping<? extends ColumnDescriptor> getMapping(Class<?> entityClass) {
		Mapping<? extends FieldDescriptor> mapping = super.getMapping(entityClass);
		if (mapping instanceof EntityMapping) {
			return (EntityMapping<? extends ColumnDescriptor>) mapping;
		}
		// 不会为空
		synchronized (this) {
			mapping = super.getMapping(entityClass);
			if (mapping instanceof EntityMapping) {
				return (EntityMapping<? extends ColumnDescriptor>) mapping;
			}

			EntityMapping<ColumnDescriptor> entityMapping = new DefaultEntityMapping<>(mapping,
					(e) -> new DefaultColumnDescriptor(e, entityClass, this), entityClass, this);
			registerMapping(entityClass, entityMapping);
			return entityMapping;
		}
	}

	@Override
	public PropertiesTransformStrategy getPropertiesTransformStrategy(TypeDescriptor typeDescriptor) {
		PropertiesTransformStrategy dottomlessStrategy = super.getPropertiesTransformStrategy(typeDescriptor);
		return getPropertiesTransformStrategy(typeDescriptor, dottomlessStrategy);
	}

	@Override
	public PropertiesTransformStrategy getPropertiesTransformStrategy(TypeDescriptor source,
			PropertiesTransformStrategy dottomlessStrategy) {
		return entityResolver.getPropertiesTransformStrategy(source, dottomlessStrategy);
	}

	@Override
	public String getName(Class<?> entityClass) {
		return entityResolver.getName(entityClass);
	}

	@Override
	public String getName(Class<?> entityClass, ParameterDescriptor descriptor) {
		return entityResolver.getName(entityClass, descriptor);
	}

	@Override
	public Elements<Range<Double>> getNumberRanges(Class<?> entityClass, ParameterDescriptor descriptor) {
		return entityResolver.getNumberRanges(entityClass, descriptor);
	}

	@Override
	public boolean hasEffectiveValue(Parameter parameter) {
		return entityResolver.hasEffectiveValue(parameter);
	}

	@Override
	public boolean isAutoIncrement(Class<?> entityClass, ParameterDescriptor descriptor) {
		return entityResolver.isAutoIncrement(entityClass, descriptor);
	}

	public boolean isConfigured() {
		return configured;
	}

	@Override
	public boolean isDisplay(Class<?> entityClass, ParameterDescriptor descriptor) {
		return entityResolver.isDisplay(entityClass, descriptor);
	}

	@Override
	public boolean isEntity(TypeDescriptor source, ParameterDescriptor descriptor) {
		return isEntity(descriptor.getTypeDescriptor()) || entityResolver.isEntity(source, descriptor);
	}

	@Override
	public boolean isEntity(TypeDescriptor source) {
		return super.isEntity(source) || entityResolver.isEntity(source);
	}

	@Override
	public boolean isIgnore(Class<?> entityClass) {
		return entityResolver.isIgnore(entityClass);
	}

	@Override
	public boolean isIgnore(Class<?> entityClass, ParameterDescriptor descriptor) {
		return entityResolver.isIgnore(entityClass, descriptor);
	}

	@Override
	public boolean isIncrement(Class<?> entityClass, ParameterDescriptor descriptor) {
		return entityResolver.isIncrement(entityClass, descriptor);
	}

	@Override
	public boolean isNullable(Class<?> entityClass, ParameterDescriptor descriptor) {
		return !isPrimaryKey(entityClass, descriptor) && entityResolver.isNullable(entityClass, descriptor);
	}

	@Override
	public boolean isPrimaryKey(Class<?> entityClass, ParameterDescriptor descriptor) {
		return entityResolver.isPrimaryKey(entityClass, descriptor);
	}

	@Override
	public boolean isUnique(Class<?> entityClass, ParameterDescriptor descriptor) {
		return entityResolver.isUnique(entityClass, descriptor);
	}

	@Override
	public boolean isVersion(Class<?> entityClass, ParameterDescriptor descriptor) {
		return entityResolver.isVersion(entityClass, descriptor);
	}

	private void appendObjectKeyByValue(StringBuilder appendable, ColumnDescriptor property, Object value) {
		appendable.append(entityKeyConnector);
		appendable.append(property.getName());
		appendable.append(entityKeyConnector);
		String str = String.valueOf(value);
		str = str.replaceAll(entityKeyConnector, "\\" + entityKeyConnector);
		appendable.append(str);
	}

	@Override
	public String getEntityKey(EntityRepository<?> repository, Iterator<? extends ColumnDescriptor> propertyIterator,
			Iterator<? extends Object> valueIterator) {
		StringBuilder sb = new StringBuilder(128);
		sb.append(repository.getName());
		while (propertyIterator.hasNext() && valueIterator.hasNext()) {
			ColumnDescriptor property = propertyIterator.next();
			Object value = valueIterator.next();
			appendObjectKeyByValue(sb, property, value);
		}
		return sb.toString();
	}

	@Override
	public Elements<IndexInfo> getIndexs(Class<?> sourceClass, ParameterDescriptor descriptor) {
		return entityResolver.getIndexs(sourceClass, descriptor);
	}
}
