package io.basc.framework.csv;

import java.io.Closeable;
import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import io.basc.framework.mapper.io.template.AbstractRecordExporter;
import io.basc.framework.mapper.io.template.Record;
import io.basc.framework.util.Assert;
import io.basc.framework.util.function.CloseableRegistry;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class CsvExporter extends AbstractRecordExporter implements Closeable {
	private final CloseableRegistry<IOException> closeableRegistry = new CloseableRegistry<>();
	@NonNull
	private final CSVPrinter csvPrinter;

	public CsvExporter(Appendable appendable) throws IOException {
		this(new CSVPrinter(appendable, CSVFormat.DEFAULT));
	}

	public CsvExporter(CSVPrinter csvPrinter) {
		Assert.requiredArgument(csvPrinter != null, "csvPrinter");
		this.csvPrinter = csvPrinter;
	}

	@Override
	public void close() throws IOException {
		try {
			flush();
		} finally {
			closeableRegistry.close();
		}
	}

	@Override
	public void doWriteRecord(Record record) throws IOException {
		csvPrinter.printRecord(record.getArgs().toArray());
	}

	@Override
	public void flush() throws IOException {
		csvPrinter.flush();
	}
}
