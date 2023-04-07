package nl.tudelft.simulation.supplychain.actor;

import java.util.ArrayList;
import java.util.List;

import org.djunits.Throw;
import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.draw.point.OrientedPoint3d;
import org.djutils.event.EventType;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

import nl.tudelft.simulation.supplychain.dsol.SCSimulatorInterface;
import nl.tudelft.simulation.supplychain.finance.Bank;
import nl.tudelft.simulation.supplychain.finance.BankAccount;
import nl.tudelft.simulation.supplychain.finance.FixedCost;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.message.Message;
import nl.tudelft.simulation.supplychain.message.handler.MessageHandlerInterface;
import nl.tudelft.simulation.supplychain.message.store.trade.TradeMessageStoreInterface;
import nl.tudelft.simulation.supplychain.message.trade.TradeMessage;

/**
 * A SupplyChainActor is an Actor from the Actor package with a bank account, and a way to keep track of its messages. It can
 * play certain roles, to which it can delegate the handling of its messages. It can also choose to handle messages itself.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class SupplyChainActor extends Actor implements SupplyChainActor
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
     * Build the SupplyChainActor with a Builder.
     * @param builder Builder; the Builder to use
     */
    public SupplyChainActor(final Builder builder)
    {
        super(builder.name, builder.messageHandler, builder.simulator, builder.location, builder.locationDescription);
        this.bankAccount = new BankAccount(this, builder.bank, builder.initialBalance);
        this.messageStore = builder.messageStore;
        this.messageStore.setOwner(this);
    }

    /**
     * Construct a new Actor.
     * @param name String; the name of the actor
     * @param messageHandler MessageHandlerInterface; the message handler to use
     * @param simulator SCSimulatorInterface; the simulator to use
     * @param location OrientedPoint3d; the location of the actor
     * @param locationDescription String; the location description of the actor (e.g., a city, country)
     * @param bank Bank; the bank for the BankAccount
     * @param initialBalance Money; the initial balance for the actor
     * @param messageStore TradeMessageStoreInterface; the message store for messages
     */
    @SuppressWarnings("checkstyle:parameternumber")
    public SupplyChainActor(final String name, final MessageHandlerInterface messageHandler,
            final SCSimulatorInterface simulator, final OrientedPoint3d location, final String locationDescription,
            final Bank bank, final Money initialBalance, final TradeMessageStoreInterface messageStore)
    {
        super(name, messageHandler, simulator, location, locationDescription);
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

    /** {@inheritDoc} */
    @Override
    public void addFixedCost(final String description, final Duration interval, final Money amount)
    {
        FixedCost fixedCost = new FixedCost(this, description, interval, amount);
        this.fixedCosts.add(fixedCost);
    }

    /** {@inheritDoc} */
    @Override
    public TradeMessageStoreInterface getMessageStore()
    {
        return this.messageStore;
    }

    /** {@inheritDoc} */
    @Override
    public BankAccount getBankAccount()
    {
        return this.bankAccount;
    }

    /** {@inheritDoc} */
    @Override
    public List<FixedCost> getFixedCosts()
    {
        return this.fixedCosts;
    }

    /**
     * SupplyChainActor.Builder builds a SupplyChainActor. This class can be extended.
     * <p>
     * Copyright (c) 2022-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
     * The supply chain Java library uses a BSD-3 style license.
     * </p>
     * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
     */
    @SuppressWarnings({"checkstyle:visibilitymodifier", "checkstyle:hiddenfield"})
    public abstract static class Builder
    {
        /** name. */
        protected String name;

        /** messsageHandler. */
        protected MessageHandlerInterface messageHandler;

        /** simulator. */
        protected SCSimulatorInterface simulator;

        /** location. */
        protected OrientedPoint3d location;

        /** locationDescription. */
        protected String locationDescription;

        /** bank. */
        protected Bank bank;

        /** initialbankBalance. */
        protected Money initialBalance;

        /** messageStore. */
        protected TradeMessageStoreInterface messageStore;

        /**
         * Check that all fields are filled and valid.
         * @return boolean; whether all fields are filled and valid
         */
        public Builder check()
        {
            Throw.whenNull(this.name, "name cannot be null");
            Throw.whenNull(this.messageHandler, "messagehandler cannot be null");
            Throw.whenNull(this.simulator, "simulator cannot be null");
            Throw.whenNull(this.location, "location cannot be null");
            Throw.whenNull(this.locationDescription, "locationDescription cannot be null");
            Throw.whenNull(this.bank, "bank cannot be null");
            Throw.whenNull(this.initialBalance, "initialBalance cannot be null");
            Throw.whenNull(this.messageStore, "messageStore cannot be null");
            return this;
        }

        /**
         * Override this method to build the correct actor.
         * @return the constructed SupplyChainActor.
         */
        public abstract SupplyChainActor build();

        /**
         * @param name String; the name of the actor
         * @return Builder for chaining
         */
        public Builder setName(final String name)
        {
            this.name = name;
            return this;
        }

        /**
         * @param messageHandler MessageHandlerInterface; the handler for messages
         * @return Builder for chaining
         */
        public Builder setMessageHandler(final MessageHandlerInterface messageHandler)
        {
            this.messageHandler = messageHandler;
            return this;
        }

        /**
         * @param simulator SCSimulatorInterface; the simulator
         * @return Builder for chaining
         */
        public Builder setSimulator(final SCSimulatorInterface simulator)
        {
            this.simulator = simulator;
            return this;
        }

        /**
         * @param location OrientedPoint3d; the location of the actor on the map
         * @return Builder for chaining
         */
        public Builder setLocation(final OrientedPoint3d location)
        {
            this.location = location;
            return this;
        }

        /**
         * @param locationDescription String; a description of the location (e.g., "Amsterdam")
         * @return Builder for chaining
         */
        public Builder setLocationDescription(final String locationDescription)
        {
            this.locationDescription = locationDescription;
            return this;
        }

        /**
         * @param bank Bank; the bank of this actor
         * @return Builder for chaining
         */
        public Builder setBank(final Bank bank)
        {
            this.bank = bank;
            return this;
        }

        /**
         * @param initialBalance Money; the initial balance of the bank account
         * @return Builder for chaining
         */
        public Builder setIinitialBalance(final Money initialBalance)
        {
            this.initialBalance = initialBalance;
            return this;
        }

        /**
         * @param messageStore MessageStoreInterface; the messageStore for the messages
         * @return Builder for chaining
         */
        public Builder setMessageStore(final TradeMessageStoreInterface messageStore)
        {
            this.messageStore = messageStore;
            return this;
        }

    }
}
