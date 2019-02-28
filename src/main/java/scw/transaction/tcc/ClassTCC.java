package scw.transaction.tcc;

import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import scw.common.exception.AlreadyExistsException;

public class ClassTCC {
	private Map<String, EnumMap<StageType, Method>> tccMethodMap = new HashMap<String, EnumMap<StageType, Method>>(4,
			1);
	private final Class<?> clz;

	public ClassTCC(Class<?> clz) {
		this.clz = clz;
		for (Method method : clz.getMethods()) {
			TCC tcc = method.getAnnotation(TCC.class);
			if (tcc == null) {
				continue;
			}

			EnumMap<StageType, Method> map = tccMethodMap.get(tcc.name());
			if (map == null) {
				map = new EnumMap<StageType, Method>(StageType.class);
				map.put(tcc.stage(), method);
				tccMethodMap.put(tcc.name(), map);
			} else {
				if (map.containsKey(tcc.stage())) {
					throw new AlreadyExistsException(
							clz.getName() + "存在相同的TCC配置,name=" + tcc.name() + ",stageType=" + tcc.stage());
				}

				map.put(tcc.stage(), method);
			}
		}
	}

	public Class<?> getClz() {
		return clz;
	}

	public EnumMap<StageType, Method> getTCCMethodMap(String name) {
		return tccMethodMap.get(name);
	}

	public Method getMethod(String name, StageType stageType) {
		EnumMap<StageType, Method> map = tccMethodMap.get(name);
		return map == null ? null : map.get(stageType);
	}

}
