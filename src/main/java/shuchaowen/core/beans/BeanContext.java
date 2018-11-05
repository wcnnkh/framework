package shuchaowen.core.beans;

import java.util.HashMap;
import java.util.Map;

import shuchaowen.core.util.Context;

public abstract class BeanContext extends Context<Map<String, Object>>{
	public abstract <T> T getBean(String name);
	
	public abstract <T> T getBean(Class<T> type);
	
	public abstract <T> T getBean(Class<T> type, Class<?>[] parameterTypes, Object ...params);
	
	@Override
	protected void firstBegin() {
		Map<String, Object> map = getValue();
		if(map == null){
			map = new HashMap<String, Object>(4);
			setValue(map);
		}
	}

	@Override
	protected void lastCommit() {
		setValue(null);
	}
}
