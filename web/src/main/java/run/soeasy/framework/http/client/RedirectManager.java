package run.soeasy.framework.http.client;

import java.net.URI;

import run.soeasy.framework.http.HttpRequest;
import run.soeasy.framework.http.HttpResponseEntity;

public interface RedirectManager {
	/**
	 * 获取重定向的url
	 * 
	 * @param request
	 * @param responseEntity
	 * @param deep           当前深度 从0开始
	 * @return
	 */
	URI getRedirect(HttpRequest request, HttpResponseEntity<?> responseEntity, long deep);
}