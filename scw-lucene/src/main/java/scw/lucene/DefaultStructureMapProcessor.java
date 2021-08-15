package scw.lucene;

import org.apache.lucene.document.Document;

import scw.convert.ConversionService;
import scw.orm.EntityStructure;
import scw.orm.EntityStructureMapProcessor;
import scw.orm.Property;

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
