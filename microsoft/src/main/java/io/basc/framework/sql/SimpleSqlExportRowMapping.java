package io.basc.framework.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

import io.basc.framework.util.stream.Processor;

public class SimpleSqlExportRowMapping implements Processor<ResultSet, String[], SQLException> {
	private int colCount;

	public SimpleSqlExportRowMapping(int colCount) {
		this.colCount = colCount;
	}

	@Override
	public String[] process(ResultSet source) throws SQLException {
		Object[] values = SqlUtils.getRowValues(source, colCount);
		String[] strs = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			strs[i] = String.valueOf(values[i]);
		}
		return strs;
	}
}
