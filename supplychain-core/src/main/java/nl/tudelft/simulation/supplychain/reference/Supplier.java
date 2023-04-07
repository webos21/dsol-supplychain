package nl.tudelft.simulation.supplychain.reference;

import org.djunits.Throw;
import org.djutils.draw.point.OrientedPoint3d;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.dsol.SCSimulatorInterface;
import nl.tudelft.simulation.supplychain.finance.Bank;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.message.Message;
import nl.tudelft.simulation.supplychain.message.handler.MessageHandlerInterface;
import nl.tudelft.simulation.supplychain.message.store.trade.TradeMessageStoreInterface;
import nl.tudelft.simulation.supplychain.role.inventory.InventoryActor;
import nl.tudelft.simulation.supplychain.role.inventory.InventoryRole;
import nl.tudelft.simulation.supplychain.role.selling.SellingActor;
import nl.tudelft.simulation.supplychain.role.selling.SellingRole;

/**
 * Reference implementation for a Supplier.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Supplier extends SupplyChainActor implements SellingActor, InventoryActor
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221206L;

    /** The role to sell. */
    private SellingRole sellingRole = null;

    /** the role to keep inventory. */
    private InventoryRole inventoryRole = null;

    /**
     * @param name String; the name of the Supplier
     * @param messageHandler MessageHandlerInterface; the message handler to use
     * @param simulator SCSimulatorInterface; the simulator
     * @param location Location; the locatrion of the actor on the map or grid
     * @param locationDescription String; a description of the location of the Supplier
     * @param bank Bank; the bank of the reSuppliertailer
     * @param initialBalance Money; the initial bank balance
     * @param messageStore TradeMessageStoreInterface; the messageStore for the messages
     */
    @SuppressWarnings("checkstyle:parameternumber")
    public Supplier(final String name, final MessageHandlerInterface messageHandler, final SCSimulatorInterface simulator,
            final OrientedPoint3d location, final String locationDescription, final Bank bank, final Money initialBalance,
            final TradeMessageStoreInterface messageStore)
    {
        super(name, messageHandler, simulator, location, locationDescription, bank, initialBalance, messageStore);
    }

    /** {@inheritDoc} */
    @Override
    public SellingRole getSellingRole()
    {
        return this.sellingRole;
    }

    /**
     * @param sellingRole The sellingRole to set.
     */
    @Override
    public void setSellingRole(final SellingRole sellingRole)
    {
        Throw.whenNull(sellingRole, "sellingRole cannot be null");
        Throw.when(this.sellingRole != null, IllegalStateException.class, "sellingRole already initialized");
        addRole(sellingRole);
        this.sellingRole = sellingRole;
    }

    /** {@inheritDoc} */
    @Override
    public InventoryRole getInventoryRole()
    {
        return this.inventoryRole;
    }

    /** {@inheritDoc} */
    @Override
    public void setInventoryRole(final InventoryRole inventoryRole)
    {
        Throw.whenNull(inventoryRole, "inventoryRole cannot be null");
        Throw.when(this.inventoryRole != null, IllegalStateException.class, "inventoryRole already initialized");
        addRole(inventoryRole);
        this.inventoryRole = inventoryRole;
    }

    /** {@inheritDoc} */
    @Override
    public void receiveMessage(final Message message)
    {
        Throw.whenNull(this.sellingRole, "SellingRole not initialized for actor: " + this.getName());
        Throw.whenNull(this.inventoryRole, "InventoryRole not initialized for actor: " + this.getName());
        super.receiveMessage(message);
    }

}
