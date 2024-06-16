package io.basc.framework.poi.xssf;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.XMLConstants;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFReader.SheetIterator;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import io.basc.framework.excel.ExcelException;
import io.basc.framework.io.IOUtils;
import io.basc.framework.mapper.io.Exporter;
import io.basc.framework.mapper.io.template.AbstractRecordImporter;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class XssfRecordImporter extends AbstractRecordImporter {
	@NonNull
	private final OPCPackage opcPackage;
	private boolean firstSheet = false;
	private boolean formulasNotResults = true;

	public XMLReader createReader() throws SAXException {
		XMLReader xmlReader = XMLReaderFactory.createXMLReader();
		xmlReader.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		return xmlReader;
	}

	@Override
	public void doRead(Exporter exporter) throws IOException {
		XSSFReader reader;
		SharedStringsTable sst = null;
		XMLReader xmlReader;
		try {
			xmlReader = createReader();
			reader = new XSSFReader(opcPackage);
			sst = reader.getSharedStringsTable();
			DefaultSheetContentsHandler contentsHandler = new DefaultSheetContentsHandler(exporter);
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

}
