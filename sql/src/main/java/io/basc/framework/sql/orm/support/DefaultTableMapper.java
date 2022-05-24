package io.basc.framework.sql.orm.support;

import java.util.Collection;
import java.util.LinkedHashSet;

import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.FieldDescriptor;
import io.basc.framework.mapper.Fields;
import io.basc.framework.orm.StructureRegistry;
import io.basc.framework.orm.support.SimpleStructureRegistry;
import io.basc.framework.sql.ResultSetMapper;
import io.basc.framework.sql.orm.IndexInfo;
import io.basc.framework.sql.orm.TableMapper;
import io.basc.framework.sql.orm.TableStructure;
import io.basc.framework.sql.orm.annotation.AnnotationTableResolverExtend;

public class DefaultTableMapper extends ResultSetMapper implements TableMapper {
	private final ConfigurableServices<TableResolverExtend> tableResolverExtends = new ConfigurableServices<>(
			TableResolverExtend.class);
	private final StructureRegistry<TableStructure> registry = new SimpleStructureRegistry<TableStructure>();

	public DefaultTableMapper() {
		AnnotationTableResolverExtend tableResolverExtend = new AnnotationTableResolverExtend();
		addService(tableResolverExtend);
		tableResolverExtends.addService(tableResolverExtend);
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
	public Collection<IndexInfo> getIndexs(Class<?> entityClass, FieldDescriptor descriptor) {
		Collection<IndexInfo> indexInfos = TableResolverExtendChain.build(tableResolverExtends.iterator())
				.getIndexs(entityClass, descriptor);
		if (indexInfos == null) {
			indexInfos = new LinkedHashSet<>(4);
		}
		return indexInfos;
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
	public Fields getFields(Class<?> entityClass, Field parentField) {
		return super.getFields(entityClass, parentField).entity();
	}

	@Override
	public boolean isStructureRegistred(Class<?> entityClass) {
		return registry.isStructureRegistred(entityClass);
	}

	@Override
	public TableStructure getStructure(Class<?> entityClass) {
		if (registry.isStructureRegistred(entityClass)) {
			return registry.getStructure(entityClass);
		}
		return TableMapper.super.getStructure(entityClass);
	}

	@Override
	public void registerStructure(Class<?> entityClass, TableStructure structure) {
		registry.registerStructure(entityClass, structure);
	}
}
