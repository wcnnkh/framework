package io.basc.framework.jdbc.template.config;

import java.util.Iterator;

import io.basc.framework.jdbc.template.IndexInfo;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.element.Elements;
import io.basc.framework.value.ParameterDescriptor;

public class TableResolverExtendChain implements TableResolver {
	private final Iterator<TableResolverExtend> iterator;
	private final TableResolver nextChain;

	public TableResolverExtendChain(Iterator<TableResolverExtend> iterator) {
		this(iterator, null);
	}

	public TableResolverExtendChain(Iterator<TableResolverExtend> iterator, @Nullable TableResolver nextChain) {
		Assert.requiredArgument(iterator != null, "iterator");
		this.iterator = iterator;
		this.nextChain = nextChain;
	}

	public static TableResolver build(Iterator<TableResolverExtend> iterator) {
		return new TableResolverExtendChain(iterator);
	}

	@Override
	public Elements<IndexInfo> getIndexs(Class<?> entityClass, ParameterDescriptor descriptor) {
		if (iterator.hasNext()) {
			return iterator.next().getIndexs(entityClass, descriptor, this);
		}
		return nextChain == null ? Elements.empty() : nextChain.getIndexs(entityClass, descriptor);
	}

	@Override
	public String getEngine(Class<?> entityClass) {
		if (iterator.hasNext()) {
			return iterator.next().getEngine(entityClass, this);
		}
		return nextChain == null ? null : nextChain.getEngine(entityClass);
	}

	@Override
	public String getRowFormat(Class<?> entityClass) {
		if (iterator.hasNext()) {
			return iterator.next().getRowFormat(entityClass, this);
		}
		return nextChain == null ? null : nextChain.getRowFormat(entityClass);
	}

	public boolean isAutoCreate(Class<?> entityClass) {
		if (iterator.hasNext()) {
			return iterator.next().isAutoCreate(entityClass, this);
		}
		return nextChain == null ? null : nextChain.isAutoCreate(entityClass);
	}
}
