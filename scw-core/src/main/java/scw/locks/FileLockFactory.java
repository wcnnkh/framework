package scw.locks;

import java.io.File;
import java.util.concurrent.locks.Lock;

import scw.codec.support.URLCodec;
import scw.core.Assert;
import scw.io.FileUtils;

public class FileLockFactory implements LockFactory{
	private final File directory;
	
	public FileLockFactory() {
		this(FileUtils.getTempDirectory());
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
