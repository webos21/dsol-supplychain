package nl.tudelft.simulation.supplychain.policy.order;

import java.io.Serializable;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.actor.StockKeepingActor;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.inventory.StockInterface;
import nl.tudelft.simulation.supplychain.message.trade.Order;
import nl.tudelft.simulation.supplychain.message.trade.OrderBasedOnQuote;
import nl.tudelft.simulation.supplychain.message.trade.OrderConfirmation;
import nl.tudelft.simulation.supplychain.message.trade.ProductionOrder;
import nl.tudelft.simulation.supplychain.production.Production;
import nl.tudelft.simulation.supplychain.production.ProductionService;
import nl.tudelft.simulation.supplychain.role.producing.ProducingActorInterface;

/**
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class OrderPolicyMake extends AbstractOrderPolicy<Order>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 12L;

    /**
     * Construct a new OrderHandler that makes the goods when ordered.
     * @param owner SupplyChainActor; the owner of the policy
     * @param stock the stock to use to handle the incoming order
     */
    public OrderPolicyMake(final SupplyChainActor owner, final StockInterface stock)
    {
        super("OrderPolicyMake", owner, stock, Order.class);
    }

    /** {@inheritDoc} */
    @Override
    public boolean handleMessage(final Order order)
    {
        // send out the confirmation
        OrderConfirmation orderConfirmation = new OrderConfirmation(getOwner(), order.getSender(), order.getInternalDemandId(),
                order, OrderConfirmation.CONFIRMED);
        getOwner().sendMessage(orderConfirmation, Duration.ZERO);

        Logger.trace("t={} - MTO ORDER CONFIRMATION of actor '{}': sent '{}'", getOwner().getSimulatorTime(),
                getOwner().getName(), orderConfirmation);

        // this is MTO, so we don't keep stock of this product. Therefore, produce it.
        if (!(getOwner() instanceof ProducingActorInterface))
        {
            Logger.error("OrderHandlerMake: Actor '{}' not a Producer", getOwner().getName());
            return false;
        }

        Production production = ((ProducingActorInterface) getOwner()).getProduction();
        if (production == null)
        {
            Logger.error("OrderHandlerMake: Production for Actor '{}' not found", getOwner().getName());
            return false;
        }
        ProductionService productionService = production.getProductionServices().get(order.getProduct());
        if (productionService == null)
        {
            Logger.error("OrderHandlerMake: ProductionService for Actor '{}' not found for product {}", getOwner().getName(),
                    order.getProduct());
            return false;
        }
        ProductionOrder productionOrder = new ProductionOrder(((StockKeepingActor) getOwner()), order.getInternalDemandId(),
                order.getDeliveryDate(), order.getProduct(), order.getAmount());
        productionService.acceptProductionOrder(productionOrder);

        // production should get an mto stock
        // tell the stock that we claimed some amount
        this.stock.changeClaimedAmount(order.getProduct(), order.getAmount());

        // wait till the right time to start shipping
        try
        {
            Duration transportationDuration =
                    order.getTransportOption().estimatedTotalTransportDuration(order.getProduct().getSku());
            Time proposedShippingDate = ((OrderBasedOnQuote) order).getQuote().getProposedShippingDate();
            Time scheduledShippingTime = proposedShippingDate.minus(transportationDuration);

            // start shipping 8 hours from now at the earliest
            Time shippingTime =
                    Time.max(getOwner().getSimulatorTime().plus(new Duration(8.0, DurationUnit.HOUR)), scheduledShippingTime);
            Serializable[] args = new Serializable[] {order};
            getOwner().getSimulator().scheduleEventAbs(shippingTime, this, this, "ship", args);

            Logger.trace("t={} - MTO SHIPPING from actor '{}': scheduled for t={}", getOwner().getSimulatorTime(),
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
