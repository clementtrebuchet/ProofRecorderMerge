package org.proof.recorder.database.models;

import java.util.HashMap;
import java.util.Map;

import org.proof.recorder.xmlrpc.XMLRPCSerializable;

public class RecordRPC implements XMLRPCSerializable {
	private String COLUMNRECODINGAPP_ID;
	private String COLUMN_TELEPHONE;
	private String COLUMN_CONTRACT_ID;
	private String COLUMN_TIMESTAMP;
	private String COLUMN_FILE;
	private String COLUMN_SENS;
	private String COLUMN_TAILLE;
	private String COLUMN_HTIME;
	private int COLUMN_ISYNC_PH;
	private String MD5;
	private byte[] SONG;
	
	public String getCOLUMNRECODINGAPP_ID() {
		return COLUMNRECODINGAPP_ID;
	}

	public void setCOLUMNRECODINGAPP_ID(String cOLUMNRECODINGAPP_ID) {
		COLUMNRECODINGAPP_ID = cOLUMNRECODINGAPP_ID;
	}

	public String getCOLUMN_TELEPHONE() {
		return COLUMN_TELEPHONE;
	}

	public void setCOLUMN_TELEPHONE(String cOLUMN_TELEPHONE) {
		COLUMN_TELEPHONE = cOLUMN_TELEPHONE;
	}

	public String getCOLUMN_TIMESTAMP() {
		return COLUMN_TIMESTAMP;
	}

	public void setCOLUMN_TIMESTAMP(String cOLUMN_TIMESTAMP) {
		COLUMN_TIMESTAMP = cOLUMN_TIMESTAMP;
	}

	public String getCOLUMN_FILE() {
		return COLUMN_FILE;
	}

	public void setCOLUMN_FILE(String cOLUMN_FILE) {
		COLUMN_FILE = cOLUMN_FILE;
	}

	public String getCOLUMN_SENS() {
		return COLUMN_SENS;
	}

	public void setCOLUMN_SENS(String cOLUMN_SENS) {
		COLUMN_SENS = cOLUMN_SENS;
	}

	public String getCOLUMN_TAILLE() {
		return COLUMN_TAILLE;
	}

	public void setCOLUMN_TAILLE(String cOLUMN_TAILLE) {
		COLUMN_TAILLE = cOLUMN_TAILLE;
	}

	public String getCOLUMN_HTIME() {
		return COLUMN_HTIME;
	}

	public void setCOLUMN_HTIME(String cOLUMN_HTIME) {
		COLUMN_HTIME = cOLUMN_HTIME;
	}

	public int getCOLUMN_ISYNC_PH() {
		return COLUMN_ISYNC_PH;
	}

	public void setCOLUMN_ISYNC_PH(int cOLUMN_ISYNC_PH) {
		COLUMN_ISYNC_PH = cOLUMN_ISYNC_PH;
	}

	public String getMD5() {
		return MD5;
	}

	public void setMD5(String mD5) {
		MD5 = mD5;
	}

	

	public RecordRPC(String _id, String tel,String ContractId, String timestamp, String file,
			String sens, String taille, String htime, int isync, String MD5,byte[] SONG) {
		this.COLUMNRECODINGAPP_ID = _id;
		this.COLUMN_TELEPHONE = tel;
		this.COLUMN_CONTRACT_ID =ContractId;
		this.COLUMN_TIMESTAMP = timestamp;
		this.COLUMN_FILE = file;
		this.COLUMN_SENS = sens;
		this.COLUMN_TAILLE = taille;
		this.COLUMN_HTIME = htime;
		this.COLUMN_ISYNC_PH = isync;
		this.MD5 = MD5;
		this.SONG = SONG;

	}

	@Override
	public Map<String, Object> getSerializable() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("COLUMNRECODINGAPP_ID", COLUMNRECODINGAPP_ID);
		map.put("COLUMN_TELEPHONE", COLUMN_TELEPHONE);
		map.put("COLUMN_CONTRACT_ID", COLUMN_CONTRACT_ID);
		map.put("COLUMN_TIMESTAMP", COLUMN_TIMESTAMP);
		map.put("COLUMN_FILE", COLUMN_FILE);
		map.put("COLUMN_SENS", COLUMN_SENS);
		map.put("COLUMN_TAILLE", COLUMN_TAILLE);
		map.put("COLUMN_HTIME", COLUMN_HTIME);
		map.put("COLUMN_ISYNC_PH", COLUMN_ISYNC_PH);
		map.put("MD5", MD5);
		map.put("SONG", SONG);
		return map;
	}

	public byte[] getSONG() {
		return SONG;
	}

	public void setSONG(byte[] sONG) {
		SONG = sONG;
	}
}
