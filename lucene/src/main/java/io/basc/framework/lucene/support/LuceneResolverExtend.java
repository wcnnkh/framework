package io.basc.framework.lucene.support;

import java.util.Collection;

import org.apache.lucene.document.Field;

import io.basc.framework.lucene.LuceneResolver;
import io.basc.framework.mapper.Parameter;

public interface LuceneResolverExtend {
	default Collection<Field> resolve(Parameter parameter, LuceneResolver chain) {
		return chain.resolve(parameter);
	}
}
