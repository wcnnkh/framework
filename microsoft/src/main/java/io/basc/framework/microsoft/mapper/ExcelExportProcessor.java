package io.basc.framework.microsoft.mapper;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.env.Sys;
import io.basc.framework.microsoft.ExcelException;
import io.basc.framework.microsoft.ExcelExport;
import io.basc.framework.microsoft.ExcelOperations;
import io.basc.framework.microsoft.MicrosoftUtils;
import io.basc.framework.orm.EntityStructure;
import io.basc.framework.orm.ObjectRelationalMapping;
import io.basc.framework.orm.transfer.ExportProcessor;
import io.basc.framework.util.ArrayUtils;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;

public class ExcelExportProcessor<T> implements ExportProcessor<T> {
	private ObjectRelationalMapping orm;
	private ConversionService conversionService;
	private ExcelOperations excelOperations;

	public ExcelExportProcessor() {
		this.orm = ExcelMapping.INSTANCE;
		this.conversionService = Sys.env.getConversionService();
		this.excelOperations = MicrosoftUtils.getExcelOperations();
	}

	protected ExcelExportProcessor(ExcelExportProcessor<T> source) {
		Assert.requiredArgument(source != null, "source");
		this.orm = source.orm;
		this.conversionService = source.conversionService;
		this.excelOperations = source.excelOperations;
	}

	public ObjectRelationalMapping getOrm() {
		return orm;
	}

	public void setOrm(ObjectRelationalMapping orm) {
		Assert.requiredArgument(orm != null, "orm");
		this.orm = orm;
	}

	public ConversionService getConversionService() {
		return conversionService;
	}

	public void setConversionService(ConversionService conversionService) {
		Assert.requiredArgument(conversionService != null, "conversionService");
		this.conversionService = conversionService;
	}

	public ExcelOperations getExcelOperations() {
		return excelOperations;
	}

	public void setExcelOperations(ExcelOperations excelOperations) {
		Assert.requiredArgument(excelOperations != null, "excelOperations");
		this.excelOperations = excelOperations;
	}

	@Override
	public final void process(Iterator<? extends T> source, File target) throws IOException {
		ExcelExport export = getExcelOperations().createExcelExport(target);
		try {
			putAll(source, export);
		} finally {
			export.close();
		}
	}

	public final void putAll(Iterator<? extends T> source, ExcelExport export) throws IOException {
		while (source.hasNext()) {
			put(source.next(), export);
		}
	}

	private final Object[] getRowValues(ResultSet resultSet, int size) throws SQLException {
		Object[] values = new Object[size];
		for (int i = 1; i <= size; i++) {
			values[i - 1] = resultSet.getObject(i);
		}
		return values;
	}

	private String[] getColumnNames(ResultSetMetaData rsmd, int size) throws SQLException {
		String[] names = new String[size];
		for (int i = 1; i <= size; i++) {
			names[i - 1] = lookupColumnName(rsmd, i);
		}
		return names;
	}

	protected String lookupColumnName(ResultSetMetaData resultSetMetaData, int columnIndex) throws SQLException {
		String name = resultSetMetaData.getColumnLabel(columnIndex);
		if (StringUtils.isEmpty(name)) {
			name = resultSetMetaData.getColumnName(columnIndex);
		}
		return name;
	}

	public final void put(T source, ExcelExport export) throws IOException {
		// TODO 是否考虑移到单独的processor, 因为并不是所有的jvm都包含 java.sql.* 环境
		if (source instanceof ResultSet) {
			ResultSet rs = (ResultSet) source;
			try {
				ResultSetMetaData rsmd = rs.getMetaData();
				int count = rsmd.getColumnCount();
				if (count == 0) {
					return;
				}

				if (export.isEmpty()) {
					titles(export, getColumnNames(rsmd, count));
				}

				Object[] values = getRowValues(rs, count);
				String[] columns = (String[]) conversionService.convert(values, TypeDescriptor.forObject(values),
						TypeDescriptor.valueOf(String[].class));
				export.put(columns);
				return;
			} catch (SQLException e) {
				throw new ExcelException(e);
			}
		}

		if (source instanceof String) {
			export.put((String) source);
		} else if (source instanceof String[]) {
			export.put((String[]) source);
		} else {
			TypeDescriptor type = TypeDescriptor.forObject(source);
			if (type.isCollection() || type.isArray()) {
				String[] values = (String[]) conversionService.convert(source, type,
						TypeDescriptor.valueOf(String[].class));
				export.put(values);
			} else {
				// ORM
				EntityStructure<?> structure = orm.getStructure(source.getClass());
				put(source, structure, export);
			}
		}
	}

	public final void titles(ExcelExport export, String... titles) throws ExcelException, IOException {
		if (ArrayUtils.isEmpty(titles)) {
			return;
		}

		titles(export, Arrays.asList(titles));
	}

	public void titles(ExcelExport export, List<String> titles) throws ExcelException, IOException {
		if (!export.isEmpty()) {
			return;
		}

		export.put(titles);
	}

	public final void titles(ExcelExport export, EntityStructure<?> structure) throws ExcelException, IOException {
		if (structure == null) {
			return;
		}

		titles(export, structure.columns().map((e) -> e.getName()).collect(Collectors.toList()));
	}

	public final void put(T source, EntityStructure<?> structure, ExcelExport export) throws IOException {
		titles(export, structure);
		List<String> values = structure.columns().map((property) -> {
			if (!property.getField().isSupportGetter()) {
				return null;
			}

			Object value = property.getField().getGetter().get(source);
			if (value == null) {
				return null;
			}

			return (String) conversionService.convert(value, new TypeDescriptor(property.getField().getGetter()),
					TypeDescriptor.valueOf(String.class));
		}).collect(Collectors.toList());
		export.put(values);
	}
}
