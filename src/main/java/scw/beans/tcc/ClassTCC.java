package scw.beans.tcc;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import scw.beans.annotation.TCCStage;
import scw.core.reflect.SerializableMethodDefinition;
import scw.core.utils.StringUtils;
import scw.lang.AlreadyExistsException;

class ClassTCC {
	private Map<String, SerializableMethodDefinition> tccMethodMap;
	private final Class<?> clz;

	public ClassTCC(Class<?> clz) {
		this.clz = clz;
		for (Method method : clz.getMethods()) {
			TCCStage tCCStage = method.getAnnotation(TCCStage.class);
			if (tCCStage != null) {
				if (tccMethodMap == null) {
					tccMethodMap = new HashMap<String, SerializableMethodDefinition>();
				}

				String name = StringUtils.isEmpty(tCCStage.name()) ? method.getName() : tCCStage.name();

				if (tccMethodMap.containsKey(name)) {
					throw new AlreadyExistsException(clz.getName() + "存在相同的TCC配置,name=" + name);
				}

				tccMethodMap.put(name, new SerializableMethodDefinition(clz, method));
			}
		}
	}

	public Class<?> getClz() {
		return clz;
	}

	public SerializableMethodDefinition getMethodDefinition(String name) {
		return tccMethodMap == null ? null : tccMethodMap.get(name);
	}
}
