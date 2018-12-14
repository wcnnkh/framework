package shuchaowen.excel.export;

import shuchaowen.db.result.ResultSet;

public interface SqlExportRow{
	public String[] exportRow(ResultSet resultSet, int index);
}
