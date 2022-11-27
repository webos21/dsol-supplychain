package nl.tudelft.simulation.supplychain.policy.shipment;

import java.io.Serializable;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.Shipment;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.stock.StockInterface;

/**
 * When a Shipment comes in, it just has to be added to the Stock.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ShipmentPolicyStock extends ShipmentPolicy
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** access to the owner's stock to look at availability of products */
    protected StockInterface stock;

    /**
     * Construct a new ShipmentHandlerStock handler.
     * @param owner the owner of the handler
     * @param stock the stock to use for storing the incoming cargo
     */
    public ShipmentPolicyStock(final SupplyChainActor owner, final StockInterface stock)
    {
        super(owner);
        this.stock = stock;
    }

    /** {@inheritDoc} */
    @Override
    public boolean handleContent(final Serializable content)
    {
        if (!isValidContent(content))
        {
            return false;
        }
        Shipment shipment = (Shipment) content;
        // get the cargo from the shipment, and add its contents to the stock
        Product product = shipment.getProduct();
        double amount = shipment.getAmount();
        this.stock.addStock(product, amount, shipment.getTotalCargoValue());
        // update the administration
        this.stock.changeOrderedAmount(product, -amount);
        shipment.setInTransit(false);
        shipment.setDelivered(true);
        return true;
    }
}
