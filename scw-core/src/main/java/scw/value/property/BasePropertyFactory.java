package scw.value.property;

import java.util.Iterator;

import scw.aop.annotation.AopEnable;
import scw.event.NamedEventRegistry;
import scw.value.BaseValueFactory;

@AopEnable(false)
public interface BasePropertyFactory extends BaseValueFactory<String>,
		Iterable<String>, NamedEventRegistry<String, PropertyEvent> {
	/**
	 * 能获取到值不一定代表可以通过此方法迭代到
	 * 
	 * @return
	 */
	Iterator<String> iterator();

	boolean containsKey(String key);
}
