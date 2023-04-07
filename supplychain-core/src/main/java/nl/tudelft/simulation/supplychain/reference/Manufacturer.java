package nl.tudelft.simulation.supplychain.reference;

import java.util.ArrayList;
import java.util.List;

import org.djunits.Throw;
import org.djutils.draw.point.OrientedPoint3d;

import nl.tudelft.simulation.supplychain.dsol.SCSimulatorInterface;
import nl.tudelft.simulation.supplychain.finance.Bank;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.message.Message;
import nl.tudelft.simulation.supplychain.message.handler.MessageHandlerInterface;
import nl.tudelft.simulation.supplychain.message.store.trade.TradeMessageStoreInterface;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.producing.ProducingActor;
import nl.tudelft.simulation.supplychain.role.producing.ProducingRole;

/**
 * Reference implementation for a manufacturer.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Manufacturer extends DistributionCenter implements ProducingActor
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** the production capabilities of this manufacturer. */
    private ProducingRole producingRole;

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
    public Manufacturer(final String name, final MessageHandlerInterface messageHandler, final SCSimulatorInterface simulator,
            final OrientedPoint3d location, final String locationDescription, final Bank bank, final Money initialBalance,
            final TradeMessageStoreInterface messageStore)
    {
        super(name, messageHandler, simulator, location, locationDescription, bank, initialBalance, messageStore);
    }

    /** {@inheritDoc} */
    @Override
    public ProducingRole getProducingRole()
    {
        return this.producingRole;
    }

    /** {@inheritDoc} */
    @Override
    public void setProducingRole(final ProducingRole producingRole)
    {
        Throw.whenNull(this.producingRole, "producingRole cannot be null");
        Throw.when(this.producingRole != null, IllegalStateException.class, "producingRole already initialized");
        addRole(this.producingRole);
        this.producingRole = producingRole;
    }

    /** {@inheritDoc} */
    @Override
    public void receiveMessage(final Message message)
    {
        Throw.whenNull(this.producingRole, "ProducingRole not initialized for actor: " + this.getName());
        super.receiveMessage(message);
    }

    /**
     * @return the raw materials
     */
    public List<Product> getRawMaterials()
    {
        List<Product> rawMaterials = new ArrayList<Product>();
        for (Product product : getInventoryRole().getInventory().getProducts())
        {
            if (product.getBillOfMaterials().getMaterials().size() == 0)
            {
                rawMaterials.add(product);
            }
        }
        return rawMaterials;
    }

    /**
     * @return the end products
     */
    public List<Product> getEndProducts()
    {
        List<Product> endProducts = new ArrayList<Product>();
        for (Product product : getInventoryRole().getInventory().getProducts())
        {
            if (product.getBillOfMaterials().getMaterials().size() > 0)
            {
                endProducts.add(product);
            }
        }
        return endProducts;
    }

}
