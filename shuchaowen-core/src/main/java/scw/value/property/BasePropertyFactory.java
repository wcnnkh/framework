package scw.value.property;

import java.util.Enumeration;

import scw.event.NamedEventDispatcher;
import scw.value.BaseValueFactory;

public interface BasePropertyFactory extends BaseValueFactory<String>, NamedEventDispatcher<PropertyEvent>{
	/**
	 * 能获取到值不一定代表可以通过此方法枚举到key
	 * @return
	 */
	Enumeration<String> enumerationKeys();
	
	boolean containsKey(String key);
}
