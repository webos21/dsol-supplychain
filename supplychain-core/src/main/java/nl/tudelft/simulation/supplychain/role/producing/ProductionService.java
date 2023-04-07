package nl.tudelft.simulation.supplychain.role.producing;

import org.djunits.Throw;
import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.supplychain.message.trade.ProductionOrder;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.inventory.InventoryActor;

/**
 * The abstract class ProductionService implements the ProductionServiceInterface and is a simple starting point for the
 * production of goods. The bill of materials of the product determines the required raw materials to use.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class ProductionService implements ProductionServiceInterface
{
    /** */
    private static final long serialVersionUID = 20221201L;

    /** The actor that owns the production service. */
    private InventoryActor owner;

    /** The product of the production service. */
    private Product product;

    /**
     * Constructs a new production service for one product.
     * @param owner the actor that owns the production service.
     * @param product Product; the product of the production service.
     */
    public ProductionService(final InventoryActor owner, final Product product)
    {
        Throw.whenNull(owner, "owner cannot be null");
        Throw.whenNull(product, "product cannot be null");
        this.owner = owner;
        this.product = product;
    }

    /** {@inheritDoc} */
    @Override
    public abstract void acceptProductionOrder(ProductionOrder productionOrder);

    /** {@inheritDoc} */
    @Override
    public abstract Duration getExpectedProductionDuration(ProductionOrder productionOrder);

    /**
     * @return the product.
     */
    @Override
    public Product getProduct()
    {
        return this.product;
    }

    /** {@inheritDoc} */
    @Override
    public InventoryActor getOwner()
    {
        return this.owner;
    }

}
