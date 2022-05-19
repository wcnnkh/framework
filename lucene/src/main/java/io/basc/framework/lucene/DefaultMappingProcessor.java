package io.basc.framework.lucene;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.AbstractObjectMappingProcessor;
import io.basc.framework.mapper.Field;

import org.apache.lucene.document.Document;

public class DefaultMappingProcessor<T, E extends Throwable> extends AbstractObjectMappingProcessor<Document, T, E> {

	public DefaultMappingProcessor(TypeDescriptor typeDescriptor) {
		super(typeDescriptor);
	}

	@Override
	protected Object mapField(Document source, Field field) throws E {
		return source.get(field.getSetter().getName());
	}
}
