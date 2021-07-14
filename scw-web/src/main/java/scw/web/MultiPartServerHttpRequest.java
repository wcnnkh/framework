package scw.web;

import java.util.Collection;

import scw.core.utils.CollectionUtils;
import scw.http.MediaType;
import scw.net.message.multipart.MultipartMessage;
import scw.util.LinkedMultiValueMap;
import scw.util.MultiValueMap;

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
		if(CollectionUtils.isEmpty(messages)) {
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
