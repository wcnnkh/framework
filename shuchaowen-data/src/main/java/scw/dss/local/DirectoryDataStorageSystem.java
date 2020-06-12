package scw.dss.local;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import scw.core.utils.StringUtils;
import scw.dss.Data;
import scw.dss.DataStorageSystem;
import scw.dss.DataStorageSystemException;
import scw.io.FileUtils;

public class DirectoryDataStorageSystem implements DataStorageSystem {
	private String directory;

	public DirectoryDataStorageSystem(String directory) {
		this.directory = StringUtils.cleanPath(directory);
	}

	private File getFile(String key) {
		return new File(directory + StringUtils.cleanPath(key));
	}

	public FileData get(String key) {
		File file = getFile(key);
		if (file.exists() && file.isFile()) {
			return new FileData(file, key);
		}
		return null;
	}

	public boolean put(String key, InputStream input) throws DataStorageSystemException, IOException {
		FileUtils.copyInputStreamToFile(input, getFile(key));
		return true;
	}

	public boolean isExist(String key) {
		return getFile(key).exists();
	}

	private final class ListFileFilter implements FileFilter {
		private String keyPrefix;
		private String marker;
		private boolean isFirst;
		private int maxSize;
		private int size = 0;

		public ListFileFilter(String keyPrefix, String marker, int maxSize) {
			this.keyPrefix = keyPrefix;
			this.marker = marker;
			this.isFirst = StringUtils.isEmpty(marker);
			this.maxSize = maxSize;
		}

		public boolean accept(File file) {
			if (!file.isFile()) {
				return false;
			}

			if (size > maxSize) {
				return false;
			}

			String key = getKey(file);
			if (StringUtils.isNotEmpty(marker) && key.equals(marker)) {
				isFirst = true;
			}

			if (!isFirst) {
				return false;
			}

			if (StringUtils.isEmpty(keyPrefix) || key.startsWith(keyPrefix)) {
				size++;
				return true;
			}

			return false;
		}
	}

	private String getKey(File file) {
		String key = file.getPath();
		key = StringUtils.cleanPath(key);
		return key.substring(directory.length());
	}

	public List<Data> getList(String keyPrefix, String marker, int limit) {
		String prefix = StringUtils.isEmpty(keyPrefix) ? "" : StringUtils.cleanPath(keyPrefix);
		File file;
		if (StringUtils.isEmpty(prefix)) {
			file = new File(directory);
		} else {
			String suffix = prefix;
			file = new File(directory + suffix);
			while (!file.exists() || !file.isDirectory()) {// 如果文件不存在或文件不是目录
				int index = suffix.lastIndexOf("/");
				if (index == -1 || index == 0) {
					break;
				}

				suffix = suffix.substring(0, index);
				file = new File(directory + suffix);
			}
		}

		FileFilter fileFilter = new ListFileFilter(keyPrefix, marker, limit);
		List<Data> list = new ArrayList<Data>(limit);
		appendFile(fileFilter, file, list);
		return list;
	}

	private void appendFile(FileFilter fileFilter, File directory, List<Data> list) {
		for (File fileToUse : directory.listFiles()) {
			if (fileToUse.isDirectory()) {
				appendFile(fileFilter, directory, list);
				continue;
			}

			if (fileFilter.accept(fileToUse)) {
				String key = getKey(fileToUse);
				list.add(new FileData(fileToUse, key));
			}
		}
	}

	public boolean delete(String key) {
		return getFile(key).delete();
	}
}