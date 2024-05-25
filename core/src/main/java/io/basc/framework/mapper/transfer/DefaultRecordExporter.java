package io.basc.framework.mapper.transfer;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.Parameter;
import io.basc.framework.execution.Parameters;
import io.basc.framework.execution.Setter;
import io.basc.framework.mapper.entity.FieldDescriptor;
import io.basc.framework.mapper.entity.Mapping;
import io.basc.framework.util.ClassUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultRecordExporter extends AbstractRecordExporter {
	@NonNull
	private final TypeDescriptor typeDescriptor;
	@NonNull
	private final Exporter exporter;

	@SuppressWarnings("unchecked")
	@Override
	public void doWriteParameters(Parameters record) throws IOException {
		if (getMapper().canConvert(record.getClass(), typeDescriptor)) {
			Object target = getMapper().convert(record, typeDescriptor);
			exporter.doWrite(target, typeDescriptor);
		} else if (typeDescriptor.isArray()) {
			TypeDescriptor elementTypeDescriptor = typeDescriptor.getElementTypeDescriptor();
			Parameter[] parameters = record.getElements().toArray(Parameter[]::new);
			Object array = Array.newInstance(elementTypeDescriptor.getType(), parameters.length);
			for (int i = 0; i < parameters.length; i++) {
				Object value = unpacking(parameters[i], elementTypeDescriptor);
				Array.set(array, i, value);
			}
			exporter.doWrite(array, typeDescriptor);
		} else if (typeDescriptor.isCollection()) {
			TypeDescriptor elementTypeDescriptor = typeDescriptor.getElementTypeDescriptor();
			Collection<Object> collection = (Collection<Object>) getMapper().newInstance(typeDescriptor);
			for (Parameter parameter : record.getElements()) {
				Object value = unpacking(parameter, elementTypeDescriptor);
				collection.add(value);
			}
			exporter.doWrite(collection, typeDescriptor);
		} else if (typeDescriptor.isMap()) {
			TypeDescriptor keyTypeDescriptor = typeDescriptor.getMapKeyTypeDescriptor();
			TypeDescriptor valueTypeDescriptor = typeDescriptor.getMapValueTypeDescriptor();
			Map<Object, Object> map = (Map<Object, Object>) getMapper().newInstance(typeDescriptor);
			if (ClassUtils.isInt(keyTypeDescriptor.getType())) {
				int index = 0;
				for (Parameter parameter : record.getElements()) {
					Object key = index++;
					Object value = unpacking(parameter, valueTypeDescriptor);
					map.put(key, value);
				}
			} else {
				for (Parameter parameter : record.getElements()) {
					Object key = getMapper().convert(parameter.getName(), keyTypeDescriptor);
					Object value = unpacking(parameter, valueTypeDescriptor);
					map.put(key, value);
				}
			}
			exporter.doWrite(map, typeDescriptor);
		} else if (getMapper().isEntity(typeDescriptor)) {
			Mapping<?> mapping = getMapper().getMapping(typeDescriptor.getType());
			Object entity = getMapper().newInstance(typeDescriptor);
			List<Parameter> parameters = new ArrayList<>(record.getElements().toList());
			for (FieldDescriptor fieldDescriptor : mapping.getElements()) {
				if (!fieldDescriptor.isSupportSetter()) {
					continue;
				}
				Setter setter = fieldDescriptor.setter();
				Iterator<Parameter> iterator = parameters.iterator();

				while (iterator.hasNext()) {
					Parameter parameter = iterator.next();
					if (parameter.getName().equals(fieldDescriptor.getName())
							|| parameter.getAliasNames().contains(fieldDescriptor.getName())
							|| fieldDescriptor.getAliasNames().contains(parameter.getName())
							|| fieldDescriptor.getAliasNames().anyMatch(parameter.getAliasNames(), String::equals)) {
						Object value = unpacking(parameter, setter.getTypeDescriptor());
						setter.set(entity, value);
						iterator.remove();
						break;
					}
				}
			}
			exporter.doWrite(entity, typeDescriptor);
		}
	}

	@Override
	public void flush() throws IOException {
		try {
			exporter.flush();
		} finally {
			super.flush();
		}
	}

	public Object unpacking(Parameter parameter, TypeDescriptor targetTypeDescriptor) {
		return parameter.convert(targetTypeDescriptor, getMapper());
	}
}
