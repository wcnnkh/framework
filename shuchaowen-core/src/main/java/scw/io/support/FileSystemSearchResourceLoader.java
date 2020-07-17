package scw.io.support;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import scw.core.utils.StringUtils;
import scw.io.DefaultResourceLoader;
import scw.io.FileSystemResource;
import scw.io.Resource;

public class FileSystemSearchResourceLoader extends DefaultResourceLoader {
	private String rootPath;
	private boolean compatible;
	private boolean search;

	public FileSystemSearchResourceLoader(String rootPath, boolean search, boolean compatible) {
		this.rootPath = StringUtils.cleanPath(rootPath);
		this.search = search;
		this.compatible = compatible;
	}

	@Override
	protected Resource getResourceByPath(String resource) {
		if (resource == null) {
			return null;
		}

		String path = StringUtils.cleanPath(resource);
		if(compatible){
			// 这样做是为了兼容老版本,但并不可靠
			if (path.startsWith(rootPath)) {
				path = path.substring(rootPath.length());
			}
		}

		File file = null;
		if (search) {
			file = searchFile(path, new File(rootPath));
		} else {
			file = new File(rootPath + File.separator + path);
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
