package run.soeasy.framework.net.call;

import lombok.NonNull;
import run.soeasy.framework.core.execution.Function;
import run.soeasy.framework.net.client.ClientRequest;

public interface RemoteProcedureCallFactory {
	ClientRequest createRequest(@NonNull Function function, @NonNull Object... args);
}
