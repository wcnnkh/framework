package scw.mvc;

import java.io.IOException;

import scw.beans.BeanFactory;
import scw.http.server.ServerHttpRequest;
import scw.http.server.ServerHttpResponse;
import scw.http.server.jsonp.JsonpUtils;
import scw.http.server.pattern.HttpPatternRegistry;
import scw.json.JSONSupportAccessor;
import scw.net.InetUtils;
import scw.net.message.multipart.FileItemParser;
import scw.web.WebUtils;

public class DefaultHttpChannelFactory extends JSONSupportAccessor implements HttpChannelFactory {
	protected final BeanFactory beanFactory;
	private FileItemParser fileItemParser;
	private final HttpPatternRegistry<Boolean> jsonpSupportConfig = new HttpPatternRegistry<Boolean>();
	private final HttpPatternRegistry<Boolean> jsonSupportWrapperConfig = new HttpPatternRegistry<Boolean>();
	private final HttpPatternRegistry<Boolean> multipartFormSupportWrapperConfig = new HttpPatternRegistry<Boolean>();

	public FileItemParser getFileItemParser() {
		return fileItemParser == null ? InetUtils.getFileItemParser() : fileItemParser;
	}

	public void setFileItemParser(FileItemParser fileItemParser) {
		this.fileItemParser = fileItemParser;
	}

	public final HttpPatternRegistry<Boolean> getJsonpSupportConfig() {
		return jsonpSupportConfig;
	}

	public final HttpPatternRegistry<Boolean> getJsonSupportWrapperConfig() {
		return jsonSupportWrapperConfig;
	}

	public final HttpPatternRegistry<Boolean> getMultipartFormSupportWrapperConfig() {
		return multipartFormSupportWrapperConfig;
	}

	public boolean isSupportJsonWrapper(ServerHttpRequest request) {
		return jsonSupportWrapperConfig.get(request, true);
	}

	public boolean isSupportJsonp(ServerHttpRequest request) {
		return jsonSupportWrapperConfig.get(request, true);
	}
	
	public boolean isSupportMultipartFormWrapper(ServerHttpRequest request) {
		return multipartFormSupportWrapperConfig.get(request, true);
	}

	public DefaultHttpChannelFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public HttpChannel create(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		ServerHttpRequest requestToUse = request;
		if(isSupportJsonWrapper(requestToUse)) {
			requestToUse = WebUtils.wrapperServerJsonRequest(requestToUse);
		}
		
		if(isSupportMultipartFormWrapper(requestToUse)) {
			requestToUse = WebUtils.wrapperServerMultipartFormRequest(requestToUse, getFileItemParser());
		}
		
		ServerHttpResponse responseToUse = response;
		if(isSupportJsonp(requestToUse)) {
			responseToUse = JsonpUtils.wrapper(requestToUse, responseToUse);
		}
		return new DefaultHttpChannel(beanFactory, getJsonSupport(), requestToUse, responseToUse);
	}
}
