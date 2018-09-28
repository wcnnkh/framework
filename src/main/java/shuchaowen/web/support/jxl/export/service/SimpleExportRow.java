package shuchaowen.web.support.jxl.export.service;

import shuchaowen.core.db.result.Result;
import shuchaowen.web.support.jxl.export.service.impl.SqlExportRowImpl;

public class SimpleExportRow implements SqlExportRowImpl{
	private int colCount;
	
	public SimpleExportRow(int colCount) {
		this.colCount = colCount;
	}
	
	public String[] exportRow(Result result) {
		Object[] values = result.getValues();
		String[] strs = new String[colCount];
		int i=0;
		for(Object v : values){
			strs[i++] = v.toString();
			if(i >= colCount){
				break;
			}
		}
		return strs;
	}
}
