package scw.transaction;

public class TransactionConfig {
	private boolean active;
	private boolean debug;

	public TransactionConfig(boolean active, boolean debug) {
		this.active = active;
		this.debug = debug;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}
}
