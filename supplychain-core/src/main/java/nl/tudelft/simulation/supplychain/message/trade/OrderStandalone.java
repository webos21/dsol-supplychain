package nl.tudelft.simulation.supplychain.message.trade;

import java.io.Serializable;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * This is a stand alone order, that is not based on an RFQ and Quote, but which is directly placed to another actor. It
 * <i>might be </i> based on a Quote, but the order is not explicitly saying so. It can also be an order to a well-known supply
 * chain partner, with whom long-term price arrangements have been made.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class OrderStandalone extends Order
{
    /** the serial version uid. */
    private static final long serialVersionUID = 12L;

    /** the product we want */
    private Product product;

    /** the amount of the product, in units for that product */
    private double amount;

    /** the price we want to pay for the product */
    private Money price;

    /**
     * The constructor for the OrderStandAlone
     * @param sender SupplyChainActor; the sender actor of the message content
     * @param receiver SupplyChainActor; the receving actor of the message content
     * @param internalDemandId the internal demand that triggered the order
     * @param deliveryDate the intended delivery date of the products
     * @param product Product; the ordered product
     * @param amount double; the amount ordered
     * @param price Money; the price to pay
     */
    public OrderStandalone(final SupplyChainActor sender, final SupplyChainActor receiver, final long internalDemandId,
            final Time deliveryDate, final Product product, final double amount, final Money price)
    {
        super(sender, receiver, internalDemandId, deliveryDate);
        this.product = product;
        this.amount = amount;
        this.price = price;
    }

    /** {@inheritDoc} */
    @Override
    public double getAmount()
    {
        return this.amount;
    }

    /** {@inheritDoc} */
    @Override
    public Money getPrice()
    {
        return this.price;
    }

    /** {@inheritDoc} */
    @Override
    public Product getProduct()
    {
        return this.product;
    }
}
