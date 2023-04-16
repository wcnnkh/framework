package io.basc.framework.sql.orm.support;

import java.util.Collection;
import java.util.LinkedHashSet;

import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.mapper.FieldDescriptor;
import io.basc.framework.orm.ObjectRelational;
import io.basc.framework.orm.Property;
import io.basc.framework.sql.ResultSetMapper;
import io.basc.framework.sql.orm.Column;
import io.basc.framework.sql.orm.IndexInfo;
import io.basc.framework.sql.orm.TableMapper;
import io.basc.framework.sql.orm.TableStructure;
import io.basc.framework.sql.orm.annotation.AnnotationTableResolverExtend;

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
	public Boolean isAutoCreate(Class<?> entityClass) {
		return TableResolverExtendChain.build(tableResolverExtends.iterator()).isAutoCreate(entityClass);
	}

	@Override
	public TableStructure getStructure(Class<?> entityClass) {
		if (!isStructureRegistred(entityClass)) {
			return TableMapper.super.getStructure(entityClass);
		}

		ObjectRelational<? extends Property> objectRelational = super.getStructure(entityClass);
		if (objectRelational instanceof TableStructure) {
			return (TableStructure) objectRelational;
		}

		return new TableStructure(objectRelational, (e) -> new Column(e, this));
	}
}
