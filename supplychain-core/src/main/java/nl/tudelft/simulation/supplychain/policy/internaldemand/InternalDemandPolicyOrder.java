package nl.tudelft.simulation.supplychain.policy.internaldemand;

import java.util.LinkedHashMap;
import java.util.Map;

import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.message.trade.InternalDemand;
import nl.tudelft.simulation.supplychain.message.trade.Order;
import nl.tudelft.simulation.supplychain.message.trade.OrderStandalone;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.stock.StockInterface;

/**
 * The InternalDemandPolicyOrder is a simple implementation of the business logic to handle a request for new products through
 * direct ordering at a known supplier. When receiving the internal demand, it just creates an Order based on a table that maps
 * Products onto Actors, and sends it after a given time delay.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class InternalDemandPolicyOrder extends AbstractInternalDemandPolicy
{
    /** the serial version uid. */
    private static final long serialVersionUID = 12L;

    /** a table to map the products onto a unique supplier. */
    private Map<Product, SupplierRecord> suppliers = new LinkedHashMap<Product, SupplierRecord>();

    /**
     * Constructs a new InternalDemandPolicyOrder.
     * @param owner the owner of the internal demand
     * @param handlingTime the handling time distribution
     * @param stock the stock for being able to change the ordered amount
     */
    public InternalDemandPolicyOrder(final SupplyChainActor owner, final DistContinuousDuration handlingTime,
            final StockInterface stock)
    {
        super("InternalDemandPolicyOrder", owner, handlingTime, stock);
    }

    /**
     * @param product Product; the product that has a fixed supplier.
     * @param supplier the supplier for that product.
     * @param unitPrice the price per unit to ask for.
     */
    public void addSupplier(final Product product, final SupplyChainActor supplier, final Money unitPrice)
    {
        this.suppliers.put(product, new SupplierRecord(supplier, unitPrice));
    }

    /** {@inheritDoc} */
    @Override
    public boolean handleMessage(final InternalDemand internalDemand)
    {
        if (!isValidMessage(internalDemand))
        {
            return false;
        }
        // resolve the suplier
        SupplierRecord supplierRecord = this.suppliers.get(internalDemand.getProduct());
        if (supplierRecord == null)
        {
            Logger.warn("checkContent", "InternalDemand for actor " + getOwner() + " contains product "
                    + internalDemand.getProduct().toString() + " without a supplier");
            return false;
        }
        // create an immediate order
        if (super.stock != null)
        {
            super.stock.changeOrderedAmount(internalDemand.getProduct(), internalDemand.getAmount());
        }
        SupplyChainActor supplier = supplierRecord.getSupplier();
        Money price = supplierRecord.getUnitPrice().multiplyBy(internalDemand.getAmount());
        Order order = new OrderStandalone(getOwner(), supplier, internalDemand, internalDemand.getLatestDeliveryDate(),
                internalDemand.getProduct(), internalDemand.getAmount(), price);
        // and send it out after the handling time
        getOwner().sendMessage(order, this.handlingTime.draw());
        return true;
    }

    /**
     * INNER CLASS FOR STORING RECORDS OF SUPPLIERS AND PRICE
     * <p>
     * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
     * The supply chain Java library uses a BSD-3 style license.
     * </p>
     * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
     */
    protected class SupplierRecord
    {
        /** the supplier. */
        private SupplyChainActor supplier;

        /** the agreed price to pay per unit of product. */
        private Money unitPrice;

        /**
         * Construct a new SupplierRecord.
         * @param supplier the supplier
         * @param unitPrice the price per unit
         */
        public SupplierRecord(final SupplyChainActor supplier, final Money unitPrice)
        {
            super();
            this.supplier = supplier;
            this.unitPrice = unitPrice;
        }

        /**
         * @return the supplier.
         */
        public SupplyChainActor getSupplier()
        {
            return this.supplier;
        }

        /**
         * @return the unitPrice.
         */
        public Money getUnitPrice()
        {
            return this.unitPrice;
        }
    }
}
