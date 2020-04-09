package scw.core.utils;

import java.io.Flushable;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import scw.core.Converter;
import scw.core.Destroy;
import scw.core.Init;
import scw.core.ResourceFactory;
import scw.core.Start;
import scw.util.ToMap;

public final class XUtils {
	private XUtils() {
	};

	public static boolean isWin() {
		return System.getProperty("os.name").toLowerCase().startsWith("win");
	}

	public static boolean isMac() {
		return System.getProperty("os.name").toLowerCase().startsWith("mac");
	}

	public static String getUUID() {
		return StringUtils.removeChar(UUID.randomUUID().toString(), '-');
	}

	public static String mergePath(String... path) {
		if (path.length == 0) {
			return null;
		}

		if (path.length == 1) {
			return path[0];
		}

		String p = addPath(path[0], path[1]);
		for (int i = 2; i < path.length; i++) {
			p = addPath(p, path[i]);
		}
		return p;
	}

	private static String addPath(String path1, String path2) {
		String p1 = path1 == null ? "" : path1;
		String p2 = path2 == null ? "" : path2;
		p1 = p1.replaceAll("\\\\", "/");
		p2 = p2.replaceAll("\\\\", "/");

		if (!StringUtils.isNull(p2)) {
			if (!p1.endsWith("/")) {
				p1 = p1 + "/";
			}
		}

		if (!StringUtils.isNull(p1)) {
			if (p2.startsWith("/")) {
				p2 = p2.substring(1);
			}
		}
		return p1 + p2;
	}

	/**
	 * 检查版本号 如果 version1>version2就返回true
	 * 
	 * @param version1
	 * @param version2
	 * @return
	 */
	public static boolean checkVersion(String version1, String version2) {
		String[] arr1 = version1.split("\\.");
		String[] arr2 = version2.split("\\.");
		for (int i = 0; i < Math.min(arr1.length, arr2.length); i++) {
			int v1 = Integer.parseInt(arr1[i]);
			int v2 = Integer.parseInt(arr2[i]);
			if (v1 > v2) {
				return true;
			}
		}
		return false;
	}

	public static void concurrentSimulation(Collection<Runnable> runnables) {
		if (CollectionUtils.isEmpty(runnables)) {
			return;
		}

		final CountDownLatch countDownLatch = new CountDownLatch(
				runnables.size());
		for (final Runnable runnable : runnables) {
			new Thread(new Runnable() {

				public void run() {
					countDownLatch.countDown();
					try {
						countDownLatch.await();
						runnable.run();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}

	@SuppressWarnings("unchecked")
	public static <V> Collection<Future<V>> submit(
			ExecutorService executorService, Collection<Callable<V>> callables) {
		if (CollectionUtils.isEmpty(callables)) {
			return Collections.EMPTY_LIST;
		}

		List<Future<V>> list = new ArrayList<Future<V>>(callables.size());
		for (Callable<V> callable : callables) {
			list.add(executorService.submit(callable));
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public static <V> Collection<V> getAndAwait(Collection<Future<V>> futures,
			long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException {
		if (CollectionUtils.isEmpty(futures)) {
			return Collections.EMPTY_LIST;
		}

		List<V> list = new ArrayList<V>(futures.size());
		for (Future<V> future : futures) {
			list.add(future.get(timeout, unit));
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public static <V> Collection<V> getAndAwait(Collection<Future<V>> futures)
			throws InterruptedException, ExecutionException {
		if (CollectionUtils.isEmpty(futures)) {
			return Collections.EMPTY_LIST;
		}

		List<V> list = new ArrayList<V>(futures.size());
		for (Future<V> future : futures) {
			list.add(future.get());
		}
		return list;
	}

	public static <V> Collection<V> submitAndAwait(
			ExecutorService executorService, Collection<Callable<V>> callables,
			long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException {
		Collection<Future<V>> collection = submit(executorService, callables);
		return getAndAwait(collection, timeout, unit);
	}

	public static <V> Collection<V> submitAndAwait(
			ExecutorService executorService, Collection<Callable<V>> callables)
			throws InterruptedException, ExecutionException {
		Collection<Future<V>> collection = submit(executorService, callables);
		return getAndAwait(collection);
	}

	public static <T, R> T useResource(ResourceFactory<R> resourceFactory,
			Converter<R, T> converter) {
		R resource = null;
		try {
			resource = resourceFactory.getResource();
			return converter.convert(resource);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			resourceFactory.release(resource);
		}
	}

	public static void start(Object start) {
		if (start == null) {
			return;
		}

		if (start instanceof Start) {
			((Start) start).start();
		}
	}

	public static void init(Object init) {
		if (init == null) {
			return;
		}

		if (init instanceof Init) {
			((Init) init).init();
		}
	}

	public static void destroy(Object destroy) {
		if (destroy == null) {
			return;
		}

		if (destroy instanceof Destroy) {
			((Destroy) destroy).destroy();
		}
	}

	public static void flush(Object flushable) throws IOException {
		if (flushable == null) {
			return;
		}

		if (flushable instanceof Flushable) {
			((Flushable) flushable).flush();
		}
	}

	public static <T, R> T execute(ResourceFactory<R> resourceFactory,
			Converter<R, T> resourceConverter) throws Exception {
		R r = null;
		try {
			r = resourceFactory.getResource();
			return resourceConverter.convert(r);
		} finally {
			resourceFactory.release(r);
		}
	}

	@SuppressWarnings("unchecked")
	public static <K> Map<K, Object> toMap(ToMap<? extends K, ?> toMap) {
		if (toMap == null) {
			return null;
		}

		Map<? extends K, ?> map = toMap.toMap();
		if (map == null) {
			return null;
		}

		if (map.isEmpty()) {
			return Collections.EMPTY_MAP;
		}

		Map<K, Object> valueMap = new LinkedHashMap<K, Object>();
		for (Entry<? extends K, ?> entry : map.entrySet()) {
			valueMap.put(entry.getKey(),
					toParameterMapTransformation(entry.getValue()));
		}
		return valueMap;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Object toParameterMapTransformation(Object value) {
		if (value == null) {
			return value;
		}

		if (value instanceof ToMap) {
			return toMap((ToMap) value);
		} else if (value instanceof Collection) {
			Collection list = (Collection) value;
			if (CollectionUtils.isEmpty(list)) {
				return value;
			}

			List<Object> newList = new ArrayList<Object>(list.size());
			for (Object v : list) {
				Object tmp = toParameterMapTransformation(v);
				if (tmp == null) {
					continue;
				}
				newList.add(tmp);
			}
			return newList;
		} else if (value instanceof Map) {
			Map map = (Map) value;
			if (CollectionUtils.isEmpty(map)) {
				return value;
			}

			Set<Map.Entry> set = map.entrySet();
			for (Map.Entry entry : set) {
				entry.setValue(toParameterMapTransformation(entry.getValue()));
			}
		} else if (value.getClass().isArray()) {
			int len = Array.getLength(value);
			if (len == 0) {
				return value;
			}

			for (int i = 0; i < len; i++) {
				Object v = Array.get(value, i);
				Array.set(value, i, toParameterMapTransformation(v));
			}
		}
		return value;
	}

	public static void appendToMap(Properties properties,
			Map<String, String> map) {
		if(properties == null || map == null){
			return ;
		}
		
		for (Entry<Object, Object> entry : properties.entrySet()) {
			map.put(entry.getKey() == null ? null : entry.getKey().toString(),
					entry.getValue() == null ? null : entry.getValue()
							.toString());
		}
	}
}