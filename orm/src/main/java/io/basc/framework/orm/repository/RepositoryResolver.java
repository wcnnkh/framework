package io.basc.framework.orm.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.mapper.Mapping;
import io.basc.framework.orm.ObjectRelationalFactory;
import io.basc.framework.orm.OrmException;
import io.basc.framework.orm.Property;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.Elements;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.Pair;
import io.basc.framework.util.Processor;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.comparator.Sort;

public interface RepositoryResolver extends ObjectRelationalFactory {
	
}
