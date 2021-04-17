package scw.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 合并器
 * 
 * @author shuchaowen
 *
 * @param <E>
 */
@FunctionalInterface
public interface Combiner<E> {

	/**
	 * 合并
	 * 
	 * @param list
	 * @return
	 */
	E combine(List<E> list);

	default E combine(E[] array) {
		if (array == null || array.length == 0) {
			return combine(Collections.emptyList());
		}
		return combine(Arrays.asList(array));
	}
}
