package kronos.comkronoscodecomandroid.activity.object;

import java.util.Date;
import java.util.List;

/**
 * Created by jhon on 11/1/16.
 */

public class Event {

    private String name;
    private String description;
    private Date start;
    private Date end;
    private List<CountryEvent> countryEventList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public List<CountryEvent> getCountryEventList() {
        return countryEventList;
    }

    public void setCountryEventList(List<CountryEvent> countryEventList) {
        this.countryEventList = countryEventList;
    }
}
