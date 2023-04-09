package nl.tudelft.simulation.supplychain.reference;

import org.djunits.Throw;
import org.djutils.draw.point.OrientedPoint2d;

import nl.tudelft.simulation.supplychain.actor.ActorAlreadyDefinedException;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;
import nl.tudelft.simulation.supplychain.finance.Bank;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.message.Message;
import nl.tudelft.simulation.supplychain.message.store.trade.TradeMessageStoreInterface;
import nl.tudelft.simulation.supplychain.role.buying.BuyingRole;
import nl.tudelft.simulation.supplychain.role.demand.DemandGenerationRole;

/**
 * A Customer is an actor which usually orders (pulls) products from a Distributor. <br>
 * <p>
 * Copyright (c) 2003-2023 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Customer extends SupplyChainActor
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** The role to buy products. */
    private BuyingRole buyingRole;

    /** The role to generate demand. */
    private DemandGenerationRole demandGenerationRole;

    /**
     * @param id String, the unique id of the customer
     * @param name String; the longer name of the customer
     * @param model SupplyChainModelInterface; the model
     * @param location OrientedPoint2d; the location of the actor
     * @param locationDescription String; the location description of the actor (e.g., a city, country)
     * @param bank Bank; the bank for the BankAccount
     * @param initialBalance Money; the initial balance for the actor
     * @param messageStore TradeMessageStoreInterface; the message store for messages
     * @throws ActorAlreadyDefinedException when the actor was already registered in the model
     */
    @SuppressWarnings("checkstyle:parameternumber")
    public Customer(final String id, final String name, final SupplyChainModelInterface model, final OrientedPoint2d location,
            final String locationDescription, final Bank bank, final Money initialBalance,
            final TradeMessageStoreInterface messageStore) throws ActorAlreadyDefinedException
    {
        super(id, name, model, location, locationDescription, bank, initialBalance, messageStore);
    }

    /**
     * Return the buying role.
     * @return BuyingRole; the buying role
     */
    public BuyingRole getBuyingRole()
    {
        return this.buyingRole;
    }

    /**
     * Set the buying role
     * @param buyingRole BuyingRole; the new buying role
     */
    public void setBuyingRole(final BuyingRole buyingRole)
    {
        Throw.whenNull(buyingRole, "buyingRole cannot be null");
        Throw.when(this.buyingRole != null, IllegalStateException.class, "buyingRole already initialized");
        addRole(buyingRole);
        this.buyingRole = buyingRole;
    }

    /**
     * Return the demand generation role.
     * @return DemandGenerationRole; the demand generation role
     */
    public DemandGenerationRole getDemandGenerationRole()
    {
        return this.demandGenerationRole;
    }

    /**
     * Set the demand generation role.
     * @param demandGenerationRole DemandGenerationRole; the new demand generation role
     */
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
