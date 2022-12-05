package nl.tudelft.simulation.supplychain.actor;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import org.djunits.value.vdouble.scalar.Time;
import org.djutils.draw.bounds.Bounds3d;
import org.djutils.draw.point.OrientedPoint3d;
import org.djutils.exceptions.Throw;
import org.djutils.immutablecollections.ImmutableLinkedHashSet;
import org.djutils.immutablecollections.ImmutableSet;

import nl.tudelft.simulation.dsol.animation.Locatable;
import nl.tudelft.simulation.supplychain.dsol.SCSimulatorInterface;
import nl.tudelft.simulation.supplychain.message.Message;
import nl.tudelft.simulation.supplychain.message.handler.MessageHandlerInterface;

/**
 * The actor is the basic class in the nl.tudelft.simulation.actor package. It implements the behavior of a 'communicating'
 * object, that is able to exchange messages with other actors and process the incoming messages.<br>
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class Actor extends AbstractPolicyHandler implements Locatable
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221126L;

    /** the name of the actor. */
    private final String name;

    /** the location description of the actor (e.g., a city, country). */
    private final String locationDescription;

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
        super(simulator);
        Throw.whenNull(name, "name cannot be null");
        Throw.whenNull(location, "location cannot be null");
        Throw.whenNull(locationDescription, "locationDescription cannot be null");
        this.name = name;
        this.locationDescription = locationDescription;
        this.location = location;
        this.messageHandler = messageHandler;
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
     * Receive a message from another actor, and handle it (storing or handling, depending on the MessageHandler).
     * @param message message; the message to receive
     */
    public void receiveMessage(final Message message)
    {
        this.messageHandler.handleMessageReceipt(message);
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
