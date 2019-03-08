package scw.transaction.tcc;

import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import scw.common.MethodConfig;
import scw.common.exception.AlreadyExistsException;

class ClassTCC {
	private Map<String, EnumMap<StageType, MethodConfig>> tccMethodMap = new HashMap<String, EnumMap<StageType, MethodConfig>>(
			4, 1);
	private final Class<?> clz;

	public ClassTCC(Class<?> clz) {
		this.clz = clz;
		for (Method method : clz.getMethods()) {
			Try t = method.getAnnotation(Try.class);
			if (t != null) {
				add(t.name(), StageType.Try, method);
			}

			Confirm confirm = method.getAnnotation(Confirm.class);
			if (confirm != null) {
				add(confirm.name(), StageType.Confirm, method);
			}

			Cancel cancel = method.getAnnotation(Cancel.class);
			if (cancel != null) {
				add(confirm.name(), StageType.Cancel, method);
			}
			
			Complate complate = method.getAnnotation(Complate.class);
			if(complate != null){
				add(complate.name(), StageType.Complate, method);
			}
		}
	}

	private void add(String name, StageType stageType, Method method) {
		EnumMap<StageType, MethodConfig> map = tccMethodMap.get(name);
		if (map == null) {
			map = new EnumMap<StageType, MethodConfig>(StageType.class);
			map.put(stageType, new MethodConfig(clz, method));
			tccMethodMap.put(name, map);
		} else {
			if (map.containsKey(name)) {
				throw new AlreadyExistsException(clz.getName() + "存在相同的TCC配置,name=" + name + ",stageType=" + stageType);
			}

			map.put(stageType, new MethodConfig(clz, method));
		}
	}

	public Class<?> getClz() {
		return clz;
	}

	public EnumMap<StageType, MethodConfig> getTCCMethodMap(String name) {
		return tccMethodMap.get(name);
	}

	public MethodConfig getMethodConfig(String name, StageType stageType) {
		EnumMap<StageType, MethodConfig> map = tccMethodMap.get(name);
		return map == null ? null : map.get(stageType);
	}

}
