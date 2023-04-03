package nl.tudelft.simulation.supplychain.test;

import java.rmi.RemoteException;
import java.util.Iterator;

import javax.naming.NamingException;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.draw.bounds.Bounds3d;
import org.djutils.draw.point.OrientedPoint3d;

import nl.tudelft.simulation.dsol.animation.D2.SingleImageRenderable;
import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.dsol.swing.charts.xy.XYChart;
import nl.tudelft.simulation.supplychain.dsol.SCSimulatorInterface;
import nl.tudelft.simulation.supplychain.finance.Bank;
import nl.tudelft.simulation.supplychain.finance.BankAccount;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.inventory.Inventory;
import nl.tudelft.simulation.supplychain.message.handler.MessageHandlerInterface;
import nl.tudelft.simulation.supplychain.message.store.trade.TradeMessageStoreInterface;
import nl.tudelft.simulation.supplychain.policy.bill.BillPolicy;
import nl.tudelft.simulation.supplychain.policy.internaldemand.InternalDemandPolicyRFQ;
import nl.tudelft.simulation.supplychain.policy.order.AbstractOrderPolicy;
import nl.tudelft.simulation.supplychain.policy.order.OrderPolicyStock;
import nl.tudelft.simulation.supplychain.policy.orderconfirmation.OrderConfirmationPolicy;
import nl.tudelft.simulation.supplychain.policy.payment.PaymentPolicy;
import nl.tudelft.simulation.supplychain.policy.payment.PaymentPolicyEnum;
import nl.tudelft.simulation.supplychain.policy.quote.AbstractQuotePolicy;
import nl.tudelft.simulation.supplychain.policy.quote.QuoteComparatorEnum;
import nl.tudelft.simulation.supplychain.policy.quote.QuotePolicyAll;
import nl.tudelft.simulation.supplychain.policy.rfq.RequestForQuotePolicy;
import nl.tudelft.simulation.supplychain.policy.shipment.AbstractShipmentPolicy;
import nl.tudelft.simulation.supplychain.policy.shipment.ShipmentPolicyStock;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.reference.Retailer;
import nl.tudelft.simulation.supplychain.reference.Supplier;
import nl.tudelft.simulation.supplychain.role.buying.BuyingRoleYP;
import nl.tudelft.simulation.supplychain.role.inventory.RestockingServiceSafety;
import nl.tudelft.simulation.supplychain.role.selling.SellingRole;
import nl.tudelft.simulation.supplychain.transport.TransportMode;
import nl.tudelft.simulation.supplychain.util.DistConstantDuration;

