package run.soeasy.framework.core.transform.property;

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
import run.soeasy.framework.core.transform.templates.DefaultMapper;
import run.soeasy.framework.core.transform.templates.MappingFilter;
import run.soeasy.framework.core.type.ClassMembers;
import run.soeasy.framework.core.type.ClassMembersLoader;

/**
 * 属性映射器，继承自{@link DefaultMapper}并实现{@link ClassMemberTemplateFactory}和{@link ObjectTemplateFactory}，
 * 用于实现对象属性之间的映射转换，支持复杂类型转换、子类父类属性映射和自定义过滤逻辑。
 * <p>
 * 该类整合了类成员模板注册表和对象模板注册表，提供灵活的属性映射能力，
 * 可处理包含继承关系的类结构，解决子类和父类存在相同字段时的映射问题。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>模板缓存：使用注册表缓存类成员和对象模板，提高映射效率</li>
 *   <li>继承结构处理：支持处理包含多层继承关系的类属性映射</li>
 *   <li>自定义过滤：通过{@link MappingFilter}支持自定义映射过滤逻辑</li>
 *   <li>双向映射：支持源到目标和目标到源的双向属性映射</li>
 *   <li>类型安全：通过泛型约束保证属性类型一致性</li>
 * </ul>
 *
 * <p><b>泛型说明：</b>
 * <ul>
 *   <li>{@code E}：属性类型，需实现{@link Property}接口</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>DTO与实体类转换：在API层与数据层之间进行对象属性映射</li>
 *   <li>配置数据注入：将配置信息映射到Java对象</li>
 *   <li>数据格式转换：在不同数据模型之间进行属性映射</li>
 *   <li>ORM框架辅助：处理复杂对象关系的映射</li>
 * </ul>
 *
 * @author soeasy.run
 * @see ClassMemberTemplateFactory
 * @see ObjectTemplateFactory
 * @see ClassMemberTemplateRegistry
 * @see ObjectTemplateRegistry
 */
