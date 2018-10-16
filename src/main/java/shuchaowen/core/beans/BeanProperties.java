package shuchaowen.core.beans;

public class BeanProperties {
	private final TParameterType type;
	private final String name;//可能为空
	private final String value;
	
	public BeanProperties(TParameterType type, String name, String value){
		this.type = type;
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
}
