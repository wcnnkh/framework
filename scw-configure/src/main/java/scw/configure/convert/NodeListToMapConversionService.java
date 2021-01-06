package scw.configure.convert;

import java.util.Collections;
import java.util.Map;
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

@SuppressWarnings({ "unchecked", "rawtypes" })
public class NodeListToMapConversionService extends ConditionalConversionService implements Configure{
	private final ConversionService conversionService;
	
	public NodeListToMapConversionService(ConversionService conversionService){
		this.conversionService = conversionService;
	}
	
	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(NodeList.class, Map.class));
	}

	public Object convert(Object source, TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		if(source == null){
			return null;
		}
		
		NodeList nodeList = (NodeList) source;
		Map map = CollectionFactory.createMap(targetType.getType(), targetType.getMapKeyTypeDescriptor().getType(), nodeList.getLength());
		configuration(nodeList, TypeDescriptor.valueOf(NodeList.class), map, targetType);
		return map;
	}

	public void configuration(Object source, TypeDescriptor sourceType,
			Object target, TypeDescriptor targetType) {
		if(source == null){
			return ;
		}
		
		NodeList nodeList = (NodeList) source;
		Map map = (Map) target;
		int len = nodeList.getLength();
		for(int i=0; i<len; i++){
			Node node = nodeList.item(i);
			if(XMLUtils.ignoreNode(node)){
				continue;
			}
			
			Object key = conversionService.convert(node.getNodeName(), TypeDescriptor.valueOf(String.class), targetType.getMapKeyTypeDescriptor());
			Object value = conversionService.convert(node, TypeDescriptor.valueOf(Node.class), targetType.getMapValueTypeDescriptor());
			map.put(key, value);
		}
	}
	
}
