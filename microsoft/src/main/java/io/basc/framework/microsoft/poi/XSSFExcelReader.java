package io.basc.framework.microsoft.poi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.xml.XMLConstants;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFReader.SheetIterator;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import io.basc.framework.io.IOUtils;
import io.basc.framework.microsoft.ExcelException;
import io.basc.framework.microsoft.ExcelReader;
import io.basc.framework.microsoft.ExcelRow;
import io.basc.framework.microsoft.ResponsiveExcelReader;

public class XSSFExcelReader extends ResponsiveExcelReader implements ExcelReader {
	private boolean firstSheet = false;
	private boolean formulasNotResults = true;

	public boolean isFirstSheet() {
		return firstSheet;
	}

	public boolean isFormulasNotResults() {
		return formulasNotResults;
	}

	public XMLReader createReader() throws SAXException {
		XMLReader xmlReader = XMLReaderFactory.createXMLReader();
		xmlReader.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		return xmlReader;
	}

	public void read(OPCPackage opcPackage, Consumer<? super ExcelRow> consumer) throws IOException, ExcelException {
		XSSFReader reader;
		SharedStringsTable sst = null;
		XMLReader xmlReader;
		try {
			xmlReader = createReader();
			reader = new XSSFReader(opcPackage);
			sst = reader.getSharedStringsTable();
			XssfSheetContentsHandler contentsHandler = new XssfSheetContentsHandler(consumer);
			XSSFSheetXMLHandler handler = new XSSFSheetXMLHandler(reader.getStylesTable(), sst, contentsHandler,
					formulasNotResults);
			xmlReader.setContentHandler(handler);
			SheetIterator iterator = (SheetIterator) reader.getSheetsData();
			while (iterator.hasNext()) {
				InputStream inputStream = null;
				try {
					inputStream = iterator.next();
					contentsHandler.setSheetName(iterator.getSheetName());
					InputSource sheetSource = new InputSource(inputStream);
					xmlReader.parse(sheetSource);
					contentsHandler.setSheetIndex(contentsHandler.getSheetIndex() + 1);
					if (firstSheet) {
						break;
					}
				} finally {
					IOUtils.close(inputStream);
				}
			}
		} catch (OpenXML4JException e) {
			throw new ExcelException(e);
		} catch (SAXException e) {
			throw new ExcelException(e);
		} finally {
			sst.close();
		}
	}

	private static class XssfSheetContentsHandler implements SheetContentsHandler {
		private Consumer<? super ExcelRow> consumer;
		private List<String> contents = new ArrayList<String>();
		private int sheetIndex = 0;
		private String sheetName;

		public XssfSheetContentsHandler(Consumer<? super ExcelRow> consumer) {
			this.consumer = consumer;
		}

		public int getSheetIndex() {
			return sheetIndex;
		}

		public void setSheetIndex(int sheetIndex) {
			this.sheetIndex = sheetIndex;
		}

		public void setSheetName(String sheetName) {
			this.sheetName = sheetName;
		}

		public void startRow(int rowNum) {
		}

		public void endRow(int rowNum) {
			consumer.accept(new ExcelRow(sheetIndex, sheetName, rowNum, contents.toArray(new String[0])));
			contents.clear();
		}

		public void cell(String cellReference, String formattedValue, XSSFComment comment) {
			contents.add(formattedValue);
		}
	}

	public void read(InputStream input, Consumer<? super ExcelRow> consumer) throws IOException {
		OPCPackage opcPackage = null;
		try {
			opcPackage = OPCPackage.open(input);
			read(opcPackage, consumer);
		} catch (InvalidFormatException e) {
			throw new ExcelException(e);
		} finally {
			IOUtils.close(opcPackage);
		}
	}

	public void read(File file, Consumer<? super ExcelRow> consumer) throws IOException, ExcelException {
		OPCPackage opcPackage = null;
		try {
			opcPackage = OPCPackage.open(file, PackageAccess.READ);
			read(opcPackage, consumer);
		} catch (InvalidFormatException e) {
			throw new ExcelException(e);
		} finally {
			IOUtils.close(opcPackage);
		}
	}
}
