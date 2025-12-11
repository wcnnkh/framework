package run.soeasy.framework.messaging.convert;

import java.util.Comparator;

import run.soeasy.framework.io.MimeType;

/**
 * 消息转换器比较器，实现{@link Comparator}接口，用于对{@link MessageConverter}实例进行排序，
 * 核心逻辑基于转换器支持的媒体类型（{@link MimeType}）的特异性，优先选择更具体的媒体类型转换器。
 * 
 * <p>排序规则：
 * 当转换器o1支持的媒体类型是o2支持的媒体类型的子集或更具体时，o1排在o2之前；
 * 否则o2排在o1之前。例如：支持"application/json"的转换器优先于支持"*&#47;*"的转换器。
 * 
 * @author soeasy.run
 * @see MessageConverter
 * @see MimeType
 */
public class MessageConverterComparator implements Comparator<MessageConverter> {

    /**
     * 默认的消息转换器比较器实例
     */
    public static final MessageConverterComparator DEFAULT = new MessageConverterComparator();

    /**
     * 比较两个消息转换器的优先级
     * 
     * <p>通过比较两者支持的媒体类型，判断哪个转换器更适合处理特定类型的消息：
     * - 若o1支持的任一媒体类型被o2的媒体类型包含（或相等），则o1优先级更高（返回-1）；
     * - 否则o2优先级更高（返回1）。
     * 
     * @param o1 第一个消息转换器
     * @param o2 第二个消息转换器
     * @return 负整数（o1优先）、正整数（o2优先）
     */
    @Override
    public int compare(MessageConverter o1, MessageConverter o2) {
    	return o1.getSupportedMediaTypes().zip(o2.getSupportedMediaTypes(), (mimeType1, mimeType2) -> {
    		 // 若o1的媒体类型与o2相等，或被o2包含，则o1更具体，优先级更高
            if (mimeType1.equals(mimeType2) || mimeType2.includes(mimeType1)) {
                return -1;
            }
            return 0;
    	}).filter((e) -> e == -1).findAny().isPresent()? -1:1;
    }
}