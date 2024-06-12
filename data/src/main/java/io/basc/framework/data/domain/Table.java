package io.basc.framework.data.domain;

import java.io.Serializable;

import io.basc.framework.convert.value.Values;
import io.basc.framework.util.element.Elements;

/**
 * 定义表格数据
 * 
 * @author wcnnkh
 *
 */
public class Table implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 表头
	 */
	private String[] titles;
	/**
	 * 行数据
	 */
	private Elements<Values> rows;

	public String[] getTitles() {
		return titles;
	}

	public void setTitles(String[] titles) {
		this.titles = titles;
	}

	public Elements<Values> getRows() {
		return rows;
	}

	public void setRows(Elements<Values> rows) {
		this.rows = rows;
	}

}
