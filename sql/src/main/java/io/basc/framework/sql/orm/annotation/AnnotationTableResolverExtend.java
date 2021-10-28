package io.basc.framework.sql.orm.annotation;

import java.util.ArrayList;
import java.util.Collection;

import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.mapper.FieldDescriptor;
import io.basc.framework.sql.orm.IndexInfo;
import io.basc.framework.sql.orm.TableResolver;
import io.basc.framework.sql.orm.support.TableResolverExtend;
import io.basc.framework.util.StringUtils;

public class AnnotationTableResolverExtend implements TableResolverExtend {

	@Override
	public Collection<IndexInfo> getIndexs(Class<?> entityClass, FieldDescriptor descriptor, TableResolver chain) {
		Collection<IndexInfo> indexInfos = chain.getIndexs(entityClass, descriptor);
		Index index = AnnotatedElementUtils.getMergedAnnotation(descriptor, Index.class);
		if (index != null) {
			if (indexInfos == null) {
				indexInfos = new ArrayList<>(4);
			}
			IndexInfo indexInfo = new IndexInfo(index.name(), index.type(), index.length(), index.method(),
					index.order());
			indexInfos.add(indexInfo);
		}
		return indexInfos;
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
