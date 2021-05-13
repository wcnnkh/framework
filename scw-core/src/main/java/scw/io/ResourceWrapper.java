package scw.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;

import scw.event.ChangeEvent;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.Observable;

public abstract class ResourceWrapper implements Resource, Observable<Resource>{

	public InputStream getInputStream() throws IOException {
		return get().getInputStream();
	}

	public OutputStream getOutputStream() throws IOException {
		return get().getOutputStream();
	}

	public boolean exists() {
		return get().exists();
	}

	public boolean isReadable() {
		return get().isReadable();
	}

	public boolean isWritable() {
		return get().isWritable();
	}

	public boolean isOpen() {
		return get().isOpen();
	}

	public URL getURL() throws IOException {
		return get().getURL();
	}

	public URI getURI() throws IOException {
		return get().getURI();
	}

	public File getFile() throws IOException, FileNotFoundException {
		return get().getFile();
	}

	public long contentLength() throws IOException {
		return get().contentLength();
	}

	public long lastModified() throws IOException {
		return get().lastModified();
	}

	public Resource createRelative(String relativePath) throws IOException {
		return get().createRelative(relativePath);
	}

	public String getName() {
		return get().getName();
	}

	public String getDescription() {
		return get().getDescription();
	}
	
	@Override
	public EventRegistration registerListener(EventListener<ChangeEvent<Resource>> eventListener) {
		return get().registerListener(eventListener);
	}
	
	@Override
	public String toString() {
		return get().toString();
	}
}
