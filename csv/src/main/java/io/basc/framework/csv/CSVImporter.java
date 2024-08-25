package io.basc.framework.csv;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import io.basc.framework.mapper.io.Exporter;
import io.basc.framework.mapper.io.template.AbstractRecordImporter;
import io.basc.framework.util.Assert;
import io.basc.framework.util.function.CloseableRegistry;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LoggerFactory;

public class CSVImporter extends AbstractRecordImporter implements Closeable {
	private static Logger logger = LoggerFactory.getLogger(CSVImporter.class);
	private final CloseableRegistry<IOException> closeableRegistry = new CloseableRegistry<>();
	private final CSVParser csvParser;

	public CSVImporter(CSVParser csvParser) {
		Assert.requiredArgument(csvParser != null, "csvParser");
		this.csvParser = csvParser;
	}

	public CSVImporter(final Reader reader) throws IOException {
		this(new CSVParser(reader, CSVFormat.DEFAULT));
	}

	@Override
	public void doRead(Exporter exporter) {
		csvParser.forEach((record) -> {
			CsvRecord csvRecord = new CsvRecord(record);
			try {
				exporter.doWrite(csvRecord);
			} catch (IOException e) {
				logger.error(e, record.toString());
			}
		});
	}

	@Override
	public void close() throws IOException {
		closeableRegistry.close();
	}
}
