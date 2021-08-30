package io.basc.framework.hessian;

import java.math.BigDecimal;

import com.caucho.hessian.io.BigDecimalDeserializer;
import com.caucho.hessian.io.SerializerFactory;
import com.caucho.hessian.io.StringValueSerializer;

import io.basc.framework.util.ClassUtils;

public class DefaultSerializerFactory extends SerializerFactory {

	public DefaultSerializerFactory() {
		this(ClassUtils.getDefaultClassLoader());
	}

	public DefaultSerializerFactory(ClassLoader classLoader) {
		super(classLoader);
		addFactory(new HessianAddSerializerFactory(BigDecimal.class,
				new StringValueSerializer(), new BigDecimalDeserializer()));
	}
}
