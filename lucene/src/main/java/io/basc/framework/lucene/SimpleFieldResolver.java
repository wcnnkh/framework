package io.basc.framework.lucene;

import io.basc.framework.mapper.FieldDescriptor;
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

public class SimpleFieldResolver implements FieldResolver {

	protected boolean isStored(FieldDescriptor fiedl) {
		return true;
	}

	protected Collection<Field> resolveNonBasicType(FieldDescriptor fieldDescriptor, Value value) {
		return Arrays.asList(new StringField(fieldDescriptor.getName(), value.getAsString(),
				isStored(fieldDescriptor) ? Store.YES : Store.NO));
	}

	@Override
	public Collection<Field> resolve(FieldDescriptor fieldDescriptor, Value value) {
		List<Field> fields = new ArrayList<>(4);
		if (ClassUtils.isLong(fieldDescriptor.getType()) || ClassUtils.isInt(fieldDescriptor.getType())
				|| ClassUtils.isShort(fieldDescriptor.getType())) {
			fields.add(new NumericDocValuesField(fieldDescriptor.getName(), value.getAsLong()));
			if (isStored(fieldDescriptor)) {
				fields.add(new StoredField(fieldDescriptor.getName(), value.getAsString()));
			}
		} else if (ClassUtils.isDouble(fieldDescriptor.getType())) {
			fields.add(new DoubleDocValuesField(fieldDescriptor.getName(), value.getAsDoubleValue()));
			if (isStored(fieldDescriptor)) {
				fields.add(new StoredField(fieldDescriptor.getName(), value.getAsString()));
			}
		} else if (ClassUtils.isFloat(fieldDescriptor.getType())) {
			fields.add(new FloatDocValuesField(fieldDescriptor.getName(), value.getAsFloatValue()));
			if (isStored(fieldDescriptor)) {
				fields.add(new StoredField(fieldDescriptor.getName(), value.getAsString()));
			}
		} else {
			fields.addAll(resolveNonBasicType(fieldDescriptor, value));
		}
		return fields;
	}

}
