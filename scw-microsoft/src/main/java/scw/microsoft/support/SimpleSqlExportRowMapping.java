package scw.microsoft.support;

import java.sql.ResultSet;
import java.sql.SQLException;

import scw.sql.SqlProcessor;
import scw.sql.SqlUtils;

public class SimpleSqlExportRowMapping implements SqlProcessor<ResultSet, String[]> {
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
