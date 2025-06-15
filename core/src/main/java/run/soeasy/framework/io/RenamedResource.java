package run.soeasy.framework.io;

import lombok.Data;
import lombok.NonNull;

@Data
class RenamedResource<W extends Resource> implements ResourceWrapper<W> {
	@NonNull
	private final W source;
	@NonNull
	private final String name;

	@Override
	public Resource rename(String name) {
		return new RenamedResource<>(this.source, name);
	}
}