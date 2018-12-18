package scw.common.net.http.entity.file;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class UploadFile implements File{
	private String contentType;
	private java.io.File file;
	private String name;
	
	public UploadFile(String name, String pathname, String contentType){
		this.contentType = contentType;
		this.file =  new java.io.File(pathname);
		this.name = name;
	}
	
	public String contentType() {
		return contentType;
	}
	public long length() {
		return file.length();
	}
	
	public String fileName() {
		return file.getName();
	}

	public String name() {
		return name;
	}

	public InputStream inputStream() throws IOException {
		return new FileInputStream(file);
	}
}
