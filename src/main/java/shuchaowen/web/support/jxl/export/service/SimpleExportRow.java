package shuchaowen.web.support.jxl.export.service;

import shuchaowen.core.db.ResultSet;
import shuchaowen.web.support.jxl.export.service.impl.SqlExportRowImpl;

public class SimpleExportRow implements SqlExportRowImpl{
	private int colCount;
	
	public SimpleExportRow(int colCount) {
		this.colCount = colCount;
	}
	
	public String[] exportRow(ResultSet resultSet, int index) {
		if(resultSet.getDataList() == null || resultSet.getDataList().isEmpty() || index >= resultSet.size()){
			return null;
		}
		
		Object[] obj = resultSet.get(Object[].class, index);
		String[] strs = new String[colCount];
		int i=0;
		for(Object v : obj){
			strs[i++] = v.toString();
			if(i >= colCount){
				break;
			}
		}
		return strs;
	}
}
