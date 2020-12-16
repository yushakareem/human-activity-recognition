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

        // Firebase initialization
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(MainHAR.class.getClassLoader().getResourceAsStream("key.json")))
                .setDatabaseUrl("https://harfirebasedb-default-rtdb.firebaseio.com/")
                .build();
        FirebaseApp.initializeApp(options);

        // Firebase event-listeners
        DatabaseReference isInRef = FirebaseDatabase.getInstance()
                .getReference("/fd06/isIn");

        isInRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                // I want to check if it is Kitchen or LivingRoom child
                // based on that initialize that ontology
                System.out.println("Child added");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                System.out.println("Child changed");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                System.out.println("Child removed");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
                System.out.println("Child moved");
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
