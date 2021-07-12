package scw.web.servlet.http.multipart;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.net.message.multipart.MultipartMessage;
import scw.util.LinkedMultiValueMap;
import scw.util.MultiValueMap;
import scw.web.MultiPartServerHttpRequest;
import scw.web.servlet.http.ServletServerHttpRequest;

public class ServletMultiPartServerHttpRequest extends ServletServerHttpRequest implements MultiPartServerHttpRequest {
	private static Logger logger = LoggerFactory.getLogger(ServletMultiPartServerHttpRequest.class);
	
	public ServletMultiPartServerHttpRequest(HttpServletRequest httpServletRequest) {
		super(httpServletRequest);
	}

	@Override
	public MultiValueMap<String, MultipartMessage> getMultipartMessageMap() {
		MultiValueMap<String, MultipartMessage> map = new LinkedMultiValueMap<>();
		try {
			for(Part part : getHttpServletRequest().getParts()) {
				map.add(part.getName(), new PartMultipartMessage(part));
			}
		} catch (Exception e) {
			logger.error(e, "解析异常");
		}
		return map;
	}
}
