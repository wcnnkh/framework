package run.soeasy.framework.io;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 文件系统迭代器，用于递归遍历指定目录下的所有文件和子目录。
 * 该迭代器支持设置最大遍历深度，可用于文件系统的深度优先遍历。
 * 
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>深度控制：通过maxDepth参数限制遍历深度，-1表示无限制</li>
 *   <li>递归遍历：按深度优先顺序访问文件和子目录</li>
 *   <li>懒加载：仅在需要时读取目录内容，避免内存浪费</li>
 *   <li>类型安全：始终返回{@link File}实例，确保遍历元素类型一致</li>
 * </ul>
 * 
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>文件系统扫描：查找特定类型的文件</li>
 *   <li>目录备份：递归遍历目录结构</li>
 *   <li>磁盘空间统计：计算目录大小</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see File
 * @see Iterator
 */
class ListFileIterator implements Iterator<File> {
    private final File directory;
    private final int depth;     // 当前深度
    private final int maxDepth;  // 最大迭代深度

    /**
     * 构造指定目录的文件迭代器，使用默认深度（0）和最大深度。
     * 
     * @param directory 要遍历的目录，不可为null
     * @param maxDepth  最大遍历深度，-1表示不限制深度
     * @throws IllegalArgumentException 如果目录为null
     */
    public ListFileIterator(File directory, int maxDepth) {
        this(directory, maxDepth, 0);
    }

    private ListFileIterator(File directory, int maxDepth, int depth) {
        if (directory == null) {
            throw new IllegalArgumentException("Directory must not be null");
        }
        this.directory = directory;
        this.maxDepth = maxDepth;
        this.depth = depth;
    }

    public File getDirectory() {
        return directory;
    }

    public int getDepth() {
        return depth;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    /**
     * 判断是否可以继续递归遍历子目录。
     * 
     * @param depth 当前遍历深度
     * @return true表示可以继续递归，false表示达到深度限制
     */
    public boolean canRecursive(int depth) {
        if (maxDepth < 0) {
            return true;
        }
        return depth <= maxDepth;
    }

    /** 当前目录下的文件数组 */
    private File[] files;
    /** 当前目录下的子目录迭代器 */
    private Iterator<File> directoryIterator;
    /** 当前迭代器（文件或子目录迭代器） */
    private Iterator<File> iterator;

    /**
     * 判断是否存在下一个文件或目录。
     * <p>
     * 遍历逻辑：
     * <ol>
     *   <li>先读取当前目录下的所有文件</li>
     *   <li>若文件迭代完成，再处理子目录的递归遍历</li>
     *   <li>根据maxDepth参数控制递归深度</li>
     * </ol>
     * 
     * @return true表示存在下一个元素，false表示遍历完成
     */
    @Override
    public boolean hasNext() {
        if (!canRecursive(depth)) {
            return false;
        }

        if (files == null) {
            files = directory.listFiles();
        }

        if (iterator == null) {
            iterator = Arrays.asList(files).iterator();
        }

        if (iterator.hasNext()) {
            return true;
        }

        if (!canRecursive(depth)) {
            return false;
        }

        if (directoryIterator == null) {
            directoryIterator = Arrays.asList(files).stream()
                    .filter(file -> file.isDirectory())
                    .iterator();
        }

        if (directoryIterator.hasNext()) {
            File nextDir = directoryIterator.next();
            iterator = new ListFileIterator(nextDir, maxDepth, depth + 1);
            return hasNext();
        }
        return false;
    }

    /**
     * 返回下一个文件或目录。
     * 
     * @return 下一个File实例
     * @throws NoSuchElementException 当没有更多元素时抛出
     */
    @Override
    public File next() {
        if (!hasNext()) {
            throw new NoSuchElementException(toString());
        }
        return iterator.next();
    }

    /**
     * 返回迭代器状态的字符串表示。
     * 
     * @return 包含最大深度、当前深度和目录路径的字符串
     */
    @Override
    public String toString() {
        return "maxDepth:" + maxDepth + ", depth:" + depth + ", directory:" + directory;
    }
}