package scw.orm.convert;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import scw.convert.ConversionFailedException;
import scw.convert.ConversionService;
import scw.convert.ConversionServiceAware;
import scw.convert.TypeDescriptor;
import scw.lang.AlreadyExistsException;
import scw.mapper.Field;
import scw.mapper.FieldFeature;
import scw.mapper.Fields;
import scw.mapper.MapperUtils;
import scw.orm.OrmUtils;
import scw.util.Accept;
import scw.util.CollectionFactory;

class CollectionToMapConversionService implements ConversionService, ConversionServiceAware {
	private static final TypeDescriptor COLLECTION_TYPE = TypeDescriptor.collection(List.class, Object.class);
	private ConversionService conversionService;

	@Override
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (sourceType == null || sourceType.isMap() || !targetType.isMap()) {
			return false;
		}

		return conversionService.canConvert(sourceType, COLLECTION_TYPE);
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
			sources = (Collection) conversionService.convert(source, sourceType, COLLECTION_TYPE);
		}

		TypeDescriptor itemType = getValueType(targetType);
		Map map = CollectionFactory.createMap(targetType.getType(), targetType.getMapKeyTypeDescriptor().getType(),
				sources.size());
		for (Object item : sources) {
			if (item == null) {
				continue;
			}

			Object value = conversionService.convert(item, sourceType.narrow(item), itemType);
			Fields primaryKeyFields = MapperUtils.getMapper().getFields(itemType.getType())
					.accept(FieldFeature.SUPPORT_GETTER).accept(new Accept<Field>() {

						@Override
						public boolean accept(Field e) {
							return OrmUtils.getMapping().isPrimaryKey(e);
						}
					}).shared();
			Iterator<Field> primaryKeyIterator = primaryKeyFields.iterator();
			Map nestMap = map;
			TypeDescriptor keyType = targetType.getMapKeyTypeDescriptor();
			TypeDescriptor valueType = targetType.getMapValueTypeDescriptor();
			while (primaryKeyIterator.hasNext()) {
				Field primaryKeyField = primaryKeyIterator.next();
				Object key = primaryKeyField.getGetter().get(value);

				key = conversionService.convert(key, new TypeDescriptor(primaryKeyField.getGetter()), keyType);
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

	private TypeDescriptor getValueType(TypeDescriptor typeDescriptor) {
		TypeDescriptor valueType = typeDescriptor;
		while (valueType.isMap()) {
			valueType = valueType.getMapValueTypeDescriptor();
		}
		return valueType;
	}
}
