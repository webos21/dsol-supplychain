package nl.tudelft.simulation.supplychain.test;

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
import nl.tudelft.simulation.dsol.swing.charts.xy.XYChart;
import nl.tudelft.simulation.jstats.distributions.DistExponential;
import nl.tudelft.simulation.jstats.distributions.DistNormal;
import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.actor.ActorAlreadyDefinedException;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;
import nl.tudelft.simulation.supplychain.finance.BankAccount;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.inventory.Inventory;
import nl.tudelft.simulation.supplychain.message.store.trade.TradeMessageStoreInterface;
import nl.tudelft.simulation.supplychain.policy.bill.BillPolicy;
import nl.tudelft.simulation.supplychain.policy.internaldemand.InternalDemandPolicyYP;
import nl.tudelft.simulation.supplychain.policy.order.OrderPolicy;
import nl.tudelft.simulation.supplychain.policy.order.OrderPolicyStock;
import nl.tudelft.simulation.supplychain.policy.orderconfirmation.OrderConfirmationPolicy;
import nl.tudelft.simulation.supplychain.policy.payment.PaymentPolicy;
import nl.tudelft.simulation.supplychain.policy.payment.PaymentPolicyEnum;
import nl.tudelft.simulation.supplychain.policy.quote.QuoteComparatorEnum;
import nl.tudelft.simulation.supplychain.policy.quote.QuotePolicy;
import nl.tudelft.simulation.supplychain.policy.quote.QuotePolicyAll;
import nl.tudelft.simulation.supplychain.policy.rfq.RequestForQuotePolicy;
import nl.tudelft.simulation.supplychain.policy.shipment.ShipmentPolicy;
import nl.tudelft.simulation.supplychain.policy.shipment.ShipmentPolicyStock;
import nl.tudelft.simulation.supplychain.policy.yellowpage.YellowPageAnswerPolicy;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.reference.Retailer;
import nl.tudelft.simulation.supplychain.reference.Supplier;
import nl.tudelft.simulation.supplychain.role.buying.BuyingActor;
import nl.tudelft.simulation.supplychain.role.buying.BuyingRoleYP;
import nl.tudelft.simulation.supplychain.role.inventory.RestockingServiceSafety;
import nl.tudelft.simulation.supplychain.role.selling.SellingActor;
import nl.tudelft.simulation.supplychain.role.selling.SellingRole;
import nl.tudelft.simulation.supplychain.role.selling.SellingRoleRFQ;
import nl.tudelft.simulation.supplychain.transport.TransportChoiceProvider;
import nl.tudelft.simulation.supplychain.transport.TransportOptionProvider;
import nl.tudelft.simulation.supplychain.util.DistConstantDuration;

