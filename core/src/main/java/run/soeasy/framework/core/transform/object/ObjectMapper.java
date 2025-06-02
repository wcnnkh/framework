package run.soeasy.framework.core.transform.object;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.transform.property.PropertyAccessor;
import run.soeasy.framework.core.transform.property.PropertyMapper;
import run.soeasy.framework.core.transform.property.PropertyTemplate;
import run.soeasy.framework.core.transform.property.TypedProperties;
import run.soeasy.framework.core.transform.templates.MappingFilter;
import run.soeasy.framework.core.type.ClassMembers;
import run.soeasy.framework.core.type.ClassMembersLoader;

@Getter
@Setter
public class ObjectMapper<E extends Property> extends PropertyMapper
		implements ClassMemberTemplateFactory<E>, ObjectTemplateFactory<E> {
	private final ClassMemberTemplateRegistry<E> classPropertyTemplateRegistry = new ClassMemberTemplateRegistry<>();
	private final ObjectTemplateRegistry<E> objectTemplateRegistry = new ObjectTemplateRegistry<>();

	@Override
	public boolean hasObjectTemplate(Class<?> objectClass) {
		return objectTemplateRegistry.hasObjectTemplate(objectClass);
	}

	@Override
	public PropertyTemplate<E> getObjectTemplate(Class<?> objectClass) {
		return objectTemplateRegistry.getObjectTemplate(objectClass);
	}

	@Override
	public boolean hasMapping(@NonNull TypeDescriptor requiredType) {
		return hasObjectTemplate(requiredType.getType()) || super.hasMapping(requiredType);
	}

	@Override
	public TypedProperties getMapping(@NonNull Object source, @NonNull TypeDescriptor requiredType) {
		PropertyTemplate<E> propertyTemplate = getObjectTemplate(requiredType.getType());
		if (propertyTemplate != null) {
			return new ObjectProperties<>(propertyTemplate, source);
		}
		return super.getMapping(source, requiredType);
	}

	@Override
	public boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		if ((hasClassPropertyTemplate(sourceTypeDescriptor.getType()) && hasMapping(targetTypeDescriptor))
				|| (hasMapping(sourceTypeDescriptor) && hasClassPropertyTemplate(targetTypeDescriptor.getType()))
				|| (hasClassPropertyTemplate(sourceTypeDescriptor.getType())
						&& hasClassPropertyTemplate(targetTypeDescriptor.getType()))) {
			return true;
		}
		return super.canTransform(sourceTypeDescriptor, targetTypeDescriptor);
	}

	private TypedProperties createTypedProperties(ClassMembers<E> classMembers, Object object) {
		PropertyTemplate<E> sourcePropertyTemplate = () -> classMembers.iterator();
		sourcePropertyTemplate = sourcePropertyTemplate.asMap(false);
		ObjectProperties<E, PropertyTemplate<E>> objectProperties = new ObjectProperties<>(sourcePropertyTemplate,
				object);
		System.out.println(classMembers.getDeclaringClass() + "," + classMembers.count());
		return objectProperties;
	}

	private boolean doClassMembersMapping(Object source, Map<Class<?>, ClassMembers<E>> sourceMap, Object target,
			Map<Class<?>, ClassMembers<E>> targetMap,
			Iterable<MappingFilter<Object, PropertyAccessor, TypedProperties>> filters) {
		boolean changed = false;
		for (Entry<Class<?>, ClassMembers<E>> entry : sourceMap.entrySet()) {
			ClassMembers<E> targetMembers = targetMap.remove(entry.getKey());
			if (targetMembers == null) {
				continue;
			}
			if (doMapping(createTypedProperties(entry.getValue(), source), createTypedProperties(targetMembers, target),
					filters)) {
				changed = true;
			}
		}
		return changed;
	}

	@Override
	public ClassMembersLoader<E> getClassPropertyTemplate(Class<?> requiredClass) {
		return classPropertyTemplateRegistry.getClassPropertyTemplate(requiredClass);
	}

	@Override
	public boolean transform(@NonNull Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull Object target, @NonNull TypeDescriptor targetTypeDescriptor,
			@NonNull Iterable<MappingFilter<Object, PropertyAccessor, TypedProperties>> filters)
			throws ConversionException {
		ClassMembersLoader<E> sourceMembersLoader = hasClassPropertyTemplate(sourceTypeDescriptor.getType())
				? getClassPropertyTemplate(sourceTypeDescriptor.getType())
				: null;
		ClassMembersLoader<E> targetMembersLoader = hasClassPropertyTemplate(targetTypeDescriptor.getType())
				? getClassPropertyTemplate(targetTypeDescriptor.getType())
				: null;
		if (sourceMembersLoader != null) {
			if (targetMembersLoader != null) {
				Map<Class<?>, ClassMembers<E>> sourceMap = sourceMembersLoader.getElements().filter((e) -> !e.isEmpty())
						.collect(Collectors.toMap((e) -> e.getDeclaringClass(), Function.identity(), (a, b) -> a,
								HashMap::new));
				Map<Class<?>, ClassMembers<E>> targetMap = sourceMembersLoader.getElements().filter((e) -> !e.isEmpty())
						.collect(Collectors.toMap((e) -> e.getDeclaringClass(), Function.identity(), (a, b) -> a,
								HashMap::new));
				boolean leftChanged = doClassMembersMapping(source, sourceMap, target, targetMap, filters);
				boolean rightChanged = doClassMembersMapping(target, targetMap, source, sourceMap, filters);
				return leftChanged || rightChanged;
			} else if (hasMapping(targetTypeDescriptor)) {
				TypedProperties targetMapping = getMapping(target, targetTypeDescriptor);
				boolean changed = false;
				for (ClassMembers<E> classMembers : sourceMembersLoader.getElements()) {
					if (doMapping(createTypedProperties(classMembers, source), targetMapping, filters)) {
						changed = true;
					}
				}
				return changed;
			}
		} else {
			if (targetMembersLoader != null && hasMapping(sourceTypeDescriptor)) {
				TypedProperties sourceMapping = getMapping(source, sourceTypeDescriptor);
				boolean changed = false;
				for (ClassMembers<E> classMembers : targetMembersLoader.getElements()) {
					if (doMapping(sourceMapping, createTypedProperties(classMembers, target), filters)) {
						changed = true;
					}
				}
				return changed;
			}
		}
		return super.transform(source, sourceTypeDescriptor, target, targetTypeDescriptor, filters);
	}
}
