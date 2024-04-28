package io.basc.framework.poi.hssf;

import java.io.IOException;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import io.basc.framework.excel.ExcelImporter;
import io.basc.framework.mapper.transfer.Exporter;

public class HSSFExcelImporter implements ExcelImporter {
	private final POIFSFileSystem poifsFileSystem;

	@Override
	public void doRead(Exporter exporter) throws IOException {
		
	}

}
