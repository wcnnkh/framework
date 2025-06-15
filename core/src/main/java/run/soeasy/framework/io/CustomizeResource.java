package run.soeasy.framework.io;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(of = "description")
public class CustomizeResource implements Resource {
	private String name;
	private String description;

	@Override
	public boolean exists() {
		return isReadable() || isWritable();
	}
}
