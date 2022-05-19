package io.basc.framework.lucene.support;

import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.lucene.LuceneResolver;
import io.basc.framework.value.Value;

import java.util.Collection;

import org.apache.lucene.document.Field;

public interface LuceneResolverExtend {
	default Collection<Field> resolve(ParameterDescriptor descriptor,
			Value value, LuceneResolver chain) {
		return chain.resolve(descriptor, value);
	}
}
