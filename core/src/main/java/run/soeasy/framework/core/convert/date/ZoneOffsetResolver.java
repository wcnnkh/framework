package run.soeasy.framework.core.convert.date;

import java.time.ZoneOffset;

import lombok.NonNull;
import run.soeasy.framework.core.convert.SourceDescriptor;

/**
 * 解析ZoneOffset
 * 
 * @author shuchaowen
 *
 */
public interface ZoneOffsetResolver {
	ZoneOffset resolveZoneOffset(@NonNull SourceDescriptor valueDescriptor);
}
