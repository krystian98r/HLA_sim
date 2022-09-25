package Statystyka;

import Klienci.Klient;
import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.FederateHandleSet;
import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.LogicalTime;
import hla.rti1516e.NullFederateAmbassador;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.OrderType;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.SynchronizationPointFailureReason;
import hla.rti1516e.TransportationTypeHandle;
import hla.rti1516e.encoding.*;
import hla.rti1516e.exceptions.FederateInternalError;
import hla.rti1516e.time.HLAfloat64Time;
import org.portico.impl.hla1516e.types.encoding.HLA1516eBoolean;
import org.portico.impl.hla1516e.types.encoding.HLA1516eInteger16BE;
import org.portico.impl.hla1516e.types.encoding.HLA1516eInteger32BE;

/**
 * This class handles all incoming callbacks from the RTI regarding a particular
 * {@link StatystykaFederate}. It will log information about any callbacks it
 * receives, thus demonstrating how to deal with the provided callback information.
 */
public class StatystykaFederateAmbassador extends NullFederateAmbassador {
    //----------------------------------------------------------
    //                    STATIC VARIABLES
    //----------------------------------------------------------

    //----------------------------------------------------------
    //                   INSTANCE VARIABLES
    //----------------------------------------------------------
    private StatystykaFederate federate;

    // these variables are accessible in the package
    protected double federateTime = 0.0;
    protected double federateLookahead = 1.0;

    protected boolean isRegulating = false;
    protected boolean isConstrained = false;
    protected boolean isAdvancing = false;

    protected boolean isAnnounced = false;
    protected boolean isReadyToRun = false;

    protected boolean isRunning = true;

    //----------------------------------------------------------
    //                      CONSTRUCTORS
    //----------------------------------------------------------

    public StatystykaFederateAmbassador(StatystykaFederate federate) {
        this.federate = federate;
    }

    //----------------------------------------------------------
    //                    INSTANCE METHODS
    //----------------------------------------------------------
    private void log(String message) {
        System.out.println("FederateAmbassador: " + message);
    }

    //////////////////////////////////////////////////////////////////////////
    ////////////////////////// RTI Callback Methods //////////////////////////
    //////////////////////////////////////////////////////////////////////////
    @Override
    public void synchronizationPointRegistrationFailed(String label,
                                                       SynchronizationPointFailureReason reason) {
        log("Failed to register sync point: " + label + ", reason=" + reason);
    }

    @Override
    public void synchronizationPointRegistrationSucceeded(String label) {
        log("Successfully registered sync point: " + label);
    }

    @Override
    public void announceSynchronizationPoint(String label, byte[] tag) {
        log("Synchronization point announced: " + label);
        if (label.equals(StatystykaFederate.READY_TO_RUN))
            this.isAnnounced = true;
    }

    @Override
    public void federationSynchronized(String label, FederateHandleSet failed) {
        log("Federation Synchronized: " + label);
        if (label.equals(StatystykaFederate.READY_TO_RUN))
            this.isReadyToRun = true;
    }

    /**
     * The RTI has informed us that time regulation is now enabled.
     */
    @Override
    public void timeRegulationEnabled(LogicalTime time) {
        this.federateTime = ((HLAfloat64Time) time).getValue();
        this.isRegulating = true;
    }

    @Override
    public void timeConstrainedEnabled(LogicalTime time) {
        this.federateTime = ((HLAfloat64Time) time).getValue();
        this.isConstrained = true;
    }

    @Override
    public void timeAdvanceGrant(LogicalTime time) {
        this.federateTime = ((HLAfloat64Time) time).getValue();
        this.isAdvancing = false;
    }

    @Override
    public void discoverObjectInstance(ObjectInstanceHandle theObject,
                                       ObjectClassHandle theObjectClass,
                                       String objectName)
            throws FederateInternalError {
        log("Discoverd Object: handle=" + theObject + ", classHandle=" +
                theObjectClass + ", name=" + objectName);
    }

