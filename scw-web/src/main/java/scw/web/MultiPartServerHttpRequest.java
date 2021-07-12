package scw.web;

import java.util.List;
import java.util.Map.Entry;

import scw.http.MediaType;
import scw.net.message.multipart.MultipartMessage;
import scw.util.MultiValueMap;

/**
 * 一个MultiPart请求
 * 
 * @author shuchaowen
 * @see MediaType#MULTIPART_FORM_DATA
 *
 */
public interface MultiPartServerHttpRequest extends ServerHttpRequest {

	MultiValueMap<String, MultipartMessage> getMultipartMessageMap();

	default MultipartMessage getFirstFile() {
		for (Entry<String, List<MultipartMessage>> entry : getMultipartMessageMap().entrySet()) {
			for (MultipartMessage message : entry.getValue()) {
				if (message.isFile()) {
					return message;
				}
			}
		}
		return null;
	}
}
