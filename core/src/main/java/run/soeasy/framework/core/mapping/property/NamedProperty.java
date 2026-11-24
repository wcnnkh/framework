package run.soeasy.framework.core.mapping.property;

class NamedProperty<W extends Property> extends NamedAccessibleDescriptor<W> implements Property, PropertyWrapper<W> {

	public NamedProperty(W source, String name) {
		super(source, name);
	}

	@Override
	public String getName() {
		return super.getName();
	}

	@Override
	public Property rename(String name) {
		return new NamedProperty<Property>(getSource(), name);
	}
}
