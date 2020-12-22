import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter {

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public String convertDateToXSDdatetime(Date date) {

        return formatter.format(date);
    }

    public Date convertXSDdatetimeToDate(String xsdDateTime) throws ParseException {

            return formatter.parse(xsdDateTime);
    }

    // owltime also takes seconds into account
    public int convertDateToOWLtime(Date date) {

        return (date.getHours()*60*60)+(date.getMinutes()*60)+date.getSeconds();
    }

    // owltime also takes seconds into account
    public Date convertOWLtimeToDate(int owltime) {

        Date date = new Date();

        int hours = owltime/(60*60);
        int minutes = (owltime-(hours*60*60))/60;
        int seconds = owltime-(hours*60*60)-(minutes*60);

        date.setHours(hours);
        date.setMinutes(minutes);
        date.setSeconds(seconds);

        return date;
    }

    public int convertXSDdatetimeToOWLtime(String xsdDateTime) throws ParseException {

        return this.convertDateToOWLtime(this.convertXSDdatetimeToDate(xsdDateTime));
    }

    public String convertOWLtimeToXSDdatetime(int owltime){

        return this.convertDateToXSDdatetime(this.convertOWLtimeToDate(owltime));
    }
}
