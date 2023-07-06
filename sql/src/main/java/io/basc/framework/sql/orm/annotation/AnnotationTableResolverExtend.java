package io.basc.framework.sql.orm.annotation;

import io.basc.framework.core.Ordered;
import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.orm.EntityMappingResolver;
import io.basc.framework.orm.config.EntityMappingResolverExtend;
import io.basc.framework.sql.orm.IndexInfo;
import io.basc.framework.sql.orm.TableResolver;
import io.basc.framework.sql.orm.support.TableResolverExtend;
import io.basc.framework.util.Elements;
import io.basc.framework.util.StringUtils;

public class AnnotationTableResolverExtend implements EntityMappingResolverExtend, TableResolverExtend, Ordered {

	@Override
	public int getOrder() {
		return Ordered.DEFAULT_PRECEDENCE - 1;
	}

	@Override
	public String getCharsetName(Class<?> entityClass, EntityMappingResolver chain) {
		Table table = AnnotatedElementUtils.getMergedAnnotation(entityClass, Table.class);
		if (table != null && StringUtils.hasText(table.charset())) {
			return table.charset();
		}
		return EntityMappingResolverExtend.super.getCharsetName(entityClass, chain);
	}

	@Override
	public String getName(Class<?> entityClass, EntityMappingResolver chain) {
		Table table = AnnotatedElementUtils.getMergedAnnotation(entityClass, Table.class);
		if (table != null && StringUtils.hasText(table.name())) {
			return table.name();
		}
		return EntityMappingResolverExtend.super.getName(entityClass, chain);
	}

	@Override
	public Elements<IndexInfo> getIndexs(Class<?> entityClass, ParameterDescriptor descriptor, TableResolver chain) {
		Elements<IndexInfo> indexs = chain.getIndexs(entityClass, descriptor);
		Index index = AnnotatedElementUtils.getMergedAnnotation(descriptor.getTypeDescriptor(), Index.class);
		if (index != null) {
			IndexInfo indexInfo = new IndexInfo(index.name(), index.type(), index.length(), index.method(),
					index.order());
			if (indexs == null) {
				indexs = Elements.singleton(indexInfo);
			} else {
				indexs = indexs.concat(Elements.singleton(indexInfo));
			}
		}
		return indexs;
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
