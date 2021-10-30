package io.basc.framework.lucene;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.orm.EntityStructure;
import io.basc.framework.orm.Property;
import io.basc.framework.orm.support.EntityStructureMapProcessor;

import org.apache.lucene.document.Document;

public class DefaultStructureMapProcessor<T, E extends Throwable> extends EntityStructureMapProcessor<Property, Document, T, E>{

	public DefaultStructureMapProcessor(EntityStructure<? extends Property> structore) {
		super(structore);
	}

	public DefaultStructureMapProcessor(EntityStructure<Property> structore,
			ConversionService conversionService) {
		super(structore, conversionService);
	}

	@Override
	protected boolean contains(Document source, Property property) {
		return true;
	}

	@Override
	protected Object getProperty(Document source, Property property) {
		return source.get(property.getName());
	}
	
}
