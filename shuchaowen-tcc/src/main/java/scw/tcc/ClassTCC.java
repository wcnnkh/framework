package scw.tcc;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import scw.core.reflect.SerializableMethodHolder;
import scw.core.utils.StringUtils;
import scw.lang.AlreadyExistsException;
import scw.tcc.annotation.TCCStage;

class ClassTCC {
	private Map<String, SerializableMethodHolder> tccMethodMap;
	private final Class<?> clz;

	public ClassTCC(Class<?> clz) {
		this.clz = clz;
		for (Method method : clz.getMethods()) {
			TCCStage tCCStage = method.getAnnotation(TCCStage.class);
			if (tCCStage != null) {
				if (tccMethodMap == null) {
					tccMethodMap = new HashMap<String, SerializableMethodHolder>();
				}

				String name = StringUtils.isEmpty(tCCStage.name()) ? method.getName() : tCCStage.name();

				if (tccMethodMap.containsKey(name)) {
					throw new AlreadyExistsException(clz.getName() + "存在相同的TCC配置,name=" + name);
				}

				tccMethodMap.put(name, new SerializableMethodHolder(clz, method));
			}
		}
	}

	public Class<?> getClz() {
		return clz;
	}

	public SerializableMethodHolder getMethodDefinition(String name) {
		return tccMethodMap == null ? null : tccMethodMap.get(name);
	}
}
