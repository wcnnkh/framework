package scw.core.reflect;

import java.io.Serializable;

public class Field implements Serializable {
	private static final long serialVersionUID = 1L;
	private final Getter getter;
	private final Setter setter;

	public Field(Getter getter, Setter setter) {
		this.getter = getter;
		this.setter = setter;
	}

	public Getter getGetter() {
		return getter;
	}

	public Setter getSetter() {
		return setter;
	}

	public boolean isSupportGetter() {
		return getter != null;
	}

	public boolean isSupportSetter() {
		return setter != null;
	}
}
