package scw.mvc.http.filter;

import java.util.Map;

import scw.mvc.Action;
import scw.mvc.Channel;

final class RestInfo {
	private String url;
	private Map<Integer, String> keyMap;
	private String[] regArr;
	private Action<Channel> action;

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

	public Action<Channel> getAction() {
		return action;
	}

	public void setAction(Action<Channel> action) {
		this.action = action;
	}
}
