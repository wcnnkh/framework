package shuchaowen.core.beans;

public class BeanProperties {
	private final TParameterType type;
	private final boolean setter;//如果存在setter方法是否调用setter方法
	private final String name;//可能为空
	private final String value;
	
	public BeanProperties(TParameterType type, boolean setter, String name, String value){
		this.type = type;
		this.setter = setter;
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

	public boolean isSetter() {
		return setter;
	}
}
