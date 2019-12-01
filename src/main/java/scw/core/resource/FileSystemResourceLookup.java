package scw.core.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import scw.core.Consumer;
import scw.io.IOUtils;

public class FileSystemResourceLookup implements ResourceLookup {
	private String rootPath;
	private boolean search;

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
			File rootFile = new File(rootPath == null ? "" : rootPath);
			if (rootFile == null || !rootFile.exists()) {
				return false;
			}

			file = searchFile(resource, rootFile);
		} else {
			file = new File(rootPath + File.separator + resource);
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
		if (!rootFile.exists()) {
			return null;
		}

		File[] files = rootFile.listFiles();
		if (files == null) {
			return null;
		}

		for (File file : files) {
			if (file.isFile()) {
				String p = file.getPath().replaceAll("\\\\", "/");
				if (p.endsWith(path.replaceAll("\\\\", "/"))) {
					return file;
				}
			} else {
				File f = searchFile(path, file);
				if (f != null) {
					return f;
				}
			}
		}
		return null;
	}
}
