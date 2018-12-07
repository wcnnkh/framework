package shuchaowen.excel.export;

import shuchaowen.db.result.Result;

public interface SqlExportRow{
	public String[] exportRow(Result result);
}
