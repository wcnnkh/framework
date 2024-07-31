package io.basc.framework.locks;

import io.basc.framework.io.FileUtils;
import io.basc.framework.lang.Constants;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.XUtils;
import io.basc.framework.util.concurrent.locks.AbstractLock;

import java.io.File;
import java.io.IOException;

/**
 * 使用文件实现的锁，不同于{@link java.nio.channels.FileLock}
 * 
 * @author wcnnkh
 *
 */
public class FileLock extends AbstractLock {
	private static Logger logger = LoggerFactory.getLogger(FileLock.class);
	private final File file;
	private final String version;

	public FileLock(File file) {
		this(file, XUtils.getUUID());
	}

	public FileLock(File file, String version) {
		this.file = file;
		this.version = version;
		file.deleteOnExit();
	}

	@Override
	public boolean tryLock() {
		if (file.exists()) {
			return false;
		}

		try {
			if (!file.createNewFile()) {
				return false;
			}
		} catch (IOException e) {
			logger.error(e, "create lock file fail, version {} file {}", version, file);
			return false;
		}

		try {
			FileUtils.write(file, version, Constants.UTF_8_NAME);
		} catch (IOException e) {
			logger.error(e, "lock fail version {} file {}", version, file);
			return false;
		}
		return true;
	}

	@Override
	public void unlock() {
		if (file.exists()) {
			try {
				String version = FileUtils.readFileToString(file, Constants.UTF_8_NAME);
				if (this.version.equals(version)) {
					file.delete();
				}
			} catch (IOException e) {
				// ignore 解锁失败
				logger.error(e, "unlock fail version {} file {}", version, file);
			}
		}
	}
}
