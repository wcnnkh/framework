package scw.mvc.output.support;

import scw.core.utils.ClassUtils;
import scw.mvc.http.HttpChannel;
import scw.mvc.output.HttpOutput;
import scw.net.http.MediaType;

public class DefaultHttpOutput extends HttpOutput<Object> {

	@Override
	protected boolean canWriteInternal(HttpChannel httpChannel, Object body) {
		return true;
	}
	
	@Override
	protected void writeBodyBefore(HttpChannel channel, Object body)
			throws Throwable {
		if(channel.getResponse().getContentType() == null){
			if(body instanceof String){
				channel.getResponse().setContentType(MediaType.TEXT_HTML);
			}else{
				channel.getResponse().setContentType(MediaType.APPLICATION_JSON);
			}
		}
		super.writeBodyBefore(channel, body);
	}

	@Override
	protected void writeBody(HttpChannel channel, Object body)
			throws Throwable {
		String content;
		if ((body instanceof String)
				|| (ClassUtils.isPrimitiveOrWrapper(body.getClass()))) {
			content = body.toString();
		} else {
			content = getJsonSupport().toJSONString(body);
		}
		channel.getResponse().getWriter().write(content);
		if (channel.isLogEnabled()) {
			channel.log(content);
		}
	}
}
