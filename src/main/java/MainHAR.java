import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

public class MainHAR {
    public static void main(String[] args) throws IOException {

        // To suppress log4j warning coming from owloop-2.1
        Logger.getRootLogger().setLevel(Level.OFF);

        // Initialize KitchenActivitiesOntology
        KitchenActivitiesOntology kitchenOntology = new KitchenActivitiesOntology();
        kitchenOntology.initializeOntology();

        // Initialize DateFormatter
        DateFormatter formatter = new DateFormatter();

        // Firebase initialization
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(MainHAR.class.getClassLoader().getResourceAsStream("key.json")))
                .setDatabaseUrl("https://harfirebasedb-default-rtdb.firebaseio.com/")
                .build();
        FirebaseApp.initializeApp(options);

        // Firebase event-listener for user's name and preferences
        DatabaseReference associatedWithName = FirebaseDatabase.getInstance()
                .getReference("/fd06");
        associatedWithName.addChildEventListener(new ChildEventListener() {
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
                    //update in ontology: fd06 isIn Kitchen
                    kitchenOntology.addOrUpdateObjectProperty("fd06", "isIn", "Kitchen");
                    //update in ontology: Instant_CurrentTime hasTime <insert_latest_time>
                    kitchenOntology.updateCurrentTime();
                } else if (dataSnapshot.getKey().equals("LivingRoom")) {
                    //update in ontology fd06 isIn LivingRoom
                    //update in ontology Instant_CurrentTime hasTime <insert_latest_time>
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                System.out.println("--> Child changed");
                if (dataSnapshot.getKey().equals("Kitchen")) {
                    //update in ontology: Instant_CurrentTime hasTime <insert_latest_time>
                    kitchenOntology.updateCurrentTime();
                } else if (dataSnapshot.getKey().equals("LivingRoom")) {
                    //update in ontology Instant_CurrentTime hasTime <insert_latest_time>
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                System.out.println("--> Child removed");
                //check for inference one last time
                //remove objectProperties and dataProperties
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
        DatabaseReference isNear = FirebaseDatabase.getInstance()
                .getReference("/fd06/isNear");
        isNear.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                System.out.println("--> Child added");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                System.out.println("--> Child changed");
                if (dataSnapshot.getKey().equals("KitchenTable")) {
                    //update in ontology: Instant_CurrentTime hasTime <insert_latest_time>
                    kitchenOntology.updateCurrentTime();
                    //update in ontology: fd06 isNear KitchenTable
                    kitchenOntology.addOrUpdateObjectProperty("fd06", "isNear", "KitchenTable");
                    //update in ontology: fd06 nearKitchenTableAt <datasnapshot.getValue()>
                    try {
                        kitchenOntology.addOrUpdateDataProperty("fd06", "nearKitchenTableAt", formatter.convertXSDdatetimeToOWLtime(String.valueOf(dataSnapshot.getValue())));
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
        DatabaseReference didAction = FirebaseDatabase.getInstance()
                .getReference("/fd06/didAction");
        didAction.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                System.out.println("--> Child added");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                System.out.println("--> Child changed");
                if (dataSnapshot.getKey().equals("Drinking")) {
                    //update in ontology: Instant_CurrentTime hasTime <insert_latest_time>
                    kitchenOntology.updateCurrentTime();
                    //update in ontology: fd06 didAction Drinking
                    kitchenOntology.addOrUpdateObjectProperty("fd06", "didAction", "Drinking");
                    //update in ontology: fd06 drankAt <datasnapshot.getValue()>
                    try {
                        kitchenOntology.addOrUpdateDataProperty("fd06", "drankAt", formatter.convertXSDdatetimeToOWLtime(String.valueOf(dataSnapshot.getValue())));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if (dataSnapshot.getKey().equals("Pouring")) {
                    //update in ontology: Instant_CurrentTime hasTime <insert_latest_time>
                    kitchenOntology.updateCurrentTime();
                    //update in ontology: fd06 didAction Pouring
                    kitchenOntology.addOrUpdateObjectProperty("fd06", "didAction", "Pouring");
                    //update in ontology: fd06 pouredAt <datasnapshot.getValue()>
                    try {
                        kitchenOntology.addOrUpdateDataProperty("fd06", "pouredAt", formatter.convertXSDdatetimeToOWLtime(String.valueOf(dataSnapshot.getValue())));
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

        // Just keeping the thread alive because we have firebase event-listeners
        while (true) {
//            try {
//                Thread.sleep(1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }
}
