package io.basc.framework.excel.factory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.basc.framework.excel.ExcelExporter;
import io.basc.framework.excel.ExcelImporter;
import io.basc.framework.excel.ExcelMetadata;

public interface TransferFactory {
	ExcelExporter createExporter(OutputStream outputStream, ExcelMetadata metadata);

	ExcelImporter createImporter(InputStream inputStream) throws IOException;
}
