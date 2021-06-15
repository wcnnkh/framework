package scw.net.message.multipart;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import scw.http.ContentDisposition;
import scw.http.MediaType;
import scw.io.FileSystemResource;
import scw.io.IoProcessor;
import scw.io.Resource;
import scw.net.FileMimeTypeUitls;
import scw.net.MimeType;

public class ResourceMultipartMessage extends AbstractMultipartMessage {
	private final Resource resource;

	public ResourceMultipartMessage(String name, File file) throws IOException {
		this(name, new FileSystemResource(file));
	}

	public ResourceMultipartMessage(String name, Resource resource) throws IOException {
		super(name);
		this.resource = resource;
		ContentDisposition contentDisposition = ContentDisposition.builder("form-data").name(name).filename(getName())
				.build();
		getHeaders().setContentDisposition(contentDisposition);
		MimeType mimeType = FileMimeTypeUitls.getMimeType(resource);
		if (mimeType != null) {
			getHeaders().setContentType(new MediaType(mimeType));
		}
		getHeaders().setContentLength(resource.contentLength());
	}

	@Override
	public InputStream getBody() throws IOException {
		return resource.getInputStream();
	}

	@Override
	public <T> T read(IoProcessor<InputStream, ? extends T> processor) throws IOException {
		return resource.read(processor);
	}

	@Override
	public Resource getResource() {
		return resource;
	}

	@Override
	public String getOriginalFilename() {
		return resource.getName();
	}

}
