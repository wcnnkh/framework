package io.basc.framework.excel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.basc.framework.io.Resource;
import io.basc.framework.io.WritableResource;
import io.basc.framework.mapper.io.template.RecordExporter;
import io.basc.framework.mapper.io.template.RecordImporter;

public interface ExcelFactory {
	Excel createExcel(Resource resource) throws IOException;

	WritableExcel createWritableExcel(WritableResource resource) throws IOException;

	RecordImporter createImporter(InputStream inputStream) throws IOException;

	RecordExporter createExporter(OutputStream outputStream, ExcelVersion excelVersion) throws IOException;
}
