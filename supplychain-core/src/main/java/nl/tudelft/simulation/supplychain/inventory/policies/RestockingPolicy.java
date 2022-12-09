package nl.tudelft.simulation.supplychain.inventory.policies;

import java.io.Serializable;

import org.djunits.value.vdouble.scalar.Duration;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.actor.StockKeepingActor;
import nl.tudelft.simulation.supplychain.dsol.SCSimulatorInterface;
import nl.tudelft.simulation.supplychain.inventory.StockInterface;
import nl.tudelft.simulation.supplychain.message.trade.InternalDemand;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class RestockingPolicy implements Serializable
{
    /** the serial version uid. */
    private static final long serialVersionUID = 12L;

    /** the simulator on which to schedule. */
    protected SCSimulatorInterface simulator;

    /** the stock for which the policy holds. */
    protected StockInterface stock;

    /** the product that has to be restocked. */
    protected Product product;

    // TODO: See if this should be a Duration or a Frequency
    /** the frequency distribution for restocking or checking the stock. */
    protected DistContinuousDuration frequency;

    /** the maximum delivery time. */
    protected Duration maxDeliveryDuration = Duration.ZERO;

    /**
     * Construct a new restocking policy, with the basic parameters that every restocking policy has.
     * @param stock the stock for which the policy holds
     * @param product Product; the product that has to be restocked
     * @param frequency the frequency distribution for restocking or checking
     * @param maxDeliveryDuration the maximum delivery time to use
     */
    public RestockingPolicy(final StockInterface stock, final Product product, final DistContinuousDuration frequency,
            final Duration maxDeliveryDuration)
    {
        super();
        this.simulator = stock.getOwner().getSimulator();
        this.stock = stock;
        this.product = product;
        this.frequency = frequency;
        this.maxDeliveryDuration = maxDeliveryDuration;
        try
        {
            this.simulator.scheduleEventRel(frequency.draw(), this, this, "checkLoop", new Serializable[] {});
        }
        catch (Exception e)
        {
            Logger.error(e, "RestockingPolicy");
        }
    }

    /**
     * The main loop for checking or refilling stock.
     */
    protected void checkLoop()
    {
        checkStockLevel();
        try
        {
            this.simulator.scheduleEventRel(this.frequency.draw(), this, this, "checkLoop", new Serializable[] {});
        }
        catch (Exception e)
        {
            Logger.error(e, "checkLoop");
        }
    }

    /**
     * Checks the stock level and takes action if needed.
     */
    protected abstract void checkStockLevel();

    /**
     * Creates an internal demand order.
     * @param orderAmount the amount to order or manufacture
     */
    protected void createInternalDemand(final double orderAmount)
    {
        StockKeepingActor owner = this.stock.getOwner();
        InternalDemand internalDemand = new InternalDemand(owner, this.product, orderAmount, owner.getSimulatorTime(),
                owner.getSimulatorTime().plus(this.maxDeliveryDuration));
        owner.sendMessage(internalDemand, Duration.ZERO);
    }

    /**
     * @return the frequency distribution.
     */
    public DistContinuousDuration getFrequency()
    {
        return this.frequency;
    }

    /**
     * @param frequency The frequency distribution to set.
     */
    public void setFrequency(final DistContinuousDuration frequency)
    {
        this.frequency = frequency;
    }

    /**
     * @return the product.
     */
    public Product getProduct()
    {
        return this.product;
    }
}
