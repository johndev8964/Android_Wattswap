package com.citrusbug.wattswap.bean;

import java.io.Serializable;

public class Floor implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String floorId;
	public String floorDiscription;
	public String noOfArea;
	public String noOfFixures;
	public boolean isVisible;

	public Floor(String floorId,String floorDiscription,String noOfArea,String noOfFixtures){
		this.floorId=floorId;
		this.floorDiscription=floorDiscription;
		this.noOfArea=noOfArea;
		this.noOfFixures=noOfFixtures;
		isVisible=false;
	}
}
