package io.basc.framework.orm.convert;

import java.util.Collections;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import io.basc.framework.core.convert.ConvertiblePair;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;
import io.basc.framework.core.convert.config.ConditionalConversionService;
import io.basc.framework.core.convert.lang.AbstractConversionService;
import io.basc.framework.dom.DomUtils;

public class NodeToObjectConversionService extends AbstractConversionService implements ConditionalConversionService {

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (source == null) {
			return null;
		}

		if (Document.class.isAssignableFrom(sourceType.getType())) {
			Node node = ((Document) source).getDocumentElement();
			return convert(node, TypeDescriptor.valueOf(NodeList.class), targetType);
		} else {
			return convert((Node) source, sourceType, targetType);
		}
	}

	public Object convert(Node node, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (Value.isBaseType(targetType.getType())) {
			return Value.of(node.getTextContent()).getAsObject(targetType.getResolvableType().getType());
		}

		NodeList nodeList = node.getChildNodes();
		int len = nodeList.getLength();
		if (len == 0) {
			nodeList = DomUtils.toNodeList(node.getAttributes());
		}
		return getConversionService().convert(nodeList, TypeDescriptor.valueOf(NodeList.class), targetType);
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Node.class, Object.class));
	}

}
