package io.basc.framework.csv;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Iterator;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import io.basc.framework.http.HttpOutputMessage;
import io.basc.framework.http.HttpUtils;
import io.basc.framework.lang.Constants;

public class CsvUtils {
	private CsvUtils() {
	}

	public static void writeAll(Iterator<? extends Object[]> source, Writer target) throws IOException {
		CSVPrinter csvPrinter = new CSVPrinter(target, CSVFormat.DEFAULT);
		long size = 0;
		try {
			while (source.hasNext()) {
				Object[] contents = source.next();
				if (contents == null) {
					continue;
				}
				csvPrinter.printRecord(contents);
				if (size++ % 256 == 0) {
					csvPrinter.flush();
				}
			}
		} finally {
			try {
				csvPrinter.flush();
			} finally {
				csvPrinter.close();
			}
		}
	}

	public static void writeAll(Iterator<? extends Object[]> source, HttpOutputMessage target, String fileName)
			throws IOException {
		writeAll(source, target, fileName, null);
	}

	public static void writeAll(Iterator<? extends Object[]> source, HttpOutputMessage target, String fileName,
			Charset charset) throws IOException {
		Charset charsetToUse = charset;
		if (charsetToUse == null) {
			charsetToUse = target.getCharset();
		}

		if (charsetToUse == null) {
			charsetToUse = Constants.UTF_8;
		}

		HttpUtils.writeFileMessageHeaders(target, fileName, charsetToUse);
		OutputStreamWriter writer = new OutputStreamWriter(target.getOutputStream(), charsetToUse);
		try {
			writeAll(source, writer);
		} finally {
			writer.flush();
		}
	}
}
