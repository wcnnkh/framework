package shuchaowen.web.support.jxl.export.service.impl;

import shuchaowen.core.db.ResultSet;

public interface SqlExportRowImpl{
	public String[] exportRow(ResultSet resultSet, int index);
}
