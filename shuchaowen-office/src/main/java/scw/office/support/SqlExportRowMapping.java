package scw.office.support;

import scw.sql.orm.ResultMapping;

public interface SqlExportRowMapping {
	String[] mapping(ResultMapping resultMapping);
}
