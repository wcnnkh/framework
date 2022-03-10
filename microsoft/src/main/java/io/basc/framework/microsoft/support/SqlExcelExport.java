package io.basc.framework.microsoft.support;

import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import io.basc.framework.convert.Converter;
import io.basc.framework.http.HttpOutputMessage;
import io.basc.framework.lang.Nullable;
import io.basc.framework.microsoft.ExcelException;
import io.basc.framework.microsoft.ExcelExport;
import io.basc.framework.microsoft.ExcelVersion;
import io.basc.framework.microsoft.MicrosoftUtils;
import io.basc.framework.sql.Sql;
import io.basc.framework.sql.SqlOperations;
import io.basc.framework.sql.SqlUtils;
import io.basc.framework.util.Assert;
import io.basc.framework.util.stream.Processor;

public class SqlExcelExport implements ExcelExport {
	private final ExcelExport excelExport;

	public SqlExcelExport(ExcelExport excelExport) {
		this.excelExport = excelExport;
	}

	@Override
	public void flush() throws IOException {
		excelExport.flush();
	}

	@Override
	public void close() throws IOException {
		excelExport.close();
	}

	@Override
	public void append(Collection<String> contents) throws IOException {
		excelExport.append(contents);
	}

	public void append(SqlOperations sqlOperations, Sql sql,
			Processor<ResultSet, String[], SQLException> sqlExportRowMapping) throws ExcelException, IOException {
		sqlOperations.query(sql, sqlExportRowMapping).forEach((contents) -> {
			try {
				excelExport.append(contents);
			} catch (IOException e) {
				throw new ExcelException(e);
			}
		});
		excelExport.flush();
	}

	public void export(String[] titles, SqlOperations sqlOperations, Collection<? extends Sql> sqls,
			Processor<ResultSet, String[], SQLException> sqlExportRowMapping) throws ExcelException, IOException {
		try {
			append(titles);
			for (Sql sql : sqls) {
				append(sqlOperations, sql, sqlExportRowMapping);
			}
		} finally {
			close();
		}
	}

	public void export(String[] titles, SqlOperations sqlOperations, Sql sql,
			Processor<ResultSet, String[], SQLException> sqlExportRowMapping) throws ExcelException, IOException {
		export(titles, sqlOperations, Arrays.asList(sql), sqlExportRowMapping);
	}

	public void export(String[] titles, SqlOperations sqlOperations, Sql... sqls) throws ExcelException, IOException {
		export(titles, sqlOperations, Arrays.asList(sqls), new SimpleSqlExportRowMapping(titles.length));
	}

	@Override
	public boolean isEmpty() {
		return excelExport.isEmpty();
	}

	public static SqlExcelExport create(ExcelExport excelExport) {
		return new SqlExcelExport(excelExport);
	}

	public static void export(SqlOperations sqlOperations, Sql sql, ExcelExport export,
			Converter<Object, String> toString) {
		Assert.requiredArgument(sqlOperations != null, "sqlOperations");
		Assert.requiredArgument(sql != null, "sql");
		Assert.requiredArgument(export != null, "export");
		Assert.requiredArgument(toString != null, "toString");
		AtomicBoolean first = new AtomicBoolean(!export.isEmpty());
		sqlOperations.query(sql, (rs) -> {
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			if (count == 0) {
				return null;
			}

			if (first.compareAndSet(false, true)) {
				export.append(SqlUtils.getColumnNames(rsmd, count));
			}

			Object[] values = SqlUtils.getRowValues(rs, count);
			String[] columns = toString.convert(values);
			export.append(columns);
			return columns;
		});
	}

	public static void export(SqlOperations sqlOperations, Sql sql, Object target, @Nullable ExcelVersion version,
			Converter<Object, String> toString) throws IOException {
		Assert.requiredArgument(target != null, "target");
		ExcelExport export = MicrosoftUtils.getExcelOperations().createExcelExport(target, version);
		try {
			export(sqlOperations, sql, export, toString);
		} finally {
			export.close();
		}
	}

	public static void export(SqlOperations sqlOperations, Sql sql, HttpOutputMessage outputMessage, String fileName,
			@Nullable Charset charset, Converter<Object, String> toString) throws IOException {
		ExcelExport export = MicrosoftUtils.createExcelExport(outputMessage, fileName, charset);
		try {
			export(sqlOperations, sql, export, toString);
		} finally {
			export.close();
		}
	}
}
