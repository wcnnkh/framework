package io.basc.framework.util.spi;

import io.basc.framework.core.Ordered;
import io.basc.framework.util.Wrapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;

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