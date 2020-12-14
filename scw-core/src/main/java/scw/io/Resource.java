package scw.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

import scw.core.GlobalPropertyFactory;
import scw.io.event.EmptyResourceEventDispatcher;
import scw.io.event.ResourceEventDispatcher;

public interface Resource extends InputStreamSource, OutputStreamSource {
	public static final EmptyResourceEventDispatcher EMPTY_EVENT_DISPATCHER = new EmptyResourceEventDispatcher();
	public static final Resource NONEXISTENT_RESOURCE = new NonexistentResource();
	/**
	 * 是否开启支资源的监听，默认开启
	 */
	public static final boolean SUPPORT_EVENT_DISPATCHER = GlobalPropertyFactory.getInstance().getValue("resource.event.dispathcer.enable", boolean.class, true);

	/**
	 * 对于jar的资源是否也应该进行事件监听，默认不监听
	 */
	public static final boolean SUPPORT_JAR_RESOURCE_EVENT_DISPATCHER = GlobalPropertyFactory.getInstance().getBooleanValue("jar.resource.event.dispathcer.enable");
	
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
	 * 是否是已打开的资源,如果是的那么无需关闭
	 * @see InputStreamResource#isOpen()
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
	
	boolean isSupportEventDispatcher();
	
	ResourceEventDispatcher getEventDispatcher();
}
