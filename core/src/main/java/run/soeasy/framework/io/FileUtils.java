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

	public static <E extends Throwable> void copy(File file, BufferConsumer<? super byte[], ? extends E> bufferConsumer)
			throws IOException, E {
		copy(file, IOUtils.DEFAULT_BUFFER_SIZE, bufferConsumer);
	}

	public static <E extends Throwable> void copy(File file, byte[] buffer,
			BufferConsumer<? super byte[], ? extends E> bufferConsumer) throws IOException, E {
		if (!file.exists()) {
			return;
		}

		FileInputStream fis = new FileInputStream(file);
		try {
			IOUtils.transferTo(fis, buffer, bufferConsumer);
		} finally {
			fis.close();
		}
	}

	public static <E extends Throwable> void copy(File file, int bufferSize,
			BufferConsumer<? super byte[], ? extends E> bufferConsumer) throws IOException, E {
		if (!file.exists()) {
			return;
		}

		FileInputStream fis = new FileInputStream(file);
		try {
			IOUtils.transferTo(fis, bufferSize, bufferConsumer);
		} finally {
			fis.close();
		}
	}

	public static long copy(File input, OutputStream output) throws IOException {
		final FileInputStream fis = new FileInputStream(input);
		try {
			return IOUtils.transferTo(fis, output);
		} finally {
			fis.close();
		}
	}

	public static long copy(File input, OutputStream output, byte[] buffer) throws IOException {
		final FileInputStream fis = new FileInputStream(input);
		try {
			return IOUtils.transferTo(fis, output, buffer);
		} finally {
			fis.close();
		}
	}

	public static long copy(File input, OutputStream output, int bufferSize) throws IOException {
		final FileInputStream fis = new FileInputStream(input);
		try {
			return IOUtils.transferTo(fis, output, bufferSize);
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

	public static long transferTo(InputStream source, File destination) throws IOException {
		FileOutputStream output = openOutputStream(destination);
		try {
			return IOUtils.transferTo(source, output);
		} finally {
			IOUtils.close(output);
		}
	}

	public static long transferTo(InputStream source, Path destination) throws IOException {
		OutputStream output = Files.newOutputStream(destination);
		try {
			return IOUtils.transferTo(source, output);
		} finally {
			IOUtils.close(output);
		}
	}
}
