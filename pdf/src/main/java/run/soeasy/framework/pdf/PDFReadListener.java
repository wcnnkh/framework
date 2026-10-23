package run.soeasy.framework.pdf;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.springframework.lang.Nullable;

import lombok.NonNull;

/**
 * PDF解析监听器接口（基于回调模式处理PDF解析全生命周期，提供逐页处理、异常捕获及资源回收能力）
 * <p>
 * 设计理念：通过回调方法串联「解析前初始化-逐页解析处理-解析完成收尾-异常统一处理」全流程，
 * 使用者仅需实现核心的{@link #onPageParsed(PDDocument, PDPage, int)}方法，
 * 其他生命周期方法可根据业务需求选择性重写。
 * </p>
 */
@FunctionalInterface // 仅包含一个抽象方法，支持Lambda表达式简化实现
public interface PDFReadListener {

    /**
     * 解析开始前触发（初始化回调）
     * <p>
     * 适用场景：文档预处理（如校验文档权限、提取元数据、初始化解析上下文等）。
     * 返回false时将直接终止解析流程，此时仅触发{@link #onParseComplete(PDDocument, int, int)}回调。
     * </p>
     *
     * @param document 已加载的PDF文档对象（非null，可直接操作文档属性）
     * @return boolean - true：继续执行解析流程；false：终止解析流程
     * @throws IOException 预处理过程中（如权限校验、元数据读取）发生的IO异常
     */
    default boolean onParseStart(@NonNull PDDocument document) throws IOException {
        return true;
    }

    /**
     * 逐页解析完成后触发（核心回调方法，必须实现）
     * <p>
     * 触发时机：每页解析完成后同步回调，可在此实现页面内容提取（文本、图片等）、页面转换（转图片）、
     * 页面拆分等核心业务逻辑。返回false时将终止后续页面解析，直接进入{@link #onParseComplete}阶段。
     * </p>
     *
     * @param document  全局唯一的PDF文档对象（非null，由框架统一管理生命周期，请勿手动关闭）
     * @param page      当前解析完成的页面对象（非null，可获取页面内容、尺寸、旋转角度等信息）
     * @param pageIndex 页面索引（从0开始，与文档内页面顺序一致，第1页对应索引0，第N页对应索引N-1）
     * @return boolean - true：继续解析下一页；false：终止后续页面解析
     * @throws IOException 页面处理过程中（如内容提取、写入）发生的IO异常，将触发{@link #onParseError}回调
     */
    boolean onPageParsed(@NonNull PDDocument document, @NonNull PDPage page, int pageIndex) throws IOException;

    /**
     * 解析流程最终完成时触发（无论成功/失败均会执行，类似finally块）
     * <p>
     * 核心用途：资源回收（如释放自定义解析上下文、关闭业务侧流对象等）。
     * 注意：框架会在该方法执行后自动关闭{@link PDDocument}，请勿在此手动关闭文档。
     * </p>
     *
     * @param document        PDF文档对象（可能为null：文档加载失败时）
     * @param totalPageCount  文档总页数（文档加载成功时有效；加载失败或未开始解析时为0）
     * @param successPageCount 成功解析的页数：
     *                        - 正常完成：等于文档总页数
     *                        - 主动终止（{@link #onParseStart}或{@link #onPageParsed}返回false）：已解析成功的页数
     *                        - 异常终止：异常发生前已成功解析的页数（可能为0）
     * @throws IOException 资源回收或收尾操作中发生的IO异常（不影响框架对文档的自动关闭逻辑）
     */
    default void onParseComplete(@Nullable PDDocument document, int totalPageCount, int successPageCount)
            throws IOException {
    }

    /**
     * 解析过程中发生异常时触发（全流程异常捕获回调）
     * <p>
     * 覆盖场景：文档加载失败、逐页解析失败、各回调方法执行异常等所有异常场景，
     * 可用于异常日志记录、告警触发、重试机制等异常处理逻辑。
     * </p>
     *
     * @param document  PDF文档对象（可能为null：文档加载阶段失败时）
     * @param page      异常发生时正在处理的页面对象（可能为null：未进入逐页解析阶段时）
     * @param pageIndex 异常发生时的页面索引：
     *                  - 逐页解析阶段异常：对应页面索引（≥0）
     *                  - 其他阶段（加载/初始化）异常：-1
     * @param error     异常根源（非null，包含完整堆栈信息，用于问题排查）
     * @throws IOException 异常处理过程中（如日志写入、重试操作）发生的IO异常
     */
    default void onParseError(@Nullable PDDocument document, @Nullable PDPage page, int pageIndex,
                             @NonNull Throwable error) throws IOException {
    }
}