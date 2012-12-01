package org.proof.recorder.database.models;

import java.util.HashMap;
import java.util.Map;

import org.proof.recorder.xmlrpc.XMLRPCSerializable;

public class NotesVoiceRPC  implements XMLRPCSerializable{

	private String COLUMNVOICE_NOTES_ID;
	public String getCOLUMNVOICE_NOTES_ID() {
		return COLUMNVOICE_NOTES_ID;
	}

	public void setCOLUMNVOICE_NOTES_ID(String cOLUMNVOICE_NOTES_ID) {
		COLUMNVOICE_NOTES_ID = cOLUMNVOICE_NOTES_ID;
	}

	public String getCOLUMNVOICE_ID_COLUMNVOICE_ID() {
		return COLUMNVOICE_ID_COLUMNVOICE_ID;
	}

	public void setCOLUMNVOICE_ID_COLUMNVOICE_ID(
			String cOLUMNVOICE_ID_COLUMNVOICE_ID) {
		COLUMNVOICE_ID_COLUMNVOICE_ID = cOLUMNVOICE_ID_COLUMNVOICE_ID;
	}

	public String getCOLUMNVOICE_TITLE() {
		return COLUMNVOICE_TITLE;
	}

	public void setCOLUMNVOICE_TITLE(String cOLUMNVOICE_TITLE) {
		COLUMNVOICE_TITLE = cOLUMNVOICE_TITLE;
	}

	public String getCOLUMNVOICE_NOTE() {
		return COLUMNVOICE_NOTE;
	}

	public void setCOLUMNVOICE_NOTE(String cOLUMNVOICE_NOTE) {
		COLUMNVOICE_NOTE = cOLUMNVOICE_NOTE;
	}

	public String getCOLUMNVOICE_DATE_CREATION() {
		return COLUMNVOICE_DATE_CREATION;
	}

	public void setCOLUMNVOICE_DATE_CREATION(String cOLUMNVOICE_DATE_CREATION) {
		COLUMNVOICE_DATE_CREATION = cOLUMNVOICE_DATE_CREATION;
	}

	public int getCOLUMN_ISYNC_NOV() {
		return COLUMN_ISYNC_NOV;
	}

	public void setCOLUMN_ISYNC_NOV(int cOLUMN_ISYNC_NOV) {
		COLUMN_ISYNC_NOV = cOLUMN_ISYNC_NOV;
	}

	private String COLUMNVOICE_ID_COLUMNVOICE_ID;
	private String COLUMNVOICE_TITLE;
	private String COLUMNVOICE_NOTE;
	private String COLUMNVOICE_DATE_CREATION;
	private int COLUMN_ISYNC_NOV;

	public NotesVoiceRPC(String _id, String voiceId, String title,String note, String htime, int isync) {
		this.COLUMNVOICE_NOTES_ID = _id;
		this.COLUMNVOICE_ID_COLUMNVOICE_ID = voiceId;
		this.COLUMNVOICE_TITLE = title;
		this.COLUMNVOICE_NOTE = note;
		this.COLUMNVOICE_DATE_CREATION = htime;
		this.COLUMN_ISYNC_NOV = isync;
		

	}

	public Map<String, Object> getSerializable() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("COLUMNVOICE_NOTES_ID", COLUMNVOICE_NOTES_ID);
		map.put("COLUMNVOICE_ID_COLUMNVOICE_ID", COLUMNVOICE_ID_COLUMNVOICE_ID);
		map.put("COLUMNVOICE_TITLE", COLUMNVOICE_TITLE);
		map.put("COLUMNVOICE_NOTE", COLUMNVOICE_NOTE);
		map.put("COLUMNVOICE_DATE_CREATION", COLUMNVOICE_DATE_CREATION);
		map.put("COLUMN_ISYNC_NOV", COLUMN_ISYNC_NOV);
		return map;
	}
}
