package io.basc.framework.value;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.element.Elements;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ObjectProperty extends ObjectValue implements Property, Cloneable {
	private static final long serialVersionUID = 1L;
	private String name;
	private int positionIndex = -1;
	private Elements<String> aliasNames;

	public ObjectProperty(String name, Object value) {
		this(name, value, null);
	}

	public ObjectProperty(String name, Value value) {
		this(name, value.getSource(), value.getTypeDescriptor());
	}

	public ObjectProperty(String name, Object value, TypeDescriptor typeDescriptor) {
		super(value, typeDescriptor);
		this.name = name;
	}

	@Override
	public ObjectProperty clone() {
		return new ObjectProperty(name, this);
	}

	@Override
	public Property rename(String name) {
		ObjectProperty property = new ObjectProperty(name, this);
		property.positionIndex = this.positionIndex;
		return property;
	}

	@Override
	public boolean isValid() {
		return StringUtils.isNotEmpty(name);
	}

	public Elements<String> getAliasNames() {
		return aliasNames == null ? Elements.empty() : aliasNames;
	}
}
