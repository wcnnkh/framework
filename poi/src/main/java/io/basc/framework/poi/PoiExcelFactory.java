package io.basc.framework.poi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import io.basc.framework.excel.Excel;
import io.basc.framework.excel.ExcelException;
import io.basc.framework.excel.ExcelFactory;
import io.basc.framework.excel.ExcelRecordExporter;
import io.basc.framework.excel.ExcelVersion;
import io.basc.framework.excel.WritableExcel;
import io.basc.framework.io.Resource;
import io.basc.framework.io.WritableResource;
import io.basc.framework.mapper.io.template.RecordExporter;
import io.basc.framework.mapper.io.template.RecordImporter;
import io.basc.framework.poi.hssf.HssfRecordImporter;
import io.basc.framework.poi.ss.PoiExcel;
import io.basc.framework.poi.xssf.XssfRecordImporter;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.reflect.ReflectionUtils;

public class PoiExcelFactory implements ExcelFactory {

	@SuppressWarnings("unchecked")
	private static final Class<? extends Workbook> SXSS_WORKBOOK_CLASS = (Class<? extends Workbook>) ClassUtils
			.getClass("org.apache.poi.xssf.streaming.SXSSFWorkbook", null);

	private static final boolean XSSF_SUPPORT = ClassUtils
			.isPresent("org.apache.poi.xssf.usermodel.XSSFWorkbookFactory", null);

	@Override
	public Excel createExcel(Resource resource) throws IOException {
		if (resource.isFile()) {
			return createExcel(resource.getFile());
		} else {
			InputStream inputStream = resource.getInputStream();
			PoiExcel excel = createExcel(inputStream);
			excel.onClose(() -> inputStream.close());
			return excel;
		}
	}

	public Excel createExcel(File file) throws IOException {
		Workbook workbook = WorkbookFactory.create(file, null, true);
		return new PoiExcel(workbook);
	}

	public PoiExcel createExcel(InputStream inputStream) throws IOException, ExcelException {
		Workbook workbook = WorkbookFactory.create(inputStream);
		return new PoiExcel(workbook);
	}

	@Override
	public RecordExporter createExporter(OutputStream outputStream, ExcelVersion excelVersion) throws IOException {
		ExcelVersion excelVersionTouse = excelVersion == null ? ExcelVersion.XLS : excelVersion;
		WritableExcel writableExcel = createWritableExcel(outputStream, excelVersionTouse, false);
		return new ExcelRecordExporter(writableExcel, excelVersionTouse);
	}

	@Override
	public RecordImporter createImporter(InputStream inputStream) throws IOException {
		// 使用可以回退的inputStream,因为判断类型会预读取44字节的数据，后面再判断时会出现异常
		InputStream is = new PushbackInputStream(inputStream);
		is = FileMagic.prepareToCheckMagic(inputStream);
		FileMagic fm = FileMagic.valueOf(is);
		switch (fm) {
		case OLE2:
			return createHssfImporter(is);
		case OOXML:
			return createXssfImporter(is);
		default:
			throw new IOException("Your InputStream was neither an OLE2 stream, nor an OOXML stream");
		}
	}

	public RecordImporter createHssfImporter(InputStream inputStream) throws IOException {
		POIFSFileSystem poifsFileSystem = new POIFSFileSystem(inputStream);
		return new HssfRecordImporter(poifsFileSystem);
	}

	public RecordImporter createXssfImporter(InputStream inputStream) throws IOException {
		OPCPackage opcPackage;
		try {
			opcPackage = OPCPackage.open(inputStream);
		} catch (InvalidFormatException e) {
			throw new ExcelException(e);
		}
		return new XssfRecordImporter(opcPackage);
	}

	public PoiExcel createWritableExcel(OutputStream outputStream) throws IOException, ExcelException {
		Workbook workbook = SXSS_WORKBOOK_CLASS != null ? ReflectionUtils.newInstance(SXSS_WORKBOOK_CLASS)
				: WorkbookFactory.create(XSSF_SUPPORT);
		PoiExcel poiExcel = new PoiExcel(workbook);
		poiExcel.setOutputStream(outputStream);
		return poiExcel;
	}

	@Override
	public WritableExcel createWritableExcel(WritableResource resource) throws IOException {
		OutputStream outputStream = resource.getOutputStream();
		PoiExcel poiExcel;
		try {
			poiExcel = createWritableExcel(outputStream);
		} catch (IOException e) {
			outputStream.close();
			throw e;
		}
		poiExcel.onClose(() -> outputStream.close());
		return poiExcel;
	}

	public PoiExcel createWritableExcel(OutputStream outputStream, ExcelVersion excelVersion, boolean closeStream)
			throws IOException, ExcelException {
		ExcelVersion excelVersionTouse = excelVersion == null ? ExcelVersion.XLS : excelVersion;
		Workbook workbook;
		if (excelVersionTouse == ExcelVersion.XLS) {
			workbook = WorkbookFactory.create(false);
		} else {
			workbook = SXSS_WORKBOOK_CLASS != null ? ReflectionUtils.newInstance(SXSS_WORKBOOK_CLASS)
					: WorkbookFactory.create(true);
		}

		PoiExcel poiExcel = new PoiExcel(workbook);
		poiExcel.setOutputStream(outputStream);
		if (closeStream) {
			poiExcel.onClose(() -> outputStream.close());
		}
		return poiExcel;
	}

	public PoiExcel createWritableExcel(File file) throws IOException {
		if (file.length() == 0) {
			// 文件内容为空
			ExcelVersion version = ExcelVersion.forFileExtension(file.getName());
			if (SXSS_WORKBOOK_CLASS != null && version == ExcelVersion.XLSX) {
				Workbook workbook = ReflectionUtils.newInstance(SXSS_WORKBOOK_CLASS);
				PoiExcel excel = new PoiExcel(workbook);
				FileOutputStream fos = new FileOutputStream(file);
				excel.setOutputStream(fos);
				excel.onClose(() -> fos.close());
				return excel;
			}
			return createWritableExcel(new FileOutputStream(file), version, true);
		}

		Workbook workbook = WorkbookFactory.create(file);
		return new PoiExcel(workbook);
	}
}
