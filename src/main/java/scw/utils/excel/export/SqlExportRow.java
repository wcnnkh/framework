package scw.utils.excel.export;

import scw.database.result.Result;

public interface SqlExportRow{
	public String[] exportRow(Result result);
}
