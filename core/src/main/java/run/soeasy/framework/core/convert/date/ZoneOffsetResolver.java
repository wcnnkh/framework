package run.soeasy.framework.core.convert.date;

import java.time.ZoneOffset;

import lombok.NonNull;
import run.soeasy.framework.core.convert.value.Readable;

/**
 * 解析ZoneOffset
 * 
 * @author shuchaowen
 *
 */
public interface ZoneOffsetResolver {
	ZoneOffset resolveZoneOffset(@NonNull Readable valueDescriptor);
}
