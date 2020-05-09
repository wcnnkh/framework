package scw.office.excel.jxl.export;

import scw.sql.orm.ResultMapping;

public interface SqlExportRow {
	public String[] exportRow(ResultMapping resultMapping);
}
