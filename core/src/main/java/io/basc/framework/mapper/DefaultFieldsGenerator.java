package io.basc.framework.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;

import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.util.Elements;
import io.basc.framework.util.LinkedMultiValueMap;

/**
 * 默认的字段生成器
 * 
 * @author wcnnkh
 *
 */
public final class DefaultFieldsGenerator implements Function<Class<?>, Elements<DefaultField>> {
	public static final DefaultFieldsGenerator DEFAULT = new DefaultFieldsGenerator();

	@Override
	public Elements<DefaultField> apply(Class<?> sourceClass) {
		// 相同的字段名视为同一个getter
		LinkedMultiValueMap<String, Getter> getterMap = new LinkedMultiValueMap<>();
		LinkedMultiValueMap<String, Setter> setterMap = new LinkedMultiValueMap<>();

		for (Field field : ReflectionUtils.getDeclaredFields(sourceClass).getElements()) {
			Getter getter = new FieldGetter(field);
			getterMap.add(getter.getName(), getter);
			Setter setter = new FieldSetter(field);
			setterMap.add(setter.getName(), setter);
		}

		for (Method method : ReflectionUtils.getDeclaredMethods(sourceClass).all().getElements()) {
			if (method.getName().startsWith(MethodSetter.METHOD_PREFIX)) {
				if (method.getParameterCount() == 1) {
					Setter setter = new MethodSetter(method);
					setterMap.add(setter.getName(), setter);
				}
			} else if (method.getName().startsWith(MethodGetter.METHOD_PREFIX)) {
				if (method.getParameterCount() == 0) {
					Getter getter = new MethodGetter(method);
					getterMap.add(getter.getName(), getter);
				}
			} else if (method.getReturnType() == boolean.class && method.getParameterCount() == 0
					&& method.getName().startsWith(MethodGetter.BOOLEAN_METHOD_PREFIX)) {
				Getter getter = new MethodGetter(method);
				getterMap.add(getter.getName(), getter);
			}
		}

		// 开始组装field
		List<DefaultField> list = new ArrayList<>(Math.max(getterMap.size(), setterMap.size()));
		// 以getter为主导
		for (Entry<String, List<Getter>> entry : getterMap.entrySet()) {
			// 将对应的setter移除
			List<Setter> setters = setterMap.remove(entry.getKey());
			DefaultField field = new DefaultField(entry.getKey());
			field.setGetters(Elements.of(entry.getValue()));
			if (setters != null) {
				field.setSetters(Elements.of(setters));
			}
			list.add(field);
		}

		// 剩下的setter都是没有找到对应getter
		for (Entry<String, List<Setter>> entry : setterMap.entrySet()) {
			DefaultField field = new DefaultField(entry.getKey());
			field.setSetters(Elements.of(entry.getValue()));
		}
		return Elements.of(list);
	}

}
