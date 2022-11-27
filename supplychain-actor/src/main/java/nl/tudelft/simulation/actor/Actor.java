package nl.tudelft.simulation.actor;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;
import org.djutils.draw.bounds.Bounds3d;
import org.djutils.draw.point.OrientedPoint3d;
import org.djutils.event.EventProducer;
import org.djutils.exceptions.Throw;
import org.djutils.immutablecollections.ImmutableLinkedHashSet;
import org.djutils.immutablecollections.ImmutableSet;

import nl.tudelft.simulation.actor.dsol.SCSimulatorInterface;
import nl.tudelft.simulation.actor.message.Message;
import nl.tudelft.simulation.actor.message.MessageType;
import nl.tudelft.simulation.actor.message.handler.MessageHandlerInterface;
import nl.tudelft.simulation.actor.message.policy.MessagePolicyInterface;
import nl.tudelft.simulation.dsol.animation.Locatable;

/**
 * The actor is the basic class in the nl.tudelft.simulation.actor package. It implements the behavior of a 'communicating'
 * object, that is able to exchange messages with other actors and process the incoming messages.<br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target= "_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public abstract class Actor extends EventProducer implements Serializable, Locatable
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221126L;

    /** the name of the actor. */
    private final String name;

    /** the location description of the actor (e.g., a city, country). */
    private final String locationDescription;

    /** the simulator to schedule simulation events on. */
    private final SCSimulatorInterface simulator;

    /** the roles. */
    private ImmutableSet<Role> roles = new ImmutableLinkedHashSet<>(new LinkedHashSet<Role>());
    
    /** the message handler. */
    private final MessageHandlerInterface messageHandler;
    
    /** the location of the actor. */
    private final OrientedPoint3d location;

    /** the bounds of the object (size and relative height in the animation). */
    private Bounds3d bounds = new Bounds3d(-1.0, 1.0, -1.0, 1.0, -1.0, 1.0);

    /**
     * Construct a new Actor.
     * @param name String; the name of the actor
     * @param messageHandler MessageHandlerInterface; the message handler to use
     * @param simulator SCSimulatorInterface; the simulator to use
     * @param location OrientedPoint3d; the location of the actor
     * @param locationDescription String; the location description of the actor (e.g., a city, country)
     */
    public Actor(final String name, final MessageHandlerInterface messageHandler, final SCSimulatorInterface simulator,
            final OrientedPoint3d location, final String locationDescription)
    {
        Throw.whenNull(name, "name cannot be null");
        Throw.whenNull(simulator, "simulator cannot be null");
        Throw.whenNull(location, "location cannot be null");
        Throw.whenNull(locationDescription, "locationDescription cannot be null");
        this.name = name;
        this.locationDescription = locationDescription;
        this.simulator = simulator;
        this.location = location;
        this.messageHandler = messageHandler;
    }

    /**
     * Add a message handling policy to the Actor.
     * @param policy MessagePolicyInterface; the policy to add
     */
    public void addMessagePolicy(final MessagePolicyInterface policy)
    {
        this.messageHandler.addMessagePolicy(policy);
    }
    
    /**
     * Remove a message handling policy from the Actor.
     * @param messageType MessageType; the message type of the policy to remove
     * @param policyId String; the id of the policy to remove
     */
    public void removeMessagePolicy(final MessageType messageType, final String policyId)
    {
        this.messageHandler.removeMessagePolicy(messageType, policyId);
    }
    
    /**
     * Add a role to the actor.
     * @param role Role; the role to add to the actor
     */
    public void addRole(final Role role)
    {
        Throw.whenNull(role, "role cannot be null");
        Set<Role> newRoles = this.roles.toSet();
        newRoles.add(role);
        this.roles = new ImmutableLinkedHashSet<>(newRoles);
    }
    
    /**
     * Return the set of roles for this actor.
     * @return Set&lt;roles&gt;; the roles of this actor
     */
    public ImmutableSet<Role> getRoles()
    {
        return this.roles;
    }

    /**
     * Receive a message from another actor, and handle it (storing or handling, depending on the MessageHandler).
     * @param message message; the message to receive
     */
    public void receiveMessage(final Message message)
    {
        this.messageHandler.handleMessageReceipt(message);
    }
    
    /**
     * Send a message to another actor with a delay.
     * @param message message; the message to send
     * @param delay Duration; the time it takes between sending and receiving
     */
    protected void sendMessage(final Message message, final Duration delay)
    {
        this.simulator.scheduleEventRel(delay, this, message.getReceiver(), "receiveMessage", new Object[] {message});
    }
    
    /**
     * Send a message to another actor without a delay.
     * @param message message; the message to send
     */
    protected void sendMessage(final Message message)
    {
        sendMessage(message, Duration.ZERO);
    }
    
    /**
     * Return the name of the actor.
     * @return String; the name of the actor
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Return the location description of the actor (e.g., a city, country).
     * @return String; the location description of the actor
     */
    public String getLocationDescription()
    {
        return this.locationDescription;
    }

    /**
     * Return the simulator to schedule simulation events on.
     * @return simulator SCSimulatorInterface the simulator
     */
    public SCSimulatorInterface getSimulator()
    {
        return this.simulator;
    }

    /**
     * Return the current simulation time.
     * @return Time; the current simulation time
     */
    public Time getSimulatorTime()
    {
        return this.simulator.getAbsSimulatorTime();
    }

    /** {@inheritDoc} */
    @Override
    public Serializable getSourceId()
    {
        return getName();
    }

    /** {@inheritDoc} */
    @Override
    public OrientedPoint3d getLocation()
    {
        return this.location;
    }

    /**
     * Set the bounds of the object (size and relative height in the animation).
     * @param bounds the bounds for the (animation) object
     */
    public void setBounds(final Bounds3d bounds)
    {
        this.bounds = bounds;
    }

    /** {@inheritDoc} */
    @Override
    public Bounds3d getBounds()
    {
        return this.bounds;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return this.name;
    }
}
