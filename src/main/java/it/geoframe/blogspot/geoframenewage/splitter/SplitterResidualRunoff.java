package it.geoframe.blogspot.geoframenewage.splitter;

import static org.hortonmachine.gears.libs.modules.HMConstants.isNovalue;

import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import oms3.annotations.Description;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Out;

/**
 * @author <a href="">Daniele Andreis</a>, Giuseppe Formetta, Riccardo Rigon
 * 
 */
public class SplitterResidualRunoff {

	@Description("")
	@In
	public HashMap<Integer, double[]> inHMRain;

	@Description("")
	@In
	public double pMaxStorage;

	@Description("")
	@In
	public HashMap<Integer, double[]> inHMActualStorage;

	@Description("")
	@In
	public double pBeta;

	@Description("")
	@Out
	public HashMap<Integer, double[]> outHMActualRainFastReservoir;

	@Description("")
	@Out
	public HashMap<Integer, double[]> outHMActualRainSlowReservoir;

	@Execute
	public void process() {
		System.out.println("enter exponential var");

		Set<Entry<Integer, double[]>> entrySet = inHMRain.entrySet();
		for (Entry<Integer, double[]> entry : entrySet) {

			int ID = entry.getKey();

			double rain = inHMRain.get(ID)[0];
			if (isNovalue(rain))
				rain = 0;
			outHMActualRainSlowReservoir = new HashMap<>();
			outHMActualRainFastReservoir = new HashMap<>();
			if (rain != 0) {
				double storage = inHMActualStorage.get(ID)[0];
				if (isNovalue(storage))
					storage = 0;
				if (storage < pMaxStorage) {
					double runoff1 = 0;
					if (storage + rain > pMaxStorage) {
						runoff1 = rain - pMaxStorage;
					}
					double diffStorage = pMaxStorage - storage;
					double runoff2 = rain - 1 / Math.pow(pMaxStorage, pBeta)
							* (Math.pow(diffStorage, pBeta + 1) - Math.pow(diffStorage, pBeta + 1));
					double runOff = runoff1 + runoff2;
					outHMActualRainSlowReservoir.put(ID, new double[] { rain - runOff });
					outHMActualRainFastReservoir.put(ID, new double[] { runOff });
				} else if (storage >= pMaxStorage) {
					outHMActualRainSlowReservoir.put(ID, new double[] { 0 });
					outHMActualRainFastReservoir.put(ID, new double[] { rain });
				}
			} else {
				outHMActualRainSlowReservoir.put(ID, new double[] { 0 });
				outHMActualRainFastReservoir.put(ID, new double[] { 0 });
			}
		}

	}
}
