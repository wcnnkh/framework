package io.basc.framework.sql.orm.support;

import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.orm.EntityMapping;
import io.basc.framework.sql.ResultSetMapper;
import io.basc.framework.sql.orm.Column;
import io.basc.framework.sql.orm.DefaultColumn;
import io.basc.framework.sql.orm.DefaultTableMapping;
import io.basc.framework.sql.orm.IndexInfo;
import io.basc.framework.sql.orm.TableMapper;
import io.basc.framework.sql.orm.TableMapping;
import io.basc.framework.sql.orm.annotation.AnnotationTableResolverExtend;
import io.basc.framework.util.Elements;

public class DefaultTableMapper extends ResultSetMapper implements TableMapper {
	private final ConfigurableServices<TableResolverExtend> tableResolverExtends = new ConfigurableServices<>(
			TableResolverExtend.class);

	public DefaultTableMapper() {
		AnnotationTableResolverExtend tableResolverExtend = new AnnotationTableResolverExtend();
		getObjectRelationalResolverExtendServices().register(tableResolverExtend);
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
		return TableResolverExtendChain.build(tableResolverExtends.iterator()).getIndexs(entityClass, descriptor);
	}

	@Override
	public String getEngine(Class<?> entityClass) {
		return TableResolverExtendChain.build(tableResolverExtends.iterator()).getEngine(entityClass);
	}

	@Override
	public String getRowFormat(Class<?> entityClass) {
		return TableResolverExtendChain.build(tableResolverExtends.iterator()).getRowFormat(entityClass);
	}

	@Override
	public boolean isAutoCreate(Class<?> entityClass) {
		return TableResolverExtendChain.build(tableResolverExtends.iterator()).isAutoCreate(entityClass);
	}

	@SuppressWarnings("unchecked")
	@Override
	public TableMapping<? extends Column> getMapping(Class<?> entityClass) {
		EntityMapping<? extends Field> mapping = super.getMapping(entityClass);
		if (mapping == null) {
			synchronized (this) {
				mapping = super.getMapping(entityClass);
				if (mapping == null) {
					TableMapping<? extends Column> tableMapping = TableMapper.super.getMapping(entityClass);
					registerMapping(entityClass, tableMapping);
					return tableMapping;
				}
			}
		}

		if (mapping instanceof TableMapping) {
			return (TableMapping<? extends Column>) mapping;
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

			TableMapping<? extends Column> tableMapping = new DefaultTableMapping<>(mapping,
					(e) -> new DefaultColumn(e, entityClass, this), entityClass, this);
			registerMapping(entityClass, tableMapping);
			return tableMapping;
		}
	}
}
