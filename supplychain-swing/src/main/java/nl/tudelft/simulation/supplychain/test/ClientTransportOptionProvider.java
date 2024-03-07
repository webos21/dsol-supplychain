package nl.tudelft.simulation.supplychain.test;

import java.util.Set;

import org.djutils.draw.point.Point;

import nl.tudelft.simulation.supplychain.transport.TransportOption;
import nl.tudelft.simulation.supplychain.transport.TransportOptionProvider;

public class ClientTransportOptionProvider implements TransportOptionProvider {

	public ClientTransportOptionProvider() {

	}

	@Override
	public Set<TransportOption> provideTransportOptions(Point<?> sender, Point<?> receiver) {
		return null;
	}

}
