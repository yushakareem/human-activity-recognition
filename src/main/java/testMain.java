import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class testMain {

    public static void main(String[] args) {

        // To suppress log4j warning coming from owloop-2.1
        Logger.getRootLogger().setLevel(Level.OFF);

        // Initialize DateFormatter
        DateFormatter formatter = new DateFormatter();

        // Initialize KitchenActivitiesOntology
        OntologyHandler kitchenOntology = new OntologyHandler();
        kitchenOntology.initializeOntology(
                "KitchenActivitiesOntology",
                "src/main/resources/KitchenActivitiesOntology.owl",
                "http://www.semanticweb.org/Arianna/KitchenActivitiesOntology",
                true
        );


        // Periodic task: update current time in ontology and reason
        ScheduledExecutorService executor =
                Executors.newSingleThreadScheduledExecutor();
        Runnable periodicTask = new Runnable() {
            public void run() {

            }
        };
        executor.scheduleAtFixedRate(periodicTask, 0, 60, TimeUnit.SECONDS);
    }
}
