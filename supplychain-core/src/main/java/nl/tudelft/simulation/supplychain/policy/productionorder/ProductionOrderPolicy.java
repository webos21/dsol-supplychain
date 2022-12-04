package nl.tudelft.simulation.supplychain.policy.productionorder;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.message.trade.ProductionOrder;
import nl.tudelft.simulation.supplychain.policy.SupplyChainPolicy;
import nl.tudelft.simulation.supplychain.production.Production;

/**
 * Handles ProductionOrders.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ProductionOrderPolicy extends SupplyChainPolicy<ProductionOrder>
{
    /** Serial version ID. */
    private static final long serialVersionUID = 1L;

    /** for debugging. */
    private static final boolean DEBUG = false;

    /** the production facility of this handler. */
    private Production production = null;

    /**
     * constructs a new ProductionOrderHandler.
     * @param owner the owner of the production order handler
     * @param production the production facility
     */
    public ProductionOrderPolicy(final SupplyChainActor owner, final Production production)
    {
        super("ProductionOrderPolicy", owner, ProductionOrder.class);
        this.production = production;
        if (ProductionOrderPolicy.DEBUG)
        {
            System.out.println("DEBUG -- ProductionOrderHandler has been created and added to: " + owner.getName());
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean handleMessage(final ProductionOrder productionOrder)
    {
        return this.production.acceptProductionOrder(productionOrder);
    }

    /**
     * Method getProduction.
     * @return returns the production
     */
    public Production getProduction()
    {
        return this.production;
    }

}
