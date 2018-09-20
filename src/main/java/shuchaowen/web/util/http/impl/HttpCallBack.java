package shuchaowen.web.util.http.impl;

import java.net.HttpURLConnection;

public interface HttpCallBack {
	public void call(HttpURLConnection conn) throws Exception;
}
