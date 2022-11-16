package io.basc.framework.dom;

import java.io.IOException;

import org.w3c.dom.Document;

import io.basc.framework.io.Resource;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Processor;

public interface DocumentParser {
	boolean canParse(Resource resource);

	/**
	 * @param <T>
	 * @param <E>
	 * @param resource
	 * @param processor 对dom的解析操作应该在这里执行，因为很多时候都是先关闭流的,但对于流式解析器来说这意味着无法进行后续解析
	 * @return
	 * @throws E
	 */
	@Nullable
	<T, E extends Throwable> T parse(Resource resource, Processor<? super Document, ? extends T, ? extends E> processor)
			throws IOException, E;
}
