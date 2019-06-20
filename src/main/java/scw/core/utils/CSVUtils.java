package scw.core.utils;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;

import scw.core.Constants;

public final class CSVUtils {
	private CSVUtils() {
	};

	public static final char DEFAULT_SPLIT = ',';

	public static void writeRow(Writer writer, char split, Object[] values) throws IOException {
		if (ArrayUtils.isEmpty(values)) {
			return;
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < values.length; i++) {
			if (i != 0) {
				sb.append(split);
			}

			sb.append(values[i]);
		}
		writer.write(sb.toString());
	}

	public static void writeRow(Writer writer, Object[] values) throws IOException {
		writeRow(writer, DEFAULT_SPLIT, values);
	}

	public static void write(Writer writer, char split, Collection<Object[]> dataList) throws IOException {
		int i = 0;
		Iterator<Object[]> iterator = dataList.iterator();
		while (iterator.hasNext()) {
			Object[] values = iterator.next();
			if (ArrayUtils.isEmpty(values)) {
				continue;
			}

			if (i != 0) {
				writer.write(Constants.LINE_SEPARATOR);
			}

			i++;
			writeRow(writer, split, values);
		}
	}

	public static void write(Writer writer, Collection<Object[]> dataList) throws IOException {
		write(writer, DEFAULT_SPLIT, dataList);
	}
}
