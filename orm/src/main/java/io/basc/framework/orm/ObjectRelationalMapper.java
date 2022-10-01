package io.basc.framework.orm;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.mapper.ObjectMapper;

public interface ObjectRelationalMapper extends ObjectRelationalFactory, ObjectMapper<Object, ConversionException> {
}
