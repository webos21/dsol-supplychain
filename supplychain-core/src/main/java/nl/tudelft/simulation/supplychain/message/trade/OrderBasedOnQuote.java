package nl.tudelft.simulation.supplychain.message.trade;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * This implementation of an Order contains a link to a Quote on which the order is based. The Order contains a link to the
 * Quote on which it was based.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class OrderBasedOnQuote extends Order
{
    /** the serial version uid. */
    private static final long serialVersionUID = 12L;

    /** the Quote the order is based on. */
    private Quote quote;

    /**
     * The constructor for the OrderBasedOnQuote.
     * @param sender SupplyChainActor; the sender actor of the message content
     * @param receiver SupplyChainActor; the receving actor of the message content
     * @param internalDemand the internal demand that triggered the order
     * @param deliveryDate the intended delivery date of the products
     * @param quote the quote on which the order is based
     */
    public OrderBasedOnQuote(final SupplyChainActor sender, final SupplyChainActor receiver,
            final InternalDemand internalDemand, final Time deliveryDate, final Quote quote)
    {
        super(TradeMessageTypes.ORDER_BASED_ON_QUOTE, sender, receiver, internalDemand, deliveryDate);
        this.quote = quote;
    }

    /** {@inheritDoc} */
    @Override
    public double getAmount()
    {
        return this.quote.getAmount();
    }

    /** {@inheritDoc} */
    @Override
    public Money getPrice()
    {
        return this.quote.getPrice();
    }

    /** {@inheritDoc} */
    @Override
    public Product getProduct()
    {
        return this.quote.getProduct();
    }

    /**
     * @return the quote.
     */
    public Quote getQuote()
    {
        return this.quote;
    }
}
