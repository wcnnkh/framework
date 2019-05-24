package scw.support.jxl.export;

import scw.sql.orm.result.Result;

public interface SqlExportRow {
	public String[] exportRow(Result result);
}
