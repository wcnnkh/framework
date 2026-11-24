package run.soeasy.framework.core.mapping.property;

class NamedPropertyAccessor<W extends PropertyAccessor> extends NamedAccessibleDescriptor<W>
		implements PropertyAccessor, PropertyAccessorWrapper<W> {

	public NamedPropertyAccessor(W source, String name) {
		super(source, name);
	}

	@Override
	public String getName() {
		return super.getName();
	}

	@Override
	public PropertyAccessor rename(String name) {
		return new NamedPropertyAccessor<>(getSource(), name);
	}
}
