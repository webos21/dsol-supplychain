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
import nl.tudelft.simulation.supplychain.actor.StockKeepingActor;
import nl.tudelft.simulation.supplychain.actor.messaging.devices.reference.FaxDevice;
import nl.tudelft.simulation.supplychain.actor.unit.dist.DistConstantDuration;
import nl.tudelft.simulation.supplychain.dsol.SCSimulatorInterface;
import nl.tudelft.simulation.supplychain.finance.Bank;
import nl.tudelft.simulation.supplychain.finance.BankAccount;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.finance.MoneyUnit;
import nl.tudelft.simulation.supplychain.inventory.Stock;
import nl.tudelft.simulation.supplychain.inventory.policies.RestockingPolicySafety;
import nl.tudelft.simulation.supplychain.message.handler.MessageHandlerInterface;
import nl.tudelft.simulation.supplychain.message.store.MessageStoreInterface;
import nl.tudelft.simulation.supplychain.messagehandlers.HandleAllMessages;
import nl.tudelft.simulation.supplychain.policy.bill.BillPolicy;
import nl.tudelft.simulation.supplychain.policy.internaldemand.InternalDemandPolicyRFQ;
import nl.tudelft.simulation.supplychain.policy.order.AbstractOrderPolicy;
import nl.tudelft.simulation.supplychain.policy.order.OrderPolicyStock;
import nl.tudelft.simulation.supplychain.policy.orderconfirmation.OrderConfirmationPolicy;
import nl.tudelft.simulation.supplychain.policy.payment.PaymentPolicy;
import nl.tudelft.simulation.supplychain.policy.payment.PaymentPolicyEnum;
import nl.tudelft.simulation.supplychain.policy.quote.QuoteComparatorEnum;
import nl.tudelft.simulation.supplychain.policy.quote.AbstractQuotePolicy;
import nl.tudelft.simulation.supplychain.policy.quote.QuotePolicyAll;
import nl.tudelft.simulation.supplychain.policy.rfq.RequestForQuotePolicy;
import nl.tudelft.simulation.supplychain.policy.shipment.AbstractShipmentPolicy;
import nl.tudelft.simulation.supplychain.policy.shipment.ShipmentPolicyStock;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.reference.Retailer;
import nl.tudelft.simulation.supplychain.role.buying.BuyingRoleYP;
import nl.tudelft.simulation.supplychain.role.selling.SellingRole;
import nl.tudelft.simulation.supplychain.transport.TransportMode;

/**
 * Retailer. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class PCShop extends Retailer
{
    /** the serial version uid. */
    private static final long serialVersionUID = 12L;

    /** the manufacturer where the PCShop buys */
    private StockKeepingActor manufacturer;

    /**
     * @param name the name of the manufacturer
     * @param simulator the simulator to use
     * @param position the position on the map
     * @param bank the bank
     * @param product initial stock product
     * @param amount amount of initial stock
     * @param manufacturer fixed manufacturer to use
     * @param messageStore the messageStore to store the messages
     * @throws RemoteException remote simulator error
     * @throws NamingException
     */
    public PCShop(final String name, final SCSimulatorInterface simulator, final OrientedPoint3d position, final Bank bank,
            final Product product, final double amount, final StockKeepingActor manufacturer,
            final MessageStoreInterface messageStore) throws RemoteException, NamingException
    {
        this(name, simulator, position, bank, new Money(0.0, MoneyUnit.USD), product, amount, manufacturer, messageStore);
    }

    /**
     * @param name the name of the manufacturer
     * @param simulator the simulator to use
     * @param position the position on the map
     * @param bank the bank
     * @param initialBankAccount the initial bank balance
     * @param product initial stock product
     * @param amount amount of initial stock
     * @param manufacturer fixed manufacturer to use
     * @param messageStore the messageStore to store the messages
     * @throws RemoteException remote simulator error
     * @throws NamingException
     */
    public PCShop(final String name, final SCSimulatorInterface simulator, final OrientedPoint3d position, final Bank bank,
            final Money initialBankAccount, final Product product, final double amount, final StockKeepingActor manufacturer,
            final MessageStoreInterface messageStore) throws RemoteException, NamingException
    {
        super(name, simulator, position, bank, initialBankAccount, messageStore);
        this.manufacturer = manufacturer;
        // give the retailer some stock
        Stock _stock = new Stock(this);
        if (product != null)
        {
            _stock.addInventory(product, amount, product.getUnitMarketPrice().multiplyBy(amount));
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
        // give the actor a fax device which is checked every hour
        FaxDevice fax = new FaxDevice("PCShopFAx", this.simulator);
        super.addSendingDevice(fax);
        MessageHandlerInterface secretary = new HandleAllMessages(this);
        super.addReceivingDevice(fax, secretary, new DistConstantDuration(new Duration(1.0, DurationUnit.HOUR)));
        //
        // tell PCshop to use the RFQhandler to handle RFQs
        RequestForQuotePolicy rfqHandler = new RequestForQuotePolicy(this, super.stock, 1.2,
                new DistConstantDuration(new Duration(1.23, DurationUnit.HOUR)), TransportMode.PLANE);
        //
        // create an order handler
        AbstractOrderPolicy orderHandler = new OrderPolicyStock(this, super.stock);
        //
        // hopefully, the PCShop will get payments in the end
        PaymentPolicy paymentHandler = new PaymentPolicy(this, super.bankAccount);
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
            new RestockingPolicySafety(super.stock, product, new Duration(24.0, DurationUnit.HOUR), false, 5.0, true, 10.0,
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
        BuyingRoleYP buyingRole = new BuyingRoleYP(this, this.simulator, internalDemandHandler, quoteHandler, confirmationHandler,
                shipmentHandler, billHandler);
        super.setBuyingRole(buyingRole);
        //
        // CHARTS
        //
        if (this.simulator instanceof AnimatorInterface)
        {
            XYChart bankChart = new XYChart(this.simulator, "BankAccount " + this.name);
            bankChart.add("bank account", this.bankAccount, BankAccount.BANK_ACCOUNT_CHANGED_EVENT);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Bounds3d getBounds()
    {
        return new Bounds3d(25.0, 25.0, 1.0);
    }
}
