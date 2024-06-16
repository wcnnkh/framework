package io.basc.framework.jxl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.basc.framework.excel.ExcelException;
import io.basc.framework.excel.ExcelFactory;
import io.basc.framework.excel.ExcelRecordExporter;
import io.basc.framework.excel.ExcelVersion;
import io.basc.framework.io.Resource;
import io.basc.framework.io.WritableResource;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.mapper.io.template.RecordExporter;
import io.basc.framework.mapper.io.template.RecordImporter;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.WritableWorkbook;

public class JxlExcelFactory implements ExcelFactory {

	@Override
	public JxlExcel createExcel(Resource resource) throws IOException {
		if (resource.isFile()) {
			return createExcel(resource.getFile());
		} else {
			InputStream inputStream = resource.getInputStream();
			try {
				JxlExcel excel = createExcel(inputStream);
				if (!resource.isOpen()) {
					excel.getCloseable().onClose(() -> inputStream.close());
				}
				return excel;
			} catch (IOException e) {
				inputStream.close();
				throw e;
			}
		}
	}

	public JxlExcel createExcel(File file) throws IOException, ExcelException {
		Workbook workbook;
		try {
			workbook = Workbook.getWorkbook(file);
		} catch (BiffException e) {
			throw new ExcelException("create workbook error", e);
		}
		return new JxlExcel(workbook);
	}

	public JxlExcel createExcel(InputStream inputStream) throws IOException, ExcelException {
		Workbook workbook;
		try {
			workbook = Workbook.getWorkbook(inputStream);
		} catch (BiffException e) {
			throw new ExcelException("create workbook error", e);
		}
		return new JxlExcel(workbook);
	}

	public JxlWritableExcel createWritableExcel(OutputStream outputStream, ExcelVersion excelVersion)
			throws IOException, ExcelException {
		if (excelVersion != null && excelVersion != ExcelVersion.XLS) {
			throw new UnsupportedException("Support only xls");
		}

		excelVersion = excelVersion == null ? ExcelVersion.XLS : excelVersion;
		WritableWorkbook workbook = Workbook.createWorkbook(outputStream);
		return new JxlWritableExcel(workbook);
	}

	public JxlWritableExcel createWritableExcel(File file) throws IOException, ExcelException {
		WritableWorkbook workbook = Workbook.createWorkbook(file);
		return new JxlWritableExcel(workbook);
	}

	@Override
	public JxlWritableExcel createWritableExcel(WritableResource resource) throws IOException {
		if (resource.isFile()) {
			return createWritableExcel(resource.getFile());
		} else {
			OutputStream outputStream = resource.getOutputStream();
			try {
				JxlWritableExcel excel = createWritableExcel(outputStream, ExcelVersion.XLS);
				if (!resource.isOpen()) {
					excel.getCloseable().onClose(() -> outputStream.close());
				}
				return excel;
			} catch (IOException e) {
				outputStream.close();
				throw e;
			}
		}
	}

	@Override
	public RecordImporter createImporter(InputStream inputStream) throws IOException {
		return createExcel(inputStream);
	}

	@Override
	public RecordExporter createExporter(OutputStream outputStream, ExcelVersion excelVersion) throws IOException {
		JxlWritableExcel writableExcel = createWritableExcel(outputStream, excelVersion);
		return new ExcelRecordExporter(writableExcel, excelVersion);
	}

}
