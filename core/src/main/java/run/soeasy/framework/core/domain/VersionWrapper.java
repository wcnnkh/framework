package run.soeasy.framework.core.domain;

import lombok.NonNull;

public interface VersionWrapper<W extends Version> extends Version, ValueWrapper<W> {

	@Override
	default int compareTo(@NonNull Value other) {
		return getSource().compareTo(other);
	}

	@Override
	default Version join(@NonNull Version version) {
		return getSource().join(version);
	}

	@Override
	default Version getAsVersion() {
		return getSource().getAsVersion();
	}
}