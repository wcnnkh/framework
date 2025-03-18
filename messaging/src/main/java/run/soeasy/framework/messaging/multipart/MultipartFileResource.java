package run.soeasy.framework.messaging.multipart;

import java.io.IOException;
import java.io.InputStream;

import run.soeasy.framework.util.Assert;
import run.soeasy.framework.util.io.AbstractResource;

/**
 * Adapt {@link MultipartFile} to {@link run.soeasy.framework.util.io.Resource},
 * exposing the content as {@code InputStream} and also overriding
 * {@link #contentLength()} as well as {@link #getFilename()}.
 */
class MultipartFileResource extends AbstractResource {

	private final MultipartMessage multipartMessage;

	public MultipartFileResource(MultipartMessage multipartMessage) {
		Assert.notNull(multipartMessage, "MultipartMessage must not be null");
		this.multipartMessage = multipartMessage;
	}

	/**
	 * This implementation always returns {@code true}.
	 */
	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public long contentLength() {
		return this.multipartMessage.getSize();
	}

	@Override
	public String getName() {
		return this.multipartMessage.getOriginalFilename();
	}

	/**
	 * This implementation throws IllegalStateException if attempting to read the
	 * underlying stream multiple times.
	 */
	@Override
	public InputStream getInputStream() throws IOException, IllegalStateException {
		return this.multipartMessage.getInputStream();
	}

	/**
	 * This implementation returns a description that has the Multipart name.
	 */
	@Override
	public String getDescription() {
		return "MultipartFile resource [" + this.multipartMessage.getName() + "]";
	}

	@Override
	public boolean equals(Object other) {
		return (this == other || (other instanceof MultipartFileResource
				&& ((MultipartFileResource) other).multipartMessage.equals(this.multipartMessage)));
	}

	@Override
	public int hashCode() {
		return this.multipartMessage.hashCode();
	}

}
