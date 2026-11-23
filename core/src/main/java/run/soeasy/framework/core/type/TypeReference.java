package run.soeasy.framework.core.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

public abstract class TypeReference<T> {

	private final Type type;
	private volatile Class<?> rawType;

	protected TypeReference() {
		Type superType = getClass().getGenericSuperclass();
		if (!(superType instanceof ParameterizedType)) {
			throw new IllegalArgumentException("必须使用匿名子类 new TypeReference<XXX>(){}");
		}
		ParameterizedType parameterizedType = (ParameterizedType) superType;
		this.type = parameterizedType.getActualTypeArguments()[0];
	}

	public Type getType() {
		return type;
	}

	public Class<?> getRawType() {
		Class<?> rt = rawType;
		if (rt == null) {
			if (type instanceof Class) {
				rt = (Class<?>) type;
			} else if (type instanceof ParameterizedType) {
				ParameterizedType pt = (ParameterizedType) type;
				rt = (Class<?>) pt.getRawType();
			} else {
				throw new IllegalStateException("无法获取rawType, type: " + type);
			}
			rawType = rt;
		}
		return rt;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		TypeReference<?> that = (TypeReference<?>) o;
		return Objects.equals(type, that.type);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type);
	}

	@Override
	public String toString() {
		return "TypeReference{type=" + type + "}";
	}
}