/**
 * Retailer.
 * <p>
 * Copyright (c) 2003-2023 Delft University of Technology, Delft, the
 * Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * 
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class PCShop extends Retailer {
	/** the serial version uid. */
	private static final long serialVersionUID = 20221201L;

	/** the manufacturer where the PCShop buys. */
	private Supplier manufacturer;

	private BankAccount bankAccount;

	/**
	 * @param id                  String, the unique id of the supplier
	 * @param name                String; the longer name of the supplier
	 * @param model               SupplyChainModelInterface; the model
	 * @param location            OrientedPoint2d; the location of the actor
	 * @param locationDescription String; the location description of the actor
	 *                            (e.g., a city, country)
	 * @param bank                Bank; the bank for the BankAccount
	 * @param initialBalance      Money; the initial balance for the actor
	 * @param messageStore        TradeMessageStoreInterface; the message store for
	 *                            messages
	 * @param product             initial stock product
	 * @param amount              amount of initial stock
	 * @param manufacturer        fixed manufacturer to use
	 * @throws ActorAlreadyDefinedException when the actor was already registered in
	 *                                      the model
	 * @throws NamingException              on animation error
	 * @throws RemoteException              on animation error
	 */
	@SuppressWarnings("checkstyle:parameternumber")
	public PCShop(final String id, final String name, final SupplyChainModelInterface model,
			final OrientedPoint2d location, final String locationDescription, final BankAccount bank,
			final Money initialBalance, final TradeMessageStoreInterface messageStore, final Product product,
			final double amount, final Supplier manufacturer)
			throws ActorAlreadyDefinedException, RemoteException, NamingException {
		super(id, name, model, location, locationDescription, bank, initialBalance, messageStore);
		this.manufacturer = manufacturer;
		this.bankAccount = bank;
		// give the retailer some stock
		getInventory().addToInventory(product, amount, product.getUnitMarketPrice().multiplyBy(amount));
		init();
		if (getSimulator() instanceof AnimatorInterface) {
			new SingleImageRenderable<>(this, getSimulator(),
					Factory.class.getResource("/nl/tudelft/simulation/supplychain/images/Retailer.gif"));
		}
	}

	/**
	 * @throws RemoteException remote simulator error
	 */
	public void init() throws RemoteException {
		// tell PCshop to use the RFQPolicy to handle RFQs
		RequestForQuotePolicy rfqPolicy = new RequestForQuotePolicy(this.getInventoryRole(), getInventory(), 1.2,
				new DistConstantDuration(new Duration(1.23, DurationUnit.HOUR)), new Duration(1.23, DurationUnit.HOUR));
		//
		// create an order Policy
		OrderPolicy orderPolicy = new OrderPolicyStock(this.getInventoryRole(), getInventory());
		//
		// hopefully, the PCShop will get payments in the end
		PaymentPolicy paymentPolicy = new PaymentPolicy(this.getBuyingRole(), getBankAccount());
		//
		// add the Policys to the buying role for PCShop
		SellingRole sellingRole = new SellingRoleRFQ((SellingActor) this.getSellingRole().getActor(), rfqPolicy,
				orderPolicy, paymentPolicy);
		super.setSellingRole(sellingRole);
		//
		// After a while, the PC Shop needs to restock and order
		// do this for every product we have initially in stock
		for (Product product : getInventory().getProducts()) {
			new RestockingServiceSafety(getInventory(), product,
					new DistContinuousDuration(new DistNormal(getModel().getDefaultStream(), 0.1, 1.0)), false, 5.0,
					true, 10.0, new Duration(14.0, DurationUnit.DAY));
			// order 100 PCs when actual+claimed < 100
			// policy will schedule itself
		}

		//
		// BUY PRODUCTS WHEN THERE IS INTERNAL DEMAND
		//

		// tell PCShop to use the InternalDemandPolicy for all products
		InternalDemandPolicyYP internalDemandPolicy = new InternalDemandPolicyYP(this.getBuyingRole(),
				new DistContinuousDuration(new DistExponential(getModel().getDefaultStream(), 24.0), DurationUnit.HOUR),
				this.getBuyingRole().getActor(), new Length(2000, LengthUnit.SI), 2, getInventory());
//		for (Product product : getInventory().getProducts()) {
//			internalDemandPolicy.addSupplier(product, this.manufacturer);
//		}
		//
		// tell PCShop to use the QuotePolicy to handle quotes
		QuotePolicy quotePolicy = new QuotePolicyAll(this.getBuyingRole(), QuoteComparatorEnum.SORT_DATE_PRICE_DISTANCE,
				new DistContinuousDuration(new DistNormal(getModel().getDefaultStream(), 0.1, 1.0)), 0.4, 0.1);
		//
		// PCShop has the standard order confirmation Policy
		OrderConfirmationPolicy confirmationPolicy = new OrderConfirmationPolicy(this.getBuyingRole());
		//
		// PCShop will get a bill in the end
		BillPolicy billPolicy = new BillPolicy(this.getBuyingRole(), getBankAccount(),
				PaymentPolicyEnum.PAYMENT_IMMEDIATE, new DistConstantDuration(Duration.ZERO));
		//
		// hopefully, PCShop will get laptop shipments, put them in stock
		ShipmentPolicy shipmentPolicy = new ShipmentPolicyStock(this.getInventoryRole(), getInventory());
		//
		// add the Policys to the buying role for PCShop
		TransportOptionProvider top = new PCShopTransportOptionProvider();
		TransportChoiceProvider tcp = new PCShopTransportChoiceProvider();
		YellowPageAnswerPolicy ypAnswerPolicy = new YellowPageAnswerPolicy(this.getBuyingRole(), top, tcp,
				new DistContinuousDuration(new DistExponential(getModel().getDefaultStream(), 24.0), DurationUnit.HOUR),
				new Duration(1.0, DurationUnit.HOUR));
		BuyingRoleYP buyingRole = new BuyingRoleYP((BuyingActor) this.getBuyingRole().getActor(), internalDemandPolicy,
				ypAnswerPolicy, quotePolicy, confirmationPolicy, shipmentPolicy, billPolicy);
		super.setBuyingRole(buyingRole);
		//
		// CHARTS
		//
		if (getSimulator() instanceof AnimatorInterface) {
			XYChart bankChart = new XYChart(getSimulator(), "BankAccount " + getName());
			bankChart.add("bank account", getBankAccount(), BankAccount.BANK_ACCOUNT_CHANGED_EVENT);
		}
	}

	/** {@inheritDoc} */
	@Override
	public Bounds3d getBounds() {
		return new Bounds3d(25.0, 25.0, 1.0);
	}

	public Inventory getInventory() {
		return getInventoryRole().getInventory();
	}

	public BankAccount getBankAccount() {
		return this.bankAccount;
	}
}
