package nl.tudelft.simulation.supplychain.test;

import java.util.Set;

import org.djutils.draw.point.Point;

import nl.tudelft.simulation.supplychain.transport.TransportOption;
import nl.tudelft.simulation.supplychain.transport.TransportOptionProvider;

public class PCShopTransportOptionProvider implements TransportOptionProvider {

	public PCShopTransportOptionProvider() {

	}

	@Override
	public Set<TransportOption> provideTransportOptions(Point<?> sender, Point<?> receiver) {
		return null;
	}

}
