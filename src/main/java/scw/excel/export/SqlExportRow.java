package scw.excel.export;

import scw.db.result.Result;

public interface SqlExportRow{
	public String[] exportRow(Result result);
}
