package nl.tudelft.simulation.supplychain.reference;

import java.io.Serializable;

import org.djutils.draw.point.OrientedPoint3d;

import nl.tudelft.simulation.supplychain.actor.StockKeepingActor;
import nl.tudelft.simulation.supplychain.actor.capabilities.SellerInterface;
import nl.tudelft.simulation.supplychain.banking.Bank;
import nl.tudelft.simulation.supplychain.contentstore.ContentStoreInterface;
import nl.tudelft.simulation.supplychain.dsol.SCSimulatorInterface;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.finance.MoneyUnit;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.roles.SellingRole;

/**
 * Reference implementation for a Supplier.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Supplier extends StockKeepingActor implements SellerInterface
{
    /** the serial version uid. */
    private static final long serialVersionUID = 12L;

    /** The role to sell. */
    private SellingRole sellingRole = null;

    /**
     * @param name the name of the supplier
     * @param simulator the simulator to use
     * @param position the position on the map
     * @param bank the bank
     * @param contentStore the contentStore for the messages
     */
    public Supplier(final String name, final SCSimulatorInterface simulator, final OrientedPoint3d position, final Bank bank,
            final ContentStoreInterface contentStore)
    {
        this(name, simulator, position, bank, new Money(0.0, MoneyUnit.USD), contentStore);
    }

    /**
     * @param name the name of the supplier
     * @param simulator the simulator to use
     * @param position the position on the map
     * @param bank the bank
     * @param initialBankAccount the initial bank balance
     * @param contentStore the contentStore for the messages
     */
    public Supplier(final String name, final SCSimulatorInterface simulator, final OrientedPoint3d position, final Bank bank,
            final Money initialBankAccount, final ContentStoreInterface contentStore)
    {
        super(name, simulator, position, bank, initialBankAccount, contentStore);
    }

    /** {@inheritDoc} */
    @Override
    public void checkStock(final Product product)
    {
        // TODO: implement checkStock
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
    public void setSellingRole(final SellingRole sellingRole)
    {
        // remove the previous selling role
        if (this.sellingRole != null)
        {
            super.removeRole(this.sellingRole);
        }
        super.addRole(sellingRole);
        this.sellingRole = sellingRole;
    }

    /** {@inheritDoc} */
    @Override
    public boolean handleContent(final Serializable content)
    {
        if (this.sellingRole == null)
        {
            throw new RuntimeException("SuyingRole not initialized for Supplier: " + this.getName());
        }
        return super.handleContent(content);
    }
}
