import autovalue.shaded.com.google$.common.annotations.$VisibleForTesting;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class testMain {

    public static void main(String[] args) {

        DateFormatter formatter = new DateFormatter();
        //testing
        //      date to xsd:datetime
        String local1 = formatter.convertDateToXSDdatetime(new Date());
        System.out.println(local1);
        //      xsd:datetime to date
        try {
            Date local2 = formatter.convertXSDdatetimeToDate("1992-12-17T09:15:30");
            System.out.println(local2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //      date to owltime
        int local3 = formatter.convertDateToOWLtime(new Date());
        System.out.println(local3);
        //      owltime to date
        Date local4 = formatter.convertOWLtimeToDate(40000);
        System.out.println(local4);
    }
}
