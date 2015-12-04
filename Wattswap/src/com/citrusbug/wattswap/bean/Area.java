package com.citrusbug.wattswap.bean;

import java.io.Serializable;

public class Area implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String areaId;
	public String floorId;
	public String loctaionName;
	public String noOfFixture;
	public boolean isVisible;
	
	public Area(String areaId,String floorId,String loctaionName,String noOfFixture){
		this.areaId=areaId;
		this.floorId=floorId;
		this.loctaionName=loctaionName;
		this.noOfFixture=noOfFixture;
		isVisible=false;
	}
}
