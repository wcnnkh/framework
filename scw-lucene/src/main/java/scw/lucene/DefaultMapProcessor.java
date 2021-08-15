package scw.lucene;

import org.apache.lucene.document.Document;

import scw.convert.TypeDescriptor;
import scw.mapper.AbstractMapProcessor;
import scw.mapper.Field;

public class DefaultMapProcessor<T, E extends Throwable> extends AbstractMapProcessor<Document, T, E>{

	public DefaultMapProcessor(TypeDescriptor typeDescriptor) {
		super(typeDescriptor);
	}

	@Override
	protected Object mapField(Document source, Field field) throws E {
		return source.get(field.getSetter().getName());
	}
}
