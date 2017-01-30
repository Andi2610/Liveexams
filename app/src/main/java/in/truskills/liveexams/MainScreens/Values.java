package in.truskills.liveexams.MainScreens;

/**
 * Created by 6155dx on 22-01-2017.
 */

public class Values {
    String name,startDateValue,endDateValue,durationValue,examId;
    Values(String name,String startDateValue,String endDateValue,String durationValue,String examId) {
        this.name=name;
        this.startDateValue=startDateValue;
        this.endDateValue=endDateValue;
        this.durationValue=durationValue;
        this.examId=examId;
    }

    public String getName() {
        return name;
    }

    public void setName(String s) {
        name = s;
    }

    public String getStartDateValue() {
        return startDateValue;
    }

    public void setStartDateValue(String s) {
        startDateValue = s;
    }

    public String getEndDateValue() {
        return endDateValue;
    }

    public void setEndDateValue(String s) {
        endDateValue = s;
    }

    public String getDurationValue() {
        return durationValue;
    }

    public void setDurationValue(String s) {
        durationValue = s;
    }

    public String getExamId() {
        return examId;
    }

    public void setExamId(String s) {
        examId = s;
    }
}
