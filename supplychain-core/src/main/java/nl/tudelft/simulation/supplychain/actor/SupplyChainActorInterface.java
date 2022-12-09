package nl.tudelft.simulation.supplychain.actor;

import java.util.List;

import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.supplychain.finance.BankAccount;
import nl.tudelft.simulation.supplychain.finance.FixedCost;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.message.store.trade.TradeMessageStoreInterface;

/**
 * SupplyChainActorInterface defines the defining methods of the SupplyChainActor. The SupplyChainActor extends the Actor, and
 * adds a bank account, cost items, and a Store for the sent and received Messages.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface SupplyChainActorInterface extends ActorInterface
{
    /**
     * Add a fixed cost item for this actor.
     * @param description String; the description of the fixed cost item
     * @param interval Duration; the interval at which the amount will be deduced from the bank account
     * @param amount Money; the amount to deduce at each interval
     */
    void addFixedCost(String description, Duration interval, Money amount);

    /**
     * Return a list of the fixed cost items for this SupplyChainActor.
     * @return List&lt;FixedCosts&gt;; a list of fixed costs items for this SupplyChainActor.
     */
    List<FixedCost> getFixedCosts();

    /**
     * Return the MessageStore for the SupplyChainActor.
     * @return TradeMessageStoreInterface; the messageStore.
     */
    TradeMessageStoreInterface getMessageStore();

    /**
     * Return the bank account of the SupplyChainActor.
     * @return BankAccount; the bankAccount of the SupplyChainActor.
     */
    BankAccount getBankAccount();

}
