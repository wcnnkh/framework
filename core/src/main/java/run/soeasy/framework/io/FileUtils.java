package run.soeasy.framework.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Enumeration;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.collection.Elements;

/**
 * 文件操作工具类，提供基于Java IO和NIO的文件系统操作工具方法。 该类封装了文件读写、复制、目录遍历等常用操作，简化文件系统编程。
 *
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>文件复制：支持通过缓冲区消费者高效复制文件内容</li>
 * <li>流操作：提供安全的输入输出流打开和关闭方法</li>
 * <li>目录遍历：支持递归或非递归的目录文件迭代</li>
 * </ul>
 *
 * <p>
 * <b>使用场景：</b>
 * <ul>
 * <li>文件内容复制：实现文件备份、数据迁移等功能</li>
 * <li>流资源管理：安全打开和关闭文件输入输出流</li>
 * <li>目录扫描：递归遍历目录获取指定类型文件</li>
 * </ul>
 *
 * @author soeasy.run
 * @see IOUtils
 * @see ListFileIterator
 */
@UtilityClass
public class FileUtils {
	/** 空文件数组，用于表示无文件的场景 */
	public static final File[] EMPTY_FILE_ARRAY = new File[0];

	/**
	 * 复制文件内容到缓冲区消费者，使用默认缓冲区大小。
	 * <p>
	 * 该方法通过缓冲区消费者模式实现文件内容的高效复制， 适用于需要自定义数据处理逻辑的场景。
	 *
	 * @param file           源文件，不可为null
	 * @param bufferConsumer 缓冲区消费者，处理读取的字节数据
	 * @param <E>            可能抛出的异常类型
	 * @throws IOException 当文件操作发生I/O错误时抛出
	 * @throws E           当缓冲区消费者处理数据时抛出异常
	 */
	public static <E extends Throwable> void copy(File file, BufferConsumer<? super byte[], ? extends E> bufferConsumer)
			throws IOException, E {
		copy(file, IOUtils.DEFAULT_BYTE_BUFFER_SIZE, bufferConsumer);
	}

	/**
	 * 使用指定缓冲区复制文件内容到缓冲区消费者。
	 *
	 * @param file           源文件，不可为null
	 * @param buffer         自定义缓冲区
	 * @param bufferConsumer 缓冲区消费者，处理读取的字节数据
	 * @param <E>            可能抛出的异常类型
	 * @throws IOException 当文件操作发生I/O错误时抛出
	 * @throws E           当缓冲区消费者处理数据时抛出异常
	 */
	public static <E extends Throwable> void copy(File file, byte[] buffer,
			BufferConsumer<? super byte[], ? extends E> bufferConsumer) throws IOException, E {
		FileInputStream fis = openInputStream(file);
		try {
			IOUtils.transferTo(fis, buffer, bufferConsumer);
		} finally {
			fis.close();
		}
	}

	/**
	 * 使用指定缓冲区大小复制文件内容到缓冲区消费者。
	 *
	 * @param file           源文件，不可为null
	 * @param bufferSize     缓冲区大小
	 * @param bufferConsumer 缓冲区消费者，处理读取的字节数据
	 * @param <E>            可能抛出的异常类型
	 * @throws IOException 当文件操作发生I/O错误时抛出
	 * @throws E           当缓冲区消费者处理数据时抛出异常
	 */
	public static <E extends Throwable> void copy(@NonNull File file, int bufferSize,
			BufferConsumer<? super byte[], ? extends E> bufferConsumer) throws IOException, E {
		copy(file, new byte[bufferSize], bufferConsumer);
	}

	/**
	 * 递归迭代目录下的所有文件（不限制深度）。
	 *
	 * @param directory 目标目录，不可为null
	 * @return 包含所有文件的元素集合
	 */
	public static Elements<File> listAllFiles(@NonNull File directory) {
		return listFiles(directory, -1);
	}

	/**
	 * 迭代目录下的文件（不进行递归）。
	 *
	 * @param directory 目标目录，不可为null
	 * @return 包含当前目录文件的元素集合
	 */
	public static Elements<File> listFiles(@NonNull File directory) {
		return listFiles(directory, 0);
	}

