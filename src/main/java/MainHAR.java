import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import java.io.IOException;
import java.text.ParseException;
import java.util.Timer;
import java.util.TimerTask;

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

        // Initialize BathRoomActivitiesOntology
        OntologyHandler bathRoomOntology = new OntologyHandler();
        bathRoomOntology.initializeOntology(
                "BathRoomActivitiesOntology",
                "src/main/resources/BathRoomActivitiesOntology.owl",
                "http://www.semanticweb.org/Arianna/BathRoomActivitiesOntology",
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
                //System.out.println("--> Child added");
                if (dataSnapshot.getKey().equals("associatedWithName")) {
                    //set Flag
                    //remove all object properties
                    kitchenOntology.removeObjectProperty("fd06", "isIn");
                    kitchenOntology.removeObjectProperty("fd06", "isNear");
                    kitchenOntology.removeObjectProperty("fd06", "didAction");
                    //remove all data   properties
                    kitchenOntology.removeDataProperty("fd06", "isInKitchenAt");
                    kitchenOntology.removeDataProperty("fd06", "nearKitchenTableAt");
                    kitchenOntology.removeDataProperty("fd06", "drankAt");
                    kitchenOntology.removeDataProperty("fd06", "pouredAt");
                    //set Flag
                    //remove all object properties
                    bathRoomOntology.removeObjectProperty("fd06", "isIn");
                    bathRoomOntology.removeObjectProperty("fd06", "isNear");
                    bathRoomOntology.removeObjectProperty("fd06", "didAction");
                    //remove all data   properties
                    bathRoomOntology.removeDataProperty("fd06", "isInBathRoomAt");
                    bathRoomOntology.removeDataProperty("fd06", "nearWashbasinAt");
                    bathRoomOntology.removeDataProperty("fd06", "brushedTeethAt");

                    kitchenOntology.addOrUpdateDataProperty("fd06", "associatedWithName", dataSnapshot.getValue());
                    bathRoomOntology.addOrUpdateDataProperty("fd06", "associatedWithName", dataSnapshot.getValue());
                } else if (dataSnapshot.getKey().equals("userPreference_averageTimeAtKitchenTableDuringBreakfast_inSeconds")) {
                    kitchenOntology.addOrUpdateDataProperty("fd06", "userPreference_averageTimeAtKitchenTableDuringBreakfast_inSeconds", dataSnapshot.getValue());
                } else if (dataSnapshot.getKey().equals("userPreference_averageTimeAtWashbasinDuringMorningRoutine_inSeconds")) {
                    bathRoomOntology.addOrUpdateDataProperty("fd06", "userPreference_averageTimeAtWashbasinDuringMorningRoutine_inSeconds", dataSnapshot.getValue());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                //System.out.println("--> Child changed");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //System.out.println("--> Child removed");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
                //System.out.println("--> Child moved");
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
                //System.out.println("--> Child added");
                System.out.println("--> Localization data added to DB");
                if (dataSnapshot.getKey().equals("Kitchen")) {
                    System.out.println("-----> Kitchen ontology updated");
                    System.out.println("-----> (isIn Kitchen)");
                    //set Flag
                    kitchenOntology.setIsRunningFlag(true);

                    //------
                    bathRoomOntology.setIsRunningFlag(false);
                    //------

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
                } else if (dataSnapshot.getKey().equals("BathRoom")) {
                    System.out.println("-----> BathRoom ontology updated");
                    System.out.println("-----> (isIn BathRoom)");
                    //set Flag
                    bathRoomOntology.setIsRunningFlag(true);

                    //------
                    kitchenOntology.setIsRunningFlag(false);
                    //------

                    //update in ontology: fd06 isIn BathRoom
                    bathRoomOntology.addOrUpdateObjectProperty("fd06", "isIn", "BathRoom");
                    //update in ontology: fd06 isInBathRoomAt <datasnapshot.getValue()>
                    try {
                        bathRoomOntology.addOrUpdateDataProperty(
                                "fd06",
                                "isInBathRoomAt",
                                formatter.convertXSDdatetimeToOWLtime(String.valueOf(dataSnapshot.getValue()))
                        );
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                //System.out.println("--> Child changed");

                if (dataSnapshot.getKey().equals("Kitchen")) {
                    System.out.println("-----> Kitchen ontology updated");
                    System.out.println("-----> (isIn Kitchen)");
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
                } else if (dataSnapshot.getKey().equals("BathRoom")) {
                    System.out.println("-----> BathRoom ontology updated");
                    System.out.println("-----> (isIn BathRoom)");
                    //set Flag
                    bathRoomOntology.setIsRunningFlag(true);
                    //update in ontology: fd06 isInBathRoomAt <datasnapshot.getValue()>
                    try {
                        bathRoomOntology.addOrUpdateDataProperty(
                                "fd06",
                                "isInBathRoomAt",
                                formatter.convertXSDdatetimeToOWLtime(String.valueOf(dataSnapshot.getValue()))
                        );
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //System.out.println("--> Child removed");
//                if (dataSnapshot.getKey().equals("Kitchen")) {
//                    System.out.println("-----> Kitchen ontology cleaned");
//                    //set Flag
//                    kitchenOntology.setIsRunningFlag(false);
//                    //remove all object properties
//                    kitchenOntology.removeObjectProperty("fd06", "isIn");
//                    kitchenOntology.removeObjectProperty("fd06", "isNear");
//                    kitchenOntology.removeObjectProperty("fd06", "didAction");
//                    //remove all data   properties
//                    //kitchenOntology.removeDataProperty("fd06", "associatedWithName");
//                    //kitchenOntology.removeDataProperty("fd06", "userPreference_averageTimeAtKitchenTableDuringBreakfast_inSeconds");
//                    kitchenOntology.removeDataProperty("fd06", "isInKitchenAt");
//                    kitchenOntology.removeDataProperty("fd06", "nearKitchenTableAt");
//                    kitchenOntology.removeDataProperty("fd06", "drankAt");
//                    kitchenOntology.removeDataProperty("fd06", "pouredAt");
//                } else if (dataSnapshot.getKey().equals("BathRoom")) {
//                    System.out.println("-----> BathRoom ontology cleaned");
//                    //set Flag
//                    bathRoomOntology.setIsRunningFlag(false);
//                    //remove all object properties
//                    bathRoomOntology.removeObjectProperty("fd06", "isIn");
//                    bathRoomOntology.removeObjectProperty("fd06", "isNear");
//                    bathRoomOntology.removeObjectProperty("fd06", "didAction");
//                    //remove all data   properties
//                    //kitchenOntology.removeDataProperty("fd06", "associatedWithName");
//                    //kitchenOntology.removeDataProperty("fd06", "userPreference_averageTimeAtKitchenTableDuringBreakfast_inSeconds");
//                    bathRoomOntology.removeDataProperty("fd06", "isInBathRoomAt");
//                    bathRoomOntology.removeDataProperty("fd06", "nearWashbasinAt");
//                    bathRoomOntology.removeDataProperty("fd06", "brushedTeethAt");
//                }
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
                //System.out.println("--> Child added");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                //System.out.println("--> Child changed");
                System.out.println("--> Localization data added to DB");
                if (dataSnapshot.getKey().equals("KitchenTable")) {
                    System.out.println("-----> Kitchen ontology updated");
                    System.out.println("-----> (isNear KitchenTable)");
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
                } else if (dataSnapshot.getKey().equals("Washbasin")) {
                    System.out.println("-----> BathRoom ontology updated");
                    System.out.println("-----> (isNear Washbasin)");
                    //update in ontology: fd06 isNear Washbasin
                    bathRoomOntology.addOrUpdateObjectProperty("fd06", "isNear", "Washbasin");
                    //update in ontology: fd06 nearWashbasinAt <datasnapshot.getValue()>
                    try {
                        bathRoomOntology.addOrUpdateDataProperty(
                                "fd06",
                                "nearWashbasinAt",
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

        // Firebase event-listener for "/fd06/didAction"
        DatabaseReference didActionRef = FirebaseDatabase.getInstance()
                .getReference("/fd06/didAction");
        didActionRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                //System.out.println("--> Child added");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                //System.out.println("--> Child changed");
                System.out.println("--> Action data added to DB");
                if (dataSnapshot.getKey().equals("Drinking")) {
                    System.out.println("-----> Kitchen ontology updated");
                    System.out.println("-----> (didAction Drinking)");
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
                    System.out.println("-----> Kitchen ontology updated");
                    System.out.println("-----> (didAction Pouring)");
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
                } else if (dataSnapshot.getKey().equals("BrushingTeeth")) {
                    System.out.println("-----> BathRoom ontology updated");
                    System.out.println("-----> (didAction BrushingTeeth)");
                    //update in ontology: fd06 didAction BrushingTeeth
                    bathRoomOntology.addOrUpdateObjectProperty("fd06", "didAction", "BrushingTeeth");
                    //update in ontology: fd06 pouredAt <datasnapshot.getValue()>
                    try {
                        bathRoomOntology.addOrUpdateDataProperty(
                                "fd06",
                                "brushedTeethAt",
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

        //Timertask
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (kitchenOntology.getIsRunningFlag()) {
                    //System.out.println("--> Periodic inference running");
                    //System.out.println("---- KitchenOnto Flag "+kitchenOntology.getIsRunningFlag());
                    // update current time in kitchenActivitiesOnto
                    kitchenOntology.updateCurrentTime();
                    // reason and save recognised activity details in queryHAROnto
                    String inference = kitchenOntology.inferObjectProperty("fd06", "didActivity");
                    //System.out.println("K, "+inference);
                    if (inference != null && inference.equalsIgnoreCase("HavingBreakfast")) {
                        System.out.println("=====> Activity recognised: HavingBreakfast");
                        System.out.println("-----> Query ontology updated");
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
                        kitchenOntology.setIsRunningFlag(false);
                    }
                } else if (bathRoomOntology.getIsRunningFlag()) {
                    //System.out.println("--> Periodic inference running");
                    //System.out.println("---- BathRoomOnto Flag "+bathRoomOntology.getIsRunningFlag());
                    // update current time in BathRoomActivitiesOnto
                    bathRoomOntology.updateCurrentTime();
                    // reason and save recognised activity details in queryHAROnto
                    String inference = bathRoomOntology.inferObjectProperty("fd06", "didActivity");
                    //System.out.println("B, "+inference);
                    if (inference != null && inference.equalsIgnoreCase("RoutineMorningHygiene")) {
                        //System.out.println("---- Activity recognised: Updating queryHARonto");
                        System.out.println("=====> Activity recognised: RoutineMorningHygiene");
                        System.out.println("-----> Query ontology updated");
                        //put all relevant info in queryHARonto
                        // Yusha associatedWithWatchID fd06
                        queryHAROntology.addOrUpdateObjectProperty("Yusha", "associatedWithWatchID", "fd06");
                        // Yusha didActivityHavingBreakfast xsd:datetime
                        String routineMorningHygieneAtTime = bathRoomOntology.inferDataProperty("fd06", "routineMorningHygieneAt");
                        String XSD_routineMorningHygieneAtTime = formatter.convertOWLtimeToXSDdatetime(Integer.parseInt(routineMorningHygieneAtTime));
                        queryHAROntology.addDataProperty("Yusha", "didActivityRoutineMorningHygiene", XSD_routineMorningHygieneAtTime);
                        // Yusha-didActivityRoutineMorningHygiene-xsd:datetime
                        String individual = "Yusha-didActivityRoutineMorningHygiene-"+XSD_routineMorningHygieneAtTime;
                        String isInBathRoomAtTime = bathRoomOntology.inferDataProperty("fd06", "isInBathRoomAt");
                        String nearWashbasinAtTime = bathRoomOntology.inferDataProperty("fd06", "nearWashbasinAt");
                        String brushedTeethAtTime = bathRoomOntology.inferDataProperty("fd06", "brushedTeethAt");
                        String XSD_isInBathRoomAtTime = formatter.convertOWLtimeToXSDdatetime(Integer.parseInt(isInBathRoomAtTime));
                        String XSD_nearWashbasinAtTime = formatter.convertOWLtimeToXSDdatetime(Integer.parseInt(nearWashbasinAtTime));
                        String XSD_brushedTeethAtTime = formatter.convertOWLtimeToXSDdatetime(Integer.parseInt(brushedTeethAtTime));
                        // isInKitchen xsd:datatime
                        queryHAROntology.addDataProperty(individual, "isInBathRoom", XSD_isInBathRoomAtTime);
                        // isNearKitchenTable xsd:datetime
                        queryHAROntology.addDataProperty(individual, "isNearWashbasin", XSD_nearWashbasinAtTime);
                        // didActionPouring xsd:datetime
                        queryHAROntology.addDataProperty(individual, "didActionBrushingTeeth", XSD_brushedTeethAtTime);
                        bathRoomOntology.setIsRunningFlag(false);
                    }
                }
            }
        };
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, 2*1000);

        // Keeping the thread alive because we have firebase event-listeners
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}