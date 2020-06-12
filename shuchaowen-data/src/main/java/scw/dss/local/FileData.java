package scw.dss.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import scw.dss.Data;
import scw.io.FileUtils;

public final class FileData implements Data {
	private final File file;
	private final String key;

	public FileData(File file, String key) {
		this.file = file;
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public InputStream getBody() throws IOException {
		return new FileInputStream(file);
	}

	public long getContentLength() {
		return file.length();
	}

	public long lastModified() throws IOException {
		return file.lastModified();
	}

	public File getFile() {
		return file;
	}

	public byte[] getByteArray() throws IOException {
		return FileUtils.readFileToByteArray(file);
	}
}
