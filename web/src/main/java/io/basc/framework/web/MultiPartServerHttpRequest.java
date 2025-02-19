package io.basc.framework.web;

import java.util.Collection;

import io.basc.framework.http.MediaType;
import io.basc.framework.http.server.ServerHttpRequest;
import io.basc.framework.net.multipart.MultipartMessage;
import io.basc.framework.util.collections.CollectionUtils;
import io.basc.framework.util.collections.LinkedMultiValueMap;
import io.basc.framework.util.collections.MultiValueMap;

/**
 * 一个MultiPart请求
 * 
 * @author wcnnkh
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
