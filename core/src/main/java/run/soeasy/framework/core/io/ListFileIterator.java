package run.soeasy.framework.core.io;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ListFileIterator implements Iterator<File> {
	private final File directory;
	private final int depth;// 当前深度
	private final int maxDepth;// 最大迭代深度

	/**
	 * @param directory
	 * @param maxDepth  迭代最大深度, -1不限制深度
	 */
	public ListFileIterator(File directory, int maxDepth) {
		this(directory, maxDepth, 0);
	}

	private ListFileIterator(File directory, int maxDepth, int depth) {
		this.directory = directory;
		this.maxDepth = maxDepth;
		this.depth = depth;
	}

	public File getDirectory() {
		return directory;
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
	private Iterator<File> iterator;

	@Override
	public boolean hasNext() {
		if (!canRecursive(depth)) {
			return false;
		}

		if (files == null) {
			files = directory.listFiles();
		}

		if (iterator == null) {
			iterator = Arrays.asList(files).iterator();
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
			iterator = new ListFileIterator(directoryIterator.next(), maxDepth, depth + 1);
			return hasNext();
		}
		// 如果当前目录下没有子目录了就无法迭代了
		return false;
	}

	@Override
	public File next() {
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
