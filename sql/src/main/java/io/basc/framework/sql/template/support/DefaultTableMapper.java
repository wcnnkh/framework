package io.basc.framework.sql.template.support;

import io.basc.framework.beans.factory.ServiceLoaderFactory;
import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.orm.EntityMapping;
import io.basc.framework.sql.ResultSetMapper;
import io.basc.framework.sql.template.Column;
import io.basc.framework.sql.template.IndexInfo;
import io.basc.framework.sql.template.TableMapper;
import io.basc.framework.sql.template.TableMapping;
import io.basc.framework.sql.template.annotation.AnnotationTableResolverExtend;
import io.basc.framework.sql.template.config.TableResolverExtend;
import io.basc.framework.sql.template.config.TableResolverExtendChain;
import io.basc.framework.util.Elements;

public class DefaultTableMapper extends ResultSetMapper implements TableMapper {
	private final ConfigurableServices<TableResolverExtend> tableResolverExtends = new ConfigurableServices<>(
			TableResolverExtend.class);

	public DefaultTableMapper() {
		AnnotationTableResolverExtend tableResolverExtend = new AnnotationTableResolverExtend();
		getentityResolverExtendServices().register(tableResolverExtend);
		tableResolverExtends.register(tableResolverExtend);
	}

	public ConfigurableServices<TableResolverExtend> getTableResolverExtends() {
		return tableResolverExtends;
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		tableResolverExtends.configure(serviceLoaderFactory);
		super.configure(serviceLoaderFactory);
	}

	@Override
	public Elements<IndexInfo> getIndexs(Class<?> entityClass, ParameterDescriptor descriptor) {
		return TableResolverExtendChain.build(tableResolverExtends.getServices().iterator()).getIndexs(entityClass,
				descriptor);
	}

	@Override
	public String getEngine(Class<?> entityClass) {
		return TableResolverExtendChain.build(tableResolverExtends.getServices().iterator()).getEngine(entityClass);
	}

	@Override
	public String getRowFormat(Class<?> entityClass) {
		return TableResolverExtendChain.build(tableResolverExtends.getServices().iterator()).getRowFormat(entityClass);
	}

	@Override
	public boolean isAutoCreate(Class<?> entityClass) {
		return TableResolverExtendChain.build(tableResolverExtends.getServices().iterator()).isAutoCreate(entityClass);
	}

	@SuppressWarnings("unchecked")
	@Override
	public TableMapping<? extends Column> getMapping(Class<?> entityClass) {
		EntityMapping<? extends Field> mapping = super.getMapping(entityClass);
		if (mapping instanceof TableMapping) {
			return (TableMapping<? extends Column>) mapping;
		}
		synchronized (this) {
			mapping = super.getMapping(entityClass);
			if (mapping instanceof TableMapping) {
				return (TableMapping<? extends Column>) mapping;
			}

			TableMapping<? extends Column> tableMapping = TableMapper.super.getMapping(entityClass);
			registerMapping(entityClass, tableMapping);
			return tableMapping;
		}
	}
}
