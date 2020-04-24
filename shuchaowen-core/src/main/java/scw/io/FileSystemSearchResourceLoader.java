package scw.io;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class FileSystemSearchResourceLoader extends DefaultResourceLoader {
	private String rootPath;
	private boolean search;

	public FileSystemSearchResourceLoader(boolean search) {
		this(null, search);
	}

	public FileSystemSearchResourceLoader(String rootPath, boolean search) {
		this.rootPath = rootPath;
		this.search = search;
	}

	@Override
	protected Resource getResourceByPath(String resource) {
		if (resource == null) {
			return null;
		}

		File file = null;
		if (search) {
			file = searchFile(resource, new File(rootPath == null ? "" : rootPath));
		} else {
			file = new File((rootPath == null ? "" : rootPath) + File.separator + resource);
		}

		if (file == null || !file.exists()) {
			return null;
		}

		return new FileSystemResource(file);
	}

	private File searchFile(String path, File rootFile) {
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
