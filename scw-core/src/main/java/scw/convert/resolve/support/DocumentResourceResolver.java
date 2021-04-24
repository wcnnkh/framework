package scw.convert.resolve.support;

import org.w3c.dom.Document;

import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.io.Resource;

public class DocumentResourceResolver extends AbstractResourceResolver{
	
	public DocumentResourceResolver(ConversionService conversionService){
		super(conversionService);
	}

	public boolean canResolveResource(Resource resource,
			TypeDescriptor targetType) {
		return resource.exists() && resource.getName().endsWith(".xml");
	}

	public Object resolveResource(Resource resource, TypeDescriptor targetType) {
		Document document = getDomBuilder().parse(resource);
		if(document == null){
			return null;
		}
		return getConversionService().convert(document, TypeDescriptor.forObject(document), targetType);
	}
}
