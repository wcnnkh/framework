package io.basc.framework.lucene;

import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.value.Value;

import java.util.Collection;

import org.apache.lucene.document.Field;

public interface LuceneResolver {
	Collection<Field> resolve(ParameterDescriptor descriptor, Value value);
}
