package scw.io;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import scw.core.Assert;
import scw.core.GlobalPropertyFactory;
import scw.core.utils.ArrayUtils;
import scw.core.utils.StringUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.util.comparator.CompareUtils;

public class ObjectFileManager {
	protected final Logger logger = LoggerUtils.getLogger(ObjectFileManager.class);

	private final File directory;
	private final Serializer serializer;
	private final String suffix;
	private final AtomicLong atomicLong = new AtomicLong(System.currentTimeMillis());

	public ObjectFileManager(String suffix) {
		this(suffix, JavaSerializer.SERIALIZER);
	}

	public ObjectFileManager(String suffix, Serializer serializer) {
		this(FileUtils.getTempDirectoryPath() + File.separator + GlobalPropertyFactory.getInstance().getSystemLocalId(),
				suffix, serializer);
	}

	public ObjectFileManager(String directory, String suffix, Serializer serializer) {
		Assert.requiredArgument(directory != null, "directory");
		Assert.requiredArgument(StringUtils.isNotEmpty(suffix), "suffix");
		Assert.requiredArgument(serializer != null, "serializer");
		logger.info("object field manager directory [{}] suffix [{}]", directory, suffix);
		File file = new File(directory);
		if (!file.exists()) {
			file.mkdirs();
		}
		this.directory = file;
		this.suffix = suffix;
		this.serializer = serializer;
	}

	public long writeObject(Object object) throws IOException {
		long index = atomicLong.incrementAndGet();
		File file = getFile(index);
		file.createNewFile();
		FileUtils.writeByteArrayToFile(file, serializer.serialize(object));
		return index;
	}

	public Object getObject(long index) throws IOException, ClassNotFoundException {
		File file = new File(directory, index + "." + suffix);
		if (!file.exists()) {
			return null;
		}

		return readObject(file);
	}

	private File getFile(long index) {
		return new File(directory, index + "." + suffix);
	}

	public boolean delete(long index) {
		File file = getFile(index);
		if (file.exists()) {
			return file.delete();
		}
		return false;
	}

	private Object readObject(File file) throws IOException, ClassNotFoundException {
		byte[] data = FileUtils.readFileToByteArray(file);
		if (ArrayUtils.isEmpty(data)) {
			return null;
		}

		return serializer.deserialize(data);
	}

	private long getIndex(File file) {
		return Long.parseLong(file.getName().substring(0, file.getName().length() - suffix.length() - 1));
	}

	public List<ObjectInfo> getObjectList() throws IOException, ClassNotFoundException {
		File[] files = directory.listFiles(new FileFilter() {

			public boolean accept(File pathname) {
				return pathname.isFile() && pathname.getName().endsWith("." + suffix);
			}
		});
		if (ArrayUtils.isEmpty(files)) {
			return Collections.emptyList();
		}

		List<File> fileList = new ArrayList<File>(Arrays.asList(files));
		fileList.sort(new Comparator<File>() {

			public int compare(File o1, File o2) {
				return CompareUtils.compare(getIndex(o1), getIndex(o2), false);
			}
		});

		List<ObjectInfo> objects = new ArrayList<ObjectInfo>(fileList.size());
		for (File file : fileList) {
			Object obj = readObject(file);
			if (obj == null) {
				continue;
			}

			objects.add(new ObjectInfo(obj, getIndex(file)));
		}
		return objects;
	}

	public final class ObjectInfo {
		private final Object instance;
		private final long index;

		public ObjectInfo(Object instance, long index) {
			this.index = index;
			this.instance = instance;
		}

		public Object getInstance() {
			return instance;
		}

		public long getIndex() {
			return index;
		}
	}
}
