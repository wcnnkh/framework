package io.basc.framework.locks;

import io.basc.framework.io.FileUtils;
import io.basc.framework.util.Assert;
import io.basc.framework.util.codec.support.URLCodec;

import java.io.File;
import java.util.concurrent.locks.Lock;

public class FileLockFactory implements LockFactory {
	private final File directory;

	public FileLockFactory() {
		this(new File(FileUtils.getTempDirectory()));
	}

	public FileLockFactory(File directory) {
		Assert.requiredArgument(directory != null && directory.exists() && directory.isDirectory(), "directory");
		this.directory = directory;
	}

	@Override
	public Lock getLock(String name) {
		return new FileLock(new File(directory, URLCodec.UTF_8.encode(name) + ".lock"));
	}

}
