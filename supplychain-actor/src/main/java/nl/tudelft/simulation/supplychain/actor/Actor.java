package nl.tudelft.simulation.supplychain.actor;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;
import org.djutils.base.Identifiable;
import org.djutils.draw.bounds.Bounds3d;
import org.djutils.draw.point.OrientedPoint2d;
import org.djutils.exceptions.Throw;
import org.djutils.immutablecollections.ImmutableLinkedHashSet;
import org.djutils.immutablecollections.ImmutableSet;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.dsol.animation.Locatable;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainSimulatorInterface;
import nl.tudelft.simulation.supplychain.message.Message;
import nl.tudelft.simulation.supplychain.message.handler.MessageHandlerInterface;

/**
 * The actor is the basic class in the nl.tudelft.simulation.actor package. It implements the behavior of a 'communicating'
 * object, that is able to exchange messages with other actors and process the incoming messages.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class Actor extends AbstractPolicyHandler implements PolicyHandlerInterface, Locatable, Identifiable
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221126L;

    /** the id of the actor. */
    private final String id;

    /** the longer name of the actor. */
    private final String name;

    /** the location description of the actor (e.g., a city, country). */
    private final String locationDescription;

    /** the roles. */
    private ImmutableSet<Role> roles = new ImmutableLinkedHashSet<>(new LinkedHashSet<>());

    /** the message handler. */
    private final MessageHandlerInterface messageHandler;

    /** the location of the actor. */
    private final OrientedPoint2d location;

    /** the bounds of the object (size and relative height in the animation). */
    private Bounds3d bounds = new Bounds3d(-1.0, 1.0, -1.0, 1.0, -1.0, 1.0);

    /**
     * Construct a new Actor.
     * @param id String; the short id of the actor
     * @param name String; the longer name of the actor
     * @param messageHandler MessageHandlerInterface; the message handler to use
     * @param model SupplyChainModelInterface; the model to retrieve the simulator, actor list, etc.
     * @param location OrientedPoint2d; the location of the actor
     * @param locationDescription String; the location description of the actor (e.g., a city, country)
     * @throws ActorAlreadyDefinedException when an actor with this id has already been registered
     * @throws NullPointerException when any of the arguments is null
     * @throws IllegalArgumentException when the id is the empty string
     */
    public Actor(final String id, final String name, final MessageHandlerInterface messageHandler,
            final SupplyChainModelInterface model, final OrientedPoint2d location, final String locationDescription)
            throws ActorAlreadyDefinedException
    {
        super(model.getSimulator());
        Throw.whenNull(id, "name cannot be null");
        Throw.when(id.length() == 0, IllegalArgumentException.class, "id of actor cannot be null");
        Throw.whenNull(name, "name cannot be null");
        Throw.whenNull(location, "location cannot be null");
        Throw.whenNull(locationDescription, "locationDescription cannot be null");
        this.id = id;
        this.name = name;
        this.locationDescription = locationDescription;
        this.location = location;
        this.messageHandler = messageHandler;
        model.registerActor(this);
        messageHandler.setOwner(this);
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
     * Receive a message from another actor, and handle it (storing or handling, depending on the MessageHandler). When the
     * message is not intended for this actor, a log warning is given, and the message is not processed.
     * @param message message; the message to receive
     */
    public void receiveMessage(final Message message)
    {
        if (!message.getReceiver().equals(this))
        {
            Logger.warn("Message " + message + " not meant for receiver " + toString());
        }
        else
        {
            this.messageHandler.handleMessageReceipt(message);
        }
    }

    /**
     * Send a message to another actor with a delay. This method is public, so Roles, Policies, Departments, ad other
     * sub-components of the Actor can send messages on its behalf. The public method has the risk that the message is sent from
     * the wrong actor. When this happens, i.e., when the message is not originating from this actor, a log warning is given,
     * but the message itself is sent.
     * @param message message; the message to send
     * @param delay Duration; the time it takes between sending and receiving
     */
    public void sendMessage(final Message message, final Duration delay)
    {
        if (!message.getSender().equals(this))
        {
            Logger.warn("Message " + message + " not originating from sender " + toString());
        }
        this.simulator.scheduleEventRel(delay, message.getReceiver(), "receiveMessage", new Object[] {message});
    }

    /**
     * Send a message to another actor without a delay.
     * @param message message; the message to send
     */
    public void sendMessage(final Message message)
    {
        sendMessage(message, Duration.ZERO);
    }

    /**
     * Return the short id of the actor.
     * @return String; the short id of the actor
     */
    @Override
    public String getId()
    {
        return this.id;
    }

    /**
     * Return the longer name of the actor.
     * @return String; the longer name of the actor
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
     * @return simulator SupplyChainSimulatorInterface the simulator
     */
    public SupplyChainSimulatorInterface getSimulator()
    {
        return this.simulator;
    }

    /**
     * Return the current simulation time.
     * @return Time; the current simulation time
     */
    public Time getSimulatorTime()
    {
        return getSimulator().getAbsSimulatorTime();
    }

    /** {@inheritDoc} */
    @Override
    public OrientedPoint2d getLocation()
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

    /** {@inheritDoc} */
    @Override
    public int hashCode()
    {
        return Objects.hash(this.id);
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Actor other = (Actor) obj;
        return Objects.equals(this.id, other.id);
    }

}
