package io.basc.framework.cloud.loadbalancer;

import java.util.HashSet;
import java.util.function.Predicate;

import io.basc.framework.http.client.HttpClientException;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.collection.Elements;
import io.basc.framework.util.collection.ServiceLoader;
import io.basc.framework.util.retry.ExhaustedRetryException;
import io.basc.framework.util.retry.RetryOperations;

/**
 * 负载均衡
 * 
 * @author wcnnkh
 *
 * @param <T>
 */
public interface LoadBalancer<T extends Node> extends ServiceLoader<T> {
	
	/**
	 * 选择一个服务
	 * 
	 * @return
	 */
	@Nullable
	default T choose() {
		return choose((e) -> true);
	}

	/**
	 * 选择一个服务
	 * 
	 * @param accept
	 * @return
	 */
	@Nullable
	T choose(@Nullable Predicate<? super T> accept);

	/**
	 * 选取一组服务中的一个
	 * 
	 * @param name
	 * @return
	 */
	default T choose(String name) {
		return choose(name, null);
	}

	/**
	 * 选取一组服务中的一个
	 * 
	 * @param name
	 * @param accept
	 * @return
	 */
	default T choose(String name, @Nullable Predicate<? super T> accept) {
		return choose(
				(service) -> StringUtils.equals(service.getName(), name) && (accept == null || accept.test(service)));
	}

	/**
	 * 选择一组服务
	 * 
	 * @param name
	 * @return
	 */
	default Elements<T> chooses(String name) {
		return getServices().filter((service) -> StringUtils.equals(service.getName(), name));
	}

	/**
	 * 统计服务状态
	 * 
	 * @param service
	 * @param state
	 */
	void stat(T service, State state);

	default <V, E extends Throwable> V execute(RetryOperations retryOperations, LoadConsumer<T, V, E> consumer)
			throws E, ExhaustedRetryException {
		HashSet<String> errorSets = new HashSet<String>();
		return retryOperations.execute((context) -> {
			T server = choose((s) -> {
				return !errorSets.contains(s.getId());
			});

			if (server == null) {
				try {
					return consumer.accept(context, server);
				} finally {
					context.setExhaustedOnly();
				}
			}

			try {
				return consumer.accept(context, server);
			} catch (HttpClientException e) {
				errorSets.add(server.getId());
				stat(server, State.FAILED);
				throw e;
			}
		});
	}

	default <V, E extends Throwable> V execute(String name, RetryOperations retryOperations,
			LoadConsumer<T, V, E> consumer) throws E, ExhaustedRetryException {
		HashSet<String> errorSets = new HashSet<String>();
		return retryOperations.execute((context) -> {
			T service = choose(name, (s) -> {
				return !errorSets.contains(s.getId());
			});

			if (service == null) {
				try {
					return consumer.accept(context, service);
				} finally {
					context.setExhaustedOnly();
				}
			}

			try {
				return consumer.accept(context, service);
			} catch (HttpClientException e) {
				errorSets.add(service.getId());
				stat(service, State.FAILED);
				throw e;
			}
		});
	}
}