/**
 * Retailer.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class PCShop extends Retailer
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** the manufacturer where the PCShop buys. */
    private Supplier manufacturer;

    /**
     * @param name String; the name of the Customer
     * @param messageHandler MessageHandlerInterface; the message handler to use
     * @param simulator SCSimulatorInterface; the simulator
     * @param location Location; the locatrion of the actor on the map or grid
     * @param locationDescription String; a description of the location of the Customer
     * @param bank Bank; the bank of the customer
     * @param initialBalance Money; the initial bank balance
     * @param messageStore TradeMessageStoreInterface; the messageStore for the messages
     * @param product initial stock product
     * @param amount amount of initial stock
     * @param manufacturer fixed manufacturer to use
     * @throws RemoteException remote simulator error
     * @throws NamingException on animation error
     */
    @SuppressWarnings("checkstyle:parameternumber")
    public PCShop(final String name, final MessageHandlerInterface messageHandler, final SCSimulatorInterface simulator,
            final OrientedPoint3d location, final String locationDescription, final Bank bank, final Money initialBalance,
            final TradeMessageStoreInterface messageStore, final Product product, final double amount,
            final Supplier manufacturer) throws RemoteException, NamingException
    {
        super(name, messageHandler, simulator, location, locationDescription, bank, initialBalance, messageStore);
        this.manufacturer = manufacturer;
        // give the retailer some stock
        Inventory _stock = new Inventory(this);
        if (product != null)
        {
            _stock.addToInventory(product, amount, product.getUnitMarketPrice().multiplyBy(amount));
            super.setInitialStock(_stock);
        }
        init();
        if (simulator instanceof AnimatorInterface)
        {
            new SingleImageRenderable<>(this, simulator,
                    Factory.class.getResource("/nl/tudelft/simulation/supplychain/images/Retailer.gif"));
        }
    }

    /**
     * @throws RemoteException remote simulator error
     */
    public void init() throws RemoteException
    {
        // tell PCshop to use the RFQhandler to handle RFQs
        RequestForQuotePolicy rfqHandler = new RequestForQuotePolicy(this, super.stock, 1.2,
                new DistConstantDuration(new Duration(1.23, DurationUnit.HOUR)), TransportMode.PLANE);
        //
        // create an order handler
        AbstractOrderPolicy orderHandler = new OrderPolicyStock(this, super.stock);
        //
        // hopefully, the PCShop will get payments in the end
        PaymentPolicy paymentHandler = new PaymentPolicy(this, getBankAccount());
        //
        // add the handlers to the buying role for PCShop
        SellingRole sellingRole = new SellingRole(this, this.simulator, rfqHandler, orderHandler, paymentHandler);
        super.setSellingRole(sellingRole);
        //
        // After a while, the PC Shop needs to restock and order
        // do this for every product we have initially in stock
        Iterator<Product> stockIter = super.stock.iterator();
        while (stockIter.hasNext())
        {
            Product product = stockIter.next();
            new RestockingServiceSafety(super.stock, product, new Duration(24.0, DurationUnit.HOUR), false, 5.0, true, 10.0,
                    new Duration(14.0, DurationUnit.DAY));
            // order 100 PCs when actual+claimed < 100
            // policy will schedule itself
        }
        //
        // BUY PRODUCTS WHEN THERE IS INTERNAL DEMAND
        //
        // tell PCShop to use the InternalDemandPolicy for all products
        InternalDemandPolicyRFQ internalDemandHandler =
                new InternalDemandPolicyRFQ(this, new Duration(1.0, DurationUnit.HOUR), super.stock);
        Iterator<Product> productIter = super.stock.iterator();
        while (productIter.hasNext())
        {
            Product product = productIter.next();
            internalDemandHandler.addSupplier(product, this.manufacturer);
        }
        //
        // tell PCShop to use the Quotehandler to handle quotes
        AbstractQuotePolicy quoteHandler = new QuotePolicyAll(this, QuoteComparatorEnum.SORT_DATE_PRICE_DISTANCE,
                new Duration(1.0, DurationUnit.HOUR), 0.4, 0.1);
        //
        // PCShop has the standard order confirmation handler
        OrderConfirmationPolicy confirmationHandler = new OrderConfirmationPolicy(this);
        //
        // PCShop will get a bill in the end
        BillPolicy billHandler = new BillPolicy(this, super.bankAccount, PaymentPolicyEnum.PAYMENT_IMMEDIATE,
                new DistConstantDuration(Duration.ZERO));
        //
        // hopefully, PCShop will get laptop shipments, put them in stock
        AbstractShipmentPolicy shipmentHandler = new ShipmentPolicyStock(this, super.stock);
        //
        // add the handlers to the buying role for PCShop
        BuyingRoleYP buyingRole = new BuyingRoleYP(this, this.simulator, internalDemandHandler, quoteHandler,
                confirmationHandler, shipmentHandler, billHandler);
        super.setBuyingRole(buyingRole);
        //
        // CHARTS
        //
        if (this.simulator instanceof AnimatorInterface)
        {
            XYChart bankChart = new XYChart(this.simulator, "BankAccount " + getName());
            bankChart.add("bank account", getBankAccount(), BankAccount.BANK_ACCOUNT_CHANGED_EVENT);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Bounds3d getBounds()
    {
        return new Bounds3d(25.0, 25.0, 1.0);
    }
}
