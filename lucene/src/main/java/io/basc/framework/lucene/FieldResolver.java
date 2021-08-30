package io.basc.framework.lucene;

import io.basc.framework.mapper.FieldDescriptor;
import io.basc.framework.value.Value;

import java.util.Collection;

import org.apache.lucene.document.Field;

public interface FieldResolver {
	Collection<Field> resolve(FieldDescriptor fieldDescriptor, Value value);
}
