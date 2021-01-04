import it.emarolab.amor.owlInterface.OWLReferences;
import it.emarolab.owloop.core.Axiom;
import it.emarolab.owloop.descriptor.utility.individualDescriptor.FullIndividualDesc;

import java.util.Date;

// The kitchen ontology class
public class OntologyHandler {

    OWLReferences ontoRef;
    DateFormatter formatter = new DateFormatter();
    boolean isRunningFlag;

    public void initializeOntology(String referenceName, String filePath, String ontologyPath, boolean bufferingReasoner){

        // Disabling 'internal logs' (so that our console is clean)
        Axiom.Descriptor.OntologyReference.activateAMORlogging(false);

        // Creating an object that is 'a reference to an ontology' (for an owl file that already exists)
        this.ontoRef = Axiom.Descriptor.OntologyReference.newOWLReferenceFromFileWithPellet(
                referenceName,
                filePath,
                ontologyPath,
                bufferingReasoner
        );
    }

    public void addOrUpdateObjectProperty(String subject, String objectProperty, String object) {

        FullIndividualDesc individualDesc = new FullIndividualDesc(subject, ontoRef);
        individualDesc.readAxioms();
        individualDesc.addObject(objectProperty, object);
        individualDesc.writeAxioms();
        ontoRef.saveOntology(ontoRef.getFilePath());
    }

    // adds new if axiom does not exist and updates axiom if it exists
    public void addOrUpdateDataProperty(String subject, String dataProperty, Object object) {

        FullIndividualDesc individualDesc = new FullIndividualDesc(subject, ontoRef);
        individualDesc.readAxioms();
        individualDesc.removeData(dataProperty);
        individualDesc.addData(dataProperty, object);
        individualDesc.writeAxioms();
        ontoRef.saveOntology(ontoRef.getFilePath());
    }

    // adds new if axiom does not exist or even if it exists
    public void addDataProperty(String subject, String dataProperty, Object object) {

        FullIndividualDesc individualDesc = new FullIndividualDesc(subject, ontoRef);
        individualDesc.readAxioms();
        individualDesc.addData(dataProperty, object);
        individualDesc.writeAxioms();
        ontoRef.saveOntology(ontoRef.getFilePath());
    }

    public void removeObjectProperty(String subject, String objectProperty) {

        FullIndividualDesc individualDesc = new FullIndividualDesc(subject, ontoRef);
        individualDesc.readAxioms();
        individualDesc.removeObject(objectProperty);
        individualDesc.writeAxioms();
        ontoRef.saveOntology(ontoRef.getFilePath());
    }

    public void removeDataProperty(String subject, String dataProperty) {

        FullIndividualDesc individualDesc = new FullIndividualDesc(subject, ontoRef);
        individualDesc.readAxioms();
        individualDesc.removeData(dataProperty);
        individualDesc.writeAxioms();
        ontoRef.saveOntology(ontoRef.getFilePath());
    }

    public void updateCurrentTime() {

        this.addOrUpdateDataProperty("Instant_CurrentTime", "hasTime", formatter.convertDateToOWLtime(new Date()));
    }

    public boolean getIsRunningFlag() {
        return isRunningFlag;
    }

    public void setIsRunningFlag(boolean booleanValue) {
        isRunningFlag = booleanValue;
    }

    public String inferObjectProperty(String subject, String objectProperty) {
        FullIndividualDesc individualDesc = new FullIndividualDesc(subject, ontoRef);
        individualDesc.readAxioms();
        ontoRef.synchronizeReasoner();
        return ontoRef.getOWLObjectName(individualDesc.getIndividualFromObjectProperty(objectProperty));
    }

    public String inferDataProperty(String subject, String dataProperty) {
        FullIndividualDesc individualDesc = new FullIndividualDesc(subject, ontoRef);
        individualDesc.readAxioms();
        ontoRef.synchronizeReasoner();
        return ontoRef.getOWLObjectName(individualDesc.getLiteralFromDataProperty(dataProperty));
    }
}