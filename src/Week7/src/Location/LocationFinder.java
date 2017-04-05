package Week7.src.Location;
import Week7.src.Utils.MacRssiPair;
import Week7.src.Utils.Position;

/**
 * Interface for your LocationFinder
 * @author Bernd
 *
 */
public interface LocationFinder {
	
	public Position locate(MacRssiPair[] data);
	
}
