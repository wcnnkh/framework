package scw.mvc.action;

import java.util.HashMap;
import java.util.Map;

final class HttpRestInfo {
	private String url;
	private Map<Integer, String> keyMap;
	private String[] regArr;
	private HttpAction action;

	public String getUrl() {
		return url;
	}

	protected void setUrl(String url) {
		this.url = url;
	}

	public Map<Integer, String> getKeyMap() {
		return keyMap;
	}

	protected void setKeyMap(Map<Integer, String> keyMap) {
		this.keyMap = keyMap;
	}

	public String[] getRegArr() {
		return regArr;
	}

	protected void setRegArr(String[] regArr) {
		this.regArr = regArr;
	}

	public HttpAction getAction() {
		return action;
	}

	public void setAction(HttpAction action) {
		this.action = action;
	}

	public static HttpRestInfo getRestInfo(HttpAction action, HttpControllerConfig config) {
		String[] requestPathArr = config.getController().split("/");
		Map<Integer, String> resultKeyMap = new HashMap<Integer, String>(4);
		StringBuilder newRequestPath = new StringBuilder(config.getController().length());
		for (int i = 0; i < requestPathArr.length; i++) {
			String str = requestPathArr[i];
			if (str.startsWith("{") && str.endsWith("}")) {
				newRequestPath.append("*");
				resultKeyMap.put(i, str.substring(1, str.length() - 1));
			} else {
				newRequestPath.append(str);
			}

			if (i < requestPathArr.length - 1) {
				newRequestPath.append("/");
			}
		}

		if (config.getController().endsWith("/")) {
			newRequestPath.append("/");
		}

		HttpRestInfo restUrl = new HttpRestInfo();
		restUrl.setUrl(newRequestPath.toString());
		restUrl.setRegArr(newRequestPath.toString().split("/"));
		restUrl.setKeyMap(resultKeyMap);
		restUrl.setAction(action);
		return restUrl;
	}
}
