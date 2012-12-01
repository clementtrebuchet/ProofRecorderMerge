package org.proof.recorder.database.models;

import java.util.HashMap;
import java.util.Map;

import org.proof.recorder.xmlrpc.XMLRPCSerializable;

public class VoiceRPC  implements XMLRPCSerializable{
	
	private String COLUMNVOICE_ID;
	private String COLUMN_VOICE_TIMESTAMP;
	private String COLUMN_VOICE_FILE;
	private String COLUMN_VOICE_TAILLE;
	private String COLUMN_VOICE_HTIME;
	private int COLUMN_ISYNC_VO;
	private String MD5;

	public String getCOLUMNVOICE_ID() {
		return COLUMNVOICE_ID;
	}

	public void setCOLUMNVOICE_ID(String cOLUMNVOICE_ID) {
		COLUMNVOICE_ID = cOLUMNVOICE_ID;
	}

	public String getCOLUMN_VOICE_TIMESTAMP() {
		return COLUMN_VOICE_TIMESTAMP;
	}

	public void setCOLUMN_VOICE_TIMESTAMP(String cOLUMN_VOICE_TIMESTAMP) {
		COLUMN_VOICE_TIMESTAMP = cOLUMN_VOICE_TIMESTAMP;
	}

	public String getCOLUMN_VOICE_FILE() {
		return COLUMN_VOICE_FILE;
	}

	public void setCOLUMN_VOICE_FILE(String cOLUMN_VOICE_FILE) {
		COLUMN_VOICE_FILE = cOLUMN_VOICE_FILE;
	}

	public String getCOLUMN_VOICE_TAILLE() {
		return COLUMN_VOICE_TAILLE;
	}

	public void setCOLUMN_VOICE_TAILLE(String cOLUMN_VOICE_TAILLE) {
		COLUMN_VOICE_TAILLE = cOLUMN_VOICE_TAILLE;
	}

	public String getCOLUMN_VOICE_HTIME() {
		return COLUMN_VOICE_HTIME;
	}

	public void setCOLUMN_VOICE_HTIME(String cOLUMN_VOICE_HTIME) {
		COLUMN_VOICE_HTIME = cOLUMN_VOICE_HTIME;
	}

	public int getCOLUMN_ISYNC_VO() {
		return COLUMN_ISYNC_VO;
	}

	public void setCOLUMN_ISYNC_VO(int cOLUMN_ISYNC_VO) {
		COLUMN_ISYNC_VO = cOLUMN_ISYNC_VO;
	}

	public String getMD5() {
		return MD5;
	}

	public void setMD5(String mD5) {
		MD5 = mD5;
	}

	public VoiceRPC(String _id, String timestamp, String file,String taille, String htime, int isync, String MD5) {
		this.COLUMNVOICE_ID = _id;
		this.COLUMN_VOICE_TIMESTAMP = timestamp;
		this.COLUMN_VOICE_FILE = file;
		this.COLUMN_VOICE_TAILLE = taille;
		this.COLUMN_VOICE_HTIME = htime;
		this.COLUMN_ISYNC_VO = isync;
		this.MD5 = MD5;

	}

	public Map<String, Object> getSerializable() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("COLUMNVOICE_ID", COLUMNVOICE_ID);
		map.put("COLUMN_VOICE_TIMESTAMP", COLUMN_VOICE_TIMESTAMP);
		map.put("COLUMN_VOICE_FILE", COLUMN_VOICE_FILE);
		map.put("COLUMN_VOICE_TAILLE", COLUMN_VOICE_TAILLE);
		map.put("COLUMN_VOICE_HTIME", COLUMN_VOICE_HTIME);
		map.put("COLUMN_ISYNC_VO", COLUMN_ISYNC_VO);
		map.put("MD5", MD5);
		return map;
	}

}
