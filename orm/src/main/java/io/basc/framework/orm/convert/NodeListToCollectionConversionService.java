package io.basc.framework.orm.convert;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.lang.AbstractConversionService;
import io.basc.framework.core.convert.service.ConditionalConversionService;
import io.basc.framework.core.convert.service.ConvertiblePair;
import io.basc.framework.core.convert.transform.stractegy.CollectionFactory;
import io.basc.framework.dom.DomUtils;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class NodeListToCollectionConversionService extends AbstractConversionService implements ConditionalConversionService {

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(NodeList.class, Collection.class));
	}
	
	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return ConditionalConversionService.super.canConvert(sourceType, targetType);
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
