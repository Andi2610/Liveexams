package in.truskills.liveexams.MainScreens;

/**
 * Created by 6155dx on 22-01-2017.
 */

public class Values {
    String name,startDateValue,endDateValue,durationValue;
    Values(String name,String startDateValue,String endDateValue,String durationValue) {
        this.name=name;
        this.startDateValue=startDateValue;
        this.endDateValue=endDateValue;
        this.durationValue=durationValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String s) {
        this.name = name;
    }

    public String getStartDateValue() {
        return startDateValue;
    }

    public void setStartDateValue(String s) {
        this.startDateValue = s;
    }

    public String getEndDateValue() {
        return endDateValue;
    }

    public void setEndDateValue(String s) {
        this.endDateValue = s;
    }

    public String getDurationValue() {
        return durationValue;
    }

    public void setDurationValue(String s) {
        this.durationValue = s;
    }
}
