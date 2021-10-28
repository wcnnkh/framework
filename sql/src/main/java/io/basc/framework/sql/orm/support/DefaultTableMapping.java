package io.basc.framework.sql.orm.support;

import java.util.Collection;
import java.util.LinkedHashSet;

import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.mapper.FieldDescriptor;
import io.basc.framework.orm.support.DefaultObjectRelationalMapping;
import io.basc.framework.sql.orm.IndexInfo;
import io.basc.framework.sql.orm.TableMapping;
import io.basc.framework.sql.orm.annotation.AnnotationTableResolverExtend;

public class DefaultTableMapping extends DefaultObjectRelationalMapping implements TableMapping {
	private final ConfigurableServices<TableResolverExtend> tableResolverExtends = new ConfigurableServices<>(
			TableResolverExtend.class);

	public DefaultTableMapping() {
		tableResolverExtends.addService(new AnnotationTableResolverExtend());
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
}
