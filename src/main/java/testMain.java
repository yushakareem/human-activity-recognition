import autovalue.shaded.com.google$.common.annotations.$VisibleForTesting;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class testMain {

    public static void main(String[] args) {

        DateFormatter formatter = new DateFormatter();

        KitchenActivitiesOntology kitOnto = new KitchenActivitiesOntology();
        kitOnto.initializeOntology();
        kitOnto.addOrUpdateObjectProperty("fd06", "isIn", "Kitchen");
        kitOnto.addOrUpdateObjectProperty("fd06", "isIn", "LivingRoom");
        kitOnto.addOrUpdateDataProperty(
                "Instant_CurrentTime",
                "hasTime",
                formatter.convertDateToOWLtime(new Date()));
    }
}
