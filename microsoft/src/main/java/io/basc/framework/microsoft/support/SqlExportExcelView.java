package io.basc.framework.microsoft.support;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import io.basc.framework.mvc.HttpChannel;
import io.basc.framework.mvc.view.View;
import io.basc.framework.sql.Sql;
import io.basc.framework.sql.SqlOperations;
import io.basc.framework.util.stream.Processor;

public class SqlExportExcelView implements View {
	private Sql sql;
	private SqlOperations sqlOperations;
	private String fileName;
	private String[] titles;
	private Processor<ResultSet, String[], SQLException> sqlExportRowMapping;

	public SqlExportExcelView(SqlOperations sqlOperations, Sql sql, String fileName, String[] titles) {
		this(sqlOperations, sql, fileName, titles, new SimpleSqlExportRowMapping(titles.length));
	}

	public SqlExportExcelView(SqlOperations sqlOperations, Sql sql, String fileName, String[] titles,
			Processor<ResultSet, String[], SQLException> sqlExportRowMapping) {
		this.sqlOperations = sqlOperations;
		this.sql = sql;
		this.fileName = fileName;
		this.titles = titles;
		this.sqlExportRowMapping = sqlExportRowMapping;
	}

	public void render(HttpChannel httpChannel) throws IOException {
		SqlExcelExport.create(httpChannel.getResponse(), fileName + ".xls").export(titles, sqlOperations, sql,
				sqlExportRowMapping);
	}
}
