package scw.mapper;

import java.io.Serializable;

public class FieldMetadata implements Serializable {
	public static final FieldMetadata[] EMPTY_ARRAY = new FieldMetadata[0];
	private static final long serialVersionUID = 1L;
	private final Getter getter;
	private final Setter setter;

	public FieldMetadata(Getter getter, Setter setter) {
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
