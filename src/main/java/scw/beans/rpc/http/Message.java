package scw.beans.rpc.http;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import scw.common.MethodDefinition;

public final class Message implements Serializable {
	private static final long serialVersionUID = -6216471725621438749L;
	private Map<String, Object> attributeMap;
	private MethodDefinition methodDefinition;
	private Object[] args;

	protected Message() {
	};

	public Message(Method method, Object[] args) {
		this.methodDefinition = new MethodDefinition(method.getDeclaringClass(), method);
		this.args = args;
	}

	public MethodDefinition getMethodDefinition() {
		return methodDefinition;
	}

	protected void setMethodDefinition(MethodDefinition methodDefinition) {
		this.methodDefinition = methodDefinition;
	}

	public Object[] getArgs() {
		return args;
	}

	protected void setArgs(Object[] args) {
		this.args = args;
	}

	public Method getMethod() {
		try {
			return methodDefinition.getMethod();
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		}
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
