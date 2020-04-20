package scw.core.instance.definition;

public class InstanceDefinitionAccessor implements InstanceDefinitionAware {
	private transient InstanceDefinition instanceDefinition;

	public InstanceDefinition getInstanceDefinition() {
		return instanceDefinition;
	}

	public void setInstanceDefinition(InstanceDefinition instanceDefinition) {
		this.instanceDefinition = instanceDefinition;
	}
}
