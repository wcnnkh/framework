package scw.compensat.policy;

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

import scw.codec.support.URLCodec;
import scw.core.utils.ArrayUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.io.FileUtils;
import scw.io.JavaSerializer;
import scw.io.NoTypeSpecifiedSerializer;
import scw.io.SerializerException;
import scw.locks.FileLockFactory;
import scw.locks.LockFactory;
import scw.util.KeyValuePair;
import scw.util.comparator.CompareUtils;

public class FileCompenstPolicy extends AbstractCompenstPolicy {
	private static final String SUFFIX = ".compenstor";
	private static final String CONNECTOR = "-";
	private final File directory;
	private final LockFactory lockFactory;
	private NoTypeSpecifiedSerializer serializer = JavaSerializer.INSTANCE;

	public FileCompenstPolicy(File directory) {
		this.directory = directory;
		this.lockFactory = new FileLockFactory(directory);
	}

	public NoTypeSpecifiedSerializer getSerializer() {
		return serializer;
	}

	public void setSerializer(NoTypeSpecifiedSerializer serializer) {
		this.serializer = serializer;
	}

	public File getDirectory() {
		return directory;
	}

	@Override
	public Lock getLock(String group, String id) {
		return lockFactory.getLock(group + "-" + id);
	}

	@Override
	public Enumeration<String> getUnfinishedGroups() {
		File[] files = directory.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return pathname.isFile() && pathname.getName().endsWith(SUFFIX);
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
			KeyValuePair<String, String> pair = StringUtils.parseKV(name,
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
		KeyValuePair<String, String> pair = StringUtils
				.parseKV(name, CONNECTOR);
		return URLCodec.UTF_8.decode(pair.getValue());
	}

	@Override
	public boolean add(String group, String id, Runnable runnable) {
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
			byte[] data = serializer.serialize(runnable);
			FileUtils.writeByteArrayToFile(file, data);
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
		File file = getFile(group, id);
		if (file.exists()) {
			try {
				byte[] data = FileUtils.readFileToByteArray(file);
				return serializer.deserialize(data);
			} catch (IOException | ClassNotFoundException e) {
				return null;
			}
		}
		return null;
	}

	@Override
	public boolean exists(String group, String id) {
		File file = getFile(group, id);
		return file.exists();
	}

	@Override
	public boolean remove(String group, String id) {
		File file = getFile(group, id);
		return file.delete();
	}
}
