package scw.io.resource;

import java.io.InputStream;

import scw.core.Consumer;

/**
 * 本地资源查找
 * 
 * @author shuchaowen
 *
 */
public class LocalResourceLookup extends ClassLoaderResourceLookup {
	private FileSystemResourceLookup fileSystemResourceLookup;
	private String workPath;

	/**
	 * @param search
	 *            是否搜索
	 */
	public LocalResourceLookup(String workPath, boolean search) {
		this.fileSystemResourceLookup = new FileSystemResourceLookup(workPath, search);
		this.workPath = workPath;
	}

	public boolean lookup(String resource, Consumer<InputStream> consumer) {
		if (fileSystemResourceLookup.lookup(resource, consumer)) {
			return true;
		}
		return super.lookup(resource, consumer);
	}

	public final String getWorkPath() {
		return workPath;
	}
}
