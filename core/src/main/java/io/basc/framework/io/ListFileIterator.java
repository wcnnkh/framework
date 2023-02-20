package io.basc.framework.io;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import io.basc.framework.lang.Nullable;

public class ListFileIterator implements Iterator<IterationFile<File>> {
	private final File directory;
	private final FileFilter fileFilter;
	private final int depth;// 当前深度
	private final int maxDepth;// 最大迭代深度

	/**
	 * @param directory
	 * @param fileFilter
	 * @param maxDepth   迭代最大深度, -1不限制深度
	 */
	public ListFileIterator(File directory, @Nullable FileFilter fileFilter, int maxDepth) {
		this(directory, fileFilter, maxDepth, 0);
	}

	private ListFileIterator(File directory, FileFilter fileFilter, int maxDepth, int depth) {
		this.directory = directory;
		this.fileFilter = fileFilter;
		this.maxDepth = maxDepth;
		this.depth = depth;
	}

	public File getDirectory() {
		return directory;
	}

	public FileFilter getFileFilter() {
		return fileFilter;
	}

	public int getDepth() {
		return depth;
	}

	public int getMaxDepth() {
		return maxDepth;
	}

	public boolean canRecursive(int depth) {
		if (maxDepth < 0) {
			return true;
		}
		return depth <= maxDepth;
	}

	/**
	 * 当前目录下的文件
	 */
	private File[] files;
	/**
	 * 当前目录下的子目录
	 */
	private Iterator<File> directoryIterator;

	/**
	 * 当前目录下的文件
	 */
	private Iterator<IterationFile<File>> iterator;

	@Override
	public boolean hasNext() {
		if (!canRecursive(depth)) {
			return false;
		}

		if (files == null) {
			files = fileFilter == null ? directory.listFiles() : directory.listFiles(fileFilter);
		}

		if (iterator == null) {
			iterator = Arrays.asList(files).stream().map((file) -> new IterationFile<File>(depth, file, directory))
					.iterator();
		}

		if (iterator.hasNext()) {
			return true;
		}

		if (!canRecursive(depth)) {
			return false;
		}

		if (directoryIterator == null) {
			directoryIterator = Arrays.asList(files).stream().filter((file) -> file.isDirectory()).iterator();
		}

		if (directoryIterator.hasNext()) {
			iterator = new ListFileIterator(directoryIterator.next(), fileFilter, maxDepth, depth + 1);
			return hasNext();
		}
		// 如果当前目录下没有子目录了就无法迭代了
		return false;
	}

	@Override
	public IterationFile<File> next() {
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
