package scw.beans.tcc;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import scw.beans.annotaion.Stage;
import scw.common.MethodDefinition;
import scw.common.exception.AlreadyExistsException;
import scw.common.utils.StringUtils;

class ClassTCC {
	private Map<String, MethodDefinition> tccMethodMap;
	private final Class<?> clz;

	public ClassTCC(Class<?> clz) {
		this.clz = clz;
		for (Method method : clz.getMethods()) {
			Stage stage = method.getAnnotation(Stage.class);
			if (stage != null) {
				if (tccMethodMap == null) {
					tccMethodMap = new HashMap<String, MethodDefinition>();
				}

				String name = StringUtils.isEmpty(stage.name()) ? method.getName() : stage.name();

				if (tccMethodMap.containsKey(name)) {
					throw new AlreadyExistsException(clz.getName() + "存在相同的TCC配置,name=" + name);
				}

				tccMethodMap.put(name, new MethodDefinition(clz, method));
			}
		}
	}

	public Class<?> getClz() {
		return clz;
	}

	public MethodDefinition getMethodDefinition(String name) {
		return tccMethodMap == null ? null : tccMethodMap.get(name);
	}
}
