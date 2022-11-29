package nl.tudelft.simulation.supplychain.message;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * The RequestForQuote is a question to provide the receiver with a certain amount of a certain product at a certain date. It
 * will be answered with a Quote.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class RequestForQuote extends TradeMessage
{
    /** the serial version uid. */
    private static final long serialVersionUID = 12L;

    /** the InternalDemand for linking purposes. */
    private final InternalDemand internalDemand;

    /** the date when the RFQ will be cut off. */
    private final Time cutoffDate;

    /**
     * Create a new RFQ, based on an internal demand.
     * @param sender SupplyChainActor; the sender actor of the message content
     * @param receiver SupplyChainActor; the receving actor of the message content
     * @param internalDemand InternalDemand; internal demand that triggered the process
     * @param cutoffDuration after how much time will the RFQ stop collecting quotes?
     */
    public RequestForQuote(final SupplyChainActor sender, final SupplyChainActor receiver, final InternalDemand internalDemand,
            final Duration cutoffDuration)
    {
        super(TradeMessageTypes.RFQ, sender, receiver, internalDemand.getInternalDemandId());
        this.internalDemand = internalDemand;
        this.cutoffDate = sender.getSimulatorTime().plus(cutoffDuration);
    }

    /** {@inheritDoc} */
    @Override
    public Product getProduct()
    {
        return this.internalDemand.getProduct();
    }

    /**
     * Return the amount of products in the product's SKU that was demanded.
     * @return double the amount that was demanded.
     */
    public double getAmount()
    {
        return this.internalDemand.getAmount();
    }

    /**
     * Return the earliest delivery date.
     * @return double the earliest delivery date for the product.
     */
    public Time getEarliestDeliveryDate()
    {
        return this.internalDemand.getEarliestDeliveryDate();
    }

    /**
     * Return the latest delivery date.
     * @return double the latest delivery date for the product.
     */
    public Time getLatestDeliveryDate()
    {
        return this.internalDemand.getLatestDeliveryDate();
    }

    /**
     * Return the cutoff date.
     * @return Time; the cutoff date
     */
    public Time getCutoffDate()
    {
        return this.cutoffDate;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return super.toString() + ", for " + this.getAmount() + " " + this.getProduct().getSKU().getName() + " of product "
                + this.getProduct().getName();
    }
}
