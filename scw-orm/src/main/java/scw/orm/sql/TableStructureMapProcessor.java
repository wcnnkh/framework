package scw.orm.sql;

import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.util.List;
import java.util.stream.Collectors;

import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.core.utils.CollectionUtils;
import scw.env.Sys;
import scw.mapper.Field;
import scw.sql.SqlUtils;
import scw.util.MultiValueMap;
import scw.util.stream.Processor;

public class TableStructureMapProcessor<T> implements
		Processor<ResultSet, T, Throwable> {
	private final TableStructure tableStructure;
	private final ConversionService conversionService;

	public TableStructureMapProcessor(TableStructure tableStructure) {
		this(tableStructure, Sys.env.getConversionService());
	}

	public TableStructureMapProcessor(TableStructure tableStructure,
			ConversionService conversionService) {
		this.tableStructure = tableStructure;
		this.conversionService = conversionService;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T process(ResultSet rs) throws Throwable {
		MultiValueMap<String, Object> valueMap = SqlUtils.getRowValueMap(rs);
		Object instance = Sys.env.getInstance(tableStructure.getEntityClass());
		for (Column column : tableStructure) {
			if (valueMap.containsKey(column.getName())) {
				Field field = column.getField();
				Object parentValue = instance;
				for (Field parentField : CollectionUtils.reversal(field
						.parents().collect(Collectors.toList()))) {
					boolean isStatic = Modifier.isStatic(parentField
							.getGetter().getModifiers());
					if (isStatic) {
						// 如果是静态方法
						parentValue = null;
					} else {
						Object value = parentField.getGetter().get(parentValue);
						if (value == null) {
							value = Sys.env.getInstance(parentField.getSetter()
									.getType());
							parentField.getSetter().set(parentValue, value);
						}
						parentValue = value;
					}
				}

				List<Object> values = valueMap.get(column.getName());
				Object value = values.size() == 1 ? values.get(0) : values;
				value = conversionService.convert(value, TypeDescriptor
						.forObject(value),
						new TypeDescriptor(field.getSetter()));
				field.getSetter().set(parentValue, value);
			}
		}
		return (T) instance;
	}

}
