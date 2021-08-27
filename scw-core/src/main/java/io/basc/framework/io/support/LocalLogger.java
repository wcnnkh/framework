package io.basc.framework.io.support;

import io.basc.framework.core.Assert;
import io.basc.framework.core.utils.StringUtils;
import io.basc.framework.io.FileUtils;
import io.basc.framework.lang.AlreadyExistsException;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.comparator.CompareUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * 本地记录器，为了实现高可靠的服务而使用的工具类，其流程是的开始在本地文件中写入日志，结束时删除日志,在服务启动时将未完成的日志进行补充处理
 * 
 * @author shuchaowen
 *
 */
public final class LocalLogger {
	private static Logger logger = LoggerFactory.getLogger(LocalLogger.class);
	private final String directory;

	public LocalLogger(String directory) {
		this.directory = StringUtils.cleanPath(directory);
		File file = new File(directory);
		if (!file.exists()) {
			file.mkdirs();
		}
		logger.info("using local logger: {}", this.directory);
	}

	public String getDirectory() {
		return directory;
	}

	private File getFile(String key) {
		return new File(directory + "/" + StringUtils.cleanPath(key));
	}

	public Record<byte[]> create(String id, byte[] data) throws IOException {
		File file = getFile(id);
		if (file.exists()) {
			throw new AlreadyExistsException(file.getPath());
		}

		if (!file.createNewFile()) {
			throw new RuntimeException("create error:" + file.getPath());
		}

		FileUtils.writeByteArrayToFile(file, data);
		return new Record<byte[]>(id, data);
	}

	public boolean delete(String key) {
		File file = getFile(key);
		return file.delete();
	}

	private String getKey(File file) {
		String key = file.getPath();
		key = StringUtils.cleanPath(key);
		Assert.isTrue(key.startsWith(directory));
		key = key.substring(directory.length());
		return key.startsWith("/") ? key.substring(1) : key;
	}

	public boolean isExist(String key) {
		File file = getFile(key);
		return file.exists() && file.isFile();
	}

	private List<File> getFileList(String directory) {
		List<File> list = new ArrayList<File>();
		File rootFile = new File(directory);
		if (rootFile.exists()) {
			File[] files = rootFile.listFiles();
			for (File file : files) {
				if (file.isFile()) {
					list.add(file);
				} else {
					list.addAll(getFileList(file.getPath()));
				}
			}
		}

		list.sort(new Comparator<File>() {

			public int compare(File o1, File o2) {
				return CompareUtils.compare(o1.lastModified(), o2.lastModified(), false);
			}
		});
		return list;
	}

	/**
	 * 返回顺序按lastModified的升序
	 * 
	 */
	public Enumeration<Record<byte[]>> enumeration() {
		return new RecordEnumeration();
	}

	private final class RecordEnumeration implements Enumeration<Record<byte[]>> {
		private Iterator<File> fileIterator = getFileList(directory).iterator();
		private Record<byte[]> record;
		private boolean next = false;

		private Record<byte[]> toRecord(File file) {
			byte[] data = null;
			try {
				data = FileUtils.readFileToByteArray(file);
			} catch (IOException e) {
				logger.error(e, "read file error: {}", file.getPath());
			}

			if (data == null) {
				return null;
			}

			return new Record<byte[]>(getKey(file), data);
		}

		public boolean hasMoreElements() {
			if (next) {
				return true;
			}

			while (fileIterator.hasNext()) {
				Record<byte[]> record = toRecord(fileIterator.next());
				if (record != null) {
					this.record = record;
					this.next = true;
					return true;
				}
			}
			return false;
		}

		public Record<byte[]> nextElement() {
			if (next) {
				next = false;
				return record;
			}

			while (fileIterator.hasNext()) {
				Record<byte[]> record = toRecord(fileIterator.next());
				if (record != null) {
					return record;
				}
			}
			throw new NoSuchElementException();
		}
	}

	public static final class Record<T> implements Serializable {
		private static final long serialVersionUID = 1L;
		private final String id;
		private final T data;

		public Record(String id, T data) {
			this.id = id;
			this.data = data;
		}

		public String getId() {
			return id;
		}

		public T getData() {
			return data;
		}
	}
}
