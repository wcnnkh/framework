package scw.core.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import scw.core.io.ByteArray;

public final class IOUtils {
	private static final int BUFFER_SIZE = 1024 * 8;

	private IOUtils() {
	};

	public static long write(OutputStream os, InputStream is, int buffSize, int mark) throws IOException {
		if (mark >= 0) {
			if (is.markSupported()) {
				is.mark(0);
			}
		}

		try {
			return write(is, os, buffSize);
		} finally {
			if (mark >= 0) {
				if (is.markSupported()) {
					is.reset();
				}
			}
		}
	}

	/**
	 * write.
	 * 
	 * @param is
	 *            InputStream instance.
	 * @param os
	 *            OutputStream instance.
	 * @return count.
	 * @throws IOException.
	 */
	public static long write(InputStream is, OutputStream os) throws IOException {
		return write(is, os, BUFFER_SIZE);
	}

	/**
	 * write.
	 * 
	 * @param is
	 *            InputStream instance.
	 * @param os
	 *            OutputStream instance.
	 * @param bufferSize
	 *            buffer size.
	 * @return count.
	 * @throws IOException.
	 */
	public static long write(InputStream is, OutputStream os, int bufferSize) throws IOException {
		int read;
		long total = 0;
		byte[] buff = new byte[bufferSize];
		while (is.available() > 0) {
			read = is.read(buff, 0, buff.length);
			if (read > 0) {
				os.write(buff, 0, read);
				total += read;
			}
		}
		return total;
	}

	/**
	 * read string.
	 * 
	 * @param reader
	 *            Reader instance.
	 * @return String.
	 * @throws IOException
	 */
	public static String read(Reader reader) throws IOException {
		StringWriter writer = new StringWriter();
		try {
			write(reader, writer);
			return writer.getBuffer().toString();
		} finally {
			writer.close();
		}
	}

	/**
	 * write string.
	 * 
	 * @param writer
	 *            Writer instance.
	 * @param string
	 *            String.
	 * @throws IOException
	 */
	public static long write(Writer writer, String string) throws IOException {
		Reader reader = new StringReader(string);
		try {
			return write(reader, writer);
		} finally {
			reader.close();
		}
	}

	/**
	 * write.
	 * 
	 * @param reader
	 *            Reader.
	 * @param writer
	 *            Writer.
	 * @return count.
	 * @throws IOException
	 */
	public static long write(Reader reader, Writer writer) throws IOException {
		return write(reader, writer, BUFFER_SIZE);
	}

	/**
	 * write.
	 * 
	 * @param reader
	 *            Reader.
	 * @param writer
	 *            Writer.
	 * @param bufferSize
	 *            buffer size.
	 * @return count.
	 * @throws IOException
	 */
	public static long write(Reader reader, Writer writer, int bufferSize) throws IOException {
		int read;
		long total = 0;
		// 原来在dubbo中此方法是这样写的，存在BUG
		// char[] buf = new char[BUFFER_SIZE];
		char[] buf = new char[bufferSize];
		while ((read = reader.read(buf)) != -1) {
			writer.write(buf, 0, read);
			total += read;
		}
		return total;
	}

	/**
	 * read lines.
	 * 
	 * @param file
	 *            file.
	 * @return lines.
	 * @throws IOException
	 */
	public static String[] readLines(File file) throws IOException {
		if (file == null || !file.exists() || !file.canRead())
			return new String[0];

		return readLines(new FileInputStream(file));
	}

	/**
	 * read lines.
	 * 
	 * @param is
	 *            input stream.
	 * @return lines.
	 * @throws IOException
	 */
	public static String[] readLines(InputStream is) throws IOException {
		List<String> lines = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		try {
			String line;
			while ((line = reader.readLine()) != null)
				lines.add(line);
			return lines.toArray(new String[0]);
		} finally {
			reader.close();
		}
	}

	/**
	 * write lines.
	 * 
	 * @param os
	 *            output stream.
	 * @param lines
	 *            lines.
	 * @throws IOException
	 */
	public static void writeLines(OutputStream os, String[] lines) throws IOException {
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(os));
		try {
			for (String line : lines)
				writer.println(line);
			writer.flush();
		} finally {
			writer.close();
		}
	}

	/**
	 * write lines.
	 * 
	 * @param file
	 *            file.
	 * @param lines
	 *            lines.
	 * @throws IOException
	 */
	public static void writeLines(File file, String[] lines) throws IOException {
		if (file == null)
			throw new IOException("File is null.");
		writeLines(new FileOutputStream(file), lines);
	}

	/**
	 * append lines.
	 * 
	 * @param file
	 *            file.
	 * @param lines
	 *            lines.
	 * @throws IOException
	 */
	public static void appendLines(File file, String[] lines) throws IOException {
		if (file == null)
			throw new IOException("File is null.");
		writeLines(new FileOutputStream(file, true), lines);
	}

	public static byte[] javaObjectToByte(Object obj) {
		if (obj == null) {
			return null;
		}

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			oos.flush();
			return bos.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			XUtils.close(oos, bos);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T byteToJavaObject(byte[] buf) {
		ByteArrayInputStream bis = new ByteArrayInputStream(buf);
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(bis);
			return (T) ois.readObject();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		} finally {
			XUtils.close(ois, bis);
		}
	}

	/**
	 * 读取内容
	 * 
	 * @param in
	 * @param buffSize
	 * @param mark
	 *            标记的位置 如果小于0就不标记
	 * @return
	 * @throws IOException
	 */
	public static ByteArray read(InputStream in, int buffSize, int mark) throws IOException {
		ByteArray byteArray = new ByteArray(buffSize);
		byte[] b = new byte[buffSize];
		int len;
		try {
			if (mark >= 0) {
				if (in.markSupported()) {
					in.mark(0);
				}
			}

			while ((len = in.read(b)) != -1) {
				byteArray.write(b, 0, len);
			}
			return byteArray;
		} finally {
			if (mark >= 0) {
				if (in.markSupported()) {
					in.reset();
				}
			}
		}
	}

	public static <T> T readJavaObject(InputStream is) throws IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(is);
		try {
			return readJavaObject(ois);
		} finally {
			ois.close();
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T readJavaObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		return (T) ois.readObject();
	}

	public static String read(Reader reader, int buffSize, int mark) throws IOException {
		StringBuilder sb = new StringBuilder(buffSize);
		char[] buff = new char[buffSize];
		int len;
		try {
			if (mark >= 0) {
				if (reader.markSupported()) {
					reader.mark(0);
				}
			}

			while ((len = reader.read(buff)) != -1) {
				sb.append(buff, 0, len);
			}
			return sb.toString();
		} finally {
			if (mark >= 0) {
				if (reader.markSupported()) {
					reader.reset();
				}
			}
		}
	}

	public static String read(BufferedReader br, int mark) throws IOException {
		StringBuilder sb = new StringBuilder();
		String line;
		try {
			if (mark >= 0) {
				if (br.markSupported()) {
					br.mark(0);
				}
			}

			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			return sb.toString();
		} finally {
			if (mark >= 0) {
				if (br.markSupported()) {
					br.reset();
				}
			}
		}
	}

	public static List<String> readLineList(BufferedReader br, int mark) throws IOException {
		List<String> list = new ArrayList<String>();
		String line;
		try {
			if (mark >= 0) {
				if (br.markSupported()) {
					br.mark(0);
				}
			}

			while ((line = br.readLine()) != null) {
				list.add(line);
			}
			return list;
		} finally {
			if (mark >= 0) {
				if (br.markSupported()) {
					br.reset();
				}
			}
		}
	}
}
