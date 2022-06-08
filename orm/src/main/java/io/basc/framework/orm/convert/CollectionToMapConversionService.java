package io.basc.framework.orm.convert;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.basc.framework.convert.ConversionFailedException;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.ConditionalConversionService;
import io.basc.framework.convert.lang.ConvertiblePair;
import io.basc.framework.lang.AlreadyExistsException;
import io.basc.framework.mapper.Field;
import io.basc.framework.orm.ObjectRelationalFactory;
import io.basc.framework.orm.Property;
import io.basc.framework.orm.support.OrmUtils;
import io.basc.framework.util.CollectionFactory;

public class CollectionToMapConversionService extends ConditionalConversionService {
	public static final TypeDescriptor COLLECTION_TYPE = TypeDescriptor.collection(List.class, Object.class);
	private ObjectRelationalFactory mapper;

	public ObjectRelationalFactory getMapper() {
		return mapper == null ? OrmUtils.getMapper() : mapper;
	}

	public void setMapper(ObjectRelationalFactory mapper) {
		this.mapper = mapper;
	}

	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Collection.class, Map.class));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (source == null) {
			return null;
		}

		Collection sources;
		if (sourceType.isCollection()) {
			sources = (Collection) source;
		} else {
			sources = (Collection) getConversionService().convert(source, sourceType, COLLECTION_TYPE);
		}

		if (sources == null) {
			return null;
		}

		TypeDescriptor itemType = getValueType(targetType);
		Map map = CollectionFactory.createMap(targetType.getType(), targetType.getMapKeyTypeDescriptor().getType(),
				sources.size());
		for (Object item : sources) {
			if (item == null) {
				continue;
			}

			Object value = getConversionService().convert(item, sourceType.narrow(item), itemType);
			List<? extends Property> primaryKeys = getMapper().getStructure(itemType.getType()).getPrimaryKeys();
			Iterator<? extends Property> primaryKeyIterator = primaryKeys.iterator();
			Map nestMap = map;
			TypeDescriptor keyType = targetType.getMapKeyTypeDescriptor();
			TypeDescriptor valueType = targetType.getMapValueTypeDescriptor();
			while (primaryKeyIterator.hasNext()) {
				Field primaryKeyField = primaryKeyIterator.next();
				Object key = primaryKeyField.getGetter().get(value);

				key = getConversionService().convert(key, new TypeDescriptor(primaryKeyField.getGetter()), keyType);
				if (primaryKeyIterator.hasNext()) {
					if (!valueType.isMap()) {
						throw new ConversionFailedException(sourceType, targetType, source, null);
					}

					Map valueMap = (Map) map.get(key);
					if (valueMap == null) {
						valueMap = CollectionFactory.createMap(valueType.getType(), 16);
						nestMap.put(key, valueMap);
					}
					nestMap = valueMap;
					keyType = valueType.getMapKeyTypeDescriptor();
					valueType = valueType.getMapValueTypeDescriptor();
				} else {
					if (nestMap.containsKey(key)) {
						Throwable alreadyex = new AlreadyExistsException(String.valueOf(key));
						throw new ConversionFailedException(sourceType, targetType, value, alreadyex);
					}
					nestMap.put(key, value);
				}
			}
		}
		return map;
	}

	public static TypeDescriptor getValueType(TypeDescriptor typeDescriptor) {
		TypeDescriptor valueType = typeDescriptor;
		while (valueType.isMap()) {
			valueType = valueType.getMapValueTypeDescriptor();
		}
		return valueType;
	}
}
