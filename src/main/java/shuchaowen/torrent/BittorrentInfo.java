package shuchaowen.torrent;

import java.util.ArrayList;
import java.util.List;

import shuchaowen.core.util.StringUtils;

public class BittorrentInfo {
	private String announce;
	private List<String> announceList;
	private long creationData;
	private String comment;
	private String createBy;
	private Info info;

	public BittorrentInfo() {
	};
	
	public BittorrentInfo(String announce, List<String> announceList, long creationDate, String comment, String createBy,
			Info info) {
		this.announce = announce;
		this.announceList = announceList;
		this.creationData = creationDate;
		this.comment = comment;
		this.createBy = createBy;
		this.info = info;
	}

	public String getAnnounce() {
		return announce;
	}

	public void setAnnounce(String announce) {
		this.announce = announce;
	}

	public List<String> getAnnounceList() {
		return announceList;
	}

	public void setAnnounceList(List<String> announceList) {
		this.announceList = announceList;
	}

	public long getCreationData() {
		return creationData;
	}

	public void setCreationData(long creationData) {
		this.creationData = creationData;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public Info getInfo() {
		return info;
	}

	public void setInfo(Info info) {
		this.info = info;
	}

	public void setValue(String key, Object value) {
		if(value == null){
			return ;
		}
		
		Key k = Key.getKey(key);
		switch (k) {
		case announce:
			this.announce = value.toString();
			break;
		case announce_list:
			if(announceList == null){
				announceList = new ArrayList<String>();
			}
			announceList.add(value.toString());
			break;
		case creation_date:
			if (StringUtils.isNumeric(value.toString())) {
				this.creationData = Long.parseLong(value.toString());
			}
			break;
		case comment:
			this.comment = value.toString();
			break;
		case created_by:
			this.createBy = value.toString();
			break;
		case length:
			List<Files> filesList1 = this.getInfo().getFiles();
			if (filesList1 != null) {
				Files files = this.getInfo().getFiles().get(filesList1.size() - 1);
				files.setLength(Long.parseLong(value.toString()));
			} else {
				
				this.getInfo().setLength(Long.parseLong(value.toString()));
			}
		case md5sum:
			List<Files> filesList2 = this.getInfo().getFiles();
			if (filesList2 != null) {
				Files files = this.getInfo().getFiles().get(filesList2.size() - 1);
				files.setMd5sum(value.toString());
			} else {
				this.getInfo().setMd5sum(value.toString());
			}
			break;
		case name:
			this.getInfo().setName(value.toString());
			break;
		case piece_length:
			this.getInfo().setPiecesLength(Long.parseLong(value.toString()));
			break;
		case pieces:
			if (StringUtils.isNumeric(value.toString())) {
				this.getInfo().setPieces(null);
			} else {
				this.getInfo().setPieces((byte[]) value);
			}
			break;
		case path:
			List<Files> filesList3 = this.getInfo().getFiles();
			Files files3 = filesList3.get(filesList3.size() - 1);
			files3.getPath().add(value.toString());
			break;
		default:
			break;
		}
	}
}
