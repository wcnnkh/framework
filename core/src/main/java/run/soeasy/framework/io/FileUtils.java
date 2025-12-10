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
import java.util.Collection;
import java.util.Enumeration;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.lang.model.util.Elements;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * 文件操作工具类，提供基于Java IO和NIO的文件系统操作工具方法。
 * 该类封装了文件读写、复制、压缩解压、目录遍历、批量删除等常用操作，简化文件系统编程，兼顾安全性和易用性。
 *
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>文件复制：支持缓冲区消费者模式，可自定义数据处理逻辑，支持指定缓冲区大小</li>
 * <li>流操作：安全打开文件输入/输出流，包含存在性、权限、目录类型校验</li>
 * <li>目录遍历：支持递归/非递归、深度控制的目录文件迭代，返回可迭代的元素集合</li>
 * <li>压缩解压：ZIP文件压缩（支持文件/目录过滤、跨平台路径兼容）、解压（防目录穿越、自动创建多级目录）</li>
 * <li>批量删除：提供强制删除（抛异常）和静默删除（不抛异常）两种模式，支持文件/空目录删除</li>
 * </ul>
 *
 * <p>
 * <b>使用场景：</b>
 * <ul>
 * <li>文件内容复制/迁移：如备份文件、流数据写入本地文件</li>
 * <li>流资源管理：安全打开/关闭文件流，避免资源泄漏</li>
 * <li>目录扫描：递归遍历目录获取指定类型文件（如配置文件、日志文件）</li>
 * <li>压缩解压：创建自定义过滤规则的ZIP包、安全解压ZIP文件（防止目录穿越攻击）</li>
 * <li>批量清理：静默删除临时文件、强制删除关键文件（需处理异常）</li>
 * </ul>
 *
 * <p>
 * <b>注意事项：</b>
 * <ul>
 * <li>所有方法均对<code>null</code>参数做严格校验（标注{@link NonNull}），避免空指针异常</li>
 * <li>IO操作均会抛出{@link IOException}，需手动处理或向上抛出</li>
 * <li>压缩/解压方法不会自动关闭传入的流/文件实例，需调用者手动关闭（建议使用try-with-resources）</li>
 * <li>删除方法仅支持文件或空目录，非空目录需先递归删除子内容</li>
 * </ul>
 *
 * @author soeasy.run
 * @see IOUtils 流操作工具类
 * @see ListFileIterator 目录文件迭代器
 * @see BufferConsumer 缓冲区消费者接口
 */
@UtilityClass
public class FileUtils {
	/** 空文件数组，用于表示无文件的场景 */
	public static final File[] EMPTY_FILE_ARRAY = new File[0];

	/**
	 * 复制文件内容到缓冲区消费者，使用默认缓冲区大小（{@link IOUtils#DEFAULT_BYTE_BUFFER_SIZE}）。
	 * <p>
	 * 缓冲区消费者模式支持自定义数据处理逻辑（如数据加密、格式转换），无需手动管理缓冲区读写。
	 *
	 * @param file           源文件，不可为null
	 * @param bufferConsumer 缓冲区消费者，处理读取的字节数据，不可为null
	 * @param <E>            缓冲区消费者可能抛出的异常类型
	 * @throws IOException 当文件不存在、为目录、无读取权限或IO操作失败时抛出
	 * @throws E           当缓冲区消费者处理数据时抛出的自定义异常
	 * @see #copy(File, int, BufferConsumer) 支持指定缓冲区大小的重载方法
	 */
	public static <E extends Throwable> void copy(@NonNull File file,
			@NonNull BufferConsumer<? super byte[], ? extends E> bufferConsumer) throws IOException, E {
		copy(file, IOUtils.DEFAULT_BYTE_BUFFER_SIZE, bufferConsumer);
	}

	/**
	 * 使用指定缓冲区复制文件内容到缓冲区消费者。
	 * <p>
	 * 适用于需要自定义缓冲区大小的场景（如大文件复制时使用更大的缓冲区提升效率）。
	 *
	 * @param file           源文件，不可为null
	 * @param buffer         自定义缓冲区，不可为null（建议大小为2的幂，如4096、8192）
	 * @param bufferConsumer 缓冲区消费者，处理读取的字节数据，不可为null
	 * @param <E>            缓冲区消费者可能抛出的异常类型
	 * @throws IOException 当文件不存在、为目录、无读取权限或IO操作失败时抛出
	 * @throws E           当缓冲区消费者处理数据时抛出的自定义异常
	 */
	public static <E extends Throwable> void copy(@NonNull File file, @NonNull byte[] buffer,
			@NonNull BufferConsumer<? super byte[], ? extends E> bufferConsumer) throws IOException, E {
		FileInputStream fis = openInputStream(file);
		try {
			IOUtils.transferTo(fis, buffer, bufferConsumer);
		} finally {
			fis.close();
		}
	}

