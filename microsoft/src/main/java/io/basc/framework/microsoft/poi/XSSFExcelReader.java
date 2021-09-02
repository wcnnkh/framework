package io.basc.framework.microsoft.poi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import io.basc.framework.io.IOUtils;
import io.basc.framework.lang.RequiredJavaVersion;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.microsoft.ExcelException;
import io.basc.framework.microsoft.ExcelReader;
import io.basc.framework.microsoft.RowCallback;

@RequiredJavaVersion(8)
public class XSSFExcelReader implements ExcelReader {
	private static Logger logger = LoggerFactory.getLogger(XSSFExcelReader.class);
	private SAXParserFactory parserFactory = SAXParserFactory.newInstance();
	private boolean firstSheet = false;
	private boolean formulasNotResults = true;

	{
		try {
			parserFactory.setValidating(true);
			parserFactory.setNamespaceAware(false);
			parserFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		} catch (SAXNotRecognizedException | SAXNotSupportedException | ParserConfigurationException e) {
			// 配置异常
			logger.error(e, "Config SAX parser factory error");
		}
	}

	public boolean isFirstSheet() {
		return firstSheet;
	}

	public boolean isFormulasNotResults() {
		return formulasNotResults;
	}

	public void read(OPCPackage opcPackage, RowCallback rowCallback) throws IOException, ExcelException {
		SAXParser parser;
		try {
			parser = parserFactory.newSAXParser();
		} catch (ParserConfigurationException | SAXException e) {
			throw new ExcelException(e);
		}

		XSSFReader reader;
		SharedStringsTable sst = null;

		try {
			reader = new XSSFReader(opcPackage);
			sst = reader.getSharedStringsTable();
			XssfSheetContentsHandler contentsHandler = new XssfSheetContentsHandler(rowCallback, 0);
			XSSFSheetXMLHandler handler = new XSSFSheetXMLHandler(reader.getStylesTable(), sst, contentsHandler,
					formulasNotResults);
			Iterator<InputStream> iterator = reader.getSheetsData();
			while (iterator.hasNext()) {
				InputStream inputStream = null;
				try {
					inputStream = iterator.next();
					InputSource sheetSource = new InputSource(inputStream);
					parser.parse(sheetSource, handler);
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
		private RowCallback rowCallback;
		private List<String> contents = new ArrayList<String>();
		private int sheetIndex;

		public XssfSheetContentsHandler(RowCallback rowCallback, int sheetIndex) {
			this.rowCallback = rowCallback;
			this.sheetIndex = sheetIndex;
		}

		public int getSheetIndex() {
			return sheetIndex;
		}

		public void setSheetIndex(int sheetIndex) {
			this.sheetIndex = sheetIndex;
		}

		public void startRow(int rowNum) {
		}

		public void endRow(int rowNum) {
			rowCallback.processRow(sheetIndex, rowNum, contents.toArray(new String[0]));
			contents.clear();
		}

		public void cell(String cellReference, String formattedValue, XSSFComment comment) {
			contents.add(formattedValue);
		}
	}

	public void read(InputStream input, RowCallback rowCallback) throws IOException {
		OPCPackage opcPackage = null;
		try {
			opcPackage = OPCPackage.open(input);
			read(opcPackage, rowCallback);
		} catch (InvalidFormatException e) {
			throw new ExcelException(e);
		} finally {
			IOUtils.close(opcPackage);
		}
	}

	public void read(File file, RowCallback rowCallback) throws IOException, ExcelException {
		OPCPackage opcPackage = null;
		try {
			opcPackage = OPCPackage.open(file, PackageAccess.READ);
			read(opcPackage, rowCallback);
		} catch (InvalidFormatException e) {
			throw new ExcelException(e);
		} finally {
			IOUtils.close(opcPackage);
		}
	}
}
