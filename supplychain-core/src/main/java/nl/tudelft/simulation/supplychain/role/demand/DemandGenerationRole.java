package nl.tudelft.simulation.supplychain.role.demand;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.djunits.value.vdouble.scalar.Time;
import org.djutils.event.TimedEvent;
import org.djutils.event.TimedEventType;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;
import nl.tudelft.simulation.jstats.distributions.DistDiscrete;
import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.actor.SupplyChainRole;
import nl.tudelft.simulation.supplychain.message.trade.InternalDemand;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * The demand generation role is a role for customers, markets, and other actors that have an autonomous generation of demand
 * for products. This is different from the InventoryRole, where demand generation is triggered by depletion of stock.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DemandGenerationRole extends SupplyChainRole
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221206L;

    /** an event fired in case demand has been generated. */
    public static final TimedEventType DEMAND_GENERATED_EVENT = new TimedEventType("DEMAND_GENERATED_EVENT");

    /** map of Product - Demand pairs. */
    private Map<Product, Demand> demandGenerators = new LinkedHashMap<Product, Demand>();

    /** the administrative delay when sending messages. */
    private DistContinuousDuration administrativeDelay;

    /**
     * @param owner the actor that has this role
     * @param administrativeDelay the administrative delay when sending messages
     */
    public DemandGenerationRole(final SupplyChainActor owner, final DistContinuousDuration administrativeDelay)
    {
        super(owner);
        this.administrativeDelay = administrativeDelay;
    }

    /**
     * @param product Product; the product
     * @param demand the demand
     */
    public void addDemandGenerator(final Product product, final Demand demand)
    {
        this.demandGenerators.put(product, demand);
        try
        {
            Serializable[] args = {product, demand};
            super.simulator.scheduleEventRel(demand.getIntervalDistribution().draw(), this, this, "createInternalDemand", args);
        }
        catch (Exception e)
        {
            Logger.error(e, "addDemandGenerator");
        }
    }

    /**
     * Method getDemandGenerator.
     * @param product Product; the product to return the demand generator for
     * @return a demand, or null if it could not be found
     */
    public Demand getDemandGenerator(final Product product)
    {
        return this.demandGenerators.get(product);
    }

    /**
     * @param product Product; the product
     */
    public void removeDemandGenerator(final Product product)
    {
        this.demandGenerators.remove(product);
    }

    /**
     * @param product Product; the product
     * @param demand the demand
     */
    protected void createInternalDemand(final Product product, final Demand demand)
    {
        // is the (same) demand still there?
        if (this.demandGenerators.get(product).equals(demand))
        {
            try
            {
                double amount = demand.getAmountDistribution() instanceof DistContinuous
                        ? ((DistContinuous) demand.getAmountDistribution()).draw()
                        : ((DistDiscrete) demand.getAmountDistribution()).draw();
                InternalDemand id = new InternalDemand(getOwner(), product, amount,
                        this.simulator.getAbsSimulatorTime().plus(demand.getEarliestDeliveryDurationDistribution().draw()),
                        this.simulator.getAbsSimulatorTime().plus(demand.getLatestDeliveryDurationDistribution().draw()));
                getOwner().sendMessage(id, this.administrativeDelay.draw());
                Serializable[] args = {product, demand};
                Time time = super.simulator.getAbsSimulatorTime().plus(demand.getIntervalDistribution().draw());
                super.simulator.scheduleEventAbs(time, this, this, "createInternalDemand", args);

                // we might collect some statistics for the internal demand
                super.fireEvent(new TimedEvent<Time>(DemandGenerationRole.DEMAND_GENERATED_EVENT, this, id,
                        super.simulator.getAbsSimulatorTime()));
            }
            catch (Exception e)
            {
                Logger.error(e, "createInternalDemand");
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getId()
    {
        return getOwner().getName() + "-DEMAND(periodic)";
    }

    /**
     * @return the administrativeDelay.
     */
    public DistContinuousDuration getAdministrativeDelay()
    {
        return this.administrativeDelay;
    }

    /**
     * @param administrativeDelay The administrativeDelay to set.
     */
    public void setAdministrativeDelay(final DistContinuousDuration administrativeDelay)
    {
        this.administrativeDelay = administrativeDelay;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return getId();
    }

}
