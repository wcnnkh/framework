package io.basc.framework.web;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import io.basc.framework.http.MediaType;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.net.message.multipart.MultipartMessage;
import io.basc.framework.net.message.multipart.MultipartMessageResolver;
import io.basc.framework.util.CollectionUtils;

/**
 * 一个MultiPart请求
 * 
 * @author wcnnkh
 * @see MediaType#MULTIPART_FORM_DATA
 *
 */
public class DefaultMultiPartServerHttpRequest extends ServerHttpRequestWrapper implements MultiPartServerHttpRequest {
	private static Logger logger = LoggerFactory.getLogger(DefaultMultiPartServerHttpRequest.class);
	private final MultipartMessageResolver multipartMessageResolver;

	public DefaultMultiPartServerHttpRequest(ServerHttpRequest targetRequest,
			MultipartMessageResolver multipartMessageResolver) {
		super(targetRequest);
		this.multipartMessageResolver = multipartMessageResolver;
	}

	private List<MultipartMessage> multipartMessages;

	public List<MultipartMessage> getMultipartMessages() {
		if (multipartMessages == null) {
			try {
				multipartMessages = multipartMessageResolver.resolve(this);
			} catch (IOException e) {
				logger.error(e, toString());
			}

			if (CollectionUtils.isEmpty(multipartMessages)) {
				this.multipartMessages = Collections.emptyList();
			} else {
				this.multipartMessages = Collections.unmodifiableList(multipartMessages);
			}
		}
		return multipartMessages;
	}
}
