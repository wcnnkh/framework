package io.basc.framework.web;

import io.basc.framework.http.MediaType;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.net.message.multipart.MultipartMessage;
import io.basc.framework.net.message.multipart.MultipartMessageResolver;
import io.basc.framework.util.CollectionUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.fileupload.FileItem;

/**
 * 一个MultiPart请求
 * 
 * @author shuchaowen
 * @see MediaType#MULTIPART_FORM_DATA
 *
 */
public class DefaultMultiPartServerHttpRequest extends ServerHttpRequestWrapper
		implements MultiPartServerHttpRequest, AutoCloseable {
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

	/**
	 * 关闭所有的item
	 * 
	 * @throws IOException
	 * 
	 * @see FileItem#close()
	 */
	public void close() throws IOException {
		if (!CollectionUtils.isEmpty(multipartMessages)) {
			for (MultipartMessage message : multipartMessages) {
				message.close();
			}
		}
	}
}