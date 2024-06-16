package io.basc.framework.lucene.support;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.apache.lucene.document.Field;

import io.basc.framework.execution.param.Parameter;
import io.basc.framework.lang.Nullable;
import io.basc.framework.lucene.LuceneResolver;
import io.basc.framework.util.Assert;

public class LuceneResolverExtendChain implements LuceneResolver {
	private final Iterator<LuceneResolverExtend> iterator;
	private final LuceneResolver nextChain;

	public LuceneResolverExtendChain(Iterator<LuceneResolverExtend> iterator) {
		this(iterator, null);
	}

	public LuceneResolverExtendChain(Iterator<LuceneResolverExtend> iterator, @Nullable LuceneResolver nextChain) {
		Assert.requiredArgument(iterator != null, "iterator");
		this.iterator = iterator;
		this.nextChain = nextChain;
	}

	public static LuceneResolver build(Iterator<LuceneResolverExtend> iterator) {
		return new LuceneResolverExtendChain(iterator);
	}

	@Override
	public Collection<Field> resolve(Parameter parameter) {
		if (iterator.hasNext()) {
			return iterator.next().resolve(parameter, this);
		}
		return nextChain == null ? Collections.emptyList() : nextChain.resolve(parameter);
	}
}
