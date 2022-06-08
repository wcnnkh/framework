package io.basc.framework.lucene.support;

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

import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.lucene.LuceneResolver;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.util.ClassUtils;

public class SimpleLuceneResolverExtend implements LuceneResolverExtend {

	protected boolean isStored(ParameterDescriptor descriptor) {
		return true;
	}

	protected Collection<Field> resolveNonBasicType(Parameter parameter) {
		return Arrays.asList(new StringField(parameter.getName(), parameter.getAsString(),
				isStored(parameter) ? Store.YES : Store.NO));
	}

	@Override
	public Collection<Field> resolve(Parameter parameter, LuceneResolver chain) {
		List<Field> fields = new ArrayList<>(4);
		if (ClassUtils.isLong(parameter.getType()) || ClassUtils.isInt(parameter.getType())
				|| ClassUtils.isShort(parameter.getType())) {
			fields.add(new NumericDocValuesField(parameter.getName(), parameter.getAsLong()));
			if (isStored(parameter)) {
				fields.add(new StoredField(parameter.getName(), parameter.getAsString()));
			}
		} else if (ClassUtils.isDouble(parameter.getType())) {
			fields.add(new DoubleDocValuesField(parameter.getName(), parameter.getAsDoubleValue()));
			if (isStored(parameter)) {
				fields.add(new StoredField(parameter.getName(), parameter.getAsString()));
			}
		} else if (ClassUtils.isFloat(parameter.getType())) {
			fields.add(new FloatDocValuesField(parameter.getName(), parameter.getAsFloatValue()));
			if (isStored(parameter)) {
				fields.add(new StoredField(parameter.getName(), parameter.getAsString()));
			}
		} else {
			fields.addAll(resolveNonBasicType(parameter));
		}
		return fields;
	}

}
