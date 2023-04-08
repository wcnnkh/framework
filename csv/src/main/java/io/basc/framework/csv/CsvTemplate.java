package io.basc.framework.csv;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;

import io.basc.framework.lang.Constants;
import io.basc.framework.orm.transfer.TableTransfer;
import io.basc.framework.util.Assert;

public class CsvTemplate extends TableTransfer {
	private Charset charset = Constants.UTF_8;
	private CSVFormat format = CSVFormat.DEFAULT;

	public Charset getCharset() {
		return charset;
	}

	public void setCharset(Charset charset) {
		Assert.requiredArgument(charset != null, "charset");
		this.charset = charset;
	}

	public CSVFormat getFormat() {
		return format;
	}

	public void setFormat(CSVFormat format) {
		Assert.requiredArgument(format != null, "format");
		this.format = format;
	}

	@Override
	public void write(Iterator<? extends Object> source, OutputStream target) throws IOException {
		OutputStreamWriter writer = new OutputStreamWriter(target, charset);
		write(source, writer);
	}

	@Override
	public void write(Iterator<? extends Object> source, Writer target) throws IOException {
		CSVPrinter printer = new CSVPrinter(target, format);
		AtomicLong count = new AtomicLong();
		try {
			exportAll(source, (contents) -> {
				printer.printRecord(contents);
				if (count.getAndIncrement() % 256 == 0) {
					printer.flush();
				}
			});
		} finally {
			try {
				printer.flush();
			} finally {
				printer.close();
			}
		}
	}

	public Stream<String[]> read(InputStream source) throws IOException {
		InputStreamReader reader = new InputStreamReader(source, charset);
		return read(reader);
	}

	public Stream<String[]> read(Reader reader) throws IOException {
		CSVParser parser = new CSVParser(reader, format);
		try {
			Stream<String[]> stream = parser.stream().map((e) -> e.stream().toArray(String[]::new));
			return stream.onClose(() -> {
				try {
					parser.close();
				} catch (IOException e) {
					throw new CsvException(e);
				}
			});
		} catch (RuntimeException e) {
			parser.close();
			throw e;
		} catch (Throwable e) {
			parser.close();
			throw new CsvException(e);
		}

	}
}