	/**
	 * 迭代目录下的文件（支持深度控制）。
	 *
	 * @param directory 目标目录，不可为null
	 * @param maxDepth  最大迭代深度，-1表示不限制，0表示不递归
	 * @return 包含迭代文件的元素集合
	 */
	public static Elements<File> listFiles(@NonNull File directory, int maxDepth) {
		Assert.isTrue(directory.isDirectory(), directory + " is not a directory");
		return Elements.of(() -> new ListFileIterator(directory, maxDepth));
	}

	/**
	 * 安全打开文件输入流，包含存在性和权限检查。
	 *
	 * @param file 目标文件
	 * @return 文件输入流
	 * @throws FileNotFoundException 文件不存在时抛出
	 * @throws IOException           文件为目录或无读取权限时抛出
	 */
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

	/**
	 * 安全打开文件输出流（覆盖模式），包含存在性和权限检查。
	 *
	 * @param file 目标文件
	 * @return 文件输出流
	 * @throws IOException 文件为目录或无写入权限时抛出
	 */
	public static FileOutputStream openOutputStream(File file) throws IOException {
		return openOutputStream(file, false);
	}

	/**
	 * 安全打开文件输出流（支持追加模式），包含存在性和权限检查。
	 *
	 * @param file   目标文件
	 * @param append 是否追加模式
	 * @return 文件输出流
	 * @throws IOException 文件为目录或无写入权限时抛出
	 */
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
	 * 将输入流内容传输到目标文件。
	 *
	 * @param source      源输入流
	 * @param bufferSize  缓冲区大小
	 * @param destination 目标文件
	 * @return 传输的字节数
	 * @throws IOException 当文件操作发生I/O错误时抛出
	 */
	public static long transferTo(InputStream source, int bufferSize, File destination) throws IOException {
		FileOutputStream output = openOutputStream(destination);
		try {
			return IOUtils.transferTo(source, bufferSize, output::write);
		} finally {
			IOUtils.close(output);
		}
	}

	/**
	 * 将输入流内容传输到目标路径。
	 *
	 * @param source      源输入流
	 * @param bufferSize  缓冲区大小
	 * @param destination 目标路径
	 * @return 传输的字节数
	 * @throws IOException 当文件操作发生I/O错误时抛出
	 */
	public static long transferTo(InputStream source, int bufferSize, Path destination) throws IOException {
		OutputStream output = Files.newOutputStream(destination);
		try {
			return IOUtils.transferTo(source, bufferSize, output::write);
		} finally {
			IOUtils.close(output);
		}
	}

	public static void unZip(@NonNull ZipFile zip, int bufferSize, @NonNull File target)
			throws ZipException, IOException {
		if (!target.exists()) {
			target.mkdirs();
		}

		File canonicalTarget = target.getCanonicalFile();
		Enumeration<? extends ZipEntry> entries = zip.entries();
		ZipEntry zipEntry;
		while (entries.hasMoreElements()) {
			zipEntry = entries.nextElement();
			File entityFile = new File(target, zipEntry.getName());
			entityFile = entityFile.getCanonicalFile();

			// 目录穿越防护：验证解压路径是否在目标目录内
			if (!entityFile.toPath().startsWith(canonicalTarget.toPath())) {
				throw new IOException("Zip entry is outside of the target directory: " + zipEntry.getName());
			}

			if (zipEntry.isDirectory()) {
				entityFile.mkdirs();
			} else {
				entityFile.createNewFile();
				try (InputStream is = zip.getInputStream(zipEntry)) {
					FileUtils.transferTo(is, bufferSize, entityFile);
				}
			}
		}
	}

	public static void zip(@NonNull Path source, @NonNull ZipOutputStream target,
			@NonNull Predicate<? super Path> filter) throws IOException {
		Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				// 应用目录过滤
				if (filter.test(dir)) {
					Path relativePath = source.relativize(dir);
					String entryName = relativePath.toString().replace("\\", "/");

					if (!entryName.isEmpty()) {
						target.putNextEntry(new ZipEntry(entryName + "/"));
						target.closeEntry();
					}
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				// 应用文件过滤
				if (filter.test(file)) {
					Path relativePath = source.relativize(file);
					String entryName = relativePath.toString().replace("\\", "/");

					target.putNextEntry(new ZipEntry(entryName));
					Files.copy(file, target);
					target.closeEntry();
				}
				return FileVisitResult.CONTINUE;
			}
		});
	}
}