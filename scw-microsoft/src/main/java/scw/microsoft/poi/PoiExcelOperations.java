package scw.microsoft.poi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import scw.core.utils.ClassUtils;
import scw.env.Sys;
import scw.lang.RequiredJavaVersion;
import scw.microsoft.DefaultExcelExport;
import scw.microsoft.Excel;
import scw.microsoft.ExcelException;
import scw.microsoft.ExcelExport;
import scw.microsoft.ExcelOperations;
import scw.microsoft.ExcelReader;
import scw.microsoft.ExcelVersion;
import scw.microsoft.RowCallback;
import scw.microsoft.WritableExcel;

@RequiredJavaVersion(8)
public class PoiExcelOperations implements ExcelOperations {
	private static final ExcelReader OLE2_READER = Sys.getInstanceFactory()
			.getInstance("scw.microsoft.poi.HSSFExcelReader");
	private static final ExcelReader OOXML_READER = Sys.getInstanceFactory()
			.getInstance("scw.microsoft.poi.XSSFExcelReader");
	private static final boolean XSSF_SUPPORT = ClassUtils
			.isPresent("org.apache.poi.xssf.usermodel.XSSFWorkbookFactory", null);
	@SuppressWarnings("unchecked")
	private static final Class<? extends Workbook> SXSS_WORKBOOK_CLASS = (Class<? extends Workbook>) ClassUtils
			.getClass("org.apache.poi.xssf.streaming.SXSSFWorkbook", null);

	public Excel create(InputStream inputStream) throws IOException, ExcelException {
		Workbook workbook = WorkbookFactory.create(inputStream);
		return new PoiExcel(workbook);
	}

	public Excel create(File file) throws IOException, ExcelException {
		Workbook workbook = WorkbookFactory.create(file, null, true);
		return new PoiExcel(workbook);
	}

	public void read(InputStream inputStream, RowCallback rowCallback) throws IOException, ExcelException {
		InputStream is = FileMagic.prepareToCheckMagic(inputStream);
		FileMagic fm = FileMagic.valueOf(is);
		switch (fm) {
		case OLE2:
			OLE2_READER.read(inputStream, rowCallback);
			break;
		case OOXML:
			OOXML_READER.read(inputStream, rowCallback);
			break;
		default:
			throw new IOException("Your InputStream was neither an OLE2 stream, nor an OOXML stream");
		}
	}

	public void read(File file, RowCallback rowCallback) throws IOException, ExcelException {
		FileMagic fm = FileMagic.valueOf(file);
		switch (fm) {
		case OLE2:
			OLE2_READER.read(file, rowCallback);
			break;
		case OOXML:
			OOXML_READER.read(file, rowCallback);
			break;
		default:
			throw new IOException("Your InputStream was neither an OLE2 stream, nor an OOXML stream");
		}
	}

	public WritableExcel create(OutputStream outputStream) throws IOException, ExcelException {
		Workbook workbook = SXSS_WORKBOOK_CLASS != null
				? Sys.getInstanceFactory().getInstance(SXSS_WORKBOOK_CLASS)
				: WorkbookFactory.create(XSSF_SUPPORT);
		return new PoiExcel(workbook, outputStream);
	}

	public WritableExcel createWritableExcel(File file) throws IOException, ExcelException {
		Workbook workbook = SXSS_WORKBOOK_CLASS != null
				? Sys.getInstanceFactory().getInstance(SXSS_WORKBOOK_CLASS)
				: WorkbookFactory.create(file, null, false);
		return new PoiExcel(workbook, new FileOutputStream(file));
	}

	public ExcelExport createExcelExport(OutputStream outputStream) throws IOException, ExcelException {
		WritableExcel writableExcel = create(outputStream);
		return new DefaultExcelExport(writableExcel, ExcelVersion.XLS, 0, 0);
	}

	public ExcelExport createExcelExport(File file) throws IOException, ExcelException {
		WritableExcel writableExcel = createWritableExcel(file);
		ExcelVersion excelVersion = ExcelVersion.forFileName(file.getName());
		if (excelVersion == null) {
			excelVersion = ExcelVersion.XLS;
		}
		return new DefaultExcelExport(writableExcel, excelVersion, 0, 0);
	}

	public WritableExcel create(OutputStream outputStream, ExcelVersion excelVersion)
			throws IOException, ExcelException {
		ExcelVersion excelVersionTouse = excelVersion == null? ExcelVersion.XLS:excelVersion;
		Workbook workbook;
		if (excelVersionTouse == ExcelVersion.XLS) {
			workbook = WorkbookFactory.create(false);
		} else {
			workbook = SXSS_WORKBOOK_CLASS != null ? Sys.getInstanceFactory().getInstance(SXSS_WORKBOOK_CLASS)
					: WorkbookFactory.create(true);
		}
		return new PoiExcel(workbook, outputStream);
	}

	public ExcelExport createExcelExport(OutputStream outputStream, ExcelVersion excelVersion)
			throws IOException, ExcelException {
		ExcelVersion excelVersionTouse = excelVersion == null? ExcelVersion.XLS:excelVersion;
		WritableExcel writableExcel = create(outputStream, excelVersionTouse);
		return new DefaultExcelExport(writableExcel, excelVersionTouse, 0, 0);
	}
}
