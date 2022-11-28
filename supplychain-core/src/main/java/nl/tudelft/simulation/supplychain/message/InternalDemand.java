package nl.tudelft.simulation.supplychain.message;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * The InternalDemand object represents content for an internal demand of a supply chain actor. The InternalDemand triggers
 * buying or manufacturing of products, and is usually the first in a long chain of messages that are exchanged between actors.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class InternalDemand extends Message
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221127L;

    /** the internal demand id (copy of uniqueId). */
    private final long internalDemandId;
    
    /** the product to order. */
    private Product product;

    /** the amount to order. */
    private double amount;

    /** the earliest delivery date. */
    private Time earliestDeliveryDate;

    /** the latest delivery date. */
    private Time latestDeliveryDate;

    /**
     * Constructs a new InternalDemand.
     * @param sender SupplyChainActor; the sender of the internal demand
     * @param product Product; the product which is demanded
     * @param amount double; the amount of the product
     * @param earliestDeliveryDate Time; the earliest delivery date
     * @param latestDeliveryDate Time; the latest delivery date
     */
    public InternalDemand(final SupplyChainActor sender, final Product product, final double amount,
            final Time earliestDeliveryDate, final Time latestDeliveryDate)
    {
        super(TradeMessageTypes.INTERNAL_DEMAND, sender, sender);
        this.internalDemandId = getUniqueId();
        this.product = product;
        this.amount = amount;
        this.earliestDeliveryDate = earliestDeliveryDate;
        this.latestDeliveryDate = latestDeliveryDate;
    }

    /**
     * Return the product for which this internal demand applies.
     * @return Product; the product for which this internal demand applies
     */
    public Product getProduct()
    {
        return this.product;
    }

    /**
     * Return the amount of products that was demanded.
     * @return double the amount that was demanded.
     */
    public double getAmount()
    {
        return this.amount;
    }

    /**
     * Return the earliest delivery date.
     * @return double the earliest delivery date for the product.
     */
    public Time getEarliestDeliveryDate()
    {
        return this.earliestDeliveryDate;
    }

    /**
     * Return the latest delivery date.
     * @return double the latest delivery date for the product.
     */
    public Time getLatestDeliveryDate()
    {
        return this.latestDeliveryDate;
    }

    /**
     * Return the internalDemandId.
     * @return long; the id of the internal demand that triggered the TtradeMessage chain.
     */
    public long getInternalDemandId()
    {
        return this.internalDemandId;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return super.toString() + ", for " + this.getAmount() + " units of product " + this.getProduct().getName();
    }
}
