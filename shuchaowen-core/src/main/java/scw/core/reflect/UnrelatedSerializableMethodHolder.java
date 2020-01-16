package scw.core.reflect;

import java.io.Serializable;
import java.util.Arrays;

import scw.core.utils.ClassUtils;
import scw.lang.NestedRuntimeException;

public class UnrelatedSerializableMethodHolder extends AbstractSerializableMethodHolder
		implements MethodHolder, Serializable {
	private static final long serialVersionUID = 1L;
	private String belongClassName;
	private String[] parameterTypeNames;

	/**
	 * 用于序列化
	 */
	@SuppressWarnings("unused")
	private UnrelatedSerializableMethodHolder() {
		this(null, null, null);
	}

	public UnrelatedSerializableMethodHolder(String belongClassName, String methodName, String[] parameterTypeNames) {
		super(methodName);
		this.belongClassName = belongClassName;
		this.parameterTypeNames = parameterTypeNames;
	}

	public String getBelongClassName() {
		return belongClassName;
	}

	public String[] getParameterTypeNames() {
		return parameterTypeNames;
	}

	private transient Class<?> belongClass;

	public Class<?> getBelongClass() {
		if (belongClass == null) {
			try {
				belongClass = ClassUtils.forName(getBelongClassName());
			} catch (ClassNotFoundException e) {
				throw new NestedRuntimeException(getBelongClassName(), e);
			}
		}
		return belongClass;
	}

	private transient Class<?>[] parameterTypes;

	public Class<?>[] getParameterTypes() {
		if (parameterTypes == null) {
			try {
				this.parameterTypes = ClassUtils.forNames(getParameterTypeNames());
			} catch (ClassNotFoundException e) {
				throw new NestedRuntimeException(Arrays.toString(getParameterTypeNames()), e);
			}
		}
		return parameterTypes;
	}
}
