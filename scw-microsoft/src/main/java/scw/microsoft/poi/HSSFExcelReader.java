package scw.microsoft.poi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import scw.lang.RequiredJavaVersion;
import scw.microsoft.ExcelException;
import scw.microsoft.ExcelReader;
import scw.microsoft.RowCallback;

@RequiredJavaVersion(8)
public class HSSFExcelReader implements ExcelReader {

	public void read(POIFSFileSystem poifsFileSystem, RowCallback rowCallback) throws IOException, ExcelException {
		XLS2CSVmra hssfReader = new XLS2CSVmra(poifsFileSystem, rowCallback, -1);
		hssfReader.process();
	}
	
	public void read(InputStream inputStream, RowCallback rowCallback) throws IOException, ExcelException {
		POIFSFileSystem poifsFileSystem = new POIFSFileSystem(inputStream);
		try {
			read(poifsFileSystem, rowCallback);
		} finally {
			poifsFileSystem.close();
		}
	}

	public void read(File file, RowCallback rowCallback) throws IOException, ExcelException {
		POIFSFileSystem poifsFileSystem = new POIFSFileSystem(file, true);
		try {
			read(poifsFileSystem, rowCallback);
		} finally {
			poifsFileSystem.close();
		}
	}
}
