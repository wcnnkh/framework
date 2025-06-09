package run.soeasy.framework.core.io;

import lombok.Data;
import lombok.NonNull;

@Data
public class RenamedResource<W extends Resource> implements ResourceWrapper<W> {
	@NonNull
	private final String name;
	@NonNull
	private final W source;
}