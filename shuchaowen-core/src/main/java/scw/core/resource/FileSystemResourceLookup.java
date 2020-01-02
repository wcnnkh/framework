package scw.core.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import scw.core.Consumer;
import scw.io.IOUtils;

public class FileSystemResourceLookup implements ResourceLookup {
	private String rootPath;
	private boolean search;

	public FileSystemResourceLookup(boolean search) {
		this(null, search);
	}

	public FileSystemResourceLookup(String rootPath, boolean search) {
		this.rootPath = rootPath;
		this.search = search;
	}

	public boolean lookup(String resource, Consumer<InputStream> consumer) {
		if (resource == null) {
			return false;
		}

		File file = null;
		if (search) {
			file = searchFile(resource, new File(rootPath == null ? "" : rootPath));
		} else {
			file = new File((rootPath == null ? "":rootPath) + File.separator + resource);
		}

		if (file == null || !file.exists()) {
			return false;
		}

		if (consumer != null) {
			InputStream inputStream = null;
			try {
				inputStream = new FileInputStream(file);
				consumer.consume(inputStream);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			} finally {
				IOUtils.close(inputStream);
			}
		}
		return true;
	}

	private static File searchFile(String path, File rootFile) {
		if (!rootFile.exists() || !rootFile.isDirectory()) {
			return null;
		}

		File[] files = rootFile.listFiles();
		if (files == null) {
			return null;
		}

		List<File> directoryList = new LinkedList<File>();
		for (File file : files) {
			if (file.isDirectory()) {
				directoryList.add(file);
			} else {
				String p = file.getPath().replaceAll("\\\\", "/");
				if (p.endsWith(path.replaceAll("\\\\", "/"))) {
					return file;
				}
			}
		}

		for (File directory : directoryList) {
			File f = searchFile(path, directory);
			if (f != null) {
				return f;
			}
		}
		return null;
	}
}
