package scw.amqp;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class Declare implements Serializable {
	private static final long serialVersionUID = 1L;
	private Map<String, Object> arguments;
	private boolean durable = true;// 默认是持久化的
	private boolean autoDelete;
	private String name;

	public Declare(String name) {
		this.name = name;
	}

	public Declare setName(String name) {
		this.name = name;
		return this;
	}

	public Map<String, Object> getArguments() {
		return arguments;
	}

	public Declare setArguments(Map<String, Object> arguments) {
		this.arguments = arguments;
		return this;
	}

	public boolean isDurable() {
		return durable;
	}

	public Declare setDurable(boolean durable) {
		this.durable = durable;
		return this;
	}

	public boolean isAutoDelete() {
		return autoDelete;
	}

	public Declare setAutoDelete(boolean autoDelete) {
		this.autoDelete = autoDelete;
		return this;
	}

	public final String getName() {
		return name;
	}

	public Declare removeArgument(String name) {
		if (arguments != null) {
			arguments.remove(name);
		}
		return this;
	}

	public Declare setArgument(String name, Object value) {
		if (arguments == null) {
			arguments = new HashMap<String, Object>(4);
		}

		arguments.put(name, value);
		return this;
	}
}
