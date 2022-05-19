package io.basc.framework.lucene.support;

import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.lucene.LuceneResolver;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.value.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.lucene.document.DoubleDocValuesField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FloatDocValuesField;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;

public class SimpleLuceneResolverExtend implements LuceneResolverExtend {

	protected boolean isStored(ParameterDescriptor descriptor) {
		return true;
	}

	protected Collection<Field> resolveNonBasicType(ParameterDescriptor descriptor, Value value) {
		return Arrays.asList(new StringField(descriptor.getName(), value.getAsString(),
				isStored(descriptor) ? Store.YES : Store.NO));
	}

	@Override
	public Collection<Field> resolve(ParameterDescriptor descriptor, Value value, LuceneResolver chain) {
		List<Field> fields = new ArrayList<>(4);
		if (ClassUtils.isLong(descriptor.getType()) || ClassUtils.isInt(descriptor.getType())
				|| ClassUtils.isShort(descriptor.getType())) {
			fields.add(new NumericDocValuesField(descriptor.getName(), value.getAsLong()));
			if (isStored(descriptor)) {
				fields.add(new StoredField(descriptor.getName(), value.getAsString()));
			}
		} else if (ClassUtils.isDouble(descriptor.getType())) {
			fields.add(new DoubleDocValuesField(descriptor.getName(), value.getAsDoubleValue()));
			if (isStored(descriptor)) {
				fields.add(new StoredField(descriptor.getName(), value.getAsString()));
			}
		} else if (ClassUtils.isFloat(descriptor.getType())) {
			fields.add(new FloatDocValuesField(descriptor.getName(), value.getAsFloatValue()));
			if (isStored(descriptor)) {
				fields.add(new StoredField(descriptor.getName(), value.getAsString()));
			}
		} else {
			fields.addAll(resolveNonBasicType(descriptor, value));
		}
		return fields;
	}

}
