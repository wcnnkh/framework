package scw.util.value.property;

import java.util.Collections;
import java.util.Enumeration;

/**
 * 不支持枚举所有属性的PropertyFactory
 * @author shuchaowen
 *
 */
public abstract class NotSupportEnumerationPropertyFactory extends AbstractPropertyFactory{
	
	public Enumeration<String> enumerationKeys() {
		return Collections.emptyEnumeration();
	}

}
