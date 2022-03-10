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
	public SqlExcelExport append(Collection<String> contents) throws IOException {
		excelExport.append(contents);
		return this;
	}

	public ExcelExport append(SqlOperations sqlOperations, Sql sql,
			Processor<ResultSet, String[], SQLException> sqlExportRowMapping) throws ExcelException, IOException {
		try {
			sqlOperations.query(sql, sqlExportRowMapping).forEach((contents) -> {
				try {
					excelExport.append(contents);
				} catch (IOException e) {
					throw new ExcelException(e);
				}
			});
			excelExport.flush();
		} catch (ExcelException | IOException e) {
			close();
			throw e;
		} catch (Throwable e) {
			close();
			throw new ExcelException(e);
		}
		return this;
	}

	public SqlExcelExport export(SqlOperations sqlOperations, Collection<? extends Sql> sqls,
			Processor<ResultSet, String[], SQLException> sqlExportRowMapping) throws ExcelException, IOException {
		for (Sql sql : sqls) {
			append(sqlOperations, sql, sqlExportRowMapping);
		}
		return this;
	}

	/**
	 * 导出并关闭
	 * 
	 * @param titles
	 * @param sqlOperations
	 * @param sqls
	 * @param sqlExportRowMapping
	 * @throws ExcelException
	 * @throws IOException
	 */
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

	/**
	 * 导出并关闭
	 * 
	 * @param titles
	 * @param sqlOperations
	 * @param sql
	 * @param sqlExportRowMapping
	 * @throws ExcelException
	 * @throws IOException
	 */
	public void export(String[] titles, SqlOperations sqlOperations, Sql sql,
			Processor<ResultSet, String[], SQLException> sqlExportRowMapping) throws ExcelException, IOException {
		export(titles, sqlOperations, Arrays.asList(sql), sqlExportRowMapping);
	}

	/**
	 * 导出并关闭
	 * 
	 * @param titles
	 * @param sqlOperations
	 * @param sqls
	 * @throws ExcelException
	 * @throws IOException
	 */
	public void export(String[] titles, SqlOperations sqlOperations, Sql... sqls) throws ExcelException, IOException {
		export(titles, sqlOperations, Arrays.asList(sqls), new SimpleSqlExportRowMapping(titles.length));
	}

	public SqlExcelExport export(SqlOperations sqlOperations, Sql sql, Converter<Object, String> toString) throws IOException {
		Assert.requiredArgument(sqlOperations != null, "sqlOperations");
		Assert.requiredArgument(sql != null, "sql");
		Assert.requiredArgument(toString != null, "toString");
		AtomicBoolean first = new AtomicBoolean(!isEmpty());
		try {
			sqlOperations.query(sql, (rs) -> {
				ResultSetMetaData rsmd = rs.getMetaData();
				int count = rsmd.getColumnCount();
				if (count == 0) {
					return null;
				}

				if (first.compareAndSet(false, true)) {
					append(SqlUtils.getColumnNames(rsmd, count));
				}

				Object[] values = SqlUtils.getRowValues(rs, count);
				String[] columns = toString.convert(values);
				append(columns);
				return columns;
			});
		} catch (RuntimeException e) {
			close();
			throw e;
		}
		return this;
	}

	@Override
	public boolean isEmpty() {
		return excelExport.isEmpty();
	}

	public static SqlExcelExport wrap(ExcelExport excelExport) {
		return new SqlExcelExport(excelExport);
	}

	public static SqlExcelExport create(HttpOutputMessage outputMessage, String fileName)
			throws ExcelException, IOException {
		return create(outputMessage, fileName, null);
	}

	public static SqlExcelExport create(HttpOutputMessage outputMessage, String fileName, @Nullable Charset charset)
			throws ExcelException, IOException {
		return wrap(MicrosoftUtils.createExcelExport(outputMessage, fileName, charset));
	}

	public static SqlExcelExport create(Object target) throws ExcelException, IOException {
		return create(target, null);
	}

	public static SqlExcelExport create(Object target, @Nullable ExcelVersion version)
			throws ExcelException, IOException {
		return wrap(MicrosoftUtils.getExcelOperations().createExcelExport(target, version));
	}
}
