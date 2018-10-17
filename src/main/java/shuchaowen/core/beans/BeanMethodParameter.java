package shuchaowen.core.beans;

public class BeanMethodParameter implements Cloneable{
	private final EParameterType type;
	private Class<?> parameterType;
	private final String name;//可能为空
	private final String value;
	
	public BeanMethodParameter(EParameterType type, Class<?> parameterType, String name, String value){
		this.type = type;
		this.parameterType = parameterType;
		this.name = name;
		this.value = value;
	}
	
	@Override
	public BeanMethodParameter clone(){
		try {
			return (BeanMethodParameter) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public EParameterType getType() {
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

	public void setParameterType(Class<?> parameterType) {
		this.parameterType = parameterType;
	}
}
