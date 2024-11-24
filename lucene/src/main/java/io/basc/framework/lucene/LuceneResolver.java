package io.basc.framework.lucene;

import java.util.Collection;

import org.apache.lucene.document.Field;

import io.basc.framework.core.execution.param.Parameter;

public interface LuceneResolver {
	Collection<Field> resolve(Parameter parameter);
}
