package scw.beans.property;

import java.util.Map;

public interface Properties {
	Map<String, Property> getPropertyMap();
	
	/**
	 * 获取刷新周期  秒
	 * @return
	 */
	long getRefreshPeriod();
}
