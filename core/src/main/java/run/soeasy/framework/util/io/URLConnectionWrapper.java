package run.soeasy.framework.util.io;

import java.io.IOException;
import java.net.URLConnection;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.util.function.Pipeline;
import run.soeasy.framework.util.function.Pipeline.PipelineWrapper;

@RequiredArgsConstructor
@Getter
public class URLConnectionWrapper<T extends URLConnection>
		implements PipelineWrapper<T, IOException, Pipeline<T, IOException>> {
	@NonNull
	private final Pipeline<T, IOException> source;
}
