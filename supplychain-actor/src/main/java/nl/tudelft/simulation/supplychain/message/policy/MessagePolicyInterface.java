package nl.tudelft.simulation.supplychain.message.policy;

import java.io.Serializable;

import org.djutils.base.Identifiable;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.message.Message;

/**
 * The HandlerInterface defines what any Message Handler should be able to do: handle the receipt of a Message.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <M> the message class for which this policy applies
 */
public interface MessagePolicyInterface<M extends Message> extends Serializable, Identifiable
{
    /**
     * Handle the content of the message.
     * @param message M; the message to be handled
     * @return a boolean acknowledgement; true or false
     */
    boolean handleMessage(M message);

    /**
     * Return the owner of this handler.
     * @return owner Actor; the owner of this handler.
     */
    Actor getOwner();

    /**
     * Return the class of messages for which this policy applies.
     * @return Class&lt;? extends M&gt;; the class of messages for which this policy applies
     */
    Class<M> getMessageClass();
}
