package nl.tudelft.simulation.supplychain.finance;

import java.io.Serializable;

import org.djutils.draw.point.OrientedPoint2d;
import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.supplychain.actor.ActorAlreadyDefinedException;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;
import nl.tudelft.simulation.supplychain.message.store.trade.EmptyTradeMessageStore;
import nl.tudelft.simulation.supplychain.message.store.trade.TradeMessageStoreInterface;
import nl.tudelft.simulation.supplychain.role.banking.BankingActor;
import nl.tudelft.simulation.supplychain.role.banking.BankingRole;
import nl.tudelft.simulation.supplychain.role.financing.FinancingActor;
import nl.tudelft.simulation.supplychain.role.financing.FinancingRole;

/**
 * <p>
 * XXX : It must be removed later
 * </p>
 * The Bank to store the interest rates for the Bank accounts. In this case, we
 * have chosen to not make the Bank work with Messages, but this is of course
 * possible ti implement, e.g. to simulate risks of banks handling international
 * transactions slowly, or to simulate cyber attacks on the financial
 * infrastructure.
 * <p>
 * Copyright (c) 2003-2023 Delft University of Technology, Delft, the
 * Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * 
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Bank extends SupplyChainActor implements FinancingActor, BankingActor, Serializable {
	/** the serial version uid. */
	private static final long serialVersionUID = 20221127L;

	/** the interest rate for a positive bank account. */
	private double annualInterestRatePos = 0.025;

	/** the interest rate for a negative bank account. */
	private double annualInterestRateNeg = 0.08;

	/** the bank account of the actor. */
	private final BankAccount bankAccount;

	/** the banking role for a bank actor. */
	private BankingRole bankingRole;

	/** the banking role for a bank actor. */
	private FinancingRole financingRole;

	/**
	 * Create a new Bank.
	 * 
	 * @param name                String; the name of the bank
	 * @param messageHandler      MessageHandlerInterface; the handler for messages
	 * @param simulator           SupplyChainSimulatorInterface; the simulator
	 * @param location            OrientedPoint3d; the location on the map
	 * @param locationDescription String; a description of the location (e.g.,
	 *                            "Frankfurt")
	 */
	public Bank(final String id, final String name, final SupplyChainModelInterface model,
			final OrientedPoint2d location, final String locationDescription,
			final TradeMessageStoreInterface messageStore) throws ActorAlreadyDefinedException {
		super(id, name, model, location, locationDescription, new EmptyTradeMessageStore());
		this.bankAccount = new BankAccount(this, this, new Money(0.0, MoneyUnit.USD));
	}

	/**
	 * Return the negative annual interest rate.
	 * 
	 * @return double; negative annual interest rate
	 */
	public double getAnnualInterestRateNeg() {
		return this.annualInterestRateNeg;
	}

	/**
	 * Set a new negative annual interest rate.
	 * 
	 * @param annualInterestRateNeg double; new negative annual interest rate
	 */
	public void setAnnualInterestRateNeg(final double annualInterestRateNeg) {
		this.annualInterestRateNeg = annualInterestRateNeg;
	}

	/**
	 * Return the positive annual interest rate.
	 * 
	 * @return double; positive annual interest rate
	 */
	public double getAnnualInterestRatePos() {
		return this.annualInterestRatePos;
	}

	/**
	 * Set a new positive annual interest rate.
	 * 
	 * @param annualInterestRatePos double; new positive annual interest rate
	 */
	public void setAnnualInterestRatePos(final double annualInterestRatePos) {
		this.annualInterestRatePos = annualInterestRatePos;
	}

	/** {@inheritDoc} */
	@Override
	public BankAccount getBankAccount() {
		return this.bankAccount;
	}

	@Override
	public void checkNecessaryRoles() {
		// TODO Auto-generated method stub

	}

	@Override
	public BankingRole getBankingRole() {
		return this.bankingRole;
	}

	@Override
	public void setBankingRole(BankingRole bankingRole) {
		Throw.whenNull(bankingRole, "bankingRole cannot be null");
		Throw.when(this.bankingRole != null, IllegalStateException.class, "bankingRole already initialized");
		addRole(bankingRole);
		this.bankingRole = bankingRole;
	}

	@Override
	public FinancingRole getFinancingRole() {
		return this.getFinancingRole();
	}

	@Override
	public void setFinancingRole(FinancingRole financingRole) {
		Throw.whenNull(financingRole, "financingRole cannot be null");
		Throw.when(this.financingRole != null, IllegalStateException.class, "financingRole already initialized");
		addRole(financingRole);
		this.financingRole = financingRole;

	}
}