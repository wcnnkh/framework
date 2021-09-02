package io.basc.framework.io;

import java.io.File;
import java.io.Serializable;

public class IterationFile<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	private final T file;
	private final File directory;
	private final int depth;

	public IterationFile(int depth, T file, File directory) {
		this.depth = depth;
		this.file = file;
		this.directory = directory;
	}

	public T getFile() {
		return file;
	}

	public File getDirectory() {
		return directory;
	}

	public int getDepth() {
		return depth;
	}
}
