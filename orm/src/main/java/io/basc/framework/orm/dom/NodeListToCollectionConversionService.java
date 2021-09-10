package io.basc.framework.orm.dom;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.ConditionalConversionService;
import io.basc.framework.convert.lang.ConvertiblePair;
import io.basc.framework.dom.DomUtils;
import io.basc.framework.util.CollectionFactory;

@SuppressWarnings({ "rawtypes", "unchecked" })
class NodeListToCollectionConversionService extends ConditionalConversionService {

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(NodeList.class, Collection.class));
	}

	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (source == null) {
			return null;
		}

		NodeList nodeList = (NodeList) source;
		int len = nodeList.getLength();
		Collection collection = CollectionFactory.createCollection(targetType.getType(),
				targetType.getElementTypeDescriptor().getType(), len);
		for (int i = 0; i < len; i++) {
			Node node = nodeList.item(i);
			if (DomUtils.ignoreNode(node)) {
				continue;
			}

			Object value = getConversionService().convert(node, TypeDescriptor.valueOf(Node.class),
					targetType.getElementTypeDescriptor());
			collection.add(value);
		}
		return collection;
	}
}
