package scw.orm.sql;

import java.sql.ResultSet;
import java.util.List;

import scw.convert.ConversionService;
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
				List<Object> columnValues = valueMap.get(column.getName());
				Object columnValue = columnValues.size() == 1 ? columnValues.get(0) : columnValues;
				field.set(instance, columnValue, conversionService);
			}
		}
		return (T) instance;
	}
}
