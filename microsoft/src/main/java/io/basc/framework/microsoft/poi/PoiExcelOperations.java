package io.basc.framework.microsoft.poi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Consumer;

import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.env.Sys;
import io.basc.framework.lang.RequiredJavaVersion;
import io.basc.framework.microsoft.AbstractExcelReader;
import io.basc.framework.microsoft.Excel;
import io.basc.framework.microsoft.ExcelException;
import io.basc.framework.microsoft.ExcelOperations;
import io.basc.framework.microsoft.ExcelReader;
import io.basc.framework.microsoft.ExcelRow;
import io.basc.framework.microsoft.ExcelVersion;
import io.basc.framework.microsoft.WritableExcel;
import io.basc.framework.util.ClassUtils;

@RequiredJavaVersion(8)
public class PoiExcelOperations extends AbstractExcelReader implements ExcelOperations {
	private static final ExcelReader OLE2_READER = (ExcelReader) Sys.getEnv()
			.getInstance("io.basc.framework.microsoft.poi.HSSFExcelReader");
	private static final ExcelReader OOXML_READER = (ExcelReader) Sys.getEnv()
			.getInstance("io.basc.framework.microsoft.poi.XSSFExcelReader");
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

	public void read(InputStream inputStream, Consumer<ExcelRow> consumer) throws IOException, ExcelException {
		InputStream is = FileMagic.prepareToCheckMagic(inputStream);
		FileMagic fm = FileMagic.valueOf(is);
		switch (fm) {
		case OLE2:
			OLE2_READER.read(inputStream, consumer);
			break;
		case OOXML:
			OOXML_READER.read(inputStream, consumer);
			break;
		default:
			throw new IOException("Your InputStream was neither an OLE2 stream, nor an OOXML stream");
		}
	}

	public void read(File file, Consumer<ExcelRow> consumer) throws IOException, ExcelException {
		FileMagic fm = FileMagic.valueOf(file);
		switch (fm) {
		case OLE2:
			OLE2_READER.read(file, consumer);
			break;
		case OOXML:
			OOXML_READER.read(file, consumer);
			break;
		default:
			throw new IOException("Your InputStream was neither an OLE2 stream, nor an OOXML stream");
		}
	}

	public WritableExcel create(OutputStream outputStream) throws IOException, ExcelException {
		Workbook workbook = SXSS_WORKBOOK_CLASS != null ? ReflectionUtils.newInstance(SXSS_WORKBOOK_CLASS)
				: WorkbookFactory.create(XSSF_SUPPORT);
		return new PoiExcel(workbook, outputStream, false);
	}

	public WritableExcel createWritableExcel(File file) throws IOException, ExcelException {
		if (file.length() == 0) {
			// 文件内容为空
			ExcelVersion version = ExcelVersion.forFileName(file.getName());
			if (SXSS_WORKBOOK_CLASS != null && version == ExcelVersion.XLSX) {
				Workbook workbook = ReflectionUtils.newInstance(SXSS_WORKBOOK_CLASS);
				return new PoiExcel(workbook, new FileOutputStream(file), true);
			}
			return create(new FileOutputStream(file), version, true);
		}

		Workbook workbook = WorkbookFactory.create(file);
		return new PoiExcel(workbook);
	}

	@Override
	public WritableExcel create(OutputStream outputStream, ExcelVersion excelVersion)
			throws IOException, ExcelException {
		return create(outputStream, excelVersion, false);
	}

	public WritableExcel create(OutputStream outputStream, ExcelVersion excelVersion, boolean closeStream)
			throws IOException, ExcelException {
		ExcelVersion excelVersionTouse = excelVersion == null ? ExcelVersion.XLS : excelVersion;
		Workbook workbook;
		if (excelVersionTouse == ExcelVersion.XLS) {
			workbook = WorkbookFactory.create(false);
		} else {
			workbook = SXSS_WORKBOOK_CLASS != null ? ReflectionUtils.newInstance(SXSS_WORKBOOK_CLASS)
					: WorkbookFactory.create(true);
		}
		return new PoiExcel(workbook, outputStream, closeStream);
	}
}
