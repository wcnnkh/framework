package io.basc.framework.poi.hssf;

import java.io.IOException;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import io.basc.framework.mapper.io.Exporter;
import io.basc.framework.mapper.io.template.AbstractRecordImporter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HssfRecordImporter extends AbstractRecordImporter {
	@NonNull
	private final POIFSFileSystem poifsFileSystem;

	@Override
	public void doRead(Exporter exporter) throws IOException {
		DefaultHSSFListener hssfListener = new DefaultHSSFListener(poifsFileSystem, exporter);
		hssfListener.process();
	}
}
