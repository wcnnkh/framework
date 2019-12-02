package scw.core.resource;

import java.io.InputStream;

import scw.core.Consumer;
import scw.core.utils.SystemPropertyUtils;

/**
 * 本地资源查找
 * 
 * @author shuchaowen
 *
 */
public class LocalResourceLookup extends ClassLoaderResourceLookup {
	private boolean search;

	/**
	 * @param search
	 *            是否搜索
	 */
	public LocalResourceLookup(boolean search) {
		this.search = search;
	}

	public boolean lookup(String resource, Consumer<InputStream> consumer) {
		FileSystemResourceLookup fileSystemResourceLookup = new FileSystemResourceLookup(
				SystemPropertyUtils.getWorkPath(), search);
		if (fileSystemResourceLookup.lookup(resource, consumer)) {
			return true;
		}
		return super.lookup(resource, consumer);
	}

}
