package run.soeasy.framework.core.type;

import java.util.function.Function;
import java.util.stream.Stream;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * 类成员加载器，用于递归加载类及其继承体系（父类和接口）的成员， 实现{@link Streamable}接口以支持成员的统一访问和管理。
 * 该类通过组合{@link ClassMembers}实现延迟加载，并提供递归加载父类和接口成员的能力，
 * 适用于需要完整类成员体系的反射操作、类结构分析等场景。
 *
 * <p>
 * 核心特性：
 * <ul>
 * <li>递归加载：自动加载当前类、父类和接口的成员</li>
 * <li>延迟初始化：成员数据在首次访问时加载，提高性能</li>
 * <li>可重新加载：支持强制刷新所有层级的成员数据</li>
 * <li>类型安全：通过泛型确保加载成员的类型一致性</li>
 * </ul>
 *
 * <p>
 * 使用场景：
 * <ul>
 * <li>反射工具：获取类及其继承体系的完整成员列表</li>
 * <li>ORM框架：分析实体类及其父类的字段和方法</li>
 * <li>注解处理：收集类层级结构中的所有注解信息</li>
 * <li>代码生成：根据类继承体系生成相关代码或文档</li>
 * <li>依赖分析：分析类及其依赖的接口和父类成员</li>
 * </ul>
 *
 * <p>
 * 示例用法：
 * 
 * <pre class="code">
 * // 定义方法加载器
 * Function&lt;Class&lt;?&gt;, Elements&lt;Method&gt;&gt; methodLoader = clazz -&gt; Elements.of(clazz.getDeclaredMethods());
 * 
 * // 创建类成员加载器（加载当前类、父类和接口的方法）
 * ClassMembersLoader&lt;Method&gt; methodLoader = new ClassMembersLoader&lt;&gt;(User.class, methodLoader).withAll();
 * 
 * // 遍历所有方法
 * for (Method method : methodLoader) {
 * 	System.out.println("方法: " + method.getName());
 * }
 * 
 * // 重新加载所有成员
 * methodLoader.reload();
 * </pre>
 *
 * @param <E> 类成员的类型（如Field, Method, Constructor等）
 * @see ClassMembers
 * @see Streamable
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ClassMembersLoader<E> implements Streamable<E> {
	/** 当前类的成员解析器 */
	@NonNull
	private final ClassMembers<E> classMembers;

	/** 父类的成员加载器，可能为null */
	private final ClassMembersLoader<E> superclass;

	/** 接口的成员加载器集合，可能为null */
	private final Streamable<ClassMembersLoader<E>> interfaces;

	/**
	 * 创建类成员加载器（仅加载当前类成员）。
	 * <p>
	 * 该构造函数使用指定的类和加载函数创建成员加载器， 不自动加载父类和接口成员，需通过withSuperclass/withInterfaces方法显式加载。
	 *
	 * @param declaringClass 声明成员的类对象，不可为null
	 * @param loader         成员加载函数，不可为null
	 */
	public ClassMembersLoader(Class<?> declaringClass, Function<? super Class<?>, ? extends Streamable<E>> loader) {
		this(new ClassMembers<>(declaringClass, loader));
	}

	/**
	 * 创建类成员加载器（仅加载当前类成员）。
	 * <p>
	 * 该构造函数使用已有的ClassMembers实例创建加载器， 适用于已有成员解析器的场景。
	 *
	 * @param provider 当前类的成员解析器，不可为null
	 */
	public ClassMembersLoader(ClassMembers<E> provider) {
		this(provider, null, null);
	}

	/**
	 * 保护性构造函数（用于内部递归创建）。
	 *
	 * @param classMembersLoader 已存在的类成员加载器，不可为null
	 */
	protected ClassMembersLoader(@NonNull ClassMembersLoader<E> classMembersLoader) {
		this.classMembers = classMembersLoader.classMembers;
		this.superclass = classMembersLoader.superclass;
		this.interfaces = classMembersLoader.interfaces;
	}

	/**
	 * 判断是否所有层级的成员均为空。
	 * <p>
	 * 实现逻辑：
	 * <ol>
	 * <li>当前类成员为空</li>
	 * <li>所有接口成员加载器均为空</li>
	 * <li>父类成员加载器为空或父类成员为空</li>
	 * </ol>
	 *
	 * @return true如果所有层级成员均为空，否则false
	 */
	@Override
	public boolean isEmpty() {
		return classMembers.isEmpty() && (interfaces == null || interfaces.allMatch((ClassMembersLoader::isEmpty)))
				&& (superclass == null || superclass.isEmpty());
	}

	/**
	 * 获取所有层级成员的迭代器（合并当前类、父类和接口成员）。
	 * <p>
	 * 实现逻辑：
	 * <ol>
	 * <li>使用MergedElements合并当前类、父类和接口的成员</li>
	 * <li>父类或接口为空时使用空集合</li>
	 * <li>保证迭代顺序：当前类 -&gt; 父类 -&gt; 接口</li>
	 * </ol>
	 *
	 * @return 合并后的成员
	 */
	@Override
	public Stream<E> stream() {
		return getMembers().stream();
	}

	public Streamable<E> getMembers() {
		return classMembers.concat(superclass == null ? Streamable.empty() : superclass)
				.concat(interfaces == null ? Streamable.empty() : interfaces.flatMap((e) -> e.stream()));
	}

	@Override
	public Streamable<E> reload() {
		return getMembers().reload();
	}

	/**
	 * 获取所有层级的类成员解析器集合。
	 * <p>
	 * 实现逻辑：
	 * <ol>
	 * <li>合并当前类、父类和接口的ClassMembers实例</li>
	 * <li>父类或接口为空时使用空集合</li>
	 * <li>返回的集合包含所有层级的ClassMembers实例</li>
	 * </ol>
	 *
	 * @return 合并后的ClassMembers集合
	 */
	public Streamable<ClassMembers<E>> getElements() {
		return Streamable.singleton(classMembers)
				.concat(superclass == null ? Streamable.empty() : superclass.getElements())
				.concat(interfaces == null ? Streamable.empty() : interfaces.flatMap((e) -> e.getElements().stream()));
	}

	/**
	 * 创建包含当前类和所有接口成员的加载器。
	 * <p>
	 * 实现逻辑：
	 * <ol>
	 * <li>递归处理当前类的所有接口</li>
	 * <li>每个接口创建对应的ClassMembersLoader</li>
	 * <li>保持父类加载器不变</li>
	 * </ol>
	 *
	 * @return 包含接口成员的新加载器
	 */
	public ClassMembersLoader<E> withInterfaces() {
		Streamable<ClassMembersLoader<E>> interfaces = classMembers.getInterfaces().map(ClassMembersLoader::new);
		return new ClassMembersLoader<>(classMembers, superclass == null ? superclass : superclass.withInterfaces(),
				interfaces.map(ClassMembersLoader::withInterfaces));
	}

	/**
	 * 创建包含当前类和父类成员的加载器。
	 * <p>
	 * 实现逻辑：
	 * <ol>
	 * <li>获取当前类的父类ClassMembers</li>
	 * <li>递归处理父类的父类层级</li>
	 * <li>保持接口加载器不变</li>
	 * </ol>
	 *
	 * @return 包含父类成员的新加载器
	 */
	public ClassMembersLoader<E> withSuperclass() {
		ClassMembers<E> superclassProvider = classMembers.getSuperclass();
		if (superclassProvider == null) {
			return this;
		}

		ClassMembersLoader<E> superclass = new ClassMembersLoader<>(superclassProvider);
		return new ClassMembersLoader<>(classMembers, superclass.withSuperclass(), interfaces);
	}

	/**
	 * 创建包含当前类、父类和所有接口成员的加载器。
	 * <p>
	 * 实现逻辑：
	 * <ol>
	 * <li>先处理父类层级</li>
	 * <li>再处理所有接口层级</li>
	 * <li>返回完整继承体系的成员加载器</li>
	 * </ol>
	 *
	 * @return 包含所有层级成员的新加载器
	 */
	public ClassMembersLoader<E> withAll() {
		return withSuperclass().withInterfaces();
	}
}