package scw.beans.rpc.http;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import scw.core.reflect.SerializableMethodDefinition;

public final class Message implements Serializable {
	private static final long serialVersionUID = 1L;
	private Map<String, Object> attributeMap;
	private SerializableMethodDefinition methodDefinition;
	private Object[] args;

	protected Message() {
	};

	public Message(Class<?> clz, Method method, Object[] args) {
		this.methodDefinition = new SerializableMethodDefinition(clz, method);
		this.args = args;
	}

	public SerializableMethodDefinition getMethodDefinition() {
		return methodDefinition;
	}

	protected void setMethodDefinition(SerializableMethodDefinition methodDefinition) {
		this.methodDefinition = methodDefinition;
	}

	public Object[] getArgs() {
		return args;
	}

	protected void setArgs(Object[] args) {
		this.args = args;
	}

	public Method getMethod() {
		return methodDefinition.getMethod();
	}

	private transient String messageKey;

	public String getMessageKey() {
		if (messageKey == null) {
			this.messageKey = getMethod().toString();
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
