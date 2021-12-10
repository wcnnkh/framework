package io.basc.framework.web;

import io.basc.framework.http.MediaType;
import io.basc.framework.net.message.multipart.MultipartMessage;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.LinkedMultiValueMap;
import io.basc.framework.util.MultiValueMap;

import java.util.Collection;

/**
 * 一个MultiPart请求
 * 
 * @author shuchaowen
 * @see MediaType#MULTIPART_FORM_DATA
 *
 */
public interface MultiPartServerHttpRequest extends ServerHttpRequest {

	Collection<MultipartMessage> getMultipartMessages();

	default MultiValueMap<String, MultipartMessage> getMultipartMessageMap() {
		Collection<MultipartMessage> messages = getMultipartMessages();
		if (CollectionUtils.isEmpty(messages)) {
			return CollectionUtils.emptyMultiValueMap();
		}

		MultiValueMap<String, MultipartMessage> map = new LinkedMultiValueMap<>(messages.size());
		for (MultipartMessage part : messages) {
			map.add(part.getName(), part);
		}
		return map;
	}

	default MultipartMessage getFirstFile() {
		return getMultipartMessages().stream().filter((m) -> m.isFile()).findFirst().orElse(null);
	}
}
