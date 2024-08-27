package io.basc.framework.csv;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;

import io.basc.framework.io.InputStreamProcessor;
import io.basc.framework.io.OutputStreamProcessor;
import io.basc.framework.io.WritableResource;
import io.basc.framework.mapper.io.Exporter;
import io.basc.framework.mapper.io.template.AbstractRecordExporter;
import io.basc.framework.mapper.io.template.Record;
import io.basc.framework.mapper.io.template.RecordExporter;
import io.basc.framework.mapper.io.template.RecordImporter;
import io.basc.framework.util.Elements;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CSV extends AbstractRecordExporter implements RecordExporter, RecordImporter {
	@NonNull
	private CSVFormat csvFormat = CSVFormat.DEFAULT;
	@NonNull
	private InputStreamProcessor<? extends Reader> inputStreamProcessor = InputStreamProcessor.toInputStreamReader();
	private OutputStreamProcessor<? extends Writer> outputStreamProcessor = OutputStreamProcessor
			.toOutputStreamWriter();
	@NonNull
	private final WritableResource writableResource;

	protected void doRead(CSVParser csvParser, Exporter exporter) throws IOException {
		CSVImporter csvImporter = new CSVImporter(csvParser);
		try {
			csvImporter.doRead(exporter);
		} finally {
			csvImporter.close();
		}
	}

	@Override
	public void doRead(Exporter exporter) throws IOException {
		InputStream inputStream = writableResource.getInputStream();
		try {
			doRead(inputStream, exporter);
		} finally {
			if (!writableResource.isOpen()) {
				inputStream.close();
			}
		}
	}

	protected void doRead(InputStream inputStream, Exporter exporter) throws IOException {
		Reader reader = inputStreamProcessor.process(inputStream);
		try {
			doRead(reader, exporter);
		} finally {
			reader.close();
		}
	}

	protected void doRead(Reader reader, Exporter exporter) throws IOException {
		CSVParser csvParser = new CSVParser(reader, csvFormat);
		try {
			doRead(csvParser, exporter);
		} finally {
			csvParser.close();
		}
	}

	@Override
	public void doWriteRecord(Record record) throws IOException {
		doWriteRecords(Elements.singleton(record));
	}

	public void doWriteRecords(Elements<? extends Record> records) throws IOException {
		OutputStream outputStream;
		if (writableResource.isFile()) {
			outputStream = new FileOutputStream(writableResource.getFile(), true);
		} else {
			outputStream = writableResource.getOutputStream();
		}
		try {
			doWriteRecords(outputStream, records);
		} finally {
			if (!writableResource.isOpen()) {
				outputStream.close();
			}
		}
	}

	protected void doWriteRecords(CSVPrinter csvPrinter, Elements<? extends Record> records) throws IOException {
		CsvExporter csvExporter = new CsvExporter(csvPrinter);
		try {
			for (Record record : records) {
				csvExporter.doWriteRecord(record);
			}
		} finally {
			csvExporter.close();
		}
	}

	protected void doWriteRecords(OutputStream outputStream, Elements<? extends Record> records) throws IOException {
		Writer writer = outputStreamProcessor.process(outputStream);
		try {
			doWriteRecords(writer, records);
		} finally {
			writer.close();
		}
	}

	protected void doWriteRecords(Writer writer, Elements<? extends Record> records) throws IOException {
		CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat);
		try {
			doWriteRecords(csvPrinter, records);
		} finally {
			csvPrinter.close();
		}
	}
}
