package shuchaowen.web.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.alibaba.fastjson.JSONObject;

import shuchaowen.core.http.enums.ContentType;
import shuchaowen.core.http.server.Response;
import shuchaowen.core.util.ClassUtils;

public class WebResponse extends HttpServletResponseWrapper implements Response{
	private static final String JSONP_CALLBACK = "callback";
	private static final String JSONP_RESP_PREFIX = "(";
	private static final String JSONP_RESP_SUFFIX = ");";
	private WebRequest request;

	public WebResponse(WebRequest request, HttpServletResponse httpServletResponse) {
		super(httpServletResponse);
		this.request = request;
	}

	public WebRequest getRequest() {
		return request;
	}
	
	protected String toJsonString(Object data){
		return JSONObject.toJSONString(data);
	}

	public void write(Object obj) throws IOException{
		if (obj != null) {
			if (obj instanceof View) {
				((View) obj).render(this);
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
					getWriter().write(callback);
					getWriter().write(JSONP_RESP_PREFIX);
					getWriter().write(content);
					getWriter().write(JSONP_RESP_SUFFIX);
				}else{
					if(getContentType() == null){
						setContentType(ContentType.TEXT_HTML.getValue());
					}
					getWriter().write(content);
				}
			}
		}
	}
}
