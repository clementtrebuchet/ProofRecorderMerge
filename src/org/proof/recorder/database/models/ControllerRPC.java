package org.proof.recorder.database.models;

import java.util.HashMap;
import java.util.Map;

import org.proof.recorder.xmlrpc.XMLRPCSerializable;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ControllerRPC implements XMLRPCSerializable{

	private int record; 
	private int noterecord; 
	private int voice; 
	private int notevoice; 
	private boolean forceSync;
	private boolean jutTcheck;
	
	public boolean isJutTcheck() {
		return jutTcheck;
	}

	public void setJutTcheck(boolean jutTcheck) {
		this.jutTcheck = jutTcheck;
	}

	public boolean isForceSync() {
		return forceSync;
	}

	public void setForceSync(boolean forceSync) {
		this.forceSync = forceSync;
	}

	public ControllerRPC(Context bh){
		assert bh != null;
		getForcePref(bh);
	}
	
	public int getRecord() {
		return record;
	}

	public void setRecord(int record) {
		this.record = record;
	}

	public int getNoterecord() {
		return noterecord;
	}

	public void setNoterecord(int noterecord) {
		this.noterecord = noterecord;
	}

	public int getVoice() {
		return voice;
	}

	public void setVoice(int voice) {
		this.voice = voice;
	}

	public int getNotevoice() {
		return notevoice;
	}

	public void setNotevoice(int notevoice) {
		this.notevoice = notevoice;
	}
	private void getForcePref(Context nh){
		SharedPreferences mSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(nh);
		this.setForceSync(mSharedPreferences.getBoolean("synchroForce", false));
		 
		
	}

	@Override
	public Map<String, Object> getSerializable() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("record",this.record);
		map.put("noterecord", this.noterecord);
		map.put("voice", this.voice);
		map.put("notevoice", this.notevoice);
		map.put("FORCESYNC", this.forceSync);
		map.put("PREOPCHECK", this.jutTcheck);
		return map;
	}
	
	

}
