package scw.lucene;

import java.util.Collection;

import org.apache.lucene.document.Field;

import scw.mapper.FieldDescriptor;
import scw.value.Value;

public interface FieldResolver {
	Collection<Field> resolve(FieldDescriptor fieldDescriptor, Value value);
}
