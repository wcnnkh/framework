package io.basc.framework.orm.convert;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.AbstractConversionService;
import io.basc.framework.convert.lang.ConditionalConversionService;
import io.basc.framework.convert.lang.ConvertiblePair;
import io.basc.framework.dom.DomUtils;
import io.basc.framework.mapper.FieldFeature;
import io.basc.framework.orm.ObjectRelationalFactory;
import io.basc.framework.orm.support.OrmUtils;
import io.basc.framework.util.CollectionFactory;

@SuppressWarnings({ "unchecked", "rawtypes" })
class NodeListToMapConversionService extends AbstractConversionService implements ConditionalConversionService {
	private static final TypeDescriptor COLLECTION_TYPE = TypeDescriptor.collection(List.class, Object.class);
	private ObjectRelationalFactory mapper;

	public ObjectRelationalFactory getMapper() {
		return mapper == null ? OrmUtils.getMapper() : mapper;
	}

	public void setObjectRelationalMapping(ObjectRelationalFactory objectRelationalMapping) {
		this.mapper = objectRelationalMapping;
	}

	public boolean hasPrimaryKeys(Class<?> entityClass) {
		return getMapper().getStructure(entityClass).filter(FieldFeature.SUPPORT_GETTER).withAll().all().stream()
				.filter((field) -> getMapper().isPrimaryKey(entityClass, field.getGetter())).findAny().isPresent();
	}

	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return ConditionalConversionService.super.canConvert(sourceType, targetType)
				&& getConversionService().canConvert(sourceType, COLLECTION_TYPE);
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
		if (hasPrimaryKeys(lastValueType.getType()) && getConversionService().canConvert(sourceType, collectionType)) {
			// 如果是存在主键的，应该进行类解析
			Collection<?> list = (Collection<?>) getConversionService().convert(source, sourceType, collectionType);
			return getConversionService().convert(list, collectionType, targetType);
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

			Object key = getConversionService().convert(node.getNodeName(), TypeDescriptor.valueOf(String.class),
					targetType.getMapKeyTypeDescriptor());
			Object value = getConversionService().convert(node, TypeDescriptor.valueOf(Node.class),
					targetType.getMapValueTypeDescriptor());
			map.put(key, value);
		}
		return map;
	}
}
