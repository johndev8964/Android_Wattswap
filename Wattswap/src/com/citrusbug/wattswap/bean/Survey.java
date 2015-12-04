package com.citrusbug.wattswap.bean;

import java.io.Serializable;

public class Survey implements Serializable {
  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;
  public String surveyId;
  public String surveyName;
  public String address1;
  public String address2;
  public String city;
  public String state;
  public String zip;
  public String contanctName;
  public String phone;
  public String email;
  public String scheduledDate;
  public String note;
  public String buildingUnit;
  public String buildingType;
  public String reffBy;
  public String totalSquareFootage;
  public String utilityCmpny;
  public String accountNumber;
  public boolean isVisible;
  public String path;
  public String status;  
  
  
  public Survey(String surveyId,String surveyName,String address1,String address2,String city,String state,String zip,
		  String contactName,String phone,String email,String scheduledDate,String note,String buildingUnit,String buildingType,String reffBy,String totalSquareFootage,String utilityCmpny,String accountNumber,String status,String path ){
	  this.surveyId=surveyId;
	  this.surveyName=surveyName;
	  this.address1=address1;
	  this.address2=address2;
	  this.city=city;
	  this.state=state;
	  this.zip=zip;
	  this.contanctName=contactName;
	  this.phone=phone;
	  this.email=email;
	  this.scheduledDate=scheduledDate;
	  this.note=note;
	  this.buildingUnit=buildingUnit;
	  this.buildingType=buildingType;
	  this.reffBy=reffBy;
	  this.totalSquareFootage=totalSquareFootage;
	  this.utilityCmpny=utilityCmpny;
	  this.accountNumber=accountNumber;
	  this.status=status;
	  this.path=path;
	  
	  
  }
}
