package io.basc.framework.sql.orm.support;

import java.util.Collection;

import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.mapper.FieldDescriptor;
import io.basc.framework.orm.support.AbstractObjectRelationalExtend;
import io.basc.framework.sql.orm.IndexInfo;
import io.basc.framework.sql.orm.TableResolver;
import io.basc.framework.sql.orm.annotation.Table;
import io.basc.framework.util.StringUtils;

public class AbstractTableResolverExtend extends AbstractObjectRelationalExtend implements TableResolverExtend {

	@Override
	public Collection<IndexInfo> getIndexs(Class<?> entityClass, FieldDescriptor descriptor, TableResolver chain) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEngine(Class<?> entityClass, TableResolver chain) {
		Table table = AnnotatedElementUtils.getMergedAnnotation(entityClass, Table.class);
		if (table != null && StringUtils.hasText(table.engine())) {
			return table.engine();
		}
		return chain.getEngine(entityClass);
	}

	@Override
	public String getRowFormat(Class<?> entityClass, TableResolver chain) {
		Table table = AnnotatedElementUtils.getMergedAnnotation(entityClass, Table.class);
		if (table != null && StringUtils.hasText(table.rowFormat())) {
			return table.rowFormat();
		}
		return chain.getRowFormat(entityClass);
	}

}
