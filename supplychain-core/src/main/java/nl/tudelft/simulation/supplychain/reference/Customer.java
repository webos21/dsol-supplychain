package nl.tudelft.simulation.supplychain.reference;

import org.djunits.Throw;
import org.djutils.draw.point.OrientedPoint3d;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainSimulatorInterface;
import nl.tudelft.simulation.supplychain.finance.Bank;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.message.Message;
import nl.tudelft.simulation.supplychain.message.handler.MessageHandlerInterface;
import nl.tudelft.simulation.supplychain.message.store.trade.TradeMessageStoreInterface;
import nl.tudelft.simulation.supplychain.role.buying.BuyingActor;
import nl.tudelft.simulation.supplychain.role.buying.BuyingRole;
import nl.tudelft.simulation.supplychain.role.demand.DemandGenerationActor;
import nl.tudelft.simulation.supplychain.role.demand.DemandGenerationRole;

/**
 * A Customer is an actor which usually orders (pulls) products from a Distributor. <br>
 * <p>
 * Copyright (c) 2003-2023 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Customer extends SupplyChainActor implements BuyingActor, DemandGenerationActor
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** The role to buy products. */
    private BuyingRole buyingRole;

    /** The role to generate demand. */
    private DemandGenerationRole demandGenerationRole;

    /**
     * @param name String; the name of the Customer
     * @param messageHandler MessageHandlerInterface; the message handler to use
     * @param simulator SupplyChainSimulatorInterface; the simulator
     * @param location Location; the locatrion of the actor on the map or grid
     * @param locationDescription String; a description of the location of the Customer
     * @param bank Bank; the bank of the customer
     * @param initialBalance Money; the initial bank balance
     * @param messageStore TradeMessageStoreInterface; the messageStore for the messages
     */
    @SuppressWarnings("checkstyle:parameternumber")
    public Customer(final String name, final MessageHandlerInterface messageHandler,
            final SupplyChainSimulatorInterface simulator, final OrientedPoint3d location, final String locationDescription,
            final Bank bank, final Money initialBalance, final TradeMessageStoreInterface messageStore)
    {
        super(name, messageHandler, simulator, location, locationDescription, bank, initialBalance, messageStore);
    }

    /** {@inheritDoc} */
    @Override
    public BuyingRole getBuyingRole()
    {
        return this.buyingRole;
    }

    /** {@inheritDoc} */
    @Override
    public void setBuyingRole(final BuyingRole buyingRole)
    {
        Throw.whenNull(buyingRole, "buyingRole cannot be null");
        Throw.when(this.buyingRole != null, IllegalStateException.class, "buyingRole already initialized");
        addRole(buyingRole);
        this.buyingRole = buyingRole;
    }

    /** {@inheritDoc} */
    @Override
    public DemandGenerationRole getDemandGenerationRole()
    {
        return this.demandGenerationRole;
    }

    /** {@inheritDoc} */
    @Override
    public void setDemandGenerationRole(final DemandGenerationRole demandGenerationRole)
    {
        Throw.whenNull(demandGenerationRole, "demandGenerationRole cannot be null");
        Throw.when(this.demandGenerationRole != null, IllegalStateException.class, "demandGenerationRole already initialized");
        addRole(demandGenerationRole);
        this.demandGenerationRole = demandGenerationRole;
    }

    /** {@inheritDoc} */
    @Override
    public void receiveMessage(final Message message)
    {
        Throw.whenNull(this.buyingRole, "BuyingRole not initialized for Customer: " + this.getName());
        Throw.whenNull(this.demandGenerationRole, "DemandGenerationRole not initialized for Customer: " + this.getName());
        super.receiveMessage(message);
    }
}
