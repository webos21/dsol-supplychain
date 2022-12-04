package nl.tudelft.simulation.supplychain.policy.rfq;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Mass;
import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.actor.StockKeepingActor;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.message.Message;
import nl.tudelft.simulation.supplychain.message.trade.Quote;
import nl.tudelft.simulation.supplychain.message.trade.RequestForQuote;
import nl.tudelft.simulation.supplychain.policy.SupplyChainPolicy;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.stock.StockInterface;
import nl.tudelft.simulation.supplychain.transport.TransportMode;

/**
 * The RequestForQuotehandler implements the business logic for a supplier who receives a RequestForQuote. The most simple
 * version answers yes if the product is on stock or ordered, and bases the price on the average costs of the items on stock,
 * after adding a fixed, but changeable, profit margin. The answer is no if the product is not on stock, nor ordered.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class RequestForQuotePolicy extends SupplyChainPolicy<RequestForQuote>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 12L;

    /** the stock on which checks can take place. */
    private StockInterface stock;

    /** the reaction time of the handler in simulation time units. */
    private DistContinuousDuration handlingTime;

    /** the profit margin to use in the quotes, 1.0 is no profit. */
    private double profitMargin;

    /** the transport mode. */
    private TransportMode transportMode;

    /**
     * Construct a new RFQ handler.
     * @param owner a trader in this case as only traders handle RFQs
     * @param stock the stock to check for products when quoting
     * @param profitMargin the profit margin to use; 1.0 is no profit
     * @param handlingTime the distribution of the time to react on the RFQ
     * @param transportMode the default transport mode
     */
    public RequestForQuotePolicy(final StockKeepingActor owner, final StockInterface stock, final double profitMargin,
            final DistContinuousDuration handlingTime, final TransportMode transportMode)
    {
        super("RequestForQuotePolicy", owner, RequestForQuote.class);
        this.stock = stock;
        this.handlingTime = handlingTime;
        this.profitMargin = profitMargin;
        this.transportMode = transportMode;
    }

    /**
     * The default implementation is an opportunistic one: send a positive answer after a certain time if the trader has the
     * product on stock or ordered. Do not look at the required quantity of the product, as the Trader might still get enough
     * units of the product on time. React negative if the actual plus ordered amount equals zero. <br>
     * {@inheritDoc}
     */
    @Override
    public boolean handleMessage(final RequestForQuote rfq)
    {
        if (!isValidMessage(rfq))
        {
            return false;
        }
        Product product = rfq.getProduct();
        // calculate the expected transportation time (in hours)
        // add half a day for handling to be sure it arrives on time
        Duration shippingDuration = this.transportMode.transportTime(rfq.getSender(), rfq.getReceiver())
                .plus(new Duration(12.0, DurationUnit.HOUR));
        Mass weight = rfq.getProduct().getAverageSkuWeight().times(rfq.getAmount());
        Money transportCosts = this.transportMode.transportCosts(rfq.getSender(), rfq.getReceiver(), weight);
        // react with a Quote. First calculate the price
        Money price = this.stock.getUnitPrice(product).multiplyBy(rfq.getAmount() * this.profitMargin).plus(transportCosts);
        // then look at the delivery date
        Time proposedShippingDate =
                Time.max(getOwner().getSimulatorTime(), rfq.getEarliestDeliveryDate().minus(shippingDuration));
        // construct the quote
        Quote quote = new Quote(getOwner(), rfq.getSender(), rfq.getInternalDemandId(), rfq, product, rfq.getAmount(), price,
                proposedShippingDate, this.transportMode);
        getOwner().sendMessage(quote, this.handlingTime.draw());
        return true;
    }

    /**
     * @param handlingTime The handlingTime to set.
     */
    public void setHandlingTime(final DistContinuousDuration handlingTime)
    {
        this.handlingTime = handlingTime;
    }

    /**
     * @param profitMargin The profitMargin to set.
     */
    public void setProfitMargin(final double profitMargin)
    {
        this.profitMargin = profitMargin;
    }
}
