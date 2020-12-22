import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import java.io.IOException;

public class MainHAR {
    public static void main(String[] args) throws IOException {

        // To suppress log4j warning coming from owloop-2.1
        Logger.getRootLogger().setLevel(Level.OFF);

        // Initialize KitchenActivitiesOntology
        KitchenActivitiesOntology kitchenOntology = new KitchenActivitiesOntology();
        kitchenOntology.initializeOntology();

        // Firebase initialization
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(MainHAR.class.getClassLoader().getResourceAsStream("key.json")))
                .setDatabaseUrl("https://harfirebasedb-default-rtdb.firebaseio.com/")
                .build();
        FirebaseApp.initializeApp(options);

        // Firebase event-listener for "/fd06/isIn"
        DatabaseReference isInRef = FirebaseDatabase.getInstance()
                .getReference("/fd06/isIn");

        isInRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                System.out.println("--> Child added");
                if (dataSnapshot.getKey().equals("Kitchen")) {
                    //update in ontology fd06 isIn Kitchen
                    kitchenOntology.addOrUpdateObjectProperty("fd06", "isIn", "Kitchen");
                    //update in ontology Instant_CurrentTime hasTime _____

                } else if (dataSnapshot.getKey().equals("LivingRoom")) {
                    //update in ontology fd06 isIn LivingRoom
                    //update in ontology Instant_CurrentTime hasTime _____
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                System.out.println("--> Child changed");
                if (dataSnapshot.getKey().equals("Kitchen")) {
                    System.out.println("Update the kitchen ontology and reason.");
                } else if (dataSnapshot.getKey().equals("LivingRoom")) {
                    System.out.println("Update the LivingRoom ontology and reason.");
                }
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
            public void onCancelled(DatabaseError databaseError) {}
        });

//        DatabaseReference kitchenRef = FirebaseDatabase.getInstance()
//                .getReference("/fd06/isIn/Kitchen");
//
//        kitchenRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Object document = dataSnapshot.getValue();
//                System.out.println(document);
//                System.out.println("Kitchen child data changed");
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//            }
//        });

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
