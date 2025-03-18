package run.soeasy.framework.messaging.multipart;

import java.io.IOException;
import java.util.List;

import run.soeasy.framework.messaging.InputMessage;

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