    @Override
    public void reflectAttributeValues(ObjectInstanceHandle theObject,
                                       AttributeHandleValueMap theAttributes,
                                       byte[] tag,
                                       OrderType sentOrder,
                                       TransportationTypeHandle transport,
                                       SupplementalReflectInfo reflectInfo)
            throws FederateInternalError {
        // just pass it on to the other method for printing purposes
        // passing null as the time will let the other method know it
        // it from us, not from the RTI
        reflectAttributeValues(theObject,
                theAttributes,
                tag,
                sentOrder,
                transport,
                null,
                sentOrder,
                reflectInfo);
    }

    @Override
    public void reflectAttributeValues(ObjectInstanceHandle theObject,
                                       AttributeHandleValueMap theAttributes,
                                       byte[] tag,
                                       OrderType sentOrdering,
                                       TransportationTypeHandle theTransport,
                                       LogicalTime time,
                                       OrderType receivedOrdering,
                                       SupplementalReflectInfo reflectInfo)
            throws FederateInternalError {
        StringBuilder builder = new StringBuilder("Reflection for object:");

        // print the handle
        builder.append(" handle=" + theObject);
        // print the tag
        builder.append(", tag=" + new String(tag));
        // print the time (if we have it) we'll get null if we are just receiving
        // a forwarded call from the other reflect callback above
        if (time != null) {
            builder.append(", time=" + ((HLAfloat64Time) time).getValue());
        }

        // print the attribute information
        builder.append(", attributeCount=" + theAttributes.size());
        builder.append("\n");
        for (AttributeHandle attributeHandle : theAttributes.keySet()) {
            // print the attibute handle
            builder.append("\tattributeHandle=");

            // if we're dealing with Flavor, decode into the appropriate enum value
            if (attributeHandle.equals(federate.nrKlientaKlientHandle)) {
                federate.statystyka.dodajNaSklepie();
                builder.append(attributeHandle);
                builder.append(" (nrKlienta)    ");
                builder.append(", attributeValue=");
                HLAinteger32BE nrKlienta = new HLA1516eInteger32BE();
                try {
                    nrKlienta.decode(theAttributes.get(attributeHandle));
                } catch (DecoderException e) {
                    e.printStackTrace();
                }
                builder.append(nrKlienta.getValue());
                federate.klienci.add(new Klient(nrKlienta.getValue(), federateTime, false));
//                federate.statystyka.dodajNaSklepie();
            } else if (attributeHandle.equals(federate.czasWejsciaKlientHandle)) {
                builder.append(attributeHandle);
                builder.append(" (czasWejscia)");
                builder.append(", attributeValue=");
                HLAinteger32BE czasWejscia = new HLA1516eInteger32BE();
                try {
                    czasWejscia.decode(theAttributes.get(attributeHandle));
                } catch (DecoderException e) {
                    e.printStackTrace();
                }
                builder.append(czasWejscia.getValue());
            } else if (attributeHandle.equals(federate.czasZakupowKlientHandle)) {
                builder.append(attributeHandle);
                builder.append(" (czasZakupow)");
                builder.append(", attributeValue=");
                HLAinteger32BE czasZakupow = new HLA1516eInteger32BE();
                try {
                    czasZakupow.decode(theAttributes.get(attributeHandle));
                } catch (DecoderException e) {
                    e.printStackTrace();
                }
                builder.append(czasZakupow.getValue());
                federate.klienci.get(federate.klienci.size() - 1).setCzasZakupow(czasZakupow.getValue());
                federate.statystyka.dodajSumaCzasZakupow(czasZakupow.getValue());
            } else if (attributeHandle.equals(federate.uprzywilejowanyKlientHandle)) {
                builder.append(attributeHandle);
                builder.append(" (uprzywilejowany)");
                builder.append(", attributeValue=");
                HLA1516eBoolean uprzywilejowany = new HLA1516eBoolean();
                try {
                    uprzywilejowany.decode(theAttributes.get(attributeHandle));
                } catch (DecoderException e) {
                    e.printStackTrace();
                }
                builder.append(uprzywilejowany.getValue());
                federate.klienci.get(federate.klienci.size() - 1).setUprzywilejowany(uprzywilejowany.getValue());
                if (uprzywilejowany.getValue()) federate.statystyka.dodajUprzywilejowany();
            } else {
                builder.append(attributeHandle);
                builder.append(" (Unknown)   ");
            }

            builder.append("\n");
        }

        log(builder.toString());
    }

