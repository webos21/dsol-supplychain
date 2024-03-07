package nl.tudelft.simulation.supplychain.demo.reference;

import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djunits.unit.DurationUnit;
import org.djunits.unit.LengthUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Length;
import org.djutils.draw.bounds.Bounds3d;
import org.djutils.draw.point.OrientedPoint2d;

import nl.tudelft.simulation.dsol.animation.d2.SingleImageRenderable;
import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.jstats.distributions.DistConstant;
import nl.tudelft.simulation.jstats.distributions.DistExponential;
import nl.tudelft.simulation.jstats.distributions.DistTriangular;
import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.supplychain.actor.messaging.devices.reference.WebApplication;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;
import nl.tudelft.simulation.supplychain.finance.Bank;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.message.receiver.MessageReceiver;
import nl.tudelft.simulation.supplychain.message.store.trade.LeanTradeMessageStore;
import nl.tudelft.simulation.supplychain.messagehandlers.HandleAllMessages;
import nl.tudelft.simulation.supplychain.policy.bill.BillPolicy;
import nl.tudelft.simulation.supplychain.policy.internaldemand.InternalDemandPolicyYP;
import nl.tudelft.simulation.supplychain.policy.orderconfirmation.OrderConfirmationPolicy;
import nl.tudelft.simulation.supplychain.policy.payment.PaymentPolicyEnum;
import nl.tudelft.simulation.supplychain.policy.quote.QuoteComparatorEnum;
import nl.tudelft.simulation.supplychain.policy.quote.QuotePolicy;
import nl.tudelft.simulation.supplychain.policy.quote.QuotePolicyAll;
import nl.tudelft.simulation.supplychain.policy.shipment.ShipmentPolicy;
import nl.tudelft.simulation.supplychain.policy.shipment.ShipmentPolicyConsume;
import nl.tudelft.simulation.supplychain.policy.yellowpage.YellowPageAnswerPolicy;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.reference.Customer;
import nl.tudelft.simulation.supplychain.reference.YellowPage;
import nl.tudelft.simulation.supplychain.role.buying.BuyingRoleYP;
import nl.tudelft.simulation.supplychain.role.demand.Demand;
import nl.tudelft.simulation.supplychain.role.demand.DemandGenerationRole;
import nl.tudelft.simulation.supplychain.util.DistConstantDuration;

/**
 * MtsMtomarket.java. <br>
 * <br>
 * Copyright (c) 2003-2023 Delft University of Technology, Delft, the
 * Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * 
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DemoMarket extends Customer {
	/** */
	private static final long serialVersionUID = 20221201L;

	private Bank bank;
	private Money initialBankAccount;
	private Product product;
	private YellowPage ypCustomre;

	/**
	 * @param name
	 * @param simulator
	 * @param position
	 * @param bank
	 * @param initialBankAccount
	 * @param product
	 * @param ypCustomre
	 * @param stream
	 */
	public DemoMarket(String name, SupplyChainModelInterface model, OrientedPoint2d location,
			String locationDescription, Bank bank, Money initialBankAccount, Product product, YellowPage ypCustomre,
			StreamInterface stream) {
		super(name, name, model, location, locationDescription, new LeanTradeMessageStore(model.getSimulator()));
		this.bank = bank;
		this.initialBankAccount = initialBankAccount;
		this.product = product;
		this.ypCustomre = ypCustomre;

		// COMMUNICATION

		WebApplication www = new WebApplication("Web-" + name, this.getSimulator());
		super.addSendingDevice(www);
		MessageReceiver webSystem = new HandleAllMessages(this);
		super.addReceivingDevice(www, webSystem, new DistConstantDuration(new Duration(10.0, DurationUnit.SECOND)));

		// DEMAND GENERATION

		Demand demand = new Demand(product,
				new DistContinuousDuration(new DistExponential(stream, 8.0), DurationUnit.HOUR),
				new DistConstant(stream, 1.0), new DistConstantDuration(Duration.ZERO),
				new DistConstantDuration(new Duration(14.0, DurationUnit.DAY)));
		DemandGenerationRole dg = new DemandGenerationRole(this,
				new DistContinuousDuration(new DistExponential(stream, 2.0), DurationUnit.MINUTE));
		dg.addDemandGenerator(product, demand);
		this.setDemandGenerationRole(dg);

		// MESSAGE HANDLING

		DistContinuousDuration administrativeDelayInternalDemand = new DistContinuousDuration(
				new DistTriangular(stream, 2, 2.5, 3), DurationUnit.HOUR);
		InternalDemandPolicyYP internalDemandHandler = new InternalDemandPolicyYP(this,
				administrativeDelayInternalDemand, ypCustomre, new Length(1E6, LengthUnit.METER), 1000, null);

		DistContinuousDuration administrativeDelayYellowPageAnswer = new DistContinuousDuration(
				new DistTriangular(stream, 2, 2.5, 3), DurationUnit.HOUR);
		YellowPageAnswerPolicy ypAnswerHandler = new YellowPageAnswerPolicy(this, administrativeDelayYellowPageAnswer);

		DistContinuousDuration administrativeDelayQuote = new DistContinuousDuration(
				new DistTriangular(stream, 2, 2.5, 3), DurationUnit.HOUR);
		QuotePolicy quoteHandler = new QuotePolicyAll(this, QuoteComparatorEnum.SORT_PRICE_DATE_DISTANCE,
				administrativeDelayQuote, 0.5, 0);

		OrderConfirmationPolicy orderConfirmationHandler = new OrderConfirmationPolicy(this);

		ShipmentPolicy shipmentHandler = new ShipmentPolicyConsume(this);

		DistContinuousDuration paymentDelay = new DistContinuousDuration(new DistConstant(stream, 0.0),
				DurationUnit.HOUR);
		BillPolicy billHandler = new BillPolicy(this, this.getBankAccount(), PaymentPolicyEnum.PAYMENT_ON_TIME,
				paymentDelay);

		BuyingRoleYP buyingRole = new BuyingRoleYP(this, model.getSimulator(), internalDemandHandler, ypAnswerHandler,
				quoteHandler, orderConfirmationHandler, shipmentHandler, billHandler);
		this.setBuyingRole(buyingRole);

		// ANIMATION

		if (model.getSimulator() instanceof AnimatorInterface) {
			try {
				new SingleImageRenderable<>(this, model.getSimulator(),
						DemoMarket.class.getResource("/nl/tudelft/simulation/supplychain/images/Market.gif"));
			} catch (RemoteException | NamingException exception) {
				exception.printStackTrace();
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public Bounds3d getBounds() {
		return new Bounds3d(25.0, 25.0, 1.0);
	}

}
