package io.basc.framework.io;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.AbstractIterator;

public class ListFilenameIterator extends AbstractIterator<IterationFile<String>> {
	private final File directory;
	private final FilenameFilter fileFilter;
	private final int depth;// 当前深度
	private final int maxDepth;// 最大迭代深度

	/**
	 * 
	 * @param directory
	 * @param fileFilter
	 * @param maxDepth   迭代最大深度, -1不限制深度
	 */
	public ListFilenameIterator(File directory, @Nullable FilenameFilter fileFilter, int maxDepth) {
		this(directory, fileFilter, maxDepth, 0);
	}

	private ListFilenameIterator(File directory, FilenameFilter fileFilter, int maxDepth, int depth) {
		this.directory = directory;
		this.fileFilter = fileFilter;
		this.maxDepth = maxDepth;
		this.depth = depth;
	}

	public boolean canRecursive(int depth) {
		if (maxDepth < 0) {
			return true;
		}
		return depth <= maxDepth;
	}

	public File getDirectory() {
		return directory;
	}

	public FilenameFilter getFileFilter() {
		return fileFilter;
	}

	public int getDepth() {
		return depth;
	}

	public int getMaxDepth() {
		return maxDepth;
	}

	/**
	 * 当前目录下的文件
	 */
	private String[] files;
	/**
	 * 当前目录下的子目录
	 */
	private Iterator<File> directoryIterator;

	/**
	 * 当前目录下的文件
	 */
	private Iterator<IterationFile<String>> iterator;

	@Override
	public boolean hasNext() {
		if (!canRecursive(depth)) {
			return false;
		}

		if (files == null) {
			files = fileFilter == null ? directory.list() : directory.list(fileFilter);
		}

		if (iterator == null) {
			iterator = Arrays.asList(files).stream().map((name) -> new IterationFile<String>(depth, name, directory))
					.iterator();
		}

		if (iterator.hasNext()) {
			return true;
		}

		if (!canRecursive(depth)) {
			return false;
		}

		if (directoryIterator == null) {
			directoryIterator = Arrays.asList(files).stream().map((name) -> new File(directory, name))
					.filter((file) -> file.isDirectory()).iterator();
		}

		if (directoryIterator.hasNext()) {
			iterator = new ListFilenameIterator(directoryIterator.next(), fileFilter, maxDepth, depth + 1);
			return hasNext();
		}
		// 如果当前目录下没有子目录了就无法迭代了
		return false;
	}

	@Override
	public IterationFile<String> next() {
		if (!hasNext()) {
			throw new NoSuchElementException(toString());
		}
		return iterator.next();
	}

	@Override
	public String toString() {
		return "maxDepth:" + maxDepth + ", depth:" + depth + ", directory:" + directory;
	}
}
