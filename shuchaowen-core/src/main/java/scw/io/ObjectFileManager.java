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
import scw.core.utils.ArrayUtils;
import scw.core.utils.StringUtils;
import scw.util.comparator.CompareUtils;

public class ObjectFileManager {
	private final File directory;
	private final Serializer serializer;
	private final String suffix;
	private final AtomicLong atomicLong = new AtomicLong(
			System.currentTimeMillis());

	public ObjectFileManager(File directory, String suffix,
			Serializer serializer) {
		Assert.requiredArgument(directory != null && directory.exists()
				&& directory.isDirectory(), "directory");
		Assert.requiredArgument(StringUtils.isNotEmpty(suffix), "suffix");
		Assert.requiredArgument(serializer != null, "suffix");
		this.directory = directory;
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

	public Object getObject(long index) throws IOException {
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

	private Object readObject(File file) throws IOException {
		byte[] data = FileUtils.readFileToByteArray(file);
		return serializer.deserialize(data);
	}

	private long getIndex(File file) {
		return Long.parseLong(file.getName().substring(0,
				file.getName().length() - suffix.length() - 1));
	}

	public List<ObjectInfo> getObjectList() throws IOException {
		File[] files = directory.listFiles(new FileFilter() {

			public boolean accept(File pathname) {
				return pathname.isFile()
						&& pathname.getName().endsWith("." + suffix);
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
			objects.add(new ObjectInfo(readObject(file), getIndex(file)));
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
