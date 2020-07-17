package scw.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;

import scw.io.event.ResourceEventDispatcher;

public class ResourceWrapper implements Resource{
	private final Resource resource;
	
	public ResourceWrapper(Resource resource){
		this.resource = resource;
	}

	public InputStream getInputStream() throws IOException {
		return resource.getInputStream();
	}

	public OutputStream getOutputStream() throws IOException {
		return resource.getOutputStream();
	}

	public boolean exists() {
		return resource.exists();
	}

	public boolean isReadable() {
		return resource.isReadable();
	}

	public boolean isWritable() {
		return resource.isWritable();
	}

	public boolean isOpen() {
		return resource.isOpen();
	}

	public URL getURL() throws IOException {
		return resource.getURL();
	}

	public URI getURI() throws IOException {
		return resource.getURI();
	}

	public File getFile() throws IOException, FileNotFoundException {
		return resource.getFile();
	}

	public long contentLength() throws IOException {
		return resource.contentLength();
	}

	public long lastModified() throws IOException {
		return resource.lastModified();
	}

	public Resource createRelative(String relativePath) throws IOException {
		return resource.createRelative(relativePath);
	}

	public String getFilename() {
		return resource.getFilename();
	}

	public String getDescription() {
		return resource.getDescription();
	}

	public boolean isSupportEventDispatcher() {
		return resource.isSupportEventDispatcher();
	}

	public ResourceEventDispatcher getEventDispatcher() {
		return resource.getEventDispatcher();
	}

	public Resource getResource() {
		if(resource instanceof ResourceWrapper){
			return ((ResourceWrapper) resource).getResource();
		}
		return resource;
	}
}
