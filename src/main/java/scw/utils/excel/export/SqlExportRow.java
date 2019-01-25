package scw.utils.excel.export;

import scw.database.Result;

public interface SqlExportRow {
	public String[] exportRow(Result result);
}
