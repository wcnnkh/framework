package io.basc.framework.core.convert.transform.stereotype;

import io.basc.framework.core.convert.transform.Accesstor;
import io.basc.framework.util.Elements;

/**
 * 模板
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public interface Template<T extends Accesstor> {
	Elements<T> getAccesstors();
}
