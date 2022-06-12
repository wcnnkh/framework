package io.basc.framework.orm.convert;

import java.util.Collections;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.AbstractConversionService;
import io.basc.framework.convert.lang.ConditionalConversionService;
import io.basc.framework.convert.lang.ConvertiblePair;
import io.basc.framework.dom.DomUtils;
import io.basc.framework.value.StringValue;
import io.basc.framework.value.Value;

class NodeToObjectConversionService extends AbstractConversionService implements ConditionalConversionService {

	@SuppressWarnings("unchecked")
	@Override
	public <R> R convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (source == null) {
			return null;
		}

		if (Document.class.isAssignableFrom(sourceType.getType())) {
			Node node = ((Document) source).getDocumentElement();
			return (R) convert(node, TypeDescriptor.valueOf(NodeList.class), targetType);
		} else {
			return (R) convert((Node) source, sourceType, targetType);
		}
	}

	public Object convert(Node node, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (Value.isBaseType(targetType.getType())) {
			StringValue value = new StringValue(node.getTextContent());
			return value.getAsObject(targetType.getResolvableType().getType());
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
