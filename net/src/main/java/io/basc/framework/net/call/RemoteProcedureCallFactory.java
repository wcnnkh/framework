package io.basc.framework.net.call;

import io.basc.framework.core.execution.Function;
import io.basc.framework.net.client.ClientRequest;
import lombok.NonNull;

public interface RemoteProcedureCallFactory {
	ClientRequest createRequest(@NonNull Function function, @NonNull Object... args);
}
