package io.basc.framework.microsoft;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.io.Resource;
import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.orm.EntityStructure;
import io.basc.framework.orm.transfer.ExportProcessor;
import io.basc.framework.orm.transfer.TableTransfer;
import io.basc.framework.util.ArrayUtils;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.stream.Cursor;
import io.basc.framework.util.stream.StreamProcessorSupport;

public class ExcelTemplate extends TableTransfer implements ExportProcessor<Object> {
	private ExcelOperations excelOperations;
	private long readStart = 0;
	private long readLimit = -1;

	public ExcelTemplate() {
		this.excelOperations = MicrosoftUtils.getExcelOperations();
	}

	protected ExcelTemplate(ExcelTemplate source) {
		super(source);
		this.excelOperations = source.excelOperations;
		this.readStart = source.readStart;
		this.readLimit = source.readLimit;
	}

	public ExcelOperations getExcelOperations() {
		return excelOperations;
	}

	public void setExcelOperations(ExcelOperations excelOperations) {
		Assert.requiredArgument(excelOperations != null, "excelOperations");
		this.excelOperations = excelOperations;
	}

	public long getReadStart() {
		return readStart;
	}

	public void setReadStart(long readStart) {
		this.readStart = readStart;
	}

	public long getReadLimit() {
		return readLimit;
	}

	public void setReadLimit(long readLimit) {
		this.readLimit = readLimit;
	}

	@Override
	public final void process(Iterator<? extends Object> source, File target) throws IOException {
		ExcelExport export = getExcelOperations().createExcelExport(target);
		try {
			putAll(source, export);
		} finally {
			export.close();
		}
	}

	public final void putAll(Iterator<? extends Object> source, ExcelExport export) throws IOException {
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

	public final void put(Object source, ExcelExport export) throws IOException {
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
				String[] columns = (String[]) getConversionService().convert(values, TypeDescriptor.forObject(values),
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
				String[] values = (String[]) getConversionService().convert(source, type,
						TypeDescriptor.valueOf(String[].class));
				export.put(values);
			} else {
				// ORM
				EntityStructure<?> structure = getOrm().getStructure(source.getClass());
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

	public final void put(Object source, EntityStructure<?> structure, ExcelExport export) throws IOException {
		titles(export, structure);
		List<String> values = structure.columns().map((property) -> {
			if (!property.getField().isSupportGetter()) {
				return null;
			}

			Object value = property.getField().getGetter().get(source);
			if (value == null) {
				return null;
			}

			return (String) getConversionService().convert(value, new TypeDescriptor(property.getField().getGetter()),
					TypeDescriptor.valueOf(String.class));
		}).collect(Collectors.toList());
		export.put(values);
	}

	public Cursor<String[]> read(Object source) throws ExcelException, IOException {
		Assert.requiredArgument(source != null, "source");
		Assert.requiredArgument(source != null, "source");
		Stream<String[]> stream;
		if (source instanceof InputStream) {
			stream = excelOperations.read((InputStream) source);
		} else if (source instanceof File) {
			stream = excelOperations.read((File) source);
		} else if (source instanceof Resource) {
			stream = ((Resource) source).read((input) -> excelOperations.read(input));
		} else {
			throw new NotSupportedException(source.getClass().getName());
		}
		return StreamProcessorSupport.cursor(stream).limit(readStart, readLimit);
	}
}
