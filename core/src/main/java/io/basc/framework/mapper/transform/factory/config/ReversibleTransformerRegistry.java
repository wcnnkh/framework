package io.basc.framework.mapper.transform.factory.config;

import io.basc.framework.mapper.transform.ReversibleTransformer;
import io.basc.framework.mapper.transform.factory.ReversibleTransformerFactory;

public interface ReversibleTransformerRegistry<S, E extends Throwable, C extends ReversibleTransformer<? super S, ? super Object, ? extends E>>
		extends TransformerRegistry<S, E, C>, ReversibleTransformerFactory<S, E> {

}
