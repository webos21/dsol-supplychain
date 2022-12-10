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
import nl.tudelft.simulation.jstats.distributions.DistConstant;
import nl.tudelft.simulation.jstats.distributions.DistExponential;
import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.supplychain.actor.messaging.devices.reference.FaxDevice;
import nl.tudelft.simulation.supplychain.actor.unit.dist.DistConstantDuration;
import nl.tudelft.simulation.supplychain.dsol.SCSimulatorInterface;
import nl.tudelft.simulation.supplychain.finance.Bank;
import nl.tudelft.simulation.supplychain.finance.BankAccount;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.message.handler.MessageHandlerInterface;
import nl.tudelft.simulation.supplychain.message.store.MessageStoreInterface;
import nl.tudelft.simulation.supplychain.messagehandlers.HandleAllMessages;
import nl.tudelft.simulation.supplychain.policy.bill.BillPolicy;
import nl.tudelft.simulation.supplychain.policy.internaldemand.InternalDemandPolicyRFQ;
import nl.tudelft.simulation.supplychain.policy.orderconfirmation.OrderConfirmationPolicy;
import nl.tudelft.simulation.supplychain.policy.payment.PaymentPolicyEnum;
import nl.tudelft.simulation.supplychain.policy.quote.QuoteComparatorEnum;
import nl.tudelft.simulation.supplychain.policy.quote.AbstractQuotePolicy;
import nl.tudelft.simulation.supplychain.policy.quote.QuotePolicyAll;
import nl.tudelft.simulation.supplychain.policy.shipment.AbstractShipmentPolicy;
import nl.tudelft.simulation.supplychain.policy.shipment.ShipmentPolicyConsume;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.reference.Customer;
import nl.tudelft.simulation.supplychain.reference.Retailer;
import nl.tudelft.simulation.supplychain.role.buying.BuyingRoleYP;
import nl.tudelft.simulation.supplychain.role.demand.Demand;
import nl.tudelft.simulation.supplychain.role.demand.DemandGenerationRolePeriodic;

/**
 * Customer. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Client extends Customer
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

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
     * @param messageStore the messageStore to store the messages
     * @throws RemoteException remote simulator error
     * @throws NamingException
     */
    public Client(final String name, final SCSimulatorInterface simulator, final OrientedPoint3d position, final Bank bank,
            final Money initialBankAccount, final Product product, final Retailer retailer,
            final MessageStoreInterface messageStore) throws RemoteException, NamingException
    {
        super(name, simulator, position, bank, initialBankAccount, messageStore);
        this.product = product;
        this.retailer = retailer;
        this.init();
        // Let's give Client its corresponding image
        if (simulator instanceof AnimatorInterface)
        {
            new SingleImageRenderable<>(this, simulator,
                    Factory.class.getResource("/nl/tudelft/simulation/supplychain/images/Market.gif"));
        }
    }

    /**
     * @throws RemoteException remote simulator error
     */
    public void init() throws RemoteException
    {
        StreamInterface stream = this.simulator.getModel().getStream("default");
        Duration hour = new Duration(1.0, DurationUnit.HOUR);
        //
        // give the actor a fax device which is checked every hour
        FaxDevice fax = new FaxDevice("ClientFax", this.simulator);
        super.addSendingDevice(fax);
        MessageHandlerInterface secretary = new HandleAllMessages(this);
        super.addReceivingDevice(fax, secretary, new DistConstantDuration(hour));
        //
        // create the internal demand for PCs
        Demand demand = new Demand(this.product,
                new DistContinuousDuration(new DistExponential(stream, 24.0), DurationUnit.HOUR), new DistConstant(stream, 1.0),
                new DistConstantDuration(Duration.ZERO), new DistConstantDuration(new Duration(14.0, DurationUnit.DAY)));
        DemandGenerationRolePeriodic dg = new DemandGenerationRolePeriodic(this,
                new DistContinuousDuration(new DistExponential(stream, 2.0), DurationUnit.MINUTE));
        dg.addDemandGenerator(this.product, demand);
        super.setDemandGeneration(dg);
        //
        // tell Client to use the InternalDemandPolicy
        InternalDemandPolicyRFQ internalDemandHandler =
                new InternalDemandPolicyRFQ(this, new Duration(24.0, DurationUnit.HOUR), null); // XXX: Why does it need stock?
        internalDemandHandler.addSupplier(this.product, this.retailer);
        //
        // tell Client to use the Quotehandler to handle quotes
        AbstractQuotePolicy quoteHandler = new QuotePolicyAll(this, QuoteComparatorEnum.SORT_PRICE_DATE_DISTANCE,
                new DistConstantDuration(new Duration(2.0, DurationUnit.HOUR)), 0.4, 0.1);
        //
        // Client has the standard order confirmation handler
        OrderConfirmationPolicy confirmationHandler = new OrderConfirmationPolicy(this);
        //
        // Client will get a bill in the end
        BillPolicy billHandler = new BillPolicy(this, super.bankAccount, PaymentPolicyEnum.PAYMENT_IMMEDIATE,
                new DistConstantDuration(Duration.ZERO));
        //
        // hopefully, Client will get laptop shipments
        AbstractShipmentPolicy shipmentHandler = new ShipmentPolicyConsume(this);
        //
        // add the handlers to the buying role for Client
        BuyingRoleYP buyingRole = new BuyingRoleYP(this, super.simulator, internalDemandHandler, quoteHandler,
                confirmationHandler, shipmentHandler, billHandler);
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
