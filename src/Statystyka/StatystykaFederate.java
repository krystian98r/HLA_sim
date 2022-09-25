package Statystyka;

import Klienci.Klient;
import hla.rti1516e.*;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.exceptions.FederatesCurrentlyJoined;
import hla.rti1516e.exceptions.FederationExecutionAlreadyExists;
import hla.rti1516e.exceptions.FederationExecutionDoesNotExist;
import hla.rti1516e.exceptions.RTIexception;
import hla.rti1516e.time.HLAfloat64Interval;
import hla.rti1516e.time.HLAfloat64Time;
import hla.rti1516e.time.HLAfloat64TimeFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class StatystykaFederate {
    //----------------------------------------------------------
    //                    STATIC VARIABLES
    //----------------------------------------------------------

    /**
     * The sync point all federates will sync up on before starting
     */
    public static final String READY_TO_RUN = "ReadyToRun";

    //----------------------------------------------------------
    //                   INSTANCE VARIABLES
    //----------------------------------------------------------
    private RTIambassador rtiamb;
    private StatystykaFederateAmbassador fedamb;  // created when we connect
    private HLAfloat64TimeFactory timeFactory; // set when we join
    protected EncoderFactory encoderFactory;     // set when we join

    // caches of handle types - set once we join a federation
    protected ObjectClassHandle statystykaHandle;
    protected Statystyka statystyka = new Statystyka();
    protected ObjectClassHandle klientHandle;
    protected AttributeHandle czasWejsciaKlientHandle;
    protected AttributeHandle czasZakupowKlientHandle;
    protected AttributeHandle nrKlientaKlientHandle;
    protected AttributeHandle nrKasyKlientHandle;
    protected AttributeHandle uprzywilejowanyKlientHandle;
    protected InteractionClassHandle obsluzHandle;
    protected ParameterHandle nrKlientaObsluzHandle;
    protected ParameterHandle nrKasyObsluzHandle;
    protected InteractionClassHandle czekajHandle;
    protected ParameterHandle nrKlientaCzekajHandle;
    protected ParameterHandle nrKasyCzekajHandle;
    protected ParameterHandle uprzywilejowanyCzekajHandle;
    protected InteractionClassHandle otworzHandle;
    protected ParameterHandle nrKasyOtworzHandle;
    protected InteractionClassHandle zamknijHandle;
    protected ParameterHandle nrKasyZamknijHandle;
    protected ArrayList<Klient> klienci = new ArrayList<>();

    //----------------------------------------------------------
    //                      CONSTRUCTORS
    //----------------------------------------------------------

    //----------------------------------------------------------
    //                    INSTANCE METHODS
    //----------------------------------------------------------

    /**
     * This is just a helper method to make sure all logging it output in the same form
     */
    private void log(String message) {
        System.out.println("StatystykaFederate   : " + message);
    }

    /**
     * This method will block until the user presses enter
     */
    private void waitForUser() {
        log(" >>>>>>>>>> Press Enter to Continue <<<<<<<<<<");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            reader.readLine();
        } catch (Exception e) {
            log("Error while waiting for user input: " + e.getMessage());
            e.printStackTrace();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    ////////////////////////// Main Simulation Method /////////////////////////
    ///////////////////////////////////////////////////////////////////////////

    /**
     * This is the main simulation loop. It can be thought of as the main method of
     * the federate. For a description of the basic flow of this federate, see the
     * class level comments
     */
    public void runFederate(String federateName) throws Exception {
        /////////////////////////////////////////////////
        // 1 & 2. create the RTIambassador and Connect //
        /////////////////////////////////////////////////
        log("Creating RTIambassador");
        rtiamb = RtiFactoryFactory.getRtiFactory().getRtiAmbassador();
        encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();

        // connect
        log("Connecting...");
        fedamb = new StatystykaFederateAmbassador(this);
        rtiamb.connect(fedamb, CallbackModel.HLA_EVOKED);

        //////////////////////////////
        // 3. create the federation //
        //////////////////////////////
        log("Creating Federation...");
        // We attempt to create a new federation with the first three of the
        // sklep FOM modules covering processes, food and drink
        try {
            URL[] modules = new URL[]{
                    (new File("foms/Sklep.xml")).toURI().toURL(),
            };

            rtiamb.createFederationExecution("SklepFederation", modules);
            log("Created Federation");
        } catch (FederationExecutionAlreadyExists exists) {
            log("Didn't create federation, it already existed");
        } catch (MalformedURLException urle) {
            log("Exception loading one of the FOM modules from disk: " + urle.getMessage());
            urle.printStackTrace();
            return;
        }

        ////////////////////////////
        // 4. join the federation //
        ////////////////////////////
        rtiamb.joinFederationExecution(federateName,            // name for the federate
                "Statystyka",   // federate type
                "SklepFederation"     // name of federation
        );           // modules we want to add

        log("Joined Federation as " + federateName);

        // cache the time factory for easy access
        this.timeFactory = (HLAfloat64TimeFactory) rtiamb.getTimeFactory();

        ////////////////////////////////
        // 5. announce the sync point //
        ////////////////////////////////
        // announce a sync point to get everyone on the same page. if the point
        // has already been registered, we'll get a callback saying it failed,
        // but we don't care about that, as long as someone registered it
        rtiamb.registerFederationSynchronizationPoint(READY_TO_RUN, null);
        // wait until the point is announced
        while (fedamb.isAnnounced == false) {
            rtiamb.evokeMultipleCallbacks(0.1, 0.2);
        }

        // WAIT FOR USER TO KICK US OFF
        // So that there is time to add other federates, we will wait until the
        // user hits enter before proceeding. That was, you have time to start
        // other federates.
        waitForUser();

        ///////////////////////////////////////////////////////
        // 6. achieve the point and wait for synchronization //
        ///////////////////////////////////////////////////////
        // tell the RTI we are ready to move past the sync point and then wait
        // until the federation has synchronized on
        rtiamb.synchronizationPointAchieved(READY_TO_RUN);
        log("Achieved sync point: " + READY_TO_RUN + ", waiting for federation...");
        while (fedamb.isReadyToRun == false) {
            rtiamb.evokeMultipleCallbacks(0.1, 0.2);
        }

        /////////////////////////////
        // 7. enable time policies //
        /////////////////////////////
        // in this section we enable/disable all time policies
        // note that this step is optional!
        enableTimePolicy();
        log("Time Policy Enabled");

        //////////////////////////////
        // 8. publish and subscribe //
        //////////////////////////////
        // in this section we tell the RTI of all the data we are going to
        // produce, and all the data we want to know about
        publishAndSubscribe();
        log("Published and Subscribed");

        /////////////////////////////////////
        // 9. register an object to update //
        /////////////////////////////////////
//        ObjectInstanceHandle objectHandle = rtiamb.registerObjectInstance(statystykaHandle);
//        log("Registered Storage, handle=" + objectHandle);

        /////////////////////////////////////
        // 10. do the main simulation loop //
        /////////////////////////////////////
        // here is where we do the meat of our work. in each iteration, we will
        // update the attribute values of the object we registered, and will
        // send an interaction.

        while (fedamb.isRunning) {
            System.out.println("\u001B[31mOtwarte kasy: " + statystyka.getOtwarteKasy());
            System.out.println("Klienci robiący zakupy: " + statystyka.getKlienciNaSklepie());
            System.out.println("Klienci przy kasach: " + statystyka.getKlienciPrzyKasach());
            System.out.println("Klienci obsłużeni: " + statystyka.getObsluzeniKlienci());
            System.out.println("Średni czas zakupów: " + statystyka.sredniCzasZakupow());
            System.out.println("Średni czas oczekiwania na obsługę: " + statystyka.sredniCzasObslugi());
            System.out.println("Uprzywilejowanych klientów: " + statystyka.getSumaUprzywilejowanych() + " (" + statystyka.procentUprzywilejowanych() + "%)\u001B[0m");

            advanceTime(1);
            log("Time Advanced to " + fedamb.federateTime);
        }

        //////////////////////////////////////
        // 11. delete the object we created //
        //////////////////////////////////////
//		deleteObject( objectHandle );
//		log( "Deleted Object, handle=" + objectHandle );

        ////////////////////////////////////
        // 12. resign from the federation //
        ////////////////////////////////////
        rtiamb.resignFederationExecution(ResignAction.DELETE_OBJECTS);
        log("Resigned from Federation");

        ////////////////////////////////////////
        // 13. try and destroy the federation //
        ////////////////////////////////////////
        // NOTE: we won't die if we can't do this because other federates
        //       remain. in that case we'll leave it for them to clean up
        try {
            rtiamb.destroyFederationExecution("ExampleFederation");
            log("Destroyed Federation");
        } catch (FederationExecutionDoesNotExist dne) {
            log("No need to destroy federation, it doesn't exist");
        } catch (FederatesCurrentlyJoined fcj) {
            log("Didn't destroy federation, federates still joined");
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////// Helper Methods //////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * This method will attempt to enable the various time related properties for
     * the federate
     */
    private void enableTimePolicy() throws Exception {
        // NOTE: Unfortunately, the LogicalTime/LogicalTimeInterval create code is
        //       Portico specific. You will have to alter this if you move to a
        //       different RTI implementation. As such, we've isolated it into a
        //       method so that any change only needs to happen in a couple of spots
        HLAfloat64Interval lookahead = timeFactory.makeInterval(fedamb.federateLookahead);

        ////////////////////////////
        // enable time regulation //
        ////////////////////////////
        this.rtiamb.enableTimeRegulation(lookahead);

        // tick until we get the callback
        while (fedamb.isRegulating == false) {
            rtiamb.evokeMultipleCallbacks(0.1, 0.2);
        }

        /////////////////////////////
        // enable time constrained //
        /////////////////////////////
        this.rtiamb.enableTimeConstrained();

        // tick until we get the callback
        while (fedamb.isConstrained == false) {
            rtiamb.evokeMultipleCallbacks(0.1, 0.2);
        }
    }

    /**
     * This method will inform the RTI about the types of data that the federate will
     * be creating, and the types of data we are interested in hearing about as other
     * federates produce it.
     */
    private void publishAndSubscribe() throws RTIexception {
        // subscribe for Klient
        this.klientHandle = rtiamb.getObjectClassHandle("HLAobjectRoot.Klient");
        this.czasWejsciaKlientHandle = rtiamb.getAttributeHandle(klientHandle, "czasWejscia");
        this.nrKlientaKlientHandle = rtiamb.getAttributeHandle(klientHandle, "nrKlienta");
        this.czasZakupowKlientHandle = rtiamb.getAttributeHandle(klientHandle, "czasZakupow");
        this.uprzywilejowanyKlientHandle = rtiamb.getAttributeHandle(klientHandle, "uprzywilejowany");
        this.nrKasyKlientHandle = rtiamb.getAttributeHandle(klientHandle, "nrKasy");
        AttributeHandleSet attributes = rtiamb.getAttributeHandleSetFactory().create();
        attributes.add(czasWejsciaKlientHandle);
        attributes.add(nrKlientaKlientHandle);
        attributes.add(uprzywilejowanyKlientHandle);
        attributes.add(czasZakupowKlientHandle);
        attributes.add(nrKasyKlientHandle);
        rtiamb.subscribeObjectClassAttributes(klientHandle, attributes);

        // subscribe for Czekaj interaction
        String iname = "HLAinteractionRoot.Klient.czekaj";
        czekajHandle = rtiamb.getInteractionClassHandle(iname);
        nrKlientaCzekajHandle = rtiamb.getParameterHandle(rtiamb.getInteractionClassHandle(iname), "nrKlienta");
        nrKasyCzekajHandle = rtiamb.getParameterHandle(rtiamb.getInteractionClassHandle(iname), "nrKasy");
        uprzywilejowanyCzekajHandle = rtiamb.getParameterHandle(rtiamb.getInteractionClassHandle(iname), "uprzywilejowany");
        rtiamb.subscribeInteractionClass(czekajHandle);

        // subscribe for Obsluz interaction
        iname = "HLAinteractionRoot.Kasa.obsluz";
        obsluzHandle = rtiamb.getInteractionClassHandle(iname);
        nrKlientaObsluzHandle = rtiamb.getParameterHandle(rtiamb.getInteractionClassHandle(iname), "nrKlienta");
        nrKasyObsluzHandle = rtiamb.getParameterHandle(rtiamb.getInteractionClassHandle(iname), "nrKasy");
        rtiamb.subscribeInteractionClass(obsluzHandle);

        // subscribe for Otworz interaction
        iname = "HLAinteractionRoot.Kasa.otworz";
        otworzHandle = rtiamb.getInteractionClassHandle(iname);
        nrKasyOtworzHandle = rtiamb.getParameterHandle(rtiamb.getInteractionClassHandle(iname), "nrKasy");
        rtiamb.subscribeInteractionClass(otworzHandle);

        // subscribe for Zamknij interaction
        iname = "HLAinteractionRoot.Kasa.zamknij";
        zamknijHandle = rtiamb.getInteractionClassHandle(iname);
        nrKasyZamknijHandle = rtiamb.getParameterHandle(rtiamb.getInteractionClassHandle(iname), "nrKasy");
        rtiamb.subscribeInteractionClass(zamknijHandle);
    }

    /**
     * This method will request a time advance to the current time, plus the given
     * timestep. It will then wait until a notification of the time advance grant
     * has been received.
     */
    private void advanceTime(double timestep) throws RTIexception {
        // request the advance
        fedamb.isAdvancing = true;
        HLAfloat64Time time = timeFactory.makeTime(fedamb.federateTime + timestep);
        rtiamb.timeAdvanceRequest(time);

        // wait for the time advance to be granted. ticking will tell the
        // LRC to start delivering callbacks to the federate
        while (fedamb.isAdvancing) {
            rtiamb.evokeMultipleCallbacks(0.1, 0.2);

        }
    }

    private short getTimeAsShort() {
        return (short) fedamb.federateTime;
    }

    private byte[] generateTag() {
        return ("(timestamp) " + System.currentTimeMillis()).getBytes();
    }

    //----------------------------------------------------------
    //                     STATIC METHODS
    //----------------------------------------------------------
    public static void main(String[] args) {
        // get a federate name, use "exampleFederate" as default
        String federateName = "Statystyka";
        if (args.length != 0) {
            federateName = args[0];
        }

        try {
            // run the example federate
            new StatystykaFederate().runFederate(federateName);
        } catch (Exception rtie) {
            // an exception occurred, just log the information and exit
            rtie.printStackTrace();
        }
    }
}