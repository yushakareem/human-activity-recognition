import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import java.io.IOException;
import java.text.ParseException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainHAR {
    public static void main(String[] args) throws IOException {

        // To suppress log4j warning coming from owloop-2.1
        Logger.getRootLogger().setLevel(Level.OFF);

        // Initialize KitchenActivitiesOntology
        OntologyHandler kitchenOntology = new OntologyHandler();
        kitchenOntology.initializeOntology(
                "KitchenActivitiesOntology",
                "src/main/resources/KitchenActivitiesOntology.owl",
                "http://www.semanticweb.org/Arianna/KitchenActivitiesOntology",
                true
        );

        // Initialize queryHARonto
        OntologyHandler queryHAROntology = new OntologyHandler();
        queryHAROntology.initializeOntology(
                "queryHARonto",
                "src/main/resources/queryHARonto.owl",
                "http://www.semanticweb.org/Arianna/QueryHumanActivityOntology",
                true
        );

        // Initialize DateFormatter
        DateFormatter formatter = new DateFormatter();

        // Firebase initialization
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(MainHAR.class.getClassLoader().getResourceAsStream("key.json")))
                .setDatabaseUrl("https://harfirebasedb-default-rtdb.firebaseio.com/")
                .build();
        FirebaseApp.initializeApp(options);

        // Firebase event-listener for user's name and preferences
        DatabaseReference userDetailsRef = FirebaseDatabase.getInstance()
                .getReference("/fd06");
        userDetailsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                System.out.println("--> Child added");
                if (dataSnapshot.getKey().equals("associatedWithName")) {
                    kitchenOntology.addOrUpdateDataProperty("fd06", "associatedWithName", dataSnapshot.getValue());
                } else if (dataSnapshot.getKey().equals("userPreference_averageTimeAtKitchenTableDuringBreakfast_inSeconds")) {
                    kitchenOntology.addOrUpdateDataProperty("fd06", "userPreference_averageTimeAtKitchenTableDuringBreakfast_inSeconds", dataSnapshot.getValue());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                System.out.println("--> Child changed");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                System.out.println("--> Child removed");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
                System.out.println("--> Child moved");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Error in reading from Firebase: /fd06/associatedWithName and /fd06/userPreference_averageTimeAtKitchenTableDuringBreakfast_inSeconds");
            }
        });

        // Firebase event-listener for "/fd06/isIn"
        DatabaseReference isInRef = FirebaseDatabase.getInstance()
                .getReference("/fd06/isIn");
        isInRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                System.out.println("--> Child added");
                if (dataSnapshot.getKey().equals("Kitchen")) {
                    //set Flag
                    kitchenOntology.setIsRunningFlag(true);
                    //update in ontology: fd06 isIn Kitchen
                    kitchenOntology.addOrUpdateObjectProperty("fd06", "isIn", "Kitchen");
                    //update in ontology: fd06 isInKitchenAt <datasnapshot.getValue()>
                    try {
                        kitchenOntology.addOrUpdateDataProperty(
                                "fd06",
                                "isInKitchenAt",
                                formatter.convertXSDdatetimeToOWLtime(String.valueOf(dataSnapshot.getValue()))
                        );
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if (dataSnapshot.getKey().equals("LivingRoom")) {
                    //update in ontology fd06 isIn LivingRoom
                    //update in ontology Instant_CurrentTime hasTime <insert_latest_time>
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                System.out.println("--> Child changed");
                if (dataSnapshot.getKey().equals("Kitchen")) {
                    //set Flag
                    kitchenOntology.setIsRunningFlag(true);
                    //update in ontology: fd06 isInKitchenAt <datasnapshot.getValue()>
                    try {
                        kitchenOntology.addOrUpdateDataProperty(
                                "fd06",
                                "isInKitchenAt",
                                formatter.convertXSDdatetimeToOWLtime(String.valueOf(dataSnapshot.getValue()))
                        );
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if (dataSnapshot.getKey().equals("LivingRoom")) {
                    //update in ontology Instant_CurrentTime hasTime <insert_latest_time>
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                System.out.println("--> Child removed");
                if (dataSnapshot.getKey().equals("Kitchen")) {
                    //set Flag
                    kitchenOntology.setIsRunningFlag(false);
                    //remove all object properties
                    kitchenOntology.removeObjectProperty("fd06", "isIn");
                    kitchenOntology.removeObjectProperty("fd06", "isNear");
                    kitchenOntology.removeObjectProperty("fd06", "didAction");
                    //remove all data   properties
                    //kitchenOntology.removeDataProperty("fd06", "associatedWithName");
                    //kitchenOntology.removeDataProperty("fd06", "userPreference_averageTimeAtKitchenTableDuringBreakfast_inSeconds");
                    kitchenOntology.removeDataProperty("fd06", "isInKitchenAt");
                    kitchenOntology.removeDataProperty("fd06", "nearKitchenTableAt");
                    kitchenOntology.removeDataProperty("fd06", "drankAt");
                    kitchenOntology.removeDataProperty("fd06", "pouredAt");
                } else if (dataSnapshot.getKey().equals("LivingRoom")) {
                    //remove all object properties
                    //remove all data   properties
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
                System.out.println("--> Child moved");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Error in reading from Firebase: /fd06/isIn");
            }
        });

        // Firebase event-listener for "/fd06/isNear"
        DatabaseReference isNearRef = FirebaseDatabase.getInstance()
                .getReference("/fd06/isNear");
        isNearRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                System.out.println("--> Child added");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                System.out.println("--> Child changed");
                if (dataSnapshot.getKey().equals("KitchenTable")) {
                    //update in ontology: fd06 isNear KitchenTable
                    kitchenOntology.addOrUpdateObjectProperty("fd06", "isNear", "KitchenTable");
                    //update in ontology: fd06 nearKitchenTableAt <datasnapshot.getValue()>
                    try {
                        kitchenOntology.addOrUpdateDataProperty(
                                "fd06",
                                "nearKitchenTableAt",
                                formatter.convertXSDdatetimeToOWLtime(String.valueOf(dataSnapshot.getValue()))
                        );
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if (dataSnapshot.getKey().equals("WorkDesk")) {
                    //update in ontology fd06 isIn LivingRoom
                    //update in ontology Instant_CurrentTime hasTime <insert_latest_time>
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                System.out.println("--> Child removed");
                //remove objectProperties and dataProperties
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
                System.out.println("--> Child moved");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Error in reading from Firebase: /fd06/isNear");
            }
        });

        // Firebase event-listener for "/fd06/didAction"
        DatabaseReference didActionRef = FirebaseDatabase.getInstance()
                .getReference("/fd06/didAction");
        didActionRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                System.out.println("--> Child added");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                System.out.println("--> Child changed");
                if (dataSnapshot.getKey().equals("Drinking")) {
                    //update in ontology: fd06 didAction Drinking
                    kitchenOntology.addOrUpdateObjectProperty("fd06", "didAction", "Drinking");
                    //update in ontology: fd06 drankAt <datasnapshot.getValue()>
                    try {
                        kitchenOntology.addOrUpdateDataProperty(
                                "fd06",
                                "drankAt",
                                formatter.convertXSDdatetimeToOWLtime(String.valueOf(dataSnapshot.getValue()))
                        );
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if (dataSnapshot.getKey().equals("Pouring")) {
                    //update in ontology: fd06 didAction Pouring
                    kitchenOntology.addOrUpdateObjectProperty("fd06", "didAction", "Pouring");
                    //update in ontology: fd06 pouredAt <datasnapshot.getValue()>
                    try {
                        kitchenOntology.addOrUpdateDataProperty(
                                "fd06",
                                "pouredAt",
                                formatter.convertXSDdatetimeToOWLtime(String.valueOf(dataSnapshot.getValue()))
                        );
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                System.out.println("--> Child removed");
                //remove objectProperties and dataProperties
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
                System.out.println("--> Child moved");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Error in reading from Firebase: /fd06/isNear");
            }
        });

        // Periodic task: update current time in ontology and reason
        ScheduledExecutorService executor =
                Executors.newSingleThreadScheduledExecutor();
        Runnable periodicTask = new Runnable() {
            public void run() {
                System.out.println("---- Periodic run");
                if (kitchenOntology.getIsRunningFlag()) {
                    System.out.println("---- KitchenOnto Flag "+kitchenOntology.getIsRunningFlag());
                    // update current time in kitchenActivitiesOnto
                    kitchenOntology.updateCurrentTime();
                    // reason and save recognised activity details in queryHAROnto
                    String inference = kitchenOntology.inferObjectProperty("fd06", "didActivity");
                    if (inference.equalsIgnoreCase("HavingBreakfast")) {
                        System.out.println("---- Activity recognised: Updating queryHARonto");
                        //put all relevant info in queryHARonto
                        // Yusha associatedWithWatchID fd06
                        queryHAROntology.addOrUpdateObjectProperty("Yusha", "associatedWithWatchID", "fd06");
                        // Yusha didActivityHavingBreakfast xsd:datetime
                        String hadBreakfastAtTime = kitchenOntology.inferDataProperty("fd06", "hadBreakfastAt");
                        String XSD_hadBreakfastAtTime = formatter.convertOWLtimeToXSDdatetime(Integer.parseInt(hadBreakfastAtTime));
                        queryHAROntology.addDataProperty("Yusha", "didActivityHavingBreakfast", XSD_hadBreakfastAtTime);
                        // Yusha-didActivityHavingBreakfast-xsd:datetime
                        String individual = "Yusha-didActivityHavingBreakfast-"+XSD_hadBreakfastAtTime;
                        String isInKitchenAtTime = kitchenOntology.inferDataProperty("fd06", "isInKitchenAt");
                        String nearKitchenTableAtTime = kitchenOntology.inferDataProperty("fd06", "nearKitchenTableAt");
                        String pouredAtTime = kitchenOntology.inferDataProperty("fd06", "pouredAt");
                        String drankAtTime = kitchenOntology.inferDataProperty("fd06", "drankAt");
                        String XSD_isInKitchenAtTime = formatter.convertOWLtimeToXSDdatetime(Integer.parseInt(isInKitchenAtTime));
                        String XSD_nearKitchenTableAtTime = formatter.convertOWLtimeToXSDdatetime(Integer.parseInt(nearKitchenTableAtTime));
                        String XSD_pouredAtTime = formatter.convertOWLtimeToXSDdatetime(Integer.parseInt(pouredAtTime));
                        String XSD_drankAtTime = formatter.convertOWLtimeToXSDdatetime(Integer.parseInt(drankAtTime));
                        // isInKitchen xsd:datatime
                        queryHAROntology.addDataProperty(individual, "isInKitchen", XSD_isInKitchenAtTime);
                        // isNearKitchenTable xsd:datetime
                        queryHAROntology.addDataProperty(individual, "isNearKitchenTable", XSD_nearKitchenTableAtTime);
                        // didActionPouring xsd:datetime
                        queryHAROntology.addDataProperty(individual, "didActionPouring", XSD_pouredAtTime);
                        // didActionDrinking xsd:datetime
                        queryHAROntology.addDataProperty(individual, "didActionDrinking", XSD_drankAtTime);
                    }
                }
            }
        };
        executor.scheduleAtFixedRate(periodicTask, 0, 60, TimeUnit.SECONDS);

        // Keeping the thread alive because we have firebase event-listeners
        while (true) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
