package run.soeasy.framework.core.transform.indexed;

@FunctionalInterface
public interface PropertyTemplate extends IndexedTemplate<IndexedDescriptor> {

	public static class EmptyPropertyTemplate extends EmptyIndexedTemplate<IndexedDescriptor>
			implements PropertyTemplate {
		private static final long serialVersionUID = 1L;
		private static final PropertyTemplate EMPTY_PROPERTY_TEMPLATE = new EmptyPropertyTemplate();

	}

	public static PropertyTemplate empty() {
		return EmptyPropertyTemplate.EMPTY_PROPERTY_TEMPLATE;
	}

	@FunctionalInterface
	public static interface PropertyTemplateWrapper<W extends PropertyTemplate>
			extends PropertyTemplate, IndexedTemplateWrapper<IndexedDescriptor, W> {
	}
}
