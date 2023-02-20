package io.basc.framework.microsoft.poi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import io.basc.framework.microsoft.AbstractExcelReader;
import io.basc.framework.microsoft.ExcelException;
import io.basc.framework.microsoft.ExcelReader;
import io.basc.framework.microsoft.ExcelRow;

public class HSSFExcelReader extends AbstractExcelReader implements ExcelReader {

	public void read(POIFSFileSystem poifsFileSystem, Consumer<ExcelRow> consumer) throws IOException, ExcelException {
		DefaultHSSFListener hssfReader = new DefaultHSSFListener(poifsFileSystem, consumer);
		hssfReader.process();
	}

	public void read(InputStream inputStream, Consumer<ExcelRow> consumer) throws IOException, ExcelException {
		POIFSFileSystem poifsFileSystem = new POIFSFileSystem(inputStream);
		try {
			read(poifsFileSystem, consumer);
		} finally {
			poifsFileSystem.close();
		}
	}

	public void read(File file, Consumer<ExcelRow> consumer) throws IOException, ExcelException {
		POIFSFileSystem poifsFileSystem = new POIFSFileSystem(file, true);
		try {
			read(poifsFileSystem, consumer);
		} finally {
			poifsFileSystem.close();
		}
	}
}
