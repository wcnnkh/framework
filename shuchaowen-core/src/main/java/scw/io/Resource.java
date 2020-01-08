package scw.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

public interface Resource extends InputStreamSource, OutputStreamSource {

	/**
	 * 是否存在
	 * 
	 * @return
	 */
	boolean exists();

	/**
	 * 是否可读,比如一个目录是不可读的，或没有可读权限
	 * 
	 * @return
	 */
	boolean isReadable();

	/**
	 * 是否可写
	 * 
	 * @return
	 */
	boolean isWritable();

	/**
	 * 是否是已打开的资源，如果已打开则不能多次调用getInputStream()
	 * 
	 * @return
	 */
	boolean isOpen();

	URL getURL() throws IOException;

	URI getURI() throws IOException;

	File getFile() throws IOException, FileNotFoundException;

	long contentLength() throws IOException;

	long lastModified() throws IOException;

	Resource createRelative(String relativePath) throws IOException;

	String getFilename();

	String getDescription();
}
