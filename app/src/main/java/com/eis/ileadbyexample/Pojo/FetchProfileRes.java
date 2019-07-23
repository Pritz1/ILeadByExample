package com.eis.ileadbyexample.Pojo;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class FetchProfileRes{

	@SerializedName("profile")
	private List<ProfileItem> profile;

	public void setProfile(List<ProfileItem> profile){
		this.profile = profile;
	}

	public List<ProfileItem> getProfile(){
		return profile;
	}

	@Override
 	public String toString(){
		return 
			"FetchProfileRes{" + 
			"profile = '" + profile + '\'' + 
			"}";
		}
}