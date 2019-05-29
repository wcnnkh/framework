package scw.utils.excel.jxl.export;

import scw.sql.orm.result.Result;

public interface SqlExportRow {
	public String[] exportRow(Result result);
}
