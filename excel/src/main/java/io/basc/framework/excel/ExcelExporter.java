package io.basc.framework.excel;

import java.io.IOException;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.mapper.io.Exporter;

public interface ExcelExporter extends ExcelImporter, Exporter {

	@Override
	WritableSheet getSheet(int positionIndex);

	@Override
	default void doWrite(Object data, TypeDescriptor typeDescriptor) throws IOException {
		WritableSheet sheet = getSheet(getNumberOfSheets());
		sheet.doWrite(data, typeDescriptor);
	}
}