	/**
	 * 使用指定缓冲区大小复制文件内容到缓冲区消费者。
	 * <p>
	 * 内部自动创建指定大小的缓冲区，平衡内存占用和复制效率。
	 *
	 * @param file           源文件，不可为null
	 * @param bufferSize     缓冲区大小（单位：字节），需大于0
	 * @param bufferConsumer 缓冲区消费者，处理读取的字节数据，不可为null
	 * @param <E>            缓冲区消费者可能抛出的异常类型
	 * @throws IOException 当文件不存在、为目录、无读取权限或IO操作失败时抛出
	 * @throws E           当缓冲区消费者处理数据时抛出的自定义异常
	 * @throws IllegalArgumentException 当bufferSize ≤ 0时抛出
	 */
	public static <E extends Throwable> void copy(@NonNull File file, int bufferSize,
			@NonNull BufferConsumer<? super byte[], ? extends E> bufferConsumer) throws IOException, E {
		Assert.isTrue(bufferSize > 0, "bufferSize must be greater than 0");
		copy(file, new byte[bufferSize], bufferConsumer);
	}

	/**
	 * 递归迭代目录下的所有文件（不限制深度）。
	 * <p>
	 * 会遍历目标目录及其所有子目录下的文件，返回的{@link Elements}支持迭代、流式处理。
	 *
	 * @param directory 目标目录，不可为null且必须是目录
	 * @return 包含所有文件的元素集合（仅文件，不包含目录）
	 * @throws IllegalArgumentException 当directory不是目录时抛出
	 * @see #listFiles(File, int) 支持深度控制的重载方法
	 */
	public static Streamable<File> listAllFiles(@NonNull File directory) {
		return listFiles(directory, -1);
	}

	/**
	 * 迭代目录下的文件（不进行递归）。
	 * <p>
	 * 仅遍历目标目录下的一级文件，不包含子目录及其内容。
	 *
	 * @param directory 目标目录，不可为null且必须是目录
	 * @return 包含当前目录下一级文件的元素集合（仅文件，不包含目录）
	 * @throws IllegalArgumentException 当directory不是目录时抛出
	 * @see #listFiles(File, int) 支持深度控制的重载方法
	 */
	public static Streamable<File> listFiles(@NonNull File directory) {
		return listFiles(directory, 0);
	}

	/**
	 * 迭代目录下的文件（支持深度控制）。
	 * <p>
	 * 灵活控制目录遍历深度，适用于需要限制遍历层级的场景（如仅遍历前两级子目录）。
	 *
	 * @param directory 目标目录，不可为null且必须是目录
	 * @param maxDepth  最大迭代深度：-1表示不限制（递归所有子目录），0表示不递归（仅当前目录），≥1表示指定深度
	 * @return 包含迭代范围内所有文件的元素集合（仅文件，不包含目录）
	 * @throws IllegalArgumentException 当directory不是目录或maxDepth &lt; -1时抛出
	 */
	public static Streamable<File> listFiles(@NonNull File directory, int maxDepth) {
		Assert.isTrue(directory.isDirectory(), () -> directory + " is not a directory");
		Assert.isTrue(maxDepth >= -1, () -> "maxDepth must be ≥ -1, but got " + maxDepth);
		return Streamable.of(() -> new ListFileIterator(directory, maxDepth));
	}

	/**
	 * 安全打开文件输入流，包含存在性、目录类型、读取权限校验。
	 * <p>
	 * 相比直接创建{@link FileInputStream}，增加了更严格的前置校验，异常信息更明确。
	 *
	 * @param file 目标文件，不可为null
	 * @return 已打开的文件输入流（需调用者手动关闭，建议使用try-with-resources）
	 * @throws FileNotFoundException 当文件不存在时抛出
	 * @throws IOException 当文件为目录、无读取权限或IO操作失败时抛出
	 */
	public static FileInputStream openInputStream(File file) throws IOException {
		if (file.exists()) {
			if (file.isDirectory()) {
				throw new IOException("File '" + file + "' exists but is a directory");
			}
			if (!file.canRead()) {
				throw new IOException("File '" + file + "' cannot be read");
			}
		} else {
			throw new FileNotFoundException("File '" + file + "' does not exist");
		}
		return new FileInputStream(file);
	}

