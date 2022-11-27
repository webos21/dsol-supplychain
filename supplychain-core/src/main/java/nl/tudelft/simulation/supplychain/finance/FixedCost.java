package nl.tudelft.simulation.supplychain.finance;

import java.io.Serializable;

import org.djunits.Throw;
import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEventInterface;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;

/**
 * When a supply chain actor is created, one or more FixedCost objects can be created to book fixed costs for e.g. personnel,
 * buildings, other resources on an interval (e.g. monthly) basis. When the interval or amount is changed, the scheduling
 * changes immediately and the amount is effective in the next scheduled fixed cost event.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class FixedCost implements Serializable
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221127L;

    /** the supply chain actor. */
    private SupplyChainActor owner;

    /** the description of the type of fixed cost. */
    private String description;

    /** The interval for booking the fixed cost. */
    private Duration interval;

    /** The amount to book on every interval. */
    private Money amount;

    /** the event for the next period -- stored to be able to remove it. */
    private SimEventInterface<Duration> fixedAmountEvent;

    /**
     * Create a Fixed cost item for an actor.
     * @param owner the supply chain actor
     * @param description the description
     * @param interval the interval for booking fixed cost
     * @param amount the fixed cost per interval
     */
    public FixedCost(final SupplyChainActor owner, final String description, final Duration interval, final Money amount)
    {
        Throw.whenNull(owner, "owner cannot be null");
        Throw.whenNull(description, "description cannot be null");
        Throw.whenNull(interval, "interval cannot be null");
        Throw.when(interval.le0(), IllegalArgumentException.class, "interval duration cannot be <= 0");
        Throw.whenNull(amount, "amount cannot be null");
        this.owner = owner;
        this.description = description;
        this.changeInterval(interval);
        this.changeAmount(amount);
    }

    /**
     * Change the interval to book fixed costs. The booking event is immediately rescheduled to the END of the interval; next
     * deduction takes place after 'interval' days.
     * @param newInterval the new interval
     */
    public void changeInterval(final Duration newInterval)
    {
        Throw.whenNull(newInterval, "interval cannot be null");
        Throw.when(newInterval.le0(), IllegalArgumentException.class, "interval duration cannot be <= 0");
        this.interval = newInterval;
        if (this.fixedAmountEvent != null)
        {
            // cancel the previous event
            this.owner.getSimulator().cancelEvent(this.fixedAmountEvent);
        }
        this.fixedAmountEvent = this.owner.getSimulator().scheduleEventRel(this.interval, this, this, "bookFixedCost", null);
    }

    /**
     * Change the fixed costs to book each interval. The change is effective on the next scheduled event for deduction of fixed
     * costs.
     * @param newAmount the new amount
     */
    public void changeAmount(final Money newAmount)
    {
        this.amount = newAmount;
    }

    /**
     * Scheduled method to book the fixed costs.
     */
    protected void bookFixedCost()
    {
        this.owner.getBankAccount().withdrawFromBalance(this.amount);
        this.fixedAmountEvent = this.owner.getSimulator().scheduleEventRel(this.interval, this, this, "bookFixedCost", null);
    }

    /**
     * Return the fixed cost per interval.
     * @return Money; the fixed cost per interval
     */
    public Money getAmount()
    {
        return this.amount;
    }

    /**
     * Return the description of the fixed cost item.
     * @return String; the description of the fixed cost item
     */
    public String getDescription()
    {
        return this.description;
    }

    /**
     * Return the withdrawal interval.
     * @return Duration; the withdrawal interval
     */
    public Duration getInterval()
    {
        return this.interval;
    }

    /**
     * Return the actor to which these fixed costs apply.
     * @return SupplyChainActor; the actor to which these fixed costs apply
     */
    public SupplyChainActor getOwner()
    {
        return this.owner;
    }
}
