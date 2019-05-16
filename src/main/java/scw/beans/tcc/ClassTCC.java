package scw.beans.tcc;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import scw.beans.annotation.Stage;
import scw.core.exception.AlreadyExistsException;
import scw.core.reflect.SerializableMethodDefinition;
import scw.core.utils.StringUtils;

class ClassTCC {
	private Map<String, SerializableMethodDefinition> tccMethodMap;
	private final Class<?> clz;

	public ClassTCC(Class<?> clz) {
		this.clz = clz;
		for (Method method : clz.getMethods()) {
			Stage stage = method.getAnnotation(Stage.class);
			if (stage != null) {
				if (tccMethodMap == null) {
					tccMethodMap = new HashMap<String, SerializableMethodDefinition>();
				}

				String name = StringUtils.isEmpty(stage.name()) ? method.getName() : stage.name();

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
