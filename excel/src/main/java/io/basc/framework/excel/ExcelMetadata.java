package io.basc.framework.excel;

public interface ExcelMetadata {
	/**
	 * 文件扩展名
	 * 
	 * @return
	 */
	String getFileExtension();

	/**
	 * 最大sheet数
	 * 
	 * @return
	 */
	int getMaxSheets();

	/**
	 * 最大行数
	 * 
	 * @return
	 */
	int getMaxRows();

	/**
	 * 最大列数
	 * 
	 * @return
	 */
	int getMaxColumns();
}
