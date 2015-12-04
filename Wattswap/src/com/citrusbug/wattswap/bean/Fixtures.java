package com.citrusbug.wattswap.bean;

import java.io.Serializable;

public class Fixtures implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String fixtureId;
	public String areaId;
	public String fixtureName;
	public String fixtureCount;
	public String code;
	public String style;
	public String mounting;
	public String controlled;
	public String option;
	public String height;
	public String firstValue;
	public String secondValue;
	public String answer;
	public String imagePath;
	public String note;
	public String ballastType;
	public String ballastFactor;
	public String bulbValue;
	public String wattsValue;
	public String bulbAnswer;
	
	public boolean isVisible;
	public Fixtures(String fixtureId,String areaId,String fixtureName,String fixturesCount,String code,String style,
			String mounting,String controlled,String option,String height,String firstValue,
			String secondValue,String answer,String imagePath,String note,String ballastType,String ballastFactor,String bulbValue,String wattsValue,String bulbAnswer ){
		this.fixtureId=fixtureId;
		this.areaId=areaId;
		this.fixtureName=fixtureName;
		this.fixtureCount=fixturesCount;
		this.code=code;
		this.style=style;
		this.mounting=mounting;
		this.controlled=controlled;
		this.option=option;
		this.height=height;
		this.firstValue=firstValue;
		this.secondValue=secondValue;
		this.answer=answer;
		this.imagePath=imagePath;
		this.note=note;
		this.ballastType=ballastType;
		this.ballastFactor=ballastFactor;
		this.bulbValue=bulbValue;
		this.wattsValue=wattsValue;
		this.bulbAnswer=bulbAnswer;
		isVisible=false;
	}
}