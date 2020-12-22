import it.emarolab.amor.owlInterface.OWLReferences;
import it.emarolab.owloop.core.Axiom;
import it.emarolab.owloop.descriptor.utility.individualDescriptor.FullIndividualDesc;

import java.util.Date;

public class KitchenActivitiesOntology {

    OWLReferences ontoRef;
    DateFormatter formatter = new DateFormatter();

    public void initializeOntology(){

        // Disabling 'internal logs' (so that our console is clean)
        Axiom.Descriptor.OntologyReference.activateAMORlogging(false);

        // Creating an object that is 'a reference to an ontology' (for an owl file that already exists)
        this.ontoRef = Axiom.Descriptor.OntologyReference.newOWLReferenceFromFileWithPellet(
                "KitchenActivitiesOntology",
                "src/main/resources/KitchenActivitiesOntology.owl",
                "http://www.semanticweb.org/Arianna/KitchenActivitiesOntology",
                true
        );
    }

    public void addOrUpdateObjectProperty(String subject, String objectProperty, String object) {

        FullIndividualDesc individualDesc = new FullIndividualDesc(subject, ontoRef);
        individualDesc.readAxioms();
        individualDesc.addObject(objectProperty, object);
        individualDesc.writeAxioms();
        ontoRef.saveOntology(ontoRef.getFilePath());
    }

    public void addOrUpdateDataProperty(String subject, String dataProperty, Object object) {

        FullIndividualDesc individualDesc = new FullIndividualDesc(subject, ontoRef);
        individualDesc.readAxioms();
        individualDesc.removeData(dataProperty);
        individualDesc.addData(dataProperty, object);
        individualDesc.writeAxioms();
        ontoRef.saveOntology(ontoRef.getFilePath());
    }

    public void updateCurrentTime() {

        this.addOrUpdateDataProperty("Instant_CurrentTime", "hasTime", formatter.convertDateToOWLtime(new Date()));
    }
}