package scw.mvc.output;

import java.io.IOException;

import scw.core.utils.ClassUtils;
import scw.http.MediaType;
import scw.mvc.HttpChannel;

public class DefaultControllerOutput extends AbstractControllerOutput<Object> {

	@Override
	protected boolean canWriteInternal(HttpChannel httpChannel, Object body) {
		return true;
	}
	
	@Override
	protected void writeBodyBefore(HttpChannel httpChannel, Object body)
			throws IOException {
		if(httpChannel.getResponse().getContentType() == null){
			if(body instanceof String){
				httpChannel.getResponse().setContentType(MediaType.TEXT_HTML);
			}else{
				httpChannel.getResponse().setContentType(MediaType.APPLICATION_JSON);
			}
		}
		super.writeBodyBefore(httpChannel, body);
	}

	@Override
	protected void writeBody(HttpChannel httpChannel, Object body)
			throws IOException {
		String content;
		if ((body instanceof String)
				|| (ClassUtils.isPrimitiveOrWrapper(body.getClass()))) {
			content = body.toString();
		} else {
			content = getJsonSupport().toJSONString(body);
		}
		httpChannel.getResponse().getWriter().write(content);
		if (httpChannel.isLogEnabled()) {
			httpChannel.log(content);
		}
	}
}
