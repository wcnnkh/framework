package io.basc.framework.feign.context;

import java.util.HashMap;
import java.util.Map;

import feign.Feign;
import feign.codec.Decoder;
import feign.codec.Encoder;
import io.basc.framework.beans.factory.ServiceLoaderFactory;
import io.basc.framework.beans.factory.config.Configurable;
import io.basc.framework.context.annotation.Component;
import io.basc.framework.context.annotation.ConditionalOnMissingBean;
import io.basc.framework.execution.reflect.MethodExecutionInterceptor;
import io.basc.framework.execution.reflect.ReflectionMethodExecutor;
import io.basc.framework.feign.FeignDecoder;
import io.basc.framework.feign.FeignEncoder;
import io.basc.framework.net.message.convert.DefaultMessageConverters;
import io.basc.framework.util.element.Elements;

@Component
@ConditionalOnMissingBean(FeginMethodExecutionInterceptor.class)
public class FeginMethodExecutionInterceptor extends MethodExecutionInterceptor implements Configurable {
	private final DefaultMessageConverters messageConverters = new DefaultMessageConverters();
	private boolean configured;
	private volatile Map<Class<?>, Object> proxyMap = new HashMap<>();

	private Object getProxy(Class<?> clazz, FeignClient feginClient) {
		Object proxy = proxyMap.get(clazz);
		if (proxy == null) {
			synchronized (proxyMap) {
				proxy = proxyMap.get(clazz);
				if (proxy == null) {
					Encoder encoder = getEncoder(clazz, feginClient);
					Decoder decoder = getDecoder(clazz, feginClient);
					String host = getHost(clazz, feginClient);
					proxy = Feign.builder().encoder(encoder).decoder(decoder).target(clazz, host);
					proxyMap.put(clazz, proxy);
				}
			}
		}
		return proxy;
	}

	private Encoder getEncoder(Class<?> clazz, FeignClient feginClient) {
		return new FeignEncoder(messageConverters);
	}

	private Decoder getDecoder(Class<?> clazz, FeignClient feginClient) {
		return new FeignDecoder(messageConverters);
	}

	private String getHost(Class<?> clazz, FeignClient feginClient) {
		return feginClient.host();
	}

	@Override
	public Object intercept(ReflectionMethodExecutor executor, Elements<? extends Object> args) throws Throwable {
		FeignClient feignClient = executor.getSource().getType().getAnnotation(FeignClient.class);
		if (feignClient == null) {
			return executor.execute(args);
		}

		Object proxy = getProxy(executor.getSource().getType(), feignClient);
		return executor.execute(proxy, args);
	}

	@Override
	public boolean isConfigured() {
		return configured;
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		configured = true;
		messageConverters.configure(serviceLoaderFactory);
	}

}
