package scw.registry;

import scw.util.attribute.SimpleAttributes;

public class InstanceInfo extends SimpleAttributes<String, String> implements Cloneable {
	private final String id;
	private boolean readyOnly;

	public InstanceInfo(String id) {
		this.id = id;
	}

	public InstanceInfo(InstanceInfo instanceInfo) {
		super(instanceInfo);
		this.id = instanceInfo.id;
	}

	public String getId() {
		return id;
	}

	public boolean isReadyOnly() {
		return readyOnly;
	}

	public InstanceInfo readyOnly() {
		if (readyOnly) {
			return this;
		}

		InstanceInfo instanceInfo = new InstanceInfo(this);
		instanceInfo.readyOnly = true;
		return instanceInfo;
	}

	@Override
	public InstanceInfo clone() {
		return new InstanceInfo(this);
	}
}
