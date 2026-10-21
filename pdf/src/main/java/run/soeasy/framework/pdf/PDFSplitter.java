package run.soeasy.framework.pdf;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.RandomUtils;

/**
 * PDF 按页拆分器
 * <p>
 * 核心定位：实现 {@link PDFReadListener} 接口，基于 PDF 逐页解析事件机制，提供「全页拆分」「指定页拆分」两种核心能力，
 * 将目标页面拆分为独立的 PDF 临时文件，自动完成文件创建、内容写入、资源释放与结果收集，简化 PDF 拆分流程。
 * </p>
 * <p>
 * 核心特性：
 * 1. 无损拆分：基于 Apache PDFBox 底层能力，完整保留原页面的旋转角度、文本内容、字体样式、图片、注释等所有属性，无格式丢失；
 * 2. 唯一命名：拆分后的临时文件采用「UUID 前缀 + 原文档页码索引 + .pdf」命名规则，确保全局文件名唯一，避免冲突；
 * 3. 有序结果：拆分文件列表顺序严格遵循解析顺序——全页拆分时与原文档页面顺序一致，指定页拆分时与传入页码索引顺序一致（支持重复页码）；
 * 4. 资源安全：拆分过程中创建的临时 {@link PDDocument} 实例通过 finally 块强制关闭，彻底避免文件句柄泄露；
 * 5. 灵活调用：支持「实例化+配合 PDFUtils 解析」的自定义流程，也可通过 {@link PDF} 类的 split() 方法一键快捷调用，适配不同使用场景。
 * </p>
 * <p>
 * 适用场景：
 * - 批量拆分多页 PDF 为单页独立文件（如合同拆分、报表分页存储）；
 * - 提取 PDF 中的指定页面生成新文件（如从多页文档中提取关键页单独保存）；
 * - 配合框架 Pipeline 机制，实现拆分+后续处理（如拆分后自动上传、加密）的链式操作。
 * </p>
 * <p>
 * 注意事项：
 * 1. 文件类型：拆分结果为临时文件（存储于系统临时目录），使用完成后需手动删除或迁移至目标目录，避免占用磁盘空间；
 * 2. 页码规则：解析时使用 0-based 页码索引（即原文档第 1 页对应索引 0，第 N 页对应索引 N-1），指定无效索引（超出文档总页数范围）会被忽略；
 * 3. 生命周期：与外部 {@link PDDocument} 实例解耦，仅负责自身创建的临时文档关闭，不影响源文档的生命周期（由调用方/框架管理）；
 * 4. 异常处理：拆分过程中若发生 IO 异常（如临时文件创建失败、磁盘空间不足、文档损坏），会直接抛出，需调用方捕获处理。
 * </p>
 */
@Getter // Lombok 注解：生成 outputFiles 字段的 getter 方法，用于获取拆分后的临时文件列表
public class PDFSplitter implements PDFReadListener {

    /**
     * 拆分后的 PDF 临时文件列表
     * <p>
     * 列表状态：解析开始前为空，每解析完成一个目标页面后动态添加对应临时文件，解析结束后可通过 getOutputFiles() 获取完整结果；
     * 顺序规则：
     * - 全页拆分场景：列表索引 0 对应原文档第 1 页（索引 0），列表索引 N 对应原文档第 N+1 页（索引 N），与原文档顺序一致；
     * - 指定页拆分场景：列表顺序与传入的 pageIndexs 数组顺序完全一致，重复传入的页码会对应重复添加的文件。
     */
    private final List<File> outputFiles = new ArrayList<>();

    /**
     * 逐页解析回调：目标页面解析完成后执行拆分逻辑（实现 {@link PDFReadListener} 接口核心方法）
     * <p>
     * 触发时机：由 {@link PDFUtils} 逐页解析目标页面时同步回调，每个目标页面仅触发一次；
     * 执行流程：1. 创建独立的临时 PDF 文档 → 2. 复制当前解析页到临时文档 → 3. 生成唯一临时文件并保存 → 4. 将文件添加到结果列表 → 5. 强制关闭临时文档。
     * </p>
     *
     * @param document  源 PDF 文档对象（非空，由 {@link PDFUtils} 传入并管理生命周期，调用方无需手动关闭）
     * @param page      当前解析完成的页面对象（非空，包含原页面完整属性，直接复用至临时文档）
     * @param pageIndex 当前页面的 0-based 索引（与原文档页面顺序一致，用于临时文件命名和结果追溯）
     * @return boolean - 固定返回 true，指示 {@link PDFUtils} 继续解析后续目标页面（若存在）
     * @throws IOException 拆分过程中发生的 IO 异常，包含以下场景：
     *                     - 临时文件创建失败（如磁盘权限不足、磁盘空间满）；
     *                     - 临时文档保存失败（如文档内容损坏、IO 流异常）；
     *                     - 临时文档关闭失败（如文件被占用）。
     */
    @Override
    public boolean onPageParsed(@NonNull PDDocument document, @NonNull PDPage page, int pageIndex) throws IOException {
        // 创建临时文档：独立于源文档，仅用于存储当前拆分页
        PDDocument newDocument = new PDDocument();
        try {
            // 复制当前页到临时文档（完整保留页面属性）
            newDocument.addPage(page);
            // 生成唯一临时文件：UUID 确保文件名唯一，页码索引便于关联原文档
            File tempFile = File.createTempFile(RandomUtils.uuid(), pageIndex + ".pdf");
            // 保存临时文档到文件
            newDocument.save(tempFile);
            // 按解析顺序添加到结果列表，确保顺序一致性
            outputFiles.add(tempFile);
        } finally {
            // 强制关闭临时文档：无论保存成功与否，均释放资源，避免泄露
            newDocument.close();
        }
        // 返回 true 表示继续解析下一个目标页面
        return true;
    }
}