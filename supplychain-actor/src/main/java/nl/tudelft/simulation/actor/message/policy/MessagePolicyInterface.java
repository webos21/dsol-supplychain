package nl.tudelft.simulation.actor.message.policy;

import java.io.Serializable;

import org.djutils.base.Identifiable;

import nl.tudelft.simulation.actor.Actor;
import nl.tudelft.simulation.actor.message.Message;
import nl.tudelft.simulation.actor.message.MessageType;

/**
 * The HandlerInterface defines what any Message Handler should be able to do: handle the receipt of a Message. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public interface MessagePolicyInterface extends Serializable, Identifiable
{
    /**
     * Handle the content of the message.
     * @param message Message; the message to be handled
     * @return a boolean acknowledgement; true or false
     */
    boolean handleMessage(Message message);

    /** 
     * Return the message type that this handler can handle.
     * @return MessageType; the message type that this handler can handle
     */
    MessageType getMessageType();
    
    /**
     * Return the owner of this handler.
     * @return owner Actor; the owner of this handler.
     */
    Actor getOwner();

}
