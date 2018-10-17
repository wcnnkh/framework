package shuchaowen.core.beans;

public class BeanProperties {
	private final EParameterType type;
	private final String name;
	private final String value;
	
	public BeanProperties(EParameterType type, String name, String value){
		this.type = type;
		this.name = name;
		this.value = value;
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
}
