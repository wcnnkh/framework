package scw.core.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import scw.core.exception.NotSupportException;

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
		String str = UUID.randomUUID().toString();
		char[] cs = new char[32];
		char[] oldCs = str.toCharArray();
		char c;
		int index = 0;
		for (int i = 0; i < oldCs.length; i++) {
			c = oldCs[i];
			switch (c) {
			case '-':
				break;
			default:
				cs[index] = c;
				index++;
				break;
			}
		}
		return new String(cs, 0, 32);
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

		final CountDownLatch countDownLatch = new CountDownLatch(runnables.size());
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
	public static <V> Collection<Future<V>> submit(ExecutorService executorService, Collection<Callable<V>> callables) {
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
	public static <V> Collection<V> getAndAwait(Collection<Future<V>> futures, long timeout, TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
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

	public static <V> Collection<V> submitAndAwait(ExecutorService executorService, Collection<Callable<V>> callables,
			long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		Collection<Future<V>> collection = submit(executorService, callables);
		return getAndAwait(collection, timeout, unit);
	}

	public static <V> Collection<V> submitAndAwait(ExecutorService executorService, Collection<Callable<V>> callables)
			throws InterruptedException, ExecutionException {
		Collection<Future<V>> collection = submit(executorService, callables);
		return getAndAwait(collection);
	}

	public static <T> Object getValue(PrimitiveTypeValueFactory<T> primitiveTypeValueFactory, T data, Class<?> type) {
		if (int.class.isAssignableFrom(type)) {
			return primitiveTypeValueFactory.getIntValue(data);
		} else if (Integer.class.isAssignableFrom(type)) {
			return primitiveTypeValueFactory.getInteger(data);
		} else if (long.class.isAssignableFrom(type)) {
			return primitiveTypeValueFactory.getLongValue(data);
		} else if (Long.class.isAssignableFrom(type)) {
			return primitiveTypeValueFactory.getLong(data);
		} else if (float.class.isAssignableFrom(type)) {
			return primitiveTypeValueFactory.getFloatValue(data);
		} else if (Float.class.isAssignableFrom(type)) {
			return primitiveTypeValueFactory.getFloat(data);
		} else if (short.class.isAssignableFrom(type)) {
			return primitiveTypeValueFactory.getShortValue(data);
		} else if (Short.class.isAssignableFrom(type)) {
			return primitiveTypeValueFactory.getShort(data);
		} else if (boolean.class.isAssignableFrom(type)) {
			return primitiveTypeValueFactory.getBooleanValue(data);
		} else if (Boolean.class.isAssignableFrom(type)) {
			return primitiveTypeValueFactory.getBoolean(data);
		} else if (byte.class.isAssignableFrom(type)) {
			return primitiveTypeValueFactory.getByteValue(data);
		} else if (Byte.class.isAssignableFrom(type)) {
			return primitiveTypeValueFactory.getByte(data);
		} else if (char.class.isAssignableFrom(type)) {
			return primitiveTypeValueFactory.getChar(data);
		} else if (Character.class.isAssignableFrom(type)) {
			return primitiveTypeValueFactory.getCharacter(data);
		}
		throw new NotSupportException("不支持的类型：" + type.getName());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> Object getValue(ValueFactory<T> valueFactory, T data, Class<?> type) {
		if (String.class.isAssignableFrom(type)) {
			return valueFactory.getString(data);
		} else if (int.class.isAssignableFrom(type)) {
			return valueFactory.getIntValue(data);
		} else if (Integer.class.isAssignableFrom(type)) {
			return valueFactory.getInteger(data);
		} else if (long.class.isAssignableFrom(type)) {
			return valueFactory.getLongValue(data);
		} else if (Long.class.isAssignableFrom(type)) {
			return valueFactory.getLong(data);
		} else if (float.class.isAssignableFrom(type)) {
			return valueFactory.getFloatValue(data);
		} else if (Float.class.isAssignableFrom(type)) {
			return valueFactory.getFloat(data);
		} else if (short.class.isAssignableFrom(type)) {
			return valueFactory.getShortValue(data);
		} else if (Short.class.isAssignableFrom(type)) {
			return valueFactory.getShort(data);
		} else if (boolean.class.isAssignableFrom(type)) {
			return valueFactory.getBooleanValue(data);
		} else if (Boolean.class.isAssignableFrom(type)) {
			return valueFactory.getBoolean(data);
		} else if (byte.class.isAssignableFrom(type)) {
			return valueFactory.getByteValue(data);
		} else if (Byte.class.isAssignableFrom(type)) {
			return valueFactory.getByte(data);
		} else if (char.class.isAssignableFrom(type)) {
			return valueFactory.getChar(data);
		} else if (Character.class.isAssignableFrom(type)) {
			return valueFactory.getCharacter(data);
		} else if (BigDecimal.class.isAssignableFrom(type)) {
			return valueFactory.getBigDecimal(data);
		} else if (BigInteger.class.isAssignableFrom(type)) {
			return valueFactory.getBigInteger(data);
		} else if(Class.class.isAssignableFrom(type)){
			return valueFactory.getClass(data);
		} else if (type.isEnum()) {
			return valueFactory.getEnum(data, (Class<? extends Enum>) type);
		} else if (type.isArray()) {
			return valueFactory.getArray(data, type.getComponentType());
		} else {
			return valueFactory.getObject(data, type);
		}
	}
}