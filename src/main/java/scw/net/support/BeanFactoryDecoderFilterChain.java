package scw.net.support;

import java.lang.reflect.Type;
import java.net.URLConnection;
import java.util.Collection;
import java.util.Iterator;

import scw.beans.BeanFactory;
import scw.core.exception.NotFoundException;
import scw.core.utils.CollectionUtils;
import scw.net.DecoderFilter;
import scw.net.DecoderFilterChain;

public final class BeanFactoryDecoderFilterChain implements DecoderFilterChain {
	private BeanFactory beanFactory;
	private Iterator<String> iterator;

	public BeanFactoryDecoderFilterChain(BeanFactory beanFactory, Collection<String> collection) {
		if (!CollectionUtils.isEmpty(collection)) {
			this.beanFactory = beanFactory;
			this.iterator = collection.iterator();
		}
	}

	public Object doDecode(URLConnection urlConnection, Type type) throws Exception {
		if (iterator == null) {
			throw new NotFoundException("not found DeserializerFilter：" + type);
		}

		if (iterator.hasNext()) {
			DecoderFilter filter = beanFactory.getInstance(iterator.next());
			return filter.decode(urlConnection, type, this);
		}
		throw new NotFoundException("not found DeserializerFilter：" + type);
	}

}
