package nl.tudelft.simulation.supplychain.actor;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.djunits.Throw;
import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.draw.point.OrientedPoint2d;
import org.djutils.event.EventType;
import org.djutils.logger.CategoryLogger;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;
import nl.tudelft.simulation.supplychain.finance.Bank;
import nl.tudelft.simulation.supplychain.finance.BankAccount;
import nl.tudelft.simulation.supplychain.finance.FixedCost;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.message.Message;
import nl.tudelft.simulation.supplychain.message.store.trade.TradeMessageStoreInterface;
import nl.tudelft.simulation.supplychain.message.trade.TradeMessage;

/**
 * A SupplyChainActor is an Actor from the Actor package with a bank account, and a way to keep track of its messages. It can
 * play certain roles, to which it can delegate the handling of its messages. It can also choose to handle messages itself.
 * <p>
 * Copyright (c) 2003-2023 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class SupplyChainActor extends Actor
{
    /** */
    private static final long serialVersionUID = 20221201L;

    /** the store for the content to use. */
    private final TradeMessageStoreInterface messageStore;

    /** the bank account of the actor. */
    private final BankAccount bankAccount;

    /** the fixed costs for this supply chain actor. */
    private List<FixedCost> fixedCosts = new ArrayList<FixedCost>();

    /** the event to indicate that information has been sent. E.g., for animation. */
    public static final EventType SEND_MESSAGE_EVENT = new EventType("SEND_MESSAGE_EVENT",
            new MetaData("sent message", "sent message", new ObjectDescriptor("message", "message", Message.class)));

    /**
     * Construct a new Actor.
     * @param id String, the unique id of the actor
     * @param name String; the longer name of the actor
     * @param model SupplyChainModelInterface; the model
     * @param location OrientedPoint2d; the location of the actor
     * @param locationDescription String; the location description of the actor (e.g., a city, country)
     * @param bank Bank; the bank for the BankAccount
     * @param initialBalance Money; the initial balance for the actor
     * @param messageStore TradeMessageStoreInterface; the message store for messages
     * @throws ActorAlreadyDefinedException when the actor was already registered in the model
     */
    @SuppressWarnings("checkstyle:parameternumber")
    public SupplyChainActor(final String id, final String name, final SupplyChainModelInterface model,
            final OrientedPoint2d location, final String locationDescription, final Bank bank, final Money initialBalance,
            final TradeMessageStoreInterface messageStore) throws ActorAlreadyDefinedException
    {
        super(id, name, model, location, locationDescription);
        Throw.whenNull(bank, "bank cannot be null");
        Throw.whenNull(initialBalance, "initialBalance cannot be null");
        Throw.whenNull(messageStore, "messageStore cannot be null");
        this.bankAccount = new BankAccount(this, bank, initialBalance);
        this.messageStore = messageStore;
        this.messageStore.setOwner(this);
    }

    /** {@inheritDoc} */
    @Override
    public void receiveMessage(final Message message)
    {
        super.receiveMessage(message);
        if (message instanceof TradeMessage)
        {
            this.messageStore.addMessage((TradeMessage) message, false);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void sendMessage(final Message message, final Duration delay)
    {
        super.sendMessage(message, delay);
        if (message instanceof TradeMessage)
        {
            this.messageStore.addMessage((TradeMessage) message, true);
        }
        fireEvent(SEND_MESSAGE_EVENT, new Object[] {message});
    }

    /** {@inheritDoc} */
    @Override
    public void sendMessage(final Message message)
    {
        super.sendMessage(message);
        if (message instanceof TradeMessage)
        {
            this.messageStore.addMessage((TradeMessage) message, true);
        }
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
     * Return the MessageStore for the SupplyChainActor.
     * @return TradeMessageStoreInterface; the messageStore.
     */
    public TradeMessageStoreInterface getMessageStore()
    {
        return this.messageStore;
    }

    /**
     * Return the bank account of the SupplyChainActor.
     * @return BankAccount; the bankAccount of the SupplyChainActor.
     */
    public BankAccount getBankAccount()
    {
        return this.bankAccount;
    }

    /**
     * Return a list of the fixed cost items for this SupplyChainActor.
     * @return List&lt;FixedCosts&gt;; a list of fixed costs items for this SupplyChainActor.
     */
    public List<FixedCost> getFixedCosts()
    {
        return this.fixedCosts;
    }

    /** {@inheritDoc} */
    @Override
    public void fireEvent(final EventType eventType, final Serializable value)
    {
        try
        {
            super.fireEvent(eventType, value);
        }
        catch (RemoteException e)
        {
            CategoryLogger.always().error(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public <C extends Comparable<C> & Serializable> void fireTimedEvent(final EventType eventType, final Serializable value,
            final C time)
    {
        try
        {
            super.fireTimedEvent(eventType, value, time);
        }
        catch (RemoteException e)
        {
            CategoryLogger.always().error(e);
        }
    }

}
