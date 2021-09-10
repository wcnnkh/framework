package io.basc.framework.orm.dom;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ConversionServiceAware;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.ConditionalConversionService;
import io.basc.framework.convert.lang.ConvertiblePair;
import io.basc.framework.dom.DomUtils;
import io.basc.framework.orm.ObjectRelationalMapping;
import io.basc.framework.orm.OrmUtils;
import io.basc.framework.orm.convert.CollectionToMapConversionService;
import io.basc.framework.util.CollectionFactory;

@SuppressWarnings({ "unchecked", "rawtypes" })
class NodeListToMapConversionService extends ConditionalConversionService implements ConversionServiceAware {
	private static final TypeDescriptor COLLECTION_TYPE = TypeDescriptor.collection(List.class, Object.class);
	private ConversionService conversionService;
	private ObjectRelationalMapping objectRelationalMapping;

	public ObjectRelationalMapping getObjectRelationalMapping() {
		return objectRelationalMapping == null ? OrmUtils.getMapping() : objectRelationalMapping;
	}

	public void setObjectRelationalMapping(ObjectRelationalMapping objectRelationalMapping) {
		this.objectRelationalMapping = objectRelationalMapping;
	}

	@Override
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	/**
	 * @see CollectionToMapConversionService
	 * @param descriptor
	 * @return
	 */
	protected boolean isExistPrimary(TypeDescriptor descriptor) {
		return getObjectRelationalMapping().getFields(descriptor.getType()).streamAll()
				.filter((field) -> getObjectRelationalMapping().isPrimaryKey(field)).findAny().isPresent();
	}

	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return super.canConvert(sourceType, targetType) && conversionService.canConvert(sourceType, COLLECTION_TYPE);
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(NodeList.class, Map.class));
	}

	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (source == null) {
			return null;
		}

		TypeDescriptor lastValueType = CollectionToMapConversionService.getValueType(targetType);
		TypeDescriptor collectionType = TypeDescriptor.collection(Collection.class, lastValueType);
		if (isExistPrimary(lastValueType) && conversionService.canConvert(sourceType, collectionType)) {
			// 如果是存在主键的，应该进行类解析
			Collection<?> list = (Collection<?>) conversionService.convert(source, sourceType, collectionType);
			return conversionService.convert(list, collectionType, targetType);
		}

		NodeList nodeList = (NodeList) source;
		Map map = CollectionFactory.createMap(targetType.getType(), targetType.getMapKeyTypeDescriptor().getType(),
				nodeList.getLength());
		int len = nodeList.getLength();
		for (int i = 0; i < len; i++) {
			Node node = nodeList.item(i);
			if (DomUtils.ignoreNode(node)) {
				continue;
			}

			Object key = conversionService.convert(node.getNodeName(), TypeDescriptor.valueOf(String.class),
					targetType.getMapKeyTypeDescriptor());
			Object value = conversionService.convert(node, TypeDescriptor.valueOf(Node.class),
					targetType.getMapValueTypeDescriptor());
			map.put(key, value);
		}
		return map;
	}
}
