package com.eis.ileadbyexample.Pojo;

import java.util.List;
import com.google.gson.annotations.SerializedName;


public class HistoryList{

	@SerializedName("CallHistory")
	private List<CallHistoryItem> callHistory;

	public void setCallHistory(List<CallHistoryItem> callHistory){
		this.callHistory = callHistory;
	}

	public List<CallHistoryItem> getCallHistory(){
		return callHistory;
	}

	@Override
 	public String toString(){
		return 
			"HistoryList{" + 
			"callHistory = '" + callHistory + '\'' + 
			"}";
		}
}