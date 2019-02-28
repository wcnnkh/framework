package scw.transaction.tcc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class TCCManager {
	private static volatile Map<Class<?>, ClassTCC> cacheMap = new HashMap<Class<?>, ClassTCC>();

	public static ClassTCC getClassTCC(Class<?> clz) {
		ClassTCC classTCC = cacheMap.get(clz);
		if (classTCC == null) {
			synchronized (cacheMap) {
				classTCC = cacheMap.get(clz);
				if (classTCC == null) {
					classTCC = new ClassTCC(clz);
					cacheMap.put(clz, classTCC);
				}
			}
		}
		return classTCC;
	}

	/**
	 * 调用TCC方法	
	 * @param clz
	 * @param name
	 * @param stageType
	 * @param bean
	 * @param args
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public static Object invokeTCC(Class<?> clz, String name, StageType stageType, Object bean, Object[] args)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method method = getClassTCC(clz).getMethod(name, stageType);
		if (method == null) {
			return null;
		}
		return method.invoke(bean, args);
	}
}
