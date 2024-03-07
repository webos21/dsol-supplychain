package nl.tudelft.simulation.supplychain.reference;

import java.io.Serializable;

import org.djutils.draw.point.OrientedPoint2d;
import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.supplychain.actor.ActorAlreadyDefinedException;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;
import nl.tudelft.simulation.supplychain.message.store.trade.TradeMessageStoreInterface;
import nl.tudelft.simulation.supplychain.role.banking.BankingActor;
import nl.tudelft.simulation.supplychain.role.banking.BankingRole;

/**
 * <p>
 * XXX : It must be removed later
 * </p>
 * The BankActor to store the interest rates for the Bank accounts. In this
 * case, we have chosen to not make the Bank work with Messages, but this is of
 * course possible ti implement, e.g. to simulate risks of banks handling
 * international transactions slowly, or to simulate cyber attacks on the
 * financial infrastructure.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the
 * Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * 
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class BankActor extends SupplyChainActor implements BankingActor, Serializable {

	/** the serial version uid. */
	private static final long serialVersionUID = 20240306L;

	/** the interest rate for a positive bank account. */
	private double annualInterestRatePos = 0.025;

	/** the interest rate for a negative bank account. */
	private double annualInterestRateNeg = 0.08;

	/** the banking role for a bank actor. */
	private BankingRole bankingRole;

	/**
	 * Create a new BankActor.
	 * 
	 * @param id                  String; the name of the bank
	 * @param name                String; the name of the bank
	 * @param model               SupplyChainModelInterface; the handler for
	 *                            messages
	 * @param location            OrientedPoint3d; the location on the map
	 * @param locationDescription String; a description of the location (e.g.,
	 *                            "Frankfurt")
	 * @param messageStore        TradeMessageStoreInterface; the message store for
	 *                            messages
	 */
	public BankActor(final String id, final String name, final SupplyChainModelInterface model,
			final OrientedPoint2d location, final String locationDescription,
			final TradeMessageStoreInterface messageStore) throws ActorAlreadyDefinedException {
		super(id, name, model, location, locationDescription, messageStore);
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
	public void checkNecessaryRoles() {
		// TODO Auto-generated method stub

	}
}