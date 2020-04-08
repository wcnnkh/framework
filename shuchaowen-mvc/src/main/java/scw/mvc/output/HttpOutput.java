package scw.mvc.output;

import scw.core.utils.StringUtils;
import scw.mvc.Channel;
import scw.mvc.http.HttpChannel;
import scw.net.MimeType;
import scw.net.MimeTypeUtils;

public abstract class HttpOutput<T> extends AbstractOutput<HttpChannel, T> {
	private static final String JSONP_RESP_PREFIX = "(";
	private static final String JSONP_RESP_SUFFIX = ");";
	private String jsonp = "";
	
	public String getJsonp() {
		return jsonp;
	}

	public void setJsonp(String jsonp) {
		this.jsonp = jsonp;
	}

	@Override
	protected boolean canWriteInternal(Channel channel, Object body) {
		if (channel instanceof HttpChannel) {
			return canWriteInternal((HttpChannel) channel, body);
		}
		return false;
	}

	protected abstract boolean canWriteInternal(HttpChannel httpChannel,
			Object body);
	
	
	protected String getJsonpCallback(HttpChannel httpChannel){
		//非GET请求不支持jsonp
		if(scw.net.http.Method.GET != httpChannel.getRequest().getMethod()){
			return null;
		}
		
		String jsonp = getJsonp();
		if(StringUtils.isEmpty(jsonp)){
			return null;
		}
		return httpChannel.getString(jsonp);
	}
	
	protected MimeType getJsonpContentType(HttpChannel channel, T body){
		return createContentType(MimeTypeUtils.TEXT_JAVASCRIPT, getCharsetName(channel, body));
	}
	
	@Override
	protected void writeBody(HttpChannel channel, T body) throws Throwable {
		String callback = getJsonpCallback(channel);
		if(StringUtils.isEmpty(callback)){
			writeBodyInternal(channel, body);
			return ;
		}
		
		channel.getResponse().setContentType(getJsonpContentType(channel, body));
		channel.getResponse().getWriter().write(JSONP_RESP_PREFIX);
		writeBodyInternal(channel, body);
		channel.getResponse().getWriter().write(JSONP_RESP_SUFFIX);
	}
	
	protected abstract void writeBodyInternal(HttpChannel channel, T body) throws Throwable;
}
