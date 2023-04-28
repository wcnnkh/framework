package io.basc.framework.mapper;

import java.util.Iterator;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.lang.CircularDependencyException;
import io.basc.framework.util.Elements;
import lombok.ToString;

@ToString
public class DefaultField implements Field, Cloneable {
	private volatile String name;
	private volatile TypeDescriptor typeDescriptor;
	private Elements<? extends Getter> getters;
	private Elements<? extends Setter> setters;
	private Field parent;
	private int nameNestingDepth = -1;
	private String nameNestingConnector = "_";
	private Boolean nullable;

	public DefaultField() {
	}

	public DefaultField(DefaultField field) {
		this.name = field.name;
		this.typeDescriptor = field.typeDescriptor;
		this.getters = field.getters;
		this.parent = field.parent;
		this.nameNestingConnector = field.nameNestingConnector;
		this.nameNestingDepth = field.nameNestingDepth;
		this.nullable = field.nullable;
	}

	@Override
	public DefaultField clone() {
		return new DefaultField(this);
	}

	public final int getNameNestingDepth() {
		return nameNestingDepth;
	}

	public void setNameNestingDepth(int nameNestingDepth) {
		this.nameNestingDepth = nameNestingDepth;
	}

	public final String getNameNestingConnector() {
		return nameNestingConnector;
	}

	public void setNameNestingConnector(String nameNestingConnector) {
		this.nameNestingConnector = nameNestingConnector;
	}

	@Override
	public Elements<? extends Getter> getGetters() {
		return getters == null ? Elements.empty() : getters;
	}

	public Field getParent() {
		return parent;
	}

	public void setParent(Field parent) {
		if (parent != null && parent == this) {
			throw new CircularDependencyException();
		}
		this.parent = parent;
	}

	@Override
	public Elements<? extends Setter> getSetters() {
		// TODO 重新定义解析名
		return setters == null ? Elements.empty() : setters;
	}

	@Override
	public String getName() {
		if (name == null) {
			synchronized (this) {
				if (name == null) {
					String name = Field.super.getName();
					if (hasParent() && this.nameNestingDepth > 0) {
						StringBuilder sb = new StringBuilder();
						Iterator<Field> parents = parents().reverse().iterator();
						int i = 0;
						while (parents.hasNext() && (i++ < this.nameNestingDepth)) {
							Field parent = parents.next();
							sb.append(parent.getName());
							sb.append(this.nameNestingConnector);
						}

						sb.append(name);
						return sb.toString();
					}
					this.name = name.toString();
				}
			}
		}
		return name;
	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		if (typeDescriptor == null) {
			synchronized (this) {
				if (typeDescriptor == null) {
					this.typeDescriptor = Field.super.getTypeDescriptor();
				}
			}
		}
		return typeDescriptor;
	}

	@Override
	public Field rename(String name) {
		DefaultField field = new DefaultField(this);
		field.name = name;
		return field;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTypeDescriptor(TypeDescriptor typeDescriptor) {
		this.typeDescriptor = typeDescriptor;
	}

	public void setGetters(Elements<? extends Getter> getters) {
		this.getters = getters;
	}

	public void setSetters(Elements<? extends Setter> setters) {
		this.setters = setters;
	}

	public boolean isNullable() {
		if (nullable == null) {
			synchronized (this) {
				if (nullable == null) {
					nullable = Field.super.isNullable();
				}
			}
		}
		return nullable;
	}

	public void setNullable(Boolean nullable) {
		this.nullable = nullable;
	}
}
