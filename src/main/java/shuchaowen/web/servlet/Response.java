package shuchaowen.web.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.alibaba.fastjson.JSONObject;

import shuchaowen.common.Logger;
import shuchaowen.connection.http.enums.ContentType;
import shuchaowen.reflect.ClassUtils;

public class Response extends HttpServletResponseWrapper{
	private static final String JSONP_CALLBACK = "callback";
	private static final String JSONP_RESP_PREFIX = "(";
	private static final String JSONP_RESP_SUFFIX = ");";
	private Request request;

	public Response(Request request, HttpServletResponse httpServletResponse) {
		super(httpServletResponse);
		this.request = request;
	}

	public Request getRequest() {
		return request;
	}
	
	protected String toJsonString(Object data){
		return JSONObject.toJSONString(data);
	}

	public void write(Object obj) throws IOException{
		if (obj != null) {
			if (obj instanceof View) {
				((View) obj).render(request, this);
			} else {
				String content;
				if((obj instanceof String) || (ClassUtils.isBasicType(obj.getClass()))){
					content = obj.toString();
				}else{
					content = toJsonString(obj);
				}
				
				String callback = null;
				try {
					callback = request.getParameter(String.class, JSONP_CALLBACK);
				} catch (Throwable e) {
					e.printStackTrace();
				}
				
				if(callback != null && callback.length() != 0){
					setContentType(ContentType.TEXT_JAVASCRIPT.getValue());
					StringBuilder sb = new StringBuilder();
					sb.append(callback);
					sb.append(JSONP_RESP_PREFIX);
					sb.append(content);
					sb.append(JSONP_RESP_SUFFIX);
					content = sb.toString();
				}else{
					if(getContentType() == null){
						setContentType(ContentType.TEXT_HTML.getValue());
					}
				}
				
				if(request.isDebug()){
					Logger.debug(this.getClass().getName(), content);
				}
				getWriter().write(content);
			}
		}
	}
}
