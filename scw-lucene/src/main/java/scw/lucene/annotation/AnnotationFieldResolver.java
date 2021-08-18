package scw.lucene.annotation;

import java.util.Arrays;
import java.util.Collection;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

import scw.lucene.SimpleFieldResolver;
import scw.mapper.FieldDescriptor;
import scw.value.Value;

public class AnnotationFieldResolver extends SimpleFieldResolver {
	@Override
	protected boolean isStored(FieldDescriptor field) {
		scw.lucene.annotation.LuceneField annotation = field.getAnnotation(scw.lucene.annotation.LuceneField.class);
		if (annotation != null) {
			return annotation.stored();
		}
		return true;
	}

	@Override
	protected Collection<Field> resolveNonBasicType(FieldDescriptor fieldDescriptor, Value value) {
		scw.lucene.annotation.LuceneField annotation = fieldDescriptor
				.getAnnotation(scw.lucene.annotation.LuceneField.class);
		if (annotation == null) {
			return super.resolveNonBasicType(fieldDescriptor, value);
		}

		if (annotation.indexed()) {
			if (annotation.tokenized()) {
				return Arrays.asList(new TextField(fieldDescriptor.getName(), value.getAsString(),
						annotation.stored() ? Store.YES : Store.NO));
			} else {
				return Arrays.asList(new StringField(fieldDescriptor.getName(), value.getAsString(),
						annotation.stored() ? Store.YES : Store.NO));
			}
		} else {
			return Arrays.asList(new StoredField(fieldDescriptor.getName(), value.getAsString()));
		}
	}
}
