package nl.tudelft.simulation.supplychain.message.store.trade;

import java.util.List;

import nl.tudelft.simulation.supplychain.message.MessageType;
import nl.tudelft.simulation.supplychain.message.store.MessageStoreInterface;
import nl.tudelft.simulation.supplychain.message.trade.TradeMessage;

/**
 * A TradeMessageStore is taking care of storing trade messages for later use, for instance for matching purposes. It acts as an
 * ERP or database system for the supply chain actor. In this implementation, all the messages are linked to an InternalDemand,
 * as this sets off the whole chain of messages, no matter whether it is a purchase, internal production, or stock
 * replenishment: in all cases the InternalDemand triggers all the other messages.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface TradeMessageStoreInterface extends MessageStoreInterface
{
    /**
     * Method removeAllMessages removes an exisiting Message object from the store. No error message is given when the message
     * was not there; this is just ignored.
     * @param internalDemandId long; the identifier of the internal demand
     */
    void removeAllMessages(long internalDemandId);

    /**
     * Method getMessageList returns a list of Message objects of type 'type' based on the internalDemandId.
     * @param internalDemandId the identifier of the message
     * @param type the message type to look for
     * @return returns a list of message of type 'type' based on the internalDemandId
     */
    List<TradeMessage> getMessageList(long internalDemandId, MessageType type);

    /**
     * Method getMessageList returns the Message object of type 'type' based on the internalDemandId, for either sent or
     * received items.
     * @param internalDemandId the identifier of the message
     * @param type the message type to look for
     * @param sent indicates whether the message was sent or received
     * @return returns a list of message of type 'type' based on the internalDemandId
     */
    List<TradeMessage> getMessageList(long internalDemandId, MessageType type, boolean sent);

}
