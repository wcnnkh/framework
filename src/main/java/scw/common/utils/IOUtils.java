package scw.common.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import scw.common.ByteArray;
import scw.common.exception.ShuChaoWenRuntimeException;

public final class IOUtils {
	private IOUtils() {
	};

	public static void write(OutputStream os, InputStream is, int buffSize, int mark) throws IOException {
		byte[] b = new byte[buffSize];
		int len;
		try {
			if (mark >= 0) {
				if (is.markSupported()) {
					is.mark(0);
				}
			}

			while ((len = is.read(b)) != -1) {
				os.write(b, 0, len);
			}
		} finally {
			if (mark >= 0) {
				if (is.markSupported()) {
					is.reset();
				}
			}
		}
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
			throw new ShuChaoWenRuntimeException(e);
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
			throw new ShuChaoWenRuntimeException(e);
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
		return readJavaObject(ois);
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
