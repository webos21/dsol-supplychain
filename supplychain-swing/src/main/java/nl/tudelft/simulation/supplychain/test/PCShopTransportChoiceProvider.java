package nl.tudelft.simulation.supplychain.test;

import java.util.Set;

import org.djutils.draw.point.Point;

import nl.tudelft.simulation.supplychain.product.Sku;
import nl.tudelft.simulation.supplychain.transport.TransportChoiceProvider;
import nl.tudelft.simulation.supplychain.transport.TransportOption;

public class PCShopTransportChoiceProvider implements TransportChoiceProvider {

	public PCShopTransportChoiceProvider() {

	}

	@Override
	public TransportOption chooseTransportOptions(Set<TransportOption> transportOptions, Sku sku) {

		return null;
	}

}
