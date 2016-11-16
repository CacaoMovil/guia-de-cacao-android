package kronos.comkronoscodecomandroid.activity.object;

import java.util.Date;
import java.util.List;

/**
 * Created by jhon on 11/1/16.
 */

public class EventObject {

    private String name;
    private String description;
    private String start;
    private String end;
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

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public List<CountryEvent> getCountryEventList() {
        return countryEventList;
    }

    public void setCountryEventList(List<CountryEvent> countryEventList) {
        this.countryEventList = countryEventList;
    }
}
