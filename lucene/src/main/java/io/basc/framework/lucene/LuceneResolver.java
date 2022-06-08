package io.basc.framework.lucene;

import java.util.Collection;

import org.apache.lucene.document.Field;

import io.basc.framework.mapper.Parameter;

public interface LuceneResolver {
	Collection<Field> resolve(Parameter parameter);
}
