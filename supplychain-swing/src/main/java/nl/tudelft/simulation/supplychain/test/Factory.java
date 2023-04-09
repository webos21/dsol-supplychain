package nl.tudelft.simulation.supplychain.test;

import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.draw.bounds.Bounds3d;
import org.djutils.draw.point.OrientedPoint3d;

import nl.tudelft.simulation.dsol.animation.D2.SingleImageRenderable;
import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.dsol.swing.charts.xy.XYChart;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainSimulatorInterface;
import nl.tudelft.simulation.supplychain.finance.Bank;
import nl.tudelft.simulation.supplychain.finance.BankAccount;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.inventory.Inventory;
import nl.tudelft.simulation.supplychain.message.receiver.MessageReceiver;
import nl.tudelft.simulation.supplychain.message.store.trade.TradeMessageStoreInterface;
import nl.tudelft.simulation.supplychain.policy.order.OrderPolicy;
import nl.tudelft.simulation.supplychain.policy.order.OrderPolicyStock;
import nl.tudelft.simulation.supplychain.policy.payment.PaymentPolicy;
import nl.tudelft.simulation.supplychain.policy.rfq.RequestForQuotePolicy;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.reference.Supplier;
import nl.tudelft.simulation.supplychain.role.selling.SellingRole;
import nl.tudelft.simulation.supplychain.transport.TransportMode;
import nl.tudelft.simulation.supplychain.util.DistConstantDuration;

/**
 * The ComputerShop named Factory.
 * <p>
 * Copyright (c) 2003-2023 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Factory extends Supplier
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /**
     * @param name String; the name of the Customer
     * @param messageReceiver MessageReceiver; the message handler to use
     * @param simulator SupplyChainSimulatorInterface; the simulator
     * @param location Location; the locatrion of the actor on the map or grid
     * @param locationDescription String; a description of the location of the Customer
     * @param bank Bank; the bank of the customer
     * @param initialBalance Money; the initial bank balance
     * @param messageStore TradeMessageStoreInterface; the messageStore for the messages
     * @param product initial stock product
     * @param amount amount of initial stock
     * @throws RemoteException remote simulator error
     * @throws NamingException on animation error
     */
    @SuppressWarnings("checkstyle:parameternumber")
    public Factory(final String name, final MessageReceiver messageReceiver,
            final SupplyChainSimulatorInterface simulator, final OrientedPoint3d location, final String locationDescription,
            final Bank bank, final Money initialBalance, final TradeMessageStoreInterface messageStore, final Product product,
            final double amount) throws RemoteException, NamingException
    {
        super(name, messageReceiver, simulator, location, locationDescription, bank, initialBalance, messageStore);
        // give the retailer some stock
        Inventory _stock = new Inventory(this);
        if (product != null)
        {
            _stock.addToInventory(product, amount, product.getUnitMarketPrice().multiplyBy(amount));
            super.setInitialStock(_stock);
        }
        // We initialize Factory
        this.init();
        // Let's give Factory its corresponding image
        if (simulator instanceof AnimatorInterface)
        {
            new SingleImageRenderable<>(this, simulator,
                    Factory.class.getResource("/nl/tudelft/simulation/supplychain/images/Manufacturer.gif"));
        }
    }

    /**
     * @throws RemoteException simulator error
     */
    public void init() throws RemoteException
    {
        // tell Factory to use the RFQhandler to handle RFQs
        RequestForQuotePolicy rfqHandler = new RequestForQuotePolicy(this, super.inventory, 1.2,
                new DistConstantDuration(new Duration(1.23, DurationUnit.HOUR)), TransportMode.PLANE);
        //
        // create an order handler
        OrderPolicy orderHandler = new OrderPolicyStock(this, super.inventory);
        //
        // hopefully, Factory will get payments in the end
        PaymentPolicy paymentHandler = new PaymentPolicy(this, getBankAccount());
        //
        // add the handlers to the SellingRole
        SellingRole sellingRole = new SellingRole(this, this.simulator, rfqHandler, orderHandler, paymentHandler);
        super.setSellingRole(sellingRole);
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
