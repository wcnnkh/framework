package shuchaowen.beans;

import java.util.HashMap;
import java.util.Map;

import shuchaowen.common.FieldInfo;

public class BeanFieldListenImpl implements BeanFieldListen{
	private static final long serialVersionUID = 1L;
	private Map<String, Object> changeColumnMap;
	private boolean startListen = false;
	
	public Map<String, Object> get_field_change_map() {
		return changeColumnMap;
	}

	public void start_field_listen() {
		if (changeColumnMap != null && !changeColumnMap.isEmpty()) {
			changeColumnMap.clear();
		}
		startListen = true;
	}

	public Map<String, Object> getChangeColumnMap() {
		return changeColumnMap;
	}

	public void setChangeColumnMap(Map<String, Object> changeColumnMap) {
		this.changeColumnMap = changeColumnMap;
	}

	public boolean isStartListen() {
		return startListen;
	}

	public void setStartListen(boolean startListen) {
		this.startListen = startListen;
	}

	public void field_change(FieldInfo fieldInfo, Object oldValue) {
		if (changeColumnMap == null) {
			changeColumnMap = new HashMap<String, Object>();
		}
		changeColumnMap.put(fieldInfo.getName(), oldValue);
	}
}
