package scw.servlet.action;

import java.util.Map;

public class RestInfo {
	private String url;
	private Map<Integer, String> keyMap;
	private String[] regArr;
	private Action action;

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

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}
}
