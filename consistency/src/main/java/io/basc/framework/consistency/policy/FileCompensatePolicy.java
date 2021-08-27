package io.basc.framework.consistency.policy;

import io.basc.framework.codec.support.URLCodec;
import io.basc.framework.core.Assert;
import io.basc.framework.core.utils.ArrayUtils;
import io.basc.framework.core.utils.CollectionUtils;
import io.basc.framework.core.utils.StringUtils;
import io.basc.framework.core.utils.XTime;
import io.basc.framework.io.FileUtils;
import io.basc.framework.io.SerializerException;
import io.basc.framework.locks.FileLockFactory;
import io.basc.framework.locks.LockFactory;
import io.basc.framework.util.Pair;
import io.basc.framework.util.comparator.CompareUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.concurrent.locks.Lock;

public class FileCompensatePolicy extends StorageCompensatePolicy {
	private static final String SUFFIX = ".compenstor";
	private final File directory;
	private final LockFactory lockFactory;

	public FileCompensatePolicy(File directory) {
		Assert.requiredArgument(directory != null && directory.isDirectory(), "directory");
		logger.info("using: " + directory);
		this.directory = directory;
		this.lockFactory = new FileLockFactory(directory);
	}

	public File getDirectory() {
		return directory;
	}
	
	@Override
	public Lock getLock(String group, String id) {
		checkParameter(group, id);
		return lockFactory.getLock(group + "-" + id);
	}

	@Override
	public Enumeration<String> getUnfinishedGroups() {
		long t = System.currentTimeMillis();
		/**
		 * 获取5分钟前的补偿文件列表
		 */
		File[] files = directory.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return pathname.isFile() && pathname.getName().endsWith(SUFFIX) && pathname.lastModified() < (t - XTime.ONE_MINUTE * getCompenstBeforeMinute());
			}
		});

		if (files == null) {
			return Collections.emptyEnumeration();
		}

		Arrays.sort(files, new Comparator<File>() {

			@Override
			public int compare(File o1, File o2) {
				return CompareUtils.compare(o1.lastModified(),
						o2.lastModified(), true);
			}
		});

		HashSet<String> sets = new LinkedHashSet<String>();
		for (File file : files) {
			String name = file.getName().substring(0,
					file.getName().length() - SUFFIX.length());
			Pair<String, String> pair = StringUtils.parseKV(name,
					CONNECTOR);
			sets.add(URLCodec.UTF_8.decode(pair.getKey()));
		}
		return CollectionUtils.toEnumeration(sets.iterator());
	}

	@Override
	public String getLastUnfinishedId(String group) {
		String prefix = URLCodec.UTF_8.encode(group);
		File[] files = directory.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return pathname.isFile() && pathname.getName().endsWith(SUFFIX)
						&& pathname.getName().startsWith(prefix + CONNECTOR);
			}
		});

		if (ArrayUtils.isEmpty(files)) {
			return null;
		}

		Arrays.sort(files, new Comparator<File>() {

			@Override
			public int compare(File o1, File o2) {
				return CompareUtils.compare(o1.lastModified(),
						o2.lastModified(), true);
			}
		});

		File file = files[0];
		String name = file.getName().substring(0,
				file.getName().length() - SUFFIX.length());
		Pair<String, String> pair = StringUtils
				.parseKV(name, CONNECTOR);
		return URLCodec.UTF_8.decode(pair.getValue());
	}

	@Override
	public boolean add(String group, String id, Runnable runnable) {
		checkParameter(group, id);
		
		File file = getFile(group, id);
		if (file.exists()) {
			return false;
		}

		try {
			if (!file.createNewFile()) {
				return false;
			}
		} catch (IOException e) {
			logger.error(e, "create file fail");
			return false;
		}

		try {
			byte[] data = getSerializer().serialize(runnable);
			FileUtils.writeByteArrayToFile(file, data);
			return true;
		} catch (SerializerException e) {
			logger.error(e, "serializer fail");
		} catch (IOException e) {
			logger.error(e, "write fail");
		}
		return false;
	}

	private File getFile(String group, String id) {
		return new File(directory, URLCodec.UTF_8.encode(group) + CONNECTOR
				+ URLCodec.UTF_8.encode(id) + SUFFIX);
	}

	@Override
	protected Runnable getRunnable(String group, String id) {
		checkParameter(group, id);
		
		File file = getFile(group, id);
		if (file.exists()) {
			try {
				byte[] data = FileUtils.readFileToByteArray(file);
				return getSerializer().deserialize(data);
			} catch (IOException | ClassNotFoundException e) {
				return null;
			}
		}
		return null;
	}

	@Override
	public boolean exists(String group, String id) {
		checkParameter(group, id);
		
		File file = getFile(group, id);
		return file.exists();
	}

	@Override
	public boolean remove(String group, String id) {
		checkParameter(group, id);
		
		File file = getFile(group, id);
		return file.delete();
	}
}
