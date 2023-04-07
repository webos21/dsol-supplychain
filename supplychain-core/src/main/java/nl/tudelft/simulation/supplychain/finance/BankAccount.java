package nl.tudelft.simulation.supplychain.finance;

import java.io.Serializable;

import org.djunits.Throw;
import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.event.EventProducer;
import org.djutils.event.TimedEventType;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;

/**
 * The BackAccount keeps track of the balance of a SupplyChainActor. This simple implementation just has one number as the
 * account. No investments or loans are possible through this implementation. The BankAccount itself does not contain logic to
 * prevent it from going negative.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class BankAccount extends EventProducer
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** the owner of the bank account. */
    private SupplyChainActor owner;

    /** the bank. */
    private Bank bank;

    /** the balance of the actor. */
    private Money balance;

    /** for who is interested, the BankAccount can send updates of changes. */
    public static final TimedEventType BANK_ACCOUNT_CHANGED_EVENT = new TimedEventType("BANK_ACCOUNT_CHANGED_EVENT",
            new MetaData("account", "bank account", new ObjectDescriptor("balance", "bank balance", double.class)));

    /**
     * Constructor for BankAccount.
     * @param owner the owner of the bank account
     * @param bank the bank where this account is located
     * @param initialBalance the opening balance
     */
    public BankAccount(final SupplyChainActor owner, final Bank bank, final Money initialBalance)
    {
        Throw.whenNull(owner, "owner cannot be null");
        Throw.whenNull(bank, "bank cannot be null");
        Throw.whenNull(initialBalance, "initialBalance cannot be null");
        this.owner = owner;
        this.bank = bank;
        this.balance = initialBalance;
        this.roundBalance();
        sendBalanceUpdateEvent();
        // start the interest process...
        this.owner.getSimulator().scheduleEventNow(this, this, "interest", null);
    }

    /**
     * Return the bank balance.
     * @return Money; the bank balance
     */
    public Money getBalance()
    {
        return this.balance;
    }

    /**
     * Add money to the bank balance.
     * @param amount Money; the amount of money to add
     */
    public synchronized void addToBalance(final Money amount)
    {
        this.balance = this.balance.plus(amount);
        this.roundBalance();
        sendBalanceUpdateEvent();
    }

    /**
     * Withdraw money from the bank balance.
     * @param amount Money; the amount of money to withdraw
     */
    public synchronized void withdrawFromBalance(final Money amount)
    {
        this.balance = this.balance.minus(amount);
        this.roundBalance();
        sendBalanceUpdateEvent();
    }

    /**
     * Send a BANK_ACCOUNT_CHANGED_EVENT to signal an update of the bank balance.
     */
    protected void sendBalanceUpdateEvent()
    {
        this.fireTimedEvent(BANK_ACCOUNT_CHANGED_EVENT, this.balance, this.owner.getSimulatorTime());
    }

    /**
     * Round the balance.
     */
    protected void roundBalance()
    {
        this.balance = new Money(0.01 * Math.round(100.0 * this.balance.getAmount()), this.balance.getMoneyUnit());
    }

    /**
     * receive or pay interest according to the current rates.
     */
    protected void interest()
    {
        if (this.balance.getAmount() < 0)
        {
            addToBalance(this.balance.multiplyBy(this.bank.getAnnualInterestRateNeg() / 365.0));
        }
        else
        {
            addToBalance(this.balance.multiplyBy(this.bank.getAnnualInterestRatePos() / 365.0));
        }
        this.roundBalance();
        this.owner.getSimulator().scheduleEventRel(new Duration(1.0, DurationUnit.DAY), this, this, "interest", null);
    }

    /** {@inheritDoc} */
    @Override
    public Serializable getSourceId()
    {
        return this.owner.getName() + ".BankAccount";
    }

}
