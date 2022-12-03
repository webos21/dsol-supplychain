package nl.tudelft.simulation.supplychain.message.trade;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.message.MessageType;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * An Order indicates: I want a certain amount of products on a certain date for a certain price. The four attributes "product",
 * "amount", "date" and "price" make up the order. Several implementations of the order can be made, i.e. a version that is
 * based on a Quote, or a version where the Order is the start of the entire chain.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class Order extends TradeMessage
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the delivery date as ordered. */
    private Time deliveryDate;

    /**
     * Constructor for an order. This abstract constructor has to be called by every extending class.
     * @param type MessageType; the specific type of order
     * @param sender SupplyChainActor; the sender actor of the message content
     * @param receiver SupplyChainActor; the receving actor of the message content
     * @param internalDemand the internal demand that triggered the order
     * @param deliveryDate the intended delivery date of the products
     */
    public Order(final MessageType type, final SupplyChainActor sender, final SupplyChainActor receiver,
            final InternalDemand internalDemand, final Time deliveryDate)
    {
        super(type, sender, receiver, internalDemand.getInternalDemandId());
        this.deliveryDate = deliveryDate;
    }

    /** {@inheritDoc} */
    @Override
    public abstract Product getProduct();

    /**
     * @return the amount.
     */
    public abstract double getAmount();

    /**
     * @return the price.
     */
    public abstract Money getPrice();

    /**
     * @return the proposedDeliveryDate.
     */
    public Time getDeliveryDate()
    {
        return this.deliveryDate;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return super.toString() + ", for " + this.getAmount() + " units of product " + this.getProduct().getName();
    }
}