	/**
	 * 安全打开文件输出流（覆盖模式），包含存在性、目录类型、写入权限校验。
	 * <p>
	 * 若文件不存在，会自动创建父目录；若文件已存在，会覆盖原有内容。
	 *
	 * @param file 目标文件，不可为null
	 * @return 已打开的文件输出流（需调用者手动关闭，建议使用try-with-resources）
	 * @throws IOException 当文件为目录、无写入权限、父目录创建失败或IO操作失败时抛出
	 * @see #openOutputStream(File, boolean) 支持追加模式的重载方法
	 */
	public static FileOutputStream openOutputStream(File file) throws IOException {
		return openOutputStream(file, false);
	}

	/**
	 * 安全打开文件输出流（支持追加模式），包含存在性、目录类型、写入权限校验。
	 * <p>
	 * 若文件不存在，会自动创建父目录；支持覆盖（append=false）或追加（append=true）模式。
	 *
	 * @param file   目标文件，不可为null
	 * @param append 是否追加模式：true=追加到文件末尾，false=覆盖原有内容
	 * @return 已打开的文件输出流（需调用者手动关闭，建议使用try-with-resources）
	 * @throws IOException 当文件为目录、无写入权限、父目录创建失败或IO操作失败时抛出
	 */
	public static FileOutputStream openOutputStream(File file, boolean append) throws IOException {
		if (file.exists()) {
			if (file.isDirectory()) {
				throw new IOException("File '" + file + "' exists but is a directory");
			}
			if (!file.canWrite()) {
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
	 * 将输入流内容传输到目标文件（覆盖模式）。
	 * <p>
	 * 自动创建目标文件及父目录，支持指定缓冲区大小提升传输效率，适用于流数据落地（如网络流、内存流写入文件）。
	 *
	 * @param source      源输入流（需调用者确保流可用，传输后不会关闭源流）
	 * @param bufferSize  缓冲区大小（单位：字节），需大于0
	 * @param destination 目标文件，不可为null
	 * @return 成功传输的字节数
	 * @throws IOException 当目标文件为目录、无写入权限或IO操作失败时抛出
	 * @throws IllegalArgumentException 当bufferSize ≤ 0时抛出
	 */
	public static long transferTo(InputStream source, int bufferSize, File destination) throws IOException {
		Assert.isTrue(bufferSize > 0, "bufferSize must be greater than 0");
		FileOutputStream output = openOutputStream(destination);
		try {
			return IOUtils.transferTo(source, bufferSize, output::write);
		} finally {
			IOUtils.close(output);
		}
	}

	/**
	 * 将输入流内容传输到目标路径（覆盖模式）。
	 * <p>
	 * 基于Java NIO的Path API，功能与{@link #transferTo(InputStream, int, File)}一致，适用于Path场景。
	 *
	 * @param source      源输入流（需调用者确保流可用，传输后不会关闭源流）
	 * @param bufferSize  缓冲区大小（单位：字节），需大于0
	 * @param destination 目标路径，不可为null
	 * @return 成功传输的字节数
	 * @throws IOException 当目标路径为目录、无写入权限或IO操作失败时抛出
	 * @throws IllegalArgumentException 当bufferSize ≤ 0时抛出
	 */
	public static long transferTo(InputStream source, int bufferSize, Path destination) throws IOException {
		Assert.isTrue(bufferSize > 0, "bufferSize must be greater than 0");
		OutputStream output = Files.newOutputStream(destination);
		try {
			return IOUtils.transferTo(source, bufferSize, output::write);
		} finally {
			IOUtils.close(output);
		}
	}

	/**
	 * 解压ZIP文件到目标目录（含目录穿越防护）。
	 * <p>
	 * 核心特性：
	 * <ul>
	 * <li>安全防护：校验解压路径，防止目录穿越攻击（确保所有文件解压到目标目录内）</li>
	 * <li>自动建目录：支持多级目录文件解压，自动创建缺失的父目录</li>
	 * <li>高效解压：支持指定缓冲区大小，适配大文件解压场景</li>
	 * </ul>
	 *
	 * @param zip        待解压的ZIP文件实例（需调用者手动关闭，建议使用try-with-resources），不可为null
	 * @param bufferSize 解压缓冲区大小（单位：字节），需大于0
	 * @param target     目标解压目录，不可为null
	 * @throws ZipException 当ZIP文件格式错误、损坏或不支持的压缩算法时抛出
	 * @throws IOException  当目标目录无写入权限、父目录创建失败、路径校验失败或IO操作失败时抛出
	 * @throws IllegalArgumentException 当bufferSize ≤ 0时抛出
	 */
	public static void unZip(@NonNull ZipFile zip, int bufferSize, @NonNull File target)
			throws ZipException, IOException {
		Assert.isTrue(bufferSize > 0, "bufferSize must be greater than 0");
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
				File parentDir = entityFile.getParentFile();
				if (parentDir != null && !parentDir.exists()) {
					parentDir.mkdirs();
				}
				try (InputStream is = zip.getInputStream(zipEntry)) {
					FileUtils.transferTo(is, bufferSize, entityFile);
				}
			}
		}
	}

	/**
	 * 递归压缩目录/文件到ZIP输出流（支持文件/目录过滤）。
	 * <p>
	 * 核心特性：
	 * <ul>
	 * <li>灵活过滤：通过Predicate自定义过滤规则，支持文件/目录级别的过滤</li>
	 * <li>路径兼容：自动将Windows路径分隔符（\）转为ZIP标准分隔符（/），跨平台兼容</li>
	 * <li>目录处理：自动保留目录层级，未通过过滤的目录会跳过其所有子内容</li>
	 * </ul>
	 *
	 * @param source  源文件/目录路径，不可为null（文件则仅压缩单个文件，目录则递归压缩）
	 * @param target  ZIP输出流（需调用者手动关闭，建议使用try-with-resources），不可为null
	 * @param filter  过滤规则：返回true表示需要压缩，false表示跳过，不可为null
	 * @throws IOException 当源文件/目录无读取权限、ZIP写入失败或IO操作失败时抛出
	 */
	public static void zip(@NonNull Path source, @NonNull ZipOutputStream target,
			@NonNull Predicate<? super Path> filter) throws IOException {
		Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				// 未通过过滤：跳过当前目录及所有子内容（避免子文件路径异常）
				if (!filter.test(dir)) {
					return FileVisitResult.SKIP_SUBTREE;
				}

				// 已通过过滤：添加目录Entry（末尾加"/"标识目录）
				Path relativePath = source.relativize(dir);
				String entryName = relativePath.toString().replace("\\", "/");

				// 非根目录才添加Entry（根目录无需单独创建，子文件会自动继承根路径）
				if (!entryName.isEmpty()) {
					target.putNextEntry(new ZipEntry(entryName + "/"));
					target.closeEntry();
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				// 未通过过滤：直接跳过文件
				if (!filter.test(file)) {
					return FileVisitResult.CONTINUE;
				}

				// 已通过过滤：添加文件Entry并复制内容
				Path relativePath = source.relativize(file);
				String entryName = relativePath.toString().replace("\\", "/");

				target.putNextEntry(new ZipEntry(entryName));
				Files.copy(file, target); // 自动复制文件内容到ZIP输出流
				target.closeEntry(); // 必须关闭Entry，否则后续Entry无法写入
				return FileVisitResult.CONTINUE;
			}
		});
	}

	/**
	 * 批量强制删除文件/空目录（删除失败抛异常）。
	 * <p>
	 * 适用于需要确保删除成功的场景（如清理关键临时文件），会明确抛出删除失败的异常。
	 *
	 * @param files 待删除的文件/空目录集合，支持null元素（自动过滤）
	 * @throws IOException 当文件/目录不存在、无删除权限、为非空目录或IO操作失败时抛出
	 */
	public static void delete(Collection<? extends File> files) throws IOException {
		if (CollectionUtils.isEmpty(files)) {
			return;
		}

		CollectionUtils.acceptAll(files, (e) -> {
			if (e != null) {
				Files.delete(e.toPath());
			}
		});
	}

	/**
	 * 批量静默删除文件/空目录（删除失败不抛异常，返回成功数量）。
	 * <p>
	 * 适用于无需确保删除成功的场景（如清理临时文件），失败时仅静默跳过，不影响后续操作。
	 *
	 * @param files 待删除的文件/空目录集合，支持null元素（自动过滤）
	 * @return 成功删除的文件/空目录数量（仅文件存在且删除成功时计数）
	 */
	public static int deleteQuietly(Collection<? extends File> files) {
		if (CollectionUtils.isEmpty(files)) {
			return 0;
		}

		int count = 0;
		for (File file : files) {
			if (file == null) {
				continue;
			}

			if (file.exists() && file.delete()) {
				count++;
			}
		}
		return count;
	}
}