@Getter
@Setter
public class PropertyMapper<E extends Property> extends DefaultMapper<Object, PropertyAccessor, TypedProperties>
        implements ClassMemberTemplateFactory<E>, ObjectTemplateFactory<E> {
    
    /** 类成员模板注册表，用于缓存和管理类与类成员属性模板的映射关系 */
    private final ClassMemberTemplateRegistry<E> classPropertyTemplateRegistry = new ClassMemberTemplateRegistry<>();
    
    /** 对象模板注册表，用于缓存和管理对象类与属性模板的映射关系 */
    private final ObjectTemplateRegistry<E> objectTemplateRegistry = new ObjectTemplateRegistry<>();

    /**
     * 检查是否存在指定对象类的属性模板
     * 
     * @param objectClass 对象类，不可为null
     * @return 若存在对应的属性模板返回true，否则返回false
     */
    @Override
    public boolean hasObjectTemplate(Class<?> objectClass) {
        return objectTemplateRegistry.hasObjectTemplate(objectClass);
    }

    /**
     * 获取指定对象类的属性模板
     * 
     * @param objectClass 对象类，不可为null
     * @return 对应的属性模板，若不存在则返回null
     */
    @Override
    public PropertyTemplate<E> getObjectTemplate(Class<?> objectClass) {
        return objectTemplateRegistry.getObjectTemplate(objectClass);
    }

    /**
     * 检查是否存在指定类型的映射关系
     * 
     * @param requiredType 目标类型描述符，不可为null
     * @return 若存在映射关系返回true，否则返回false
     */
    @Override
    public boolean hasMapping(@NonNull TypeDescriptor requiredType) {
        return super.hasMapping(requiredType) || hasObjectTemplate(requiredType.getType());
    }

    /**
     * 获取指定源对象和目标类型的映射关系
     * 
     * @param source 源对象，不可为null
     * @param requiredType 目标类型描述符，不可为null
     * @return 对应的类型化属性映射，若不存在则返回null
     */
    @Override
    public TypedProperties getMapping(@NonNull Object source, @NonNull TypeDescriptor requiredType) {
        TypedProperties typedProperties = super.getMapping(source, requiredType);
        if(typedProperties == null) {
        	PropertyTemplate<E> propertyTemplate = getObjectTemplate(requiredType.getType());
            if (propertyTemplate != null) {
            	typedProperties = new ObjectProperties<>(propertyTemplate, source);
            }
        }
        return typedProperties;
    }

    /**
     * 判断是否可以在两个类型之间进行转换
     * 
     * @param sourceTypeDescriptor 源类型描述符，不可为null
     * @param targetTypeDescriptor 目标类型描述符，不可为null
     * @return 若可以转换返回true，否则返回false
     */
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

    /**
     * 创建类型化属性集合
     * 
     * @param classMembers 类成员集合
     * @param object 目标对象
     * @return 类型化属性集合
     */
    private TypedProperties createTypedProperties(ClassMembers<E> classMembers, Object object) {
        // 创建属性模板并转换为Map形式
        PropertyTemplate<E> sourcePropertyTemplate = () -> classMembers.iterator();
        sourcePropertyTemplate = sourcePropertyTemplate.asMap(false);
        
        // 创建对象属性集合
        ObjectProperties<E, PropertyTemplate<E>> objectProperties = new ObjectProperties<>(sourcePropertyTemplate, object);
        return objectProperties;
    }

    /**
     * 执行类成员之间的映射转换
     * 
     * @param source 源对象
     * @param sourceMap 源类成员映射
     * @param target 目标对象
     * @param targetMap 目标类成员映射
     * @param filters 映射过滤器集合
     * @return 若发生映射转换返回true，否则返回false
     */
    private boolean doClassMembersMapping(Object source, Map<Class<?>, ClassMembers<E>> sourceMap, Object target,
            Map<Class<?>, ClassMembers<E>> targetMap,
            Iterable<MappingFilter<Object, PropertyAccessor, TypedProperties>> filters) {
        boolean changed = false;
        
        // 遍历源类成员映射
        for (Entry<Class<?>, ClassMembers<E>> entry : sourceMap.entrySet()) {
            // 获取对应目标类成员
            ClassMembers<E> targetMembers = targetMap.remove(entry.getKey());
            if (targetMembers == null) {
                continue;
            }

            // 创建源和目标的类型化属性集合
            TypedProperties sourceProperties = createTypedProperties(entry.getValue(), source);
            TypedProperties targetProperties = createTypedProperties(targetMembers, target);
            
            // 执行映射转换
            if (doMapping(sourceProperties, targetProperties, filters)) {
                changed = true;
            }
        }
        return changed;
    }

    /**
     * 返回类结构，解决子类和父类存在相同字段时的映射问题
     * 
     * @param requiredClass 目标类，不可为null
     * @return 类成员加载器，若不存在则返回null
     */
    @Override
    public ClassMembersLoader<E> getClassPropertyTemplate(Class<?> requiredClass) {
        return classPropertyTemplateRegistry.getClassPropertyTemplate(requiredClass);
    }

    /**
     * 执行对象之间的属性映射转换
     * 
     * @param source 源对象，不可为null
     * @param sourceTypeDescriptor 源类型描述符，不可为null
     * @param target 目标对象，不可为null
     * @param targetTypeDescriptor 目标类型描述符，不可为null
     * @param filters 映射过滤器集合，不可为null
     * @return 若发生映射转换返回true，否则返回false
     * @throws ConversionException 转换过程中发生错误时抛出
     */
    @Override
    public boolean transform(@NonNull Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
            @NonNull Object target, @NonNull TypeDescriptor targetTypeDescriptor,
            @NonNull Iterable<MappingFilter<Object, PropertyAccessor, TypedProperties>> filters)
            throws ConversionException {
        // 获取源和目标的类成员加载器
        ClassMembersLoader<E> sourceMembersLoader = hasClassPropertyTemplate(sourceTypeDescriptor.getType())
                ? getClassPropertyTemplate(sourceTypeDescriptor.getType())
                : null;
        ClassMembersLoader<E> targetMembersLoader = hasClassPropertyTemplate(targetTypeDescriptor.getType())
                ? getClassPropertyTemplate(targetTypeDescriptor.getType())
                : null;
        
        // 处理源和目标都有类成员加载器的情况
        if (sourceMembersLoader != null) {
            if (targetMembersLoader != null) {
                // 构建源和目标的类成员映射
                Map<Class<?>, ClassMembers<E>> sourceMap = sourceMembersLoader.getElements().filter((e) -> !e.isEmpty())
                        .collect(Collectors.toMap((e) -> e.getDeclaringClass(), Function.identity(), (a, b) -> a,
                                HashMap::new));
                Map<Class<?>, ClassMembers<E>> targetMap = sourceMembersLoader.getElements().filter((e) -> !e.isEmpty())
                        .collect(Collectors.toMap((e) -> e.getDeclaringClass(), Function.identity(), (a, b) -> a,
                                HashMap::new));
                
                // 执行双向映射转换
                boolean leftChanged = doClassMembersMapping(source, sourceMap, target, targetMap, filters);
                boolean rightChanged = doClassMembersMapping(target, targetMap, source, sourceMap, filters);
                return leftChanged || rightChanged;
            } else if (hasMapping(targetTypeDescriptor)) {
                // 目标没有类成员加载器但有映射关系的情况
                TypedProperties targetMapping = getMapping(target, targetTypeDescriptor);
                boolean changed = false;
                
                // 遍历源类成员并执行映射
                for (ClassMembers<E> classMembers : sourceMembersLoader.getElements()) {
                    if (doMapping(createTypedProperties(classMembers, source), targetMapping, filters)) {
                        changed = true;
                    }
                }
                return changed;
            }
        } else if (targetMembersLoader != null && hasMapping(sourceTypeDescriptor)) {
            // 源没有类成员加载器但目标有且源有映射关系的情况
            TypedProperties sourceMapping = getMapping(source, sourceTypeDescriptor);
            boolean changed = false;
            
            // 遍历目标类成员并执行映射
            for (ClassMembers<E> classMembers : targetMembersLoader.getElements()) {
                if (doMapping(sourceMapping, createTypedProperties(classMembers, target), filters)) {
                    changed = true;
                }
            }
            return changed;
        }
        
        // 其他情况委托给父类处理
        return super.transform(source, sourceTypeDescriptor, target, targetTypeDescriptor, filters);
    }
}