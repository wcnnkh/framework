package scw.configure.convert;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.configure.Configure;
import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.convert.support.ConditionalConversionService;
import scw.convert.support.ConvertiblePair;
import scw.util.CollectionFactory;
import scw.xml.XMLUtils;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class NodeListToCollectionConversionService extends ConditionalConversionService
		implements Configure {
	private final ConversionService conversionService;

	public NodeListToCollectionConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(NodeList.class,
				Collection.class));
	}

	public Object convert(Object source, TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		if (source == null) {
			return null;
		}

		NodeList nodeList = (NodeList) source;
		int len = nodeList.getLength();
		Collection collection = CollectionFactory.createCollection(targetType
				.getType(), targetType.getElementTypeDescriptor().getType(),
				len);
		configuration(source, sourceType, collection, targetType);
		return collection;
	}

	public void configuration(Object source, TypeDescriptor sourceType,
			Object target, TypeDescriptor targetType) {
		if (source == null) {
			return;
		}

		Collection targetItems = (Collection) target;
		NodeList nodeList = (NodeList) source;
		int len = nodeList.getLength();
		for (int i = 0; i < len; i++) {
			Node node = nodeList.item(i);
			if(XMLUtils.ignoreNode(node)){
				continue;
			}
			
			Object value = conversionService.convert(node,
					TypeDescriptor.valueOf(Node.class),
					targetType.getElementTypeDescriptor());
			targetItems.add(value);
		}
	}
}
