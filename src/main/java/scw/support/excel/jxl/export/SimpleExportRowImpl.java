package scw.support.excel.jxl.export;

import scw.sql.orm.result.Result;

public class SimpleExportRowImpl implements SqlExportRow {
	private int colCount;

	public SimpleExportRowImpl(int colCount) {
		this.colCount = colCount;
	}

	public String[] exportRow(Result result) {
		Object[] values = result.getValues();
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
