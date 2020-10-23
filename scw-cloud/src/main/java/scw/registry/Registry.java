package scw.registry;

import java.util.List;

import scw.event.BasicEventRegister;

public interface Registry extends BasicEventRegister<RegistryEvent> {
	List<InstanceInfo> getInstanceInfo(String id);

	void register(InstanceInfo instanceInfo);
}
