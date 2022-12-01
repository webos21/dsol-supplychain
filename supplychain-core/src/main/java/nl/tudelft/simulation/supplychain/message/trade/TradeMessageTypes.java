package nl.tudelft.simulation.supplychain.message;

import nl.tudelft.simulation.supplychain.dsol.SCModelInterface;

/**
 * TradeMessageTypes contains the types of trade messages, and a method to register them in the model.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public final class TradeMessageTypes
{
    /** */
    private TradeMessageTypes()
    {
        // dummy constructor
    }

    /** Bill. */
    public static final MessageType BILL = new MessageType("Bill");

    /** InternalDemand. */
    public static final MessageType INTERNAL_DEMAND = new MessageType("InternalDemand");

    /** Order. */
    public static final MessageType ORDER = new MessageType("Order");

    /** OrderBasedOnQuote. */
    public static final MessageType ORDER_BASED_ON_QUOTE = new MessageType("OrderBasedOnQuote");

    /** OrderConfirmation. */
    public static final MessageType ORDER_CONFIRMATION = new MessageType("OrderConfirmation");

    /** OrderStandalone. */
    public static final MessageType ORDER_STANDALONE = new MessageType("OrderStandalone");

    /** Payment. */
    public static final MessageType PAYMENT = new MessageType("Payment");

    /** ProductionOrder. */
    public static final MessageType PRODUCTION_ORDER = new MessageType("ProductionOrder");

    /** Quote. */
    public static final MessageType QUOTE = new MessageType("Quote");

    /** RequestForQuote. */
    public static final MessageType RFQ = new MessageType("RFQ");

    /** Shipment. */
    public static final MessageType SHIPMENT = new MessageType("Shipment");

    /** ShipmentQuality. */
    public static final MessageType SHIPMENT_QUALITY = new MessageType("ShipmentQuality");

    /** YellowPageAnswer. */
    public static final MessageType YP_ANSWER = new MessageType("YPAnswer");

    /** YellowPageQequest. */
    public static final MessageType YP_REQUEST = new MessageType("YPRequest");

    /**
     * Register the trade message types in the model (to be called once in constructModel() or model constructor.
     * @param model SCModelInterface; the supply chain model to register the types in.
     */
    public static void registerTradeMessageTypes(final SCModelInterface model)
    {
        model.registerMessageType(BILL);
        model.registerMessageType(INTERNAL_DEMAND);
        model.registerMessageType(ORDER);
        model.registerMessageType(ORDER_BASED_ON_QUOTE);
        model.registerMessageType(ORDER_CONFIRMATION);
        model.registerMessageType(ORDER_STANDALONE);
        model.registerMessageType(PAYMENT);
        model.registerMessageType(PRODUCTION_ORDER);
        model.registerMessageType(QUOTE);
        model.registerMessageType(RFQ);
        model.registerMessageType(SHIPMENT);
        model.registerMessageType(SHIPMENT_QUALITY);
        model.registerMessageType(YP_ANSWER);
        model.registerMessageType(YP_REQUEST);
    }

}
