package scw.beans.rpc.http;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public final class Message implements Serializable {
	private static final long serialVersionUID = -6216471725621438749L;
	private Map<String, Object> attributeMap;
	private Class<?> clz;
	private String name;
	private Class<?>[] types;
	private Object[] args;

	private transient volatile String messageKey;

	protected Message() {
	};

	public Message(Method method, Object[] args) {
		this.clz = method.getDeclaringClass();
		this.name = method.getName();
		this.types = method.getParameterTypes();
		this.args = args;
	}

	public Class<?> getClz() {
		return clz;
	}

	public void setClz(Class<?> clz) {
		this.clz = clz;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class<?>[] getTypes() {
		return types;
	}

	public void setTypes(Class<?>[] types) {
		this.types = types;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public Method getMethod() throws NoSuchMethodException, SecurityException {
		return clz.getMethod(name, types);
	}

	public String getMessageKey() {
		if (messageKey == null) {
			synchronized (this) {
				StringBuilder sb = new StringBuilder();
				sb.append(clz.getName());
				sb.append(name);
				if (types.length != 0) {
					for (Class<?> type : types) {
						sb.append("," + type.getName());
					}
				}

				this.messageKey = sb.toString();
			}
		}
		return messageKey;
	}

	public Object getAttribute(String name) {
		if (attributeMap == null) {
			return null;
		}

		return attributeMap.get(name);
	}

	public void setAttribute(String name, Object value) {
		if (attributeMap == null) {
			attributeMap = new HashMap<String, Object>();
		}
		attributeMap.put(name, value);
	}

	public Map<String, Object> getAttributeMap() {
		return attributeMap;
	}

	public void setAttributeMap(Map<String, Object> attributeMap) {
		this.attributeMap = attributeMap;
	}
}
