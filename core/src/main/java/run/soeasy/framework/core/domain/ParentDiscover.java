package run.soeasy.framework.core.domain;

import run.soeasy.framework.core.collection.Elements;

/**
 * 父节点发现接口，定义了具有父子层级关系的对象的父节点访问和遍历能力。
 * 实现该接口的类可以获取其父节点、遍历所有上级节点，以及判断某个节点是否为其父节点。
 *
 * <p>核心特性：
 * <ul>
 *   <li>父节点访问：通过{@link #getParent()}获取直接父节点</li>
 *   <li>层级遍历：通过{@link #parents()}获取包含所有上级节点的元素集合</li>
 *   <li>关系判断：通过{@link #isParents(ParentDiscover)}判断某个节点是否为当前节点的父节点</li>
 *   <li>递归支持：接口采用递归泛型定义，确保类型安全的层级访问</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>组织结构树：部门、员工等具有层级关系的实体</li>
 *   <li>文件系统：目录和文件的层级结构</li>
 *   <li>UI组件树：界面元素的嵌套关系</li>
 *   <li>数据结构：如DOM树、AST树等</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * // 假设Directory实现了ParentDiscover接口
 * Directory root = new Directory("root");
 * Directory subDir = new Directory("subDir", root);
 * Directory file = new Directory("file.txt", subDir);
 * 
 * // 获取直接父节点
 * Directory parent = file.getParent(); // subDir
 * 
 * // 遍历所有父节点
 * Elements&lt;Directory&gt; allParents = file.parents(); // [subDir, root]
 * 
 * // 判断是否为父节点
 * boolean isParent = file.isParents(root); // true
 * </pre>
 *
 * @param <T> 具体实现类的类型，必须实现ParentDiscover接口自身
 * @see Elements
 */
public interface ParentDiscover<T extends ParentDiscover<T>> {
    
    /**
     * 获取当前节点的直接父节点。
     * <p>
     * 若当前节点没有父节点（如根节点），则返回null。
     *
     * @return 直接父节点，可能为null
     */
    T getParent();

    /**
     * 判断当前节点是否有父节点。
     * <p>
     * 默认实现通过检查{@link #getParent()}是否为null来判断。
     *
     * @return true如果有父节点，false否则
     */
    default boolean hasParent() {
        return getParent() != null;
    }

    /**
     * 获取包含当前节点所有父节点的元素集合，按从直接父节点到根节点的顺序排列。
     * <p>
     * 集合是惰性计算的，仅在遍历时才会实际获取父节点。
     *
     * @return 包含所有父节点的元素集合，若没有父节点则返回空集合
     */
    default Elements<T> parents() {
        return Elements.of(() -> new ParentIterator<>(this));
    }

    /**
     * 判断指定节点是否为当前节点的父节点（包括直接父节点和所有上级父节点）。
     * <p>
     * 判断逻辑：
     * <ol>
     *   <li>若指定节点为null或当前节点没有父节点，返回false</li>
     *   <li>逐级向上检查每个父节点，直到找到匹配节点或到达根节点</li>
     * </ol>
     *
     * @param parent 待检查的父节点，可为null
     * @return true如果指定节点是当前节点的父节点，false否则
     */
    default boolean isParents(T parent) {
        if (parent == null || !hasParent()) {
            return false;
        }

        T p = getParent();
        while (true) {
            if (p == parent || parent.equals(p)) {
                return true;
            }

            if (!p.hasParent()) {
                return false;
            }

            p = p.getParent();
        }
    }
}