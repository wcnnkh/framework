package scw.mvc.output.support;

import scw.core.utils.ClassUtils;
import scw.mvc.Channel;
import scw.mvc.output.AbstractOutput;
import scw.net.http.MediaType;

public class DefaultHttpOutput extends AbstractOutput<Object> {

	@Override
	protected boolean canWriteInternal(Channel channel, Object body) {
		return true;
	}
	
	@Override
	protected void writeBodyBefore(Channel channel, Object body)
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
	protected void writeBody(Channel channel, Object body)
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
