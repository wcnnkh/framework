package io.basc.framework.data.file;

import io.basc.framework.data.Storage;
import io.basc.framework.io.FileUtils;
import io.basc.framework.io.Serializer;
import io.basc.framework.io.SerializerUtils;
import io.basc.framework.lang.NestedRuntimeException;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.net.uri.UriUtils;
import io.basc.framework.util.Assert;
import io.basc.framework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@SuppressWarnings("unchecked")
public class DirectoryStorage extends TimerTask implements Storage {
	private static Logger logger = LoggerFactory.getLogger(DirectoryStorage.class);
	// 守望线程，自动退出
	private static final Timer TIMER = new Timer(DirectoryStorage.class.getSimpleName(), true);
	private final int exp;// 0表示不过期
	private final Serializer serializer;
	private final File directory;

	/**
	 * @param exp 单位:秒
	 */
	protected DirectoryStorage(int exp) {
		this(exp, SerializerUtils.getSerializer(),
				FileUtils.getTempDirectory() + File.separator + "file_cache_" + exp);
	}

	/**
	 * @param exp            单位:秒
	 * @param cacheDirectory
	 */
	public DirectoryStorage(int exp, String cacheDirectory) {
		this(exp, SerializerUtils.getSerializer(), cacheDirectory);
	}

	public DirectoryStorage(int exp, Serializer serializer, String directory) {
		this(exp, SerializerUtils.getSerializer(), new File(Assert.securePathArgument(directory, "directory")));
	}

	/**
	 * @param exp         单位:秒
	 * @param serializer
	 * @param charsetName
	 * @param directory
	 */
	public DirectoryStorage(int exp, Serializer serializer, File directory) {
		Assert.requiredArgument(serializer != null, "serializer");
		Assert.requiredArgument(directory != null, "directory");
		this.exp = exp;
		this.serializer = serializer;
		this.directory = directory;
		logger.info("{} exp is {}s use cache directory: {}", getClass().getName(), exp, this.directory);
		if (exp > 0) {
			TIMER.schedule(this, exp * 1000L, exp * 1000L);
		}
	}

	public final Serializer getSerializer() {
		return serializer;
	}

	public File getDirectory() {
		return directory;
	}

	protected final String getKey(File file) {
		return UriUtils.decode(file.getName());
	}

	protected final File getFile(String key) {
		StringBuilder sb = new StringBuilder();
		sb.append(directory);
		sb.append(File.separator);
		sb.append(hashPath(key));
		sb.append(File.separator);
		sb.append(UriUtils.encode(key));
		return new File(sb.toString());
	}

	protected int hashPath(String key) {
		return key.hashCode() % 1024;
	}

	protected final Object readObject(File file) {
		if (file.exists()) {
			byte[] data;
			try {
				data = FileUtils.readFileToByteArray(file);
				return serializer.deserialize(data);
			} catch (Exception e) {
				throw new NestedRuntimeException(e);
			}
		}
		return null;
	}

	protected final void writeObject(File file, Object value, boolean touch) {
		try {
			FileUtils.writeByteArrayToFile(file, serializer.serialize(value));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		if (touch) {
			touchFile(file);
		}
	}

	protected final void touchFile(File file) {
		if (!file.setLastModified(System.currentTimeMillis())) {
			logger.warn("touch file fail:{}", file.getPath());
		}
	}

	protected final boolean isExpire(File file) {
		if (exp <= 0) {
			return false;
		}

		return System.currentTimeMillis() - file.lastModified() > exp;
	}

	protected File getNotExpireFile(String key) {
		File file = getFile(key);
		if (!file.exists()) {
			return null;
		}

		if (isExpire(file)) {
			return null;
		}
		return file;
	}

	protected Object getNotFound(String key) {
		return null;
	}

	public <T> T get(String key) {
		File file = getNotExpireFile(key);
		if (file == null) {
			return (T) getNotFound(key);
		}

		return (T) readObject(file);
	}

	public <T> T getAndTouch(String key) {
		File file = getNotExpireFile(key);
		if (file == null) {
			return (T) getNotFound(key);
		}

		touchFile(file);
		return (T) readObject(file);
	}

	public void set(String key, Object value) {
		File file = getNotExpireFile(key);
		writeObject(getFile(key), value, file == null);
	}

	public boolean add(String key, Object value) {
		File file = getNotExpireFile(key);
		if (file == null) {
			writeObject(getFile(key), value, true);
		}
		return false;
	}

	public boolean touch(String key) {
		File file = getNotExpireFile(key);
		if (file == null) {
			return false;
		}

		touchFile(file);
		return true;
	}

	public boolean delete(String key) {
		File file = getFile(key);
		if (file.exists()) {
			return file.delete();
		}
		return false;
	}

	public boolean isExist(String key) {
		return getNotExpireFile(key) != null;
	}

	public <T> Map<String, T> get(Collection<String> keyCollections) {
		if (CollectionUtils.isEmpty(keyCollections)) {
			return Collections.EMPTY_MAP;
		}

		Map<String, T> map = new HashMap<String, T>(keyCollections.size());
		for (String key : keyCollections) {
			T value = get(key);
			if (value == null) {
				continue;
			}

			map.put(key, value);
		}
		return map;
	}

	/**
	 * 过期后会调用此函数
	 * 
	 * @param file
	 * @param currentTimeMillis
	 */
	protected void expireExecute(File file, long currentTimeMillis) {
		file.delete();
	}

	protected void scanner(File file, long currentTimeMillis) {
		if (isExpire(file)) {
			try {
				logger.debug("Start processing expired cache:" + file.getPath());
				expireExecute(file, currentTimeMillis);
			} catch (Exception e) {
				logger.error(e, "处理[{}]异常", file.getPath());
			}
		}
	}

	private void sannerExpireFile(File directory, long currentTimeMillis) {
		if (!directory.exists()) {
			return;
		}

		for (File file : directory.listFiles()) {
			if (file == null) {
				continue;
			}

			if (file.isDirectory()) {
				sannerExpireFile(file, currentTimeMillis);
			} else {
				scanner(file, currentTimeMillis);
			}
		}
	}

	@Override
	public void run() {
		try {
			sannerExpireFile(directory, scheduledExecutionTime());
		} catch (Exception e) {
			logger.error(e, "执行过期缓存扫描异常");
		}
	}

	public void delete(Collection<String> keys) {
		if (CollectionUtils.isEmpty(keys)) {
			return;
		}

		for (String key : keys) {
			delete(key);
		}
	}

	public int getMaxExpirationDate() {
		return exp;
	}

	public static DirectoryStorage create(String cacheDirectorySuffix, int exp) {
		return new DirectoryStorage(exp,
				FileUtils.getTempDirectory() + File.separator + cacheDirectorySuffix);
	}
}
