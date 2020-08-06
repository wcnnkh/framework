package scw.office.support;

import scw.sql.orm.ResultMapping;

public class SimpleSqlExportRowMapping implements SqlExportRowMapping {
	private int colCount;

	public SimpleSqlExportRowMapping(int colCount) {
		this.colCount = colCount;
	}

	public String[] mapping(ResultMapping resultMapping) {
		Object[] values = resultMapping.getValues();
		String[] strs = new String[colCount];
		int i = 0;
		for (Object v : values) {
			strs[i++] = v.toString();
			if (i >= colCount) {
				break;
			}
		}
		return strs;
	}
}
