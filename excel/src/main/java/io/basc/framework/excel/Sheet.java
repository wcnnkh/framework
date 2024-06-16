package io.basc.framework.excel;

import java.io.IOException;

import io.basc.framework.mapper.io.table.RowImporter;

/**
 * 这里使用RowImporter而不使用TableImporter的原因是部分实现不支持获取列的数量
 * 
 * @author shuchaowen
 *
 */
public interface Sheet extends SheetContext, RowImporter {
	@Override
	SheetRow readRow(int rowIndex) throws IOException;
}
