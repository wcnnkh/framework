package io.basc.framework.lucene.annotation;

import java.util.Arrays;
import java.util.Collection;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

import io.basc.framework.core.convert.transform.Parameter;
import io.basc.framework.core.convert.transform.ParameterDescriptor;
import io.basc.framework.lucene.support.SimpleLuceneResolverExtend;

public class AnnotationLuceneResolverExtend extends SimpleLuceneResolverExtend {
	@Override
	protected boolean isStored(ParameterDescriptor descriptor) {
		io.basc.framework.lucene.annotation.LuceneField annotation = descriptor.getTypeDescriptor()
				.getAnnotation(io.basc.framework.lucene.annotation.LuceneField.class);
		if (annotation != null) {
			return annotation.stored();
		}
		return true;
	}

	@Override
	protected Collection<Field> resolveNonBasicType(Parameter parameter) {
		io.basc.framework.lucene.annotation.LuceneField annotation = parameter.getTypeDescriptor()
				.getAnnotation(io.basc.framework.lucene.annotation.LuceneField.class);
		if (annotation == null) {
			return super.resolveNonBasicType(parameter);
		}

		if (annotation.indexed()) {
			if (annotation.tokenized()) {
				return Arrays.asList(new TextField(parameter.getName(), parameter.getAsString(),
						annotation.stored() ? Store.YES : Store.NO));
			} else {
				return Arrays.asList(new StringField(parameter.getName(), parameter.getAsString(),
						annotation.stored() ? Store.YES : Store.NO));
			}
		} else {
			return Arrays.asList(new StoredField(parameter.getName(), parameter.getAsString()));
		}
	}
}
