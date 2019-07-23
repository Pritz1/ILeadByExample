package com.eis.ileadbyexample.Pojo;

import com.google.gson.annotations.SerializedName;

public class ProfileItem{

	@SerializedName("Mobileno")
	private String mobileno;

	@SerializedName("EName")
	private String eName;

	@SerializedName("Pre")
	private String pre;

	@SerializedName("State")
	private String state;

	@SerializedName("City")
	private String city;

	@SerializedName("Add3")
	private String add3;

	@SerializedName("Add2")
	private String add2;

	@SerializedName("Add1")
	private String add1;

	@SerializedName("PinCode")
	private String pinCode;

	public void setMobileno(String mobileno){
		this.mobileno = mobileno;
	}

	public String getMobileno(){
		return mobileno;
	}

	public void setEName(String eName){
		this.eName = eName;
	}

	public String getEName(){
		return eName;
	}

	public void setPre(String pre){
		this.pre = pre;
	}

	public String getPre(){
		return pre;
	}

	public void setState(String state){
		this.state = state;
	}

	public String getState(){
		return state;
	}

	public void setCity(String city){
		this.city = city;
	}

	public String getCity(){
		return city;
	}

	public void setAdd3(String add3){
		this.add3 = add3;
	}

	public String getAdd3(){
		return add3;
	}

	public void setAdd2(String add2){
		this.add2 = add2;
	}

	public String getAdd2(){
		return add2;
	}

	public void setAdd1(String add1){
		this.add1 = add1;
	}

	public String getAdd1(){
		return add1;
	}

	public void setPinCode(String pinCode){
		this.pinCode = pinCode;
	}

	public String getPinCode(){
		return pinCode;
	}

	@Override
 	public String toString(){
		return 
			"ProfileItem{" + 
			"mobileno = '" + mobileno + '\'' + 
			",eName = '" + eName + '\'' + 
			",pre = '" + pre + '\'' + 
			",state = '" + state + '\'' + 
			",city = '" + city + '\'' + 
			",add3 = '" + add3 + '\'' + 
			",add2 = '" + add2 + '\'' + 
			",add1 = '" + add1 + '\'' + 
			",pinCode = '" + pinCode + '\'' + 
			"}";
		}
}