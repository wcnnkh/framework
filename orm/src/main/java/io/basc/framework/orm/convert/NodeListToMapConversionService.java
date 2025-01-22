package io.basc.framework.orm.convert;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.config.ConditionalConversionService;
import io.basc.framework.core.convert.config.ConvertiblePair;
import io.basc.framework.core.convert.support.AbstractConversionService;
import io.basc.framework.core.convert.transform.stereotype.stractegy.CollectionFactory;
import io.basc.framework.dom.DomUtils;
import io.basc.framework.orm.EntityMapper;
import io.basc.framework.orm.support.OrmUtils;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class NodeListToMapConversionService extends AbstractConversionService implements ConditionalConversionService {
	private static final TypeDescriptor COLLECTION_TYPE = TypeDescriptor.collection(List.class, Object.class);
	private EntityMapper mapper;

	public EntityMapper getMapper() {
		return mapper == null ? OrmUtils.getMapper() : mapper;
	}

	public void setMapper(EntityMapper mapper) {
		this.mapper = mapper;
	}

	public boolean hasPrimaryKeys(Class<?> entityClass) {
		return !getMapper().getMapping(entityClass).getPrimaryKeys().isEmpty();
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
