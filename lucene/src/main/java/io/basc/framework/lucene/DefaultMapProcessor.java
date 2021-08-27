package io.basc.framework.lucene;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.AbstractMapProcessor;
import io.basc.framework.mapper.Field;

import org.apache.lucene.document.Document;

public class DefaultMapProcessor<T, E extends Throwable> extends AbstractMapProcessor<Document, T, E>{

	public DefaultMapProcessor(TypeDescriptor typeDescriptor) {
		super(typeDescriptor);
	}

	@Override
	protected Object mapField(Document source, Field field) throws E {
		return source.get(field.getSetter().getName());
	}
}
