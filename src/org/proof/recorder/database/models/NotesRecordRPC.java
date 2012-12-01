package org.proof.recorder.database.models;

import java.util.HashMap;
import java.util.Map;

import org.proof.recorder.xmlrpc.XMLRPCSerializable;

public class NotesRecordRPC implements XMLRPCSerializable {
	
	private String COLUMNNOTES_ID;
	private String COLUMN_ID_COLUMNRECODINGAPP_ID;
	private String COLUMN_TITLE;
	private String COLUMN_NOTE;
	private String COLUMN_DATE_LAST_MODIF;
	private int COLUMN_ISYNC_NOP;
	
	public String getCOLUMNNOTES_ID() {
		return COLUMNNOTES_ID;
	}

	public void setCOLUMNNOTES_ID(String cOLUMNNOTES_ID) {
		COLUMNNOTES_ID = cOLUMNNOTES_ID;
	}

	public String getCOLUMN_ID_COLUMNRECODINGAPP_ID() {
		return COLUMN_ID_COLUMNRECODINGAPP_ID;
	}

	public void setCOLUMN_ID_COLUMNRECODINGAPP_ID(
			String cOLUMN_ID_COLUMNRECODINGAPP_ID) {
		COLUMN_ID_COLUMNRECODINGAPP_ID = cOLUMN_ID_COLUMNRECODINGAPP_ID;
	}

	public String getCOLUMN_TITLE() {
		return COLUMN_TITLE;
	}

	public void setCOLUMN_TITLE(String cOLUMN_TITLE) {
		COLUMN_TITLE = cOLUMN_TITLE;
	}

	public String getCOLUMN_NOTE() {
		return COLUMN_NOTE;
	}

	public void setCOLUMN_NOTE(String cOLUMN_NOTE) {
		COLUMN_NOTE = cOLUMN_NOTE;
	}

	public String getCOLUMN_DATE_LAST_MODIF() {
		return COLUMN_DATE_LAST_MODIF;
	}

	public void setCOLUMN_DATE_LAST_MODIF(String cOLUMN_DATE_LAST_MODIF) {
		COLUMN_DATE_LAST_MODIF = cOLUMN_DATE_LAST_MODIF;
	}

	public int getCOLUMN_ISYNC_NOP() {
		return COLUMN_ISYNC_NOP;
	}

	public void setCOLUMN_ISYNC_NOP(int cOLUMN_ISYNC_NOP) {
		COLUMN_ISYNC_NOP = cOLUMN_ISYNC_NOP;
	}

	public NotesRecordRPC(String _id, String idForeignRecord, String title,
			String note, String lastChange, int isSync) {
		this.COLUMNNOTES_ID = _id;
		this.COLUMN_ID_COLUMNRECODINGAPP_ID = idForeignRecord;
		this.COLUMN_TITLE = title;
		this.COLUMN_NOTE = note;
		this.COLUMN_DATE_LAST_MODIF = lastChange;
		this.COLUMN_ISYNC_NOP = isSync;

	}
	
	@Override
	public Map<String, Object> getSerializable() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("COLUMNNOTES_ID", COLUMNNOTES_ID);
		map.put("COLUMN_ID_COLUMNRECODINGAPP_ID", COLUMN_ID_COLUMNRECODINGAPP_ID);
		map.put("COLUMN_TITLE", COLUMN_TITLE);
		map.put("COLUMN_NOTE", COLUMN_NOTE);
		map.put("COLUMN_DATE_LAST_MODIF", COLUMN_DATE_LAST_MODIF);
		map.put("COLUMN_ISYNC_NOP", COLUMN_ISYNC_NOP);
		return map;
	}

}
