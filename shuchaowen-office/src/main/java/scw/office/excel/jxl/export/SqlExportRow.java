package scw.office.excel.jxl.export;

import scw.orm.sql.ResultMapping;

public interface SqlExportRow {
	public String[] exportRow(ResultMapping resultMapping);
}
