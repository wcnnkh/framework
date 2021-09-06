package io.basc.framework.dom;

import java.io.IOException;

import org.w3c.dom.Document;

import io.basc.framework.io.Resource;
import io.basc.framework.util.stream.Processor;

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
	<T, E extends Throwable> T parse(Resource resource, Processor<Document, ? extends T, E> processor)
			throws IOException, E;
}
