package scw.core.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import scw.core.Consumer;
import scw.io.IOUtils;

public class FileSystemResourceLookup implements ResourceLookup {
	public static final FileSystemResourceLookup FILE_SYSTEM_RESOURCE_LOOKUP = new FileSystemResourceLookup();

	public boolean lookup(String resource, Consumer<InputStream> consumer) {
		if (resource == null) {
			return false;
		}

		File file = new File(resource);
		if (!file.exists()) {
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

}
