package io.basc.framework.net.message.multipart;

import io.basc.framework.net.message.InputMessage;

import java.io.IOException;
import java.util.List;

public interface MultipartMessageResolver {
	/**
	 * Determine if the given request contains multipart content.
	 * <p>
	 * Will typically check for content type "multipart/form-data", but the actually
	 * accepted requests might depend on the capabilities of the resolver
	 * implementation.
	 * 
	 * @return whether the request contains multipart content
	 */
	boolean isMultipart(InputMessage inputMessage);

	List<MultipartMessage> resolve(InputMessage inputMessage) throws IOException;
}