    @Override
    public void receiveInteraction(InteractionClassHandle interactionClass,
                                   ParameterHandleValueMap theParameters,
                                   byte[] tag,
                                   OrderType sentOrdering,
                                   TransportationTypeHandle theTransport,
                                   SupplementalReceiveInfo receiveInfo)
            throws FederateInternalError {
        // just pass it on to the other method for printing purposes
        // passing null as the time will let the other method know it
        // it from us, not from the RTI
        this.receiveInteraction(interactionClass,
                theParameters,
                tag,
                sentOrdering,
                theTransport,
                null,
                sentOrdering,
                receiveInfo);
    }

    @Override
    public void receiveInteraction(InteractionClassHandle interactionClass,
                                   ParameterHandleValueMap theParameters,
                                   byte[] tag,
                                   OrderType sentOrdering,
                                   TransportationTypeHandle theTransport,
                                   LogicalTime time,
                                   OrderType receivedOrdering,
                                   SupplementalReceiveInfo receiveInfo)
            throws FederateInternalError {
        StringBuilder builder = new StringBuilder("Interaction Received:");

        // print the handle
        builder.append(" handle=" + interactionClass);
        if (interactionClass.equals(federate.czekajHandle)) {
            builder.append(" (czekaj)");
            federate.statystyka.dodajPrzyKasach();
            federate.statystyka.odejmijNaSklepie();
        } else if (interactionClass.equals(federate.obsluzHandle)) {
            builder.append(" (obsluz)");
            federate.statystyka.odejmijPrzyKasach();
            federate.statystyka.dodajObsluzeni();
            byte[] bytes = theParameters.get(federate.nrKlientaObsluzHandle);
            HLAinteger32BE nrKlienta = new HLA1516eInteger32BE();
            try {
                nrKlienta.decode(bytes);
            } catch (DecoderException e) {
                e.printStackTrace();
            }
            for (Klient k : federate.klienci) {
                if (k.getNrKlienta() == nrKlienta.getValue()) {
                    int czasObslugi = (int) federateTime - (int) k.getCzasWejscia() - k.getCzasZakupow();
                    federate.statystyka.dodajSumaCzasOblugi(czasObslugi);
                }
            }
        } else if (interactionClass.equals(federate.otworzHandle)) {
            builder.append(" (otworz)");
            federate.statystyka.dodajKase();
        } else if (interactionClass.equals(federate.zamknijHandle)) {
            builder.append(" (zamknij)");
            federate.statystyka.odejmijKase();
        }

        // print the tag
        builder.append(", tag=" + new String(tag));
        // print the time (if we have it) we'll get null if we are just receiving
        // a forwarded call from the other reflect callback above
        if (time != null) {
            builder.append(", time=" + ((HLAfloat64Time) time).getValue());
        }

        // print the parameer information
        builder.append(", parameterCount=" + theParameters.size());
        builder.append("\n");

        log(builder.toString());
    }

    @Override
    public void removeObjectInstance(ObjectInstanceHandle theObject,
                                     byte[] tag,
                                     OrderType sentOrdering,
                                     SupplementalRemoveInfo removeInfo)
            throws FederateInternalError {
        log("Object Removed: handle=" + theObject);
    }

    //----------------------------------------------------------
    //                     STATIC METHODS
    //----------------------------------------------------------
}