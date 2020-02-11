package nl.tudelft.simulation.supplychain.test;

import java.rmi.RemoteException;

import javax.media.j3d.Bounds;
import javax.naming.NamingException;
import javax.vecmath.Point3d;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.actor.messagehandlers.HandleAllMessages;
import nl.tudelft.simulation.actor.messagehandlers.MessageHandlerInterface;
import nl.tudelft.simulation.dsol.animation.D2.SingleImageRenderable;
import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.dsol.swing.charts.xy.XYChart;
import nl.tudelft.simulation.jstats.distributions.DistConstant;
import nl.tudelft.simulation.jstats.distributions.DistExponential;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.language.d3.BoundingBox;
import nl.tudelft.simulation.messaging.devices.reference.FaxDevice;
import nl.tudelft.simulation.supplychain.banking.Bank;
import nl.tudelft.simulation.supplychain.banking.BankAccount;
import nl.tudelft.simulation.supplychain.contentstore.ContentStoreInterface;
import nl.tudelft.simulation.supplychain.demand.Demand;
import nl.tudelft.simulation.supplychain.demand.DemandGeneration;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.handlers.BillHandler;
import nl.tudelft.simulation.supplychain.handlers.InternalDemandHandlerRFQ;
import nl.tudelft.simulation.supplychain.handlers.OrderConfirmationHandler;
import nl.tudelft.simulation.supplychain.handlers.PaymentPolicyEnum;
import nl.tudelft.simulation.supplychain.handlers.QuoteComparatorEnum;
import nl.tudelft.simulation.supplychain.handlers.QuoteHandler;
import nl.tudelft.simulation.supplychain.handlers.QuoteHandlerAll;
import nl.tudelft.simulation.supplychain.handlers.ShipmentHandler;
import nl.tudelft.simulation.supplychain.handlers.ShipmentHandlerConsume;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.reference.Customer;
import nl.tudelft.simulation.supplychain.reference.Retailer;
import nl.tudelft.simulation.supplychain.roles.BuyingRole;
import nl.tudelft.simulation.unit.dist.DistConstantDurationUnit;
import nl.tudelft.simulation.unit.dist.DistContinuousDurationUnit;

/**
 * Customer. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class Client extends Customer
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the product that Client wants to buy */
    private Product product;

    /** the fixed retailer where Client buys */
    private Retailer retailer;

    /**
     * @param name the name of the manufacturer
     * @param simulator the simulator to use
     * @param position the position on the map
     * @param bank the bank
     * @param initialBankAccount the initial bank balance
     * @param product product to order
     * @param retailer fixed retailer to use
     * @param contentStore the contentStore to store the messages
     * @throws RemoteException remote simulator error
     * @throws NamingException
     */
    public Client(final String name, final DEVSSimulatorInterface.TimeDoubleUnit simulator, final Point3d position,
            final Bank bank, final Money initialBankAccount, final Product product, final Retailer retailer,
            final ContentStoreInterface contentStore) throws RemoteException, NamingException
    {
        super(name, simulator, position, bank, initialBankAccount, contentStore);
        this.product = product;
        this.retailer = retailer;
        this.init();
        // Let's give Client its corresponding image
        if (simulator instanceof AnimatorInterface)
        {
            new SingleImageRenderable(this, simulator,
                    Factory.class.getResource("/nl/tudelft/simulation/supplychain/images/Market.gif"));
        }
    }

    /**
     * @throws RemoteException remote simulator error
     */
    public void init() throws RemoteException
    {
        StreamInterface stream = this.simulator.getReplication().getStream("default");
        Duration hour = new Duration(1.0, DurationUnit.HOUR);
        //
        // give the actor a fax device which is checked every hour
        FaxDevice fax = new FaxDevice("ClientFax", this.simulator);
        super.addSendingDevice(fax);
        MessageHandlerInterface secretary = new HandleAllMessages(this);
        super.addReceivingDevice(fax, secretary, new DistConstantDurationUnit(hour));
        //
        // create the internal demand for PCs
        Demand demand =
                new Demand(this.product, new DistContinuousDurationUnit(new DistExponential(stream, 24.0), DurationUnit.HOUR),
                        new DistConstant(stream, 1.0), new DistConstantDurationUnit(Duration.ZERO),
                        new DistConstantDurationUnit(new Duration(14.0, DurationUnit.DAY)));
        DemandGeneration dg = new DemandGeneration(this, super.simulator,
                new DistContinuousDurationUnit(new DistExponential(stream, 2.0), DurationUnit.MINUTE));
        dg.addDemandGenerator(this.product, demand);
        super.setDemandGeneration(dg);
        //
        // tell Client to use the InternalDemandHandler
        InternalDemandHandlerRFQ internalDemandHandler =
                new InternalDemandHandlerRFQ(this, new Duration(24.0, DurationUnit.HOUR), null); // XXX: Why does it need stock?
        internalDemandHandler.addSupplier(this.product, this.retailer);
        //
        // tell Client to use the Quotehandler to handle quotes
        QuoteHandler quoteHandler = new QuoteHandlerAll(this, QuoteComparatorEnum.SORT_PRICE_DATE_DISTANCE,
                new DistConstantDurationUnit(new Duration(2.0, DurationUnit.HOUR)), 0.4, 0.1);
        //
        // Client has the standard order confirmation handler
        OrderConfirmationHandler confirmationHandler = new OrderConfirmationHandler(this);
        //
        // Client will get a bill in the end
        BillHandler billHandler = new BillHandler(this, super.bankAccount, PaymentPolicyEnum.PAYMENT_IMMEDIATE,
                new DistConstantDurationUnit(Duration.ZERO));
        //
        // hopefully, Client will get laptop shipments
        ShipmentHandler shipmentHandler = new ShipmentHandlerConsume(this);
        //
        // add the handlers to the buying role for Client
        BuyingRole buyingRole = new BuyingRole(this, super.simulator, internalDemandHandler, quoteHandler, confirmationHandler,
                shipmentHandler, billHandler);
        super.setBuyingRole(buyingRole);

        //
        // CHARTS
        //
        if (this.simulator instanceof AnimatorInterface)
        {
            XYChart bankChart = new XYChart("BankAccount " + this.name);
            bankChart.add("bank account", this.bankAccount, BankAccount.BANK_ACCOUNT_CHANGED_EVENT);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Bounds getBounds()
    {
        return new BoundingBox(25.0, 25.0, 1.0);
    }
}
