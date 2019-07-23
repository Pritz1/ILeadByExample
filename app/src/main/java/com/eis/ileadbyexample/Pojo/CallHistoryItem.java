package com.eis.ileadbyexample.Pojo;


import com.google.gson.annotations.SerializedName;


public class CallHistoryItem{

	@SerializedName("FC_latlang")
	private String fCLatlang;

	@SerializedName("rep_date1")
	private String repDate;

	@SerializedName("WK_time_diff")
	private String wKTimeDiff;

	@SerializedName("FC_time")
	private String fCTime;

	@SerializedName("LS_time")
	private String lSTime;

	@SerializedName("loc_id")
	private String locId;

	@SerializedName("LS_latlang")
	private String lSLatlang;

	@SerializedName("LS_loc_address")
	private String lSLocAddress;

	@SerializedName("FC_loc_address")
	private String fCLocAddress;

	@SerializedName("ecode")
	private String ecode;

	public void setFCLatlang(String fCLatlang){
		this.fCLatlang = fCLatlang;
	}

	public String getFCLatlang(){
		return fCLatlang;
	}

	public void setRepDate(String repDate){
		this.repDate = repDate;
	}

	public String getRepDate(){
		return repDate;
	}

	public void setWKTimeDiff(String wKTimeDiff){
		this.wKTimeDiff = wKTimeDiff;
	}

	public String getWKTimeDiff(){
		return wKTimeDiff;
	}

	public void setFCTime(String fCTime){
		this.fCTime = fCTime;
	}

	public String getFCTime(){
		return fCTime;
	}

	public void setLSTime(String lSTime){
		this.lSTime = lSTime;
	}

	public String getLSTime(){
		return lSTime;
	}

	public void setLocId(String locId){
		this.locId = locId;
	}

	public String getLocId(){
		return locId;
	}

	public void setLSLatlang(String lSLatlang){
		this.lSLatlang = lSLatlang;
	}

	public String getLSLatlang(){
		return lSLatlang;
	}

	public void setLSLocAddress(String lSLocAddress){
		this.lSLocAddress = lSLocAddress;
	}

	public String getLSLocAddress(){
		return lSLocAddress;
	}

	public void setFCLocAddress(String fCLocAddress){
		this.fCLocAddress = fCLocAddress;
	}

	public String getFCLocAddress(){
		return fCLocAddress;
	}

	public void setEcode(String ecode){
		this.ecode = ecode;
	}

	public String getEcode(){
		return ecode;
	}

	@Override
 	public String toString(){
		return 
			"CallHistoryItem{" + 
			"fC_latlang = '" + fCLatlang + '\'' + 
			",rep_date = '" + repDate + '\'' + 
			",wK_time_diff = '" + wKTimeDiff + '\'' + 
			",fC_time = '" + fCTime + '\'' + 
			",lS_time = '" + lSTime + '\'' + 
			",loc_id = '" + locId + '\'' + 
			",lS_latlang = '" + lSLatlang + '\'' + 
			",lS_loc_address = '" + lSLocAddress + '\'' + 
			",fC_loc_address = '" + fCLocAddress + '\'' + 
			",ecode = '" + ecode + '\'' + 
			"}";
		}
}