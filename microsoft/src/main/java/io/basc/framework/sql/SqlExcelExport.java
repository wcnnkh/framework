package io.basc.framework.sql;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;

import io.basc.framework.microsoft.ExcelException;
import io.basc.framework.microsoft.ExcelExport;
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
			Processor<ResultSet, String[], SQLException> sqlExportRowMapping)
			throws ExcelException, IOException {
		sqlOperations.query(sql, sqlExportRowMapping).forEach((contents) -> {
			try {
				excelExport.append(contents);
			} catch (IOException e) {
				throw new ExcelException(e);
			}
		});
		excelExport.flush();
	}

	public void export(String[] titles, SqlOperations sqlOperations,
			Collection<? extends Sql> sqls,
			Processor<ResultSet, String[], SQLException> sqlExportRowMapping)
			throws ExcelException, IOException {
		try {
			append(titles);
			for (Sql sql : sqls) {
				append(sqlOperations, sql, sqlExportRowMapping);
			}
		} finally {
			close();
		}
	}
	
	public void export(String[] titles, SqlOperations sqlOperations,
			Sql sql,
			Processor<ResultSet, String[], SQLException> sqlExportRowMapping)
			throws ExcelException, IOException {
		export(titles, sqlOperations, Arrays.asList(sql), sqlExportRowMapping);
	}

	public void export(String[] titles, SqlOperations sqlOperations,
			Sql... sqls) throws ExcelException, IOException {
		export(titles, sqlOperations, Arrays.asList(sqls),
				new SimpleSqlExportRowMapping(titles.length));
	}

	public static SqlExcelExport create(ExcelExport excelExport) {
		return new SqlExcelExport(excelExport);
	}
}
