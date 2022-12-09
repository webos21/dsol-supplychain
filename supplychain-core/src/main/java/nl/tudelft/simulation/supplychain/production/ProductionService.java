package nl.tudelft.simulation.supplychain.production;

import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.supplychain.actor.StockKeepingActor;
import nl.tudelft.simulation.supplychain.inventory.InventoryInterface;
import nl.tudelft.simulation.supplychain.message.trade.ProductionOrder;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * The abstract class ProductionService implements the ProductionServiceInterface and is a simple starting point for the
 * production of goods. The bill of materials of the product determines the required raw materials to use.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class ProductionService implements ProductionServiceInterface
{
    /** */
    private static final long serialVersionUID = 1L;

    /** The actor that owns the production service. */
    protected StockKeepingActor owner;

    /** the stock for getting and storing materials. */
    protected InventoryInterface stock;

    /** The product of the production service. */
    protected Product product;

    /**
     * Constructs a new production service for one product.
     * @param owner the actor that owns the production service.
     * @param stock the stock for getting and storing materials.
     * @param product Product; the product of the production service.
     */
    public ProductionService(final StockKeepingActor owner, final InventoryInterface stock, final Product product)
    {
        super();
        this.owner = owner;
        this.stock = stock;
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
    public Product getProduct()
    {
        return this.product;
    }
}
