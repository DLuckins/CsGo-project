package com.example.scenedemo;

public class Data1 {
    private String Name, Condition,Collection,COF,FW,NOF;
    private double AveragePrice,Cost,FP,ROI;
    private int HMFTP,HMTP;
    public Data1 (String Name,String Collection,String Condition,double AveragePrice,int HMTP,String NOF,String COF,String FW,double FP,int HMFTP,Double Cost,Double ROI){
        this.Name=Name;
        this.Collection=Collection;
        this.Condition=Condition;
        this.AveragePrice=AveragePrice;
        this.HMTP=HMTP;
        this.NOF=NOF;
        this.COF=COF;
        this.FW=FW;
        this.FP=FP;
        this.HMFTP=HMFTP;
        this.Cost=Cost;
        this.ROI=ROI;

    }

    public String getCollection() {
        return Collection;
    }

    public void setCollection(String collection) {
        Collection = collection;
    }

    public String getCOF() {
        return COF;
    }

    public void setCOF(String COF) {
        this.COF = COF;
    }

    public String getFW() {
        return FW;
    }

    public void setFW(String FW) {
        this.FW = FW;
    }

    public String getNOF() {
        return NOF;
    }

    public void setNOF(String NOF) {
        this.NOF = NOF;
    }

    public double getCost() {
        return Cost;
    }

    public void setCost(double cost) {
        Cost = cost;
    }

    public double getFP() {
        return FP;
    }

    public void setFP(double FP) {
        this.FP = FP;
    }

    public double getROI() {
        return ROI;
    }

    public void setROI(double ROI) {
        this.ROI = ROI;
    }

    public int getHMFTP() {
        return HMFTP;
    }

    public void setHMFTP(int HMFTP) {
        this.HMFTP = HMFTP;
    }

    public int getHMTP() {
        return HMTP;
    }

    public void setHMTP(int HMTP) {
        this.HMTP = HMTP;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCondition() {
        return Condition;
    }

    public void setCondition(String condition) {
        Condition = condition;
    }

    public double getAveragePrice() {
        return AveragePrice;
    }

    public void setAveragePrice(double averagePrice) {
        AveragePrice = averagePrice;
    }
}
