package nl.tudelft.simulation.supplychain.policy.order;

import java.io.Serializable;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.message.Message;
import nl.tudelft.simulation.supplychain.message.trade.Order;
import nl.tudelft.simulation.supplychain.message.trade.OrderBasedOnQuote;
import nl.tudelft.simulation.supplychain.message.trade.OrderConfirmation;
import nl.tudelft.simulation.supplychain.message.trade.TradeMessageTypes;
import nl.tudelft.simulation.supplychain.stock.StockInterface;
import nl.tudelft.simulation.supplychain.transport.TransportMode;

/**
 * The most simple form of an OrderHandler that takes the orders from stock is one that sends out an OrderConfirmation right
 * away, and waits till the delivery date (should be minus the expected transportation time), picks the order, and ships it out
 * as a Shipment. When the order is not available: wait one day and try again till it is available.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class OrderPolicyStock extends AbstractOrderPolicy
{
    /** the serial version uid. */
    private static final long serialVersionUID = 12L;

    /**
     * Construct a new OrderHandler that takes the goods from stock when ordered.
     * @param owner SupplyChainActor; the owner of the policy
     * @param stock the stock to use to handle the incoming order
     */
    public OrderPolicyStock(final SupplyChainActor owner, final StockInterface stock)
    {
        super("OrderPolicyStock", owner, stock, TradeMessageTypes.ORDER);
    }

    /** {@inheritDoc} */
    @Override
    public boolean handleMessage(final Message message)
    {
        // get the order
        Order order = (Order) message;

        // send out the confirmation
        OrderConfirmation orderConfirmation = new OrderConfirmation(getOwner(), order.getSender(), order.getInternalDemandId(),
                order, OrderConfirmation.CONFIRMED);
        getOwner().sendMessage(orderConfirmation, Duration.ZERO);

        Logger.trace("t={} - MTS ORDER CONFIRMATION of actor '{}': sent '{}'", getOwner().getSimulatorTime(),
                getOwner().getName(), orderConfirmation);

        // tell the stock that we claimed some amount
        this.stock.changeClaimedAmount(order.getProduct(), order.getAmount());

        // wait till the right time to start shipping
        try
        {
            // for now we use the TransportMode.PLANE
            // TODO get the transportation mode, currently using TransportMode.PLANE
            Duration transportationDuration = TransportMode.PLANE.transportTime(order.getSender(), order.getReceiver());

            Time proposedShippingDate = ((OrderBasedOnQuote) order).getQuote().getProposedShippingDate();

            Time scheduledShippingTime = proposedShippingDate.minus(transportationDuration);

            // start shipping 8 hours from now at the earliest
            Time shippingTime =
                    Time.max(getOwner().getSimulatorTime().plus(new Duration(8.0, DurationUnit.HOUR)), scheduledShippingTime);
            Serializable[] args = new Serializable[] {order};
            getOwner().getSimulator().scheduleEventAbs(shippingTime, this, this, "ship", args);

            Logger.trace("t={} - MTS SHIPPING from actor '{}': scheduled for t={}", getOwner().getSimulatorTime(),
                    getOwner().getName(), shippingTime);
        }
        catch (Exception e)
        {
            Logger.error(e, "handleContent");
            return false;
        }
        return true;
    }

}
