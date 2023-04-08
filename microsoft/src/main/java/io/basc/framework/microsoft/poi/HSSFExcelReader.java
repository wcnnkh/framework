package io.basc.framework.microsoft.poi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import io.basc.framework.microsoft.ExcelException;
import io.basc.framework.microsoft.ExcelReader;
import io.basc.framework.microsoft.ExcelRow;
import io.basc.framework.microsoft.ResponsiveExcelReader;

public class HSSFExcelReader extends ResponsiveExcelReader implements ExcelReader {

	public void read(POIFSFileSystem poifsFileSystem, Consumer<? super ExcelRow> consumer) throws IOException, ExcelException {
		DefaultHSSFListener hssfReader = new DefaultHSSFListener(poifsFileSystem, consumer);
		hssfReader.process();
	}

	public void read(InputStream inputStream, Consumer<? super ExcelRow> consumer) throws IOException, ExcelException {
		POIFSFileSystem poifsFileSystem = new POIFSFileSystem(inputStream);
		try {
			read(poifsFileSystem, consumer);
		} finally {
			poifsFileSystem.close();
		}
	}

	public void read(File file, Consumer<? super ExcelRow> consumer) throws IOException, ExcelException {
		POIFSFileSystem poifsFileSystem = new POIFSFileSystem(file, true);
		try {
			read(poifsFileSystem, consumer);
		} finally {
			poifsFileSystem.close();
		}
	}
}
