package run.soeasy.framework.messaging.multipart;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import run.soeasy.framework.messaging.ContentDisposition;
import run.soeasy.framework.messaging.MediaType;
import run.soeasy.framework.util.io.FileMimeTypeUitls;
import run.soeasy.framework.util.io.FileSystemResource;
import run.soeasy.framework.util.io.MimeType;
import run.soeasy.framework.util.io.Resource;

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
		getHeaders().setContentLength(resource.contentLength().longValue());
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return resource.getInputStream();
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
