package io.basc.framework.util.element;

import java.util.function.Function;

import io.basc.framework.util.Reloadable;
import io.basc.framework.util.select.Dispatcher;

public interface ServiceLoader<S> extends Reloadable{
	@SuppressWarnings("unchecked")
	public static <T> ServiceLoader<T> empty() {
		return (ServiceLoader<T>) EmptyServiceLoader.EMPTY;
	}

	public static <T> ServiceLoader<T> of(Elements<T> services) {
		if (services == null) {
			return empty();
		}

		return new FinalServiceLoader<>(services);
	}

	/**
	 * 根据指定的分发器来分发服务
	 * 
	 * @param dispatcher
	 * @return
	 */
	default Elements<S> dispatch(Dispatcher<S> dispatcher) {
		return dispatcher.dispatch(getServices());
	}

	default <U> ServiceLoader<U> convert(Function<? super Elements<S>, ? extends Elements<U>> converter) {
		return new ConvertibleServiceLoader<>(this, converter);
	}

	Elements<S> getServices();

	default ServiceLoader<S> concat(ServiceLoader<S> serviceLoader) {
		Elements<? extends ServiceLoader<S>> serviceLoaders = Elements.forArray(this, serviceLoader);
		return new MultiServiceLoader<>(serviceLoaders);
	}
}
