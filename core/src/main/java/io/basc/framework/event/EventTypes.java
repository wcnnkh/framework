package io.basc.framework.event;

public enum EventTypes implements EventType {
	CREATE("CREATE"),
	UPDATE("UPDATE"),
	DELETE("DELETE"),
	;

	private final String name;

	EventTypes(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
}
