package run.soeasy.framework.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.collection.Elements;

@UtilityClass
public class FileUtils {
	/**
	 * An empty array of type <code>File</code>.
	 */
	public static final File[] EMPTY_FILE_ARRAY = new File[0];

	public static FileInputStream openInputStream(File file) throws IOException {
		if (file.exists()) {
			if (file.isDirectory()) {
				throw new IOException("File '" + file + "' exists but is a directory");
			}
			if (file.canRead() == false) {
				throw new IOException("File '" + file + "' cannot be read");
			}
		} else {
			throw new FileNotFoundException("File '" + file + "' does not exist");
		}
		return new FileInputStream(file);
	}

	public static FileOutputStream openOutputStream(File file) throws IOException {
		return openOutputStream(file, false);
	}

	public static FileOutputStream openOutputStream(File file, boolean append) throws IOException {
		if (file.exists()) {
			if (file.isDirectory()) {
				throw new IOException("File '" + file + "' exists but is a directory");
			}
			if (file.canWrite() == false) {
				throw new IOException("File '" + file + "' cannot be written to");
			}
		} else {
			File parent = file.getParentFile();
			if (parent != null) {
				if (!parent.mkdirs() && !parent.isDirectory()) {
					throw new IOException("Directory '" + parent + "' could not be created");
				}
			}
		}
		return new FileOutputStream(file, append);
	}

	/**
	 * Copy bytes from a <code>File</code> to an <code>OutputStream</code>.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedInputStream</code>.
	 * </p>
	 * 
	 * @param input  the <code>File</code> to read from
	 * @param output the <code>OutputStream</code> to write to
	 * @return the number of bytes copied
	 * @throws NullPointerException if the input or output is null
	 * @throws IOException          if an I/O error occurs
	 */
	public static long copyFile(File input, OutputStream output) throws IOException {
		final FileInputStream fis = new FileInputStream(input);
		try {
			return IOUtils.copy(fis, output);
		} finally {
			fis.close();
		}
	}

	public static long copyInputStreamToFile(InputStream source, File destination) throws IOException {
		FileOutputStream output = openOutputStream(destination);
		try {
			return IOUtils.copy(source, output);
		} finally {
			IOUtils.close(output);
		}
	}

	public static long copyInputStreamToPath(InputStream source, Path destination) throws IOException {
		OutputStream output = Files.newOutputStream(destination);
		try {
			return IOUtils.copy(source, output);
		} finally {
			IOUtils.close(output);
		}
	}

	public static <E extends Throwable> void read(File file, BufferConsumer<? super byte[], ? extends E> bufferConsumer)
			throws IOException, E {
		read(file, IOUtils.DEFAULT_BUFFER_SIZE, bufferConsumer);
	}

	public static <E extends Throwable> void read(File file, int bufferSize,
			BufferConsumer<? super byte[], ? extends E> bufferConsumer) throws IOException, E {
		if (!file.exists()) {
			return;
		}

		FileInputStream fis = new FileInputStream(file);
		try {
			IOUtils.read(fis, bufferSize, bufferConsumer);
		} finally {
			fis.close();
		}
	}

	/**
	 * 递归迭代目录下的所有文件
	 * 
	 * @param directory
	 * @return
	 */
	public static Elements<File> listAllFiles(@NonNull File directory) {
		return listFiles(directory, -1);
	}

	/**
	 * 迭代目录下的文件，不进行递归
	 * 
	 * @param directory
	 * @return
	 */
	public static Elements<File> listFiles(@NonNull File directory) {
		return listFiles(directory, 0);
	}

	/**
	 * 迭代目录下的文件
	 * 
	 * @param directory
	 * @param maxDepth  迭代最大深度, -1不限制深度
	 * @return
	 */
	public static Elements<File> listFiles(@NonNull File directory, int maxDepth) {
		Assert.isTrue(directory.isDirectory(), directory + " is not a directory");
		return Elements.of(() -> new ListFileIterator(directory, maxDepth));
	}
}
