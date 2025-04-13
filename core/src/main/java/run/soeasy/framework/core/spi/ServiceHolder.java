package run.soeasy.framework.core.spi;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.Wrapper;
import run.soeasy.framework.core.comparator.Ordered;

/**
 * @author shuchaowen
 *
 * @param <T>
 */
@Data
@RequiredArgsConstructor
public class ServiceHolder<T> implements Wrapper<T>, Ordered {
	private final int order;
	private final T source;

	public ServiceHolder(T source) {
		this(0, source);
	}
}