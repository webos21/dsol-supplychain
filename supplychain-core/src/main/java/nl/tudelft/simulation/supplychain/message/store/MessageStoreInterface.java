package nl.tudelft.simulation.supplychain.message.store;

import java.io.Serializable;
import java.util.List;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.message.Message;
import nl.tudelft.simulation.supplychain.message.MessageType;

/**
 * A MessageStore is taking care of storing messages for later use, for instance for matching purposes. It acts as an ERP or
 * database system for the supply chain actor. In this implementation, all the messages are linked to an InternalDemand, as this
 * sets off the whole chain of messages, no matter whether it is a purchase, internal production, or stock replenishment: in all
 * cases the InternalDemand triggers all the other messages.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface MessageStoreInterface extends Serializable
{
    /**
     * Set the owner for the message store after is has been created. The reason for explicitly having to set the owner and not
     * include the owner in the constructor is that the SupplyChainActor needs a MessageStore in its constructor, so the
     * MessageStore cannot be constructed with the owner.
     * @param owner the owner
     */
    void setOwner(SupplyChainActor owner);

    /**
     * Method addMessage stores a new message object into the store.
     * @param message the message to add
     * @param sent sent or not
     */
    void addMessage(Message message, boolean sent);

    /**
     * Method removeMessage removes a Message object from the store.
     * @param message the message to remove
     * @param sent indicates whether the message was sent or received
     */
    void removeMessage(Message message, boolean sent);

    /**
     * Method removeSentReceivedMessage removes a Message object from the sent / received store.
     * @param message the message to remove
     * @param sent indicates whether the message was sent or received
     */
    void removeSentReceivedMessage(Message message, boolean sent);

    /**
     * Method removeAllMessage removes an exisiting Message object from the store. No error message is given when the message
     * was not there; this is just ignored.
     * @param internalDemandId long; the identifier of the internal demand
     */
    void removeAllMessage(long internalDemandId);

    /**
     * Method getMessageList returns a list of Message objects of type 'type' based on the internalDemandId.
     * @param internalDemandId the identifier of the message
     * @param type the message type to look for
     * @return returns a list of message of type 'type' based on the internalDemandId
     */
    List<Message> getMessageList(long internalDemandId, MessageType type);

    /**
     * Method getMessageList returns the Message object of type 'type' based on the internalDemandId, for either sent or
     * received items.
     * @param internalDemandId the identifier of the message
     * @param type the message type to look for
     * @param sent indicates whether the message was sent or received
     * @return returns a list of message of type 'type' based on the internalDemandId
     */
    List<Message> getMessageList(long internalDemandId, MessageType type, boolean sent);

    /**
     * Return the owner.
     * @return SupplyChainActor; the owner
     */
    SupplyChainActor getOwner();
}
