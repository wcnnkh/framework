package shuchaowen.core.beans;

public class BeanMethodParameter {
	private final TParameterType type;
	private final Class<?> parameterType;
	private final String name;//可能为空
	private final String value;
	
	public BeanMethodParameter(TParameterType type, Class<?> parameterType, String name, String value){
		this.type = type;
		this.parameterType = parameterType;
		this.name = name;
		this.value = value;
	}
	
	public TParameterType getType() {
		return type;
	}
	public String getName() {
		return name;
	}
	public String getValue() {
		return value;
	}

	public Class<?> getParameterType() {
		return parameterType;
	}
}
