package nl.tudelft.simulation.actor.dsol;

import java.util.LinkedHashMap;
import java.util.Map;

import org.djunits.Throw;
import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.immutablecollections.ImmutableLinkedHashMap;
import org.djutils.immutablecollections.ImmutableMap;

import nl.tudelft.simulation.actor.ActorType;
import nl.tudelft.simulation.actor.RoleType;
import nl.tudelft.simulation.dsol.experiment.StreamInformation;
import nl.tudelft.simulation.dsol.model.AbstractDSOLModel;

/**
 * AbstractSupplyChainModel is the base model for supply chain models as it contains a registry of actor types and role types.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class AbstractSupplyChainModel extends AbstractDSOLModel<Duration, SCSimulatorInterface>
        implements SCModelInterface
{
    /** */
    private static final long serialVersionUID = 20221127L;
    
    /** the actor types. */
    private ImmutableMap<String, ActorType> actorTypes = new ImmutableLinkedHashMap<>(new LinkedHashMap<>());

    /** the role types. */
    private ImmutableMap<String, RoleType> roleTypes = new ImmutableLinkedHashMap<>(new LinkedHashMap<>());

    /**
     * Construct a new supply chain model with a default stream information object.
     * @param simulator SCSimulatorInterface; the simulator to use for this model
     * @throws NullPointerException when simulator is null
     */
    public AbstractSupplyChainModel(final SCSimulatorInterface simulator)
    {
        super(simulator);
    }

    /**
     * Construct a new supply chain model with stream information.
     * @param simulator SCSimulatorInterface; the simulator to use for this model
     * @param streamInformation StreamInformation; the streams that have been prepared in a StreamInformation class
     * @throws NullPointerException when simulator or streamInformation is null
     */
    public AbstractSupplyChainModel(final SCSimulatorInterface simulator, final StreamInformation streamInformation)
    {
        super(simulator, streamInformation);
    }

    /** {@inheritDoc} */
    @Override
    public void registerActorType(final ActorType actorType)
    {
        Throw.when(getActorTypes().containsKey(actorType.getId()), IllegalArgumentException.class,
                "Duplicate registration of actor type " + actorType.getId());
        Map<String, ActorType> newMap = this.actorTypes.toMap();
        newMap.put(actorType.getId(), actorType);
        this.actorTypes = new ImmutableLinkedHashMap<>(newMap);
    }

    /** {@inheritDoc} */
    @Override
    public void registerRoleType(final RoleType roleType)
    {
        Throw.when(getRoleTypes().containsKey(roleType.getId()), IllegalArgumentException.class,
                "Duplicate registration of role type " + roleType.getId());
        Map<String, RoleType> newMap = this.roleTypes.toMap();
        newMap.put(roleType.getId(), roleType);
        this.roleTypes = new ImmutableLinkedHashMap<>(newMap);
    }

    /** {@inheritDoc} */
    @Override
    public ImmutableMap<String, ActorType> getActorTypes()
    {
        return this.actorTypes;
    }

    /** {@inheritDoc} */
    @Override
    public ImmutableMap<String, RoleType> getRoleTypes()
    {
        return this.roleTypes;
    }

}
