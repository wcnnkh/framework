package scw.integration.excel.jxl.export;

import scw.orm.sql.ResultMapping;

public class SimpleExportRowImpl implements SqlExportRow {
	private int colCount;

	public SimpleExportRowImpl(int colCount) {
		this.colCount = colCount;
	}

	public String[] exportRow(ResultMapping resultMapping) {
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
