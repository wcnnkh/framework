package scw.upload;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import scw.core.Assert;
import scw.core.utils.StringUtils;
import scw.data.ResourceStorageService;
import scw.data.StorageException;
import scw.http.HttpRequestEntity;
import scw.io.FileUtils;
import scw.io.IOUtils;
import scw.io.UrlResource;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.net.message.InputMessage;

/**
 * 上传器
 * 
 * @author shuchaowen
 *
 */
public class Uploader implements ResourceStorageService {
	public static final String CONTROLLER = "${upload.controller:/upload}";
	private static Logger logger = LoggerFactory.getLogger(Uploader.class);
	private final UploadPolicy uploadPolicy;
	private final String directory;

	public Uploader(UploadPolicy uploadPolicy, String directory) {
		this.uploadPolicy = uploadPolicy;
		this.directory = directory;
	}

	public UploadPolicy getUploadPolicy() {
		return uploadPolicy;
	}

	@Override
	public UrlResource get(String key) throws StorageException, IOException {
		StringBuilder sb = new StringBuilder();
		sb.append(uploadPolicy.getBaseUrl());
		sb.append("/");
		sb.append(key);
		return new UrlResource(sb.toString());
	}

	@Override
	public boolean put(String key, InputMessage input) throws StorageException,
			IOException {
		logger.info("put [{}]", key);
		File file = new File(directory, key);
		InputStream is = null;
		try {
			is = input.getBody();
			FileUtils.copyInputStreamToFile(input.getBody(), file);
		} finally {
			IOUtils.close(is);
		}
		return true;
	}

	@Override
	public boolean delete(String key) throws StorageException {
		logger.info("delete [{}]", key);
		File file = new File(directory, StringUtils.cleanPath(key));
		return file.delete();
	}

	@Override
	public boolean delete(URI uri) throws StorageException {
		logger.info("delete [{}]", uri);
		String str = uri.toString();
		str = StringUtils.cleanPath(str);
		if (!str.startsWith(uploadPolicy.getBaseUrl())) {
			return false;
		}

		String key = str.substring(uploadPolicy.getBaseUrl().length());
		return delete(key);
	}

	@Override
	public HttpRequestEntity<?> generatePolicy(String key, Date expiration)
			throws StorageException {
		return uploadPolicy.generatePolicy(key, expiration);
	}
	
	@Override
	public List<UrlResource> list(String keyPrefix, String marker, int limit)
			throws StorageException, IOException {
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
		List<UrlResource> list = new ArrayList<UrlResource>(limit);
		appendFile(fileFilter, file, list);
		return list;
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
		Assert.isTrue(key.startsWith(directory));
		key = key.substring(directory.length());
		return key.startsWith("/")? key.substring(1):key;
	}

	private void appendFile(FileFilter fileFilter, File directory, List<UrlResource> list) throws StorageException, IOException {
		for (File fileToUse : directory.listFiles()) {
			if (fileToUse.isDirectory()) {
				appendFile(fileFilter, directory, list);
				continue;
			}

			if (fileFilter.accept(fileToUse)) {
				String key = getKey(fileToUse);
				list.add(get(key));
			}
		}
	}
}
