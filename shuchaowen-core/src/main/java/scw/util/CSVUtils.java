package scw.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import scw.core.utils.ArrayUtils;
import scw.core.utils.StringUtils;

public final class CSVUtils {
	private CSVUtils() {
	};

	public static final char DEFAULT_SPLIT = ',';

	public static void writeRow(Writer writer, char split, boolean removeLineBreaks, Object[] values)
			throws IOException {
		if (ArrayUtils.isEmpty(values)) {
			return;
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < values.length; i++) {
			if (i != 0) {
				sb.append(split);
			}

			Object v = values[i];
			if (v == null) {
				sb.append(v);
			} else {
				if (removeLineBreaks) {
					sb.append(v.toString().replaceAll("\\n", ""));
				} else {
					sb.append(v.toString().replaceAll("\\n", "\\\n"));
				}
			}
		}
		writer.write(sb.toString());
	}

	public static void writeRow(Writer writer, Object[] values) throws IOException {
		writeRow(writer, DEFAULT_SPLIT, true, values);
	}

	public static void write(Writer writer, char split, boolean removeLineBreaks, Collection<Object[]> dataList)
			throws IOException {
		int i = 0;
		Iterator<Object[]> iterator = dataList.iterator();
		while (iterator.hasNext()) {
			Object[] values = iterator.next();
			if (ArrayUtils.isEmpty(values)) {
				continue;
			}

			if (i != 0) {
				writer.write("\n");
			}

			i++;
			writeRow(writer, split, removeLineBreaks, values);
		}
	}

	public static void write(Writer writer, Collection<Object[]> dataList) throws IOException {
		write(writer, DEFAULT_SPLIT, true, dataList);
	}

	public static List<String[]> readLines(InputStream inputStream, char split) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		try {
			return readLines(bufferedReader, split);
		} finally {
			bufferedReader.close();
		}
	}

	public static List<String[]> readLines(BufferedReader reader, char split) throws IOException {
		List<String[]> lines = new ArrayList<String[]>();
		String line;
		while ((line = reader.readLine()) != null) {
			lines.add(StringUtils.split(line, split));
		}
		return lines;
	}

}
