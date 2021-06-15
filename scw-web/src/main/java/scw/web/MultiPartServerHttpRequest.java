package scw.web;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.fileupload.FileItem;

import scw.core.utils.CollectionUtils;
import scw.http.MediaType;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.net.message.multipart.MultipartMessage;
import scw.net.message.multipart.MultipartMessageResolver;
import scw.util.LinkedMultiValueMap;
import scw.util.MultiValueMap;

/**
 * 一个MultiPart请求
 * 
 * @author shuchaowen
 * @see MediaType#MULTIPART_FORM_DATA
 *
 */
public class MultiPartServerHttpRequest extends ServerHttpRequestWrapper implements AutoCloseable {
	private static Logger logger = LoggerFactory.getLogger(MultiPartServerHttpRequest.class);
	private final MultipartMessageResolver multipartMessageResolver;

	public MultiPartServerHttpRequest(ServerHttpRequest targetRequest,
			MultipartMessageResolver multipartMessageResolver) {
		super(targetRequest);
		this.multipartMessageResolver = multipartMessageResolver;
	}

	private List<MultipartMessage> multipartMessageList;

	public List<MultipartMessage> getMultipartMessageList() {
		if (multipartMessageList == null) {
			try {
				multipartMessageList = multipartMessageResolver.resolve(this);
			} catch (IOException e) {
				logger.error(e, toString());
			}

			if (CollectionUtils.isEmpty(multipartMessageList)) {
				this.multipartMessageList = Collections.emptyList();
			} else {
				this.multipartMessageList = Collections.unmodifiableList(multipartMessageList);
			}
		}
		return multipartMessageList;
	}

	private MultiValueMap<String, MultipartMessage> multipartMessageMap;

	public MultiValueMap<String, MultipartMessage> getMultipartMessageMap() {
		if (multipartMessageMap == null) {
			List<MultipartMessage> multipartMessages = getMultipartMessageList();
			if (CollectionUtils.isEmpty(multipartMessages)) {
				multipartMessageMap = CollectionUtils.emptyMultiValueMap();
				return multipartMessageMap;
			}

			multipartMessageMap = new LinkedMultiValueMap<String, MultipartMessage>();
			for (MultipartMessage multipartMessage : multipartMessages) {
				if (multipartMessage == null) {
					continue;
				}

				multipartMessageMap.add(multipartMessage.getName(), multipartMessage);
			}

			this.multipartMessageMap = CollectionUtils.unmodifiableMultiValueMap(multipartMessageMap);
		}
		return multipartMessageMap;
	}

	public MultipartMessage getFirstFile() {
		for (MultipartMessage message : getMultipartMessageList()) {
			if (message.isFile()) {
				return message;
			}
		}
		return null;
	}

	/**
	 * 关闭所有的item
	 * 
	 * @throws IOException
	 * 
	 * @see FileItem#close()
	 */
	public void close() throws IOException {
		if (!CollectionUtils.isEmpty(multipartMessageMap)) {
			for (MultipartMessage message : multipartMessageList) {
				message.close();
			}
		}
	}
}
