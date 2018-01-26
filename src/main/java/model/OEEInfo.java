package model;

import java.sql.Timestamp;

/**
 * @Author You Jia
 * @Date 1/12/2018 11:36 AM
 */
public class OEEInfo {
    String line;
    Timestamp beginDate;
    Timestamp endDate;
    Timestamp shiftDate;
    String shiftName;
    long running;
    long warning;
    long planDown;
    long nonProductionPlan;
    long qualityLoss;
    long performanceLoss;
    long holiday;
    long totalDuration;
    long outPut;

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public Timestamp getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Timestamp beginDate) {
        this.beginDate = beginDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public void setShiftDate(Timestamp shiftDate) {
        this.shiftDate = shiftDate;
    }

    public Timestamp getShiftDate() {
        return shiftDate;
    }

    public String getShiftName() {
        return shiftName;
    }

    public void setShiftName(String shiftName) {
        this.shiftName = shiftName;
    }

    public long getRunning() {
        return running;
    }

    public void setRunning(long running) {
        this.running = running;
    }

    public long getWarning() {
        return warning;
    }

    public void setWarning(long warning) {
        this.warning = warning;
    }

    public long getPlanDown() {
        return planDown;
    }

    public void setPlanDown(long planDown) {
        this.planDown = planDown;
    }

    public long getNonProductionPlan() {
        return nonProductionPlan;
    }

    public void setNonProductionPlan(long nonProductionPlan) {
        this.nonProductionPlan = nonProductionPlan;
    }

    public long getQualityLoss() {
        return qualityLoss;
    }

    public void setQualityLoss(long qualityLoss) {
        this.qualityLoss = qualityLoss;
    }

    public void setPerformanceLoss(long performanceLoss) {
        this.performanceLoss = performanceLoss;
    }

    public long getPerformanceLoss() {
        return performanceLoss;
    }

    public long getHoliday() {
        return holiday;
    }

    public void setHoliday(long holiday) {
        this.holiday = holiday;
    }

    public long getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(long totalDuration) {
        this.totalDuration = totalDuration;
    }

    public long getOutPut() {
        return outPut;
    }

    public void setOutPut(long outPut) {
        this.outPut = outPut;
    }
}
