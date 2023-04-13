package nl.tudelft.simulation.supplychain.role.financing;

import java.util.ArrayList;
import java.util.List;

import org.djunits.Throw;
import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.event.EventProducer;
import org.djutils.event.LocalEventProducer;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.finance.BankAccount;
import nl.tudelft.simulation.supplychain.finance.FixedCost;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.message.receiver.MessageReceiver;

/**
 * The FinancingRole manages the bank account of an organization and can take care of paying bills and receiving money.
 * <p>
 * Copyright (c) 2023-2023 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class FinancingRole extends Role
{
    /** */
    private static final long serialVersionUID = 20230413L;

    /** the bank account of the actor. */
    private final BankAccount bankAccount;

    /** the fixed costs for this supply chain actor. */
    private List<FixedCost> fixedCosts = new ArrayList<FixedCost>();

    /**
     * Create a new FinancingRole with an attached BankAccount.
     * @param id String; the id of the role
     * @param actor Actor; the actor to which this role belongs
     * @param messageReceiver MessageReceiver; the message handler to use for processing the messages
     * @param eventProducer EventProducer; a special EventProducer to use, e.g., a RmiEventProducer
     * @param bank Bank; the bank for the BankAccount
     * @param initialBalance Money; the initial balance for the actor
     */
    public FinancingRole(final String id, final Actor actor, final MessageReceiver messageReceiver, final BankAccount bankAccount,
            final EventProducer eventProducer)
    {
        super(id, actor, messageReceiver, eventProducer);
        Throw.whenNull(bank, "bank cannot be null");
        Throw.whenNull(initialBalance, "initialBalance cannot be null");
        this.bankAccount = new BankAccount(this, bank, initialBalance);
    }

    /**
     * Create a new FinancingRole with an attached BankAccount.
     * @param id String; the id of the role
     * @param actor Actor; the actor to which this role belongs
     * @param messageReceiver MessageReceiver; the message handler to use for processing the messages
     * @param bank Bank; the bank for the BankAccount
     * @param initialBalance Money; the initial balance for the actor
     */
    public FinancingRole(final String id, final Actor actor, final MessageReceiver messageReceiver, final BankAccount bankAccount)
    {
        this(id, actor, messageReceiver, bankAccount, new LocalEventProducer());
    }

    /**
     * Add a fixed cost item for this actor.
     * @param description String; the description of the fixed cost item
     * @param interval Duration; the interval at which the amount will be deduced from the bank account
     * @param amount Money; the amount to deduce at each interval
     */
    public void addFixedCost(final String description, final Duration interval, final Money amount)
    {
        FixedCost fixedCost = new FixedCost(this, description, interval, amount);
        this.fixedCosts.add(fixedCost);
    }

    /**
     * Return the bank account of the Actor.
     * @return BankAccount; the bankAccount of the Actor.
     */
    public BankAccount getBankAccount()
    {
        return this.bankAccount;
    }

    /**
     * Return a list of the fixed cost items for this Actor.
     * @return List&lt;FixedCosts&gt;; a list of fixed costs items for this Actor.
     */
    public List<FixedCost> getFixedCosts()
    {
        return this.fixedCosts;
    }

}
