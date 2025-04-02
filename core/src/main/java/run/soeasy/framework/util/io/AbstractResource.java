package run.soeasy.framework.util.io;

import java.io.File;
import java.io.IOException;

public abstract class AbstractResource implements Resource {
	private volatile File file;

	@Override
	public boolean isFile() {
		return this.file == null ? Resource.super.isFile() : true;
	}

	@Override
	public File getFile() throws IOException {
		File file = this.file;
		if (file != null) {
			return file;
		}
		file = Resource.super.getFile();
		this.file = file;
		return file;
	}

	@Override
	public final String toString() {
		return getDescription();
	}
}
