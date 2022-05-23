package Sklep;

import hla.rti1516e.*;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.exceptions.RTIexception;
import hla.rti1516e.time.HLAfloat64TimeFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SklepFederate {
    //----------------------------------------------------------
    //                    STATIC VARIABLES
    //----------------------------------------------------------
    /**
     * The number of times we will update our attributes and send an interaction
     */
    public static final int ITERATIONS = 20;

    /**
     * The sync point all federates will sync up on before starting
     */
    public static final String READY_TO_RUN = "ReadyToRun";

    //----------------------------------------------------------
    //                   INSTANCE VARIABLES
    //----------------------------------------------------------
    private RTIambassador rtiamb;
    private SklepFederateAmbassador fedamb;  // created when we connect
    private HLAfloat64TimeFactory timeFactory; // set when we join
    protected EncoderFactory encoderFactory;     // set when we join

    // caches of handle types - set once we join a federation
    protected ObjectClassHandle storageHandle;
    protected AttributeHandle storageMaxHandle;
    protected AttributeHandle storageAvailableHandle;
    protected InteractionClassHandle addProductsHandle;
    protected InteractionClassHandle getProductsHandle;
    protected ParameterHandle countHandle;

    private void log(String message) {
        System.out.println("SklepFederate   : " + message);
    }

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

    public void runFederate(String federateName) throws Exception {

    }

    private void enableTimePolicy() throws Exception {

    }

    private void publishAndSubscribe() throws RTIexception {

    }

    private void advanceTime(double timestep) throws RTIexception {

    }

    private short getTimeAsShort() {
        return (short) fedamb.federateTime;
    }

    private byte[] generateTag() {
        return ("(timestamp) " + System.currentTimeMillis()).getBytes();
    }

    public static void main(String[] args) {
        String federateName = "Sklep";
        if (args.length != 0) {
            federateName = args[0];
        }

        try {
            // run the example federate
            new SklepFederate().runFederate(federateName);
        } catch (Exception rtie) {
            // an exception occurred, just log the information and exit
            rtie.printStackTrace();
        }
    }
}
