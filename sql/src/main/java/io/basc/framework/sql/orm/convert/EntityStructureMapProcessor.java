package io.basc.framework.sql.orm.convert;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.env.Sys;
import io.basc.framework.mapper.Field;
import io.basc.framework.orm.EntityStructure;
import io.basc.framework.orm.Property;
import io.basc.framework.sql.SqlUtils;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.MultiValueMap;
import io.basc.framework.util.stream.Processor;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class EntityStructureMapProcessor<T> implements Processor<ResultSet, T, Throwable> {
	private final EntityStructure<? extends Property> structure;
	private final ConversionService conversionService;

	public EntityStructureMapProcessor(EntityStructure<? extends Property> structure) {
		this(structure, Sys.env.getConversionService());
	}

	public EntityStructureMapProcessor(EntityStructure<? extends Property> structure,
			ConversionService conversionService) {
		this.structure = structure;
		this.conversionService = conversionService;
	}

	public <V> V getValue(Map<String, V> valueMap, Property property) {
		if (valueMap.containsKey(property.getName())) {
			return valueMap.get(property.getName());
		}

		Collection<String> names = property.getAliasNames();
		if (CollectionUtils.isEmpty(names)) {
			return null;
		}

		for (String name : names) {
			if (valueMap.containsKey(name)) {
				return valueMap.get(name);
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T process(ResultSet rs) throws Throwable {
		MultiValueMap<String, Object> valueMap = SqlUtils.getRowValueMap(rs);
		Object instance = Sys.env.getInstance(structure.getEntityClass());
		for (Property column : structure) {
			Object value = getValue(valueMap, column);
			if (value == null) {
				continue;
			}

			Field field = column.getField();
			List<Object> columnValues = valueMap.get(column.getName());
			Object columnValue = columnValues.size() == 1 ? columnValues.get(0) : columnValues;
			field.set(instance, columnValue, conversionService);
		}
		return (T) instance;
	}
}
