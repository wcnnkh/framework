package io.basc.framework.lucene.annotation;

import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.lucene.SimpleFieldResolver;
import io.basc.framework.value.Value;

import java.util.Arrays;
import java.util.Collection;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

public class AnnotationFieldResolver extends SimpleFieldResolver {
	@Override
	protected boolean isStored(ParameterDescriptor descriptor) {
		io.basc.framework.lucene.annotation.LuceneField annotation = descriptor
				.getAnnotation(io.basc.framework.lucene.annotation.LuceneField.class);
		if (annotation != null) {
			return annotation.stored();
		}
		return true;
	}

	@Override
	protected Collection<Field> resolveNonBasicType(
			ParameterDescriptor descriptor, Value value) {
		io.basc.framework.lucene.annotation.LuceneField annotation = descriptor
				.getAnnotation(io.basc.framework.lucene.annotation.LuceneField.class);
		if (annotation == null) {
			return super.resolveNonBasicType(descriptor, value);
		}

		if (annotation.indexed()) {
			if (annotation.tokenized()) {
				return Arrays.asList(new TextField(descriptor.getName(), value
						.getAsString(), annotation.stored() ? Store.YES
						: Store.NO));
			} else {
				return Arrays.asList(new StringField(descriptor.getName(),
						value.getAsString(), annotation.stored() ? Store.YES
								: Store.NO));
			}
		} else {
			return Arrays.asList(new StoredField(descriptor.getName(), value
					.getAsString()));
		}
	}
}
