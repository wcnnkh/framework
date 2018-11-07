package shuchaowen.excel.export;

import shuchaowen.core.db.result.Result;

public interface SqlExportRow{
	public String[] exportRow(Result result);
}
