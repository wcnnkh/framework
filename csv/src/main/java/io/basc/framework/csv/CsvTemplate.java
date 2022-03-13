package io.basc.framework.csv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;

import io.basc.framework.lang.Constants;
import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.orm.transfer.TableTransfer;
import io.basc.framework.util.Assert;
import io.basc.framework.util.stream.Cursor;
import io.basc.framework.util.stream.StreamProcessorSupport;

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

	public void process(Iterator<? extends Object> source, OutputStream target) throws IOException {
		OutputStreamWriter writer = new OutputStreamWriter(target, charset);
		CSVPrinter printer = new CSVPrinter(writer, format);
		AtomicLong count = new AtomicLong();
		try {
			exportAll(source, (contents) -> {
				printer.printRecord(contents);
				if(count.getAndIncrement()%256 == 0) {
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

	@Override
	public final void process(Iterator<? extends Object> source, File target) throws IOException {
		FileOutputStream fos = new FileOutputStream(target);
		try {
			process(source, fos);
		} finally {
			fos.close();
		}
	}

	@Override
	public Cursor<String[]> read(Object source) throws IOException {
		if (source instanceof InputStream) {
			return read((InputStream) source);
		} else if (source instanceof Reader) {
			return read((Reader) source);
		} else if (source instanceof File) {
			FileInputStream fis = new FileInputStream((File) source);
			try {
				return read(fis).onClose(() -> {
					try {
						fis.close();
					} catch (IOException e) {
						throw new CsvException(e);
					}
				});
			} catch (RuntimeException | IOException e) {
				fis.close();
				throw e;
			} catch (Throwable e) {
				fis.close();
				throw new CsvException(e);
			}
		}
		throw new NotSupportedException(source.toString());
	}

	public Cursor<String[]> read(InputStream source) throws IOException {
		InputStreamReader reader = new InputStreamReader(source, charset);
		return read(reader);
	}

	public Cursor<String[]> read(Reader reader) throws IOException {
		CSVParser parser = new CSVParser(reader, format);
		try {
			Stream<String[]> stream = parser.stream().map((e) -> e.stream().toArray(String[]::new));
			return StreamProcessorSupport.cursor(stream).onClose(() -> {
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
