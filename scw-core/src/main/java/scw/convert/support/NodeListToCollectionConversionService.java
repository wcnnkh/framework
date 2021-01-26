package scw.convert.support;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.dom.DomUtils;
import scw.util.CollectionFactory;

@SuppressWarnings({ "rawtypes", "unchecked" })
class NodeListToCollectionConversionService extends ConditionalConversionService {
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
		for (int i = 0; i < len; i++) {
			Node node = nodeList.item(i);
			if(DomUtils.ignoreNode(node)){
				continue;
			}
			
			Object value = conversionService.convert(node,
					TypeDescriptor.valueOf(Node.class),
					targetType.getElementTypeDescriptor());
			collection.add(value);
		}
		return collection;
	}
}
