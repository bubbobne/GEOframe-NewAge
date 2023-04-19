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
public class SplitterExponential {

	@Description("")
	@In
	public HashMap<Integer, double[]> inHMRain;

	@Description("")
	@In
	public double pMaxStorage;

	public double pMinStorage = 0;

	@Description("")
	@In
	public double pAlpha;

	@Description("")
	@Out
	public HashMap<Integer, double[]> outHMActualRainFastReservoir;

	@Description("")
	@Out
	public HashMap<Integer, double[]> outHMActualRainSlowReservoir;

	@Description("")
	@In
	public HashMap<Integer, double[]> inHMActualStorage;

	private int step = 0;

	@Execute
	public void process() {
		System.out.println("enter exponential var");

		Set<Entry<Integer, double[]>> entrySet = inHMRain.entrySet();
		for (Entry<Integer, double[]> entry : entrySet) {
			int ID = entry.getKey();
			double rain = inHMRain.get(ID)[0];
			double storage = pMaxStorage / 2;
			if (step != 0) {
				storage = inHMActualStorage.get(ID)[0];
			}
			step = step + 1;
			if (isNovalue(rain)) {
				rain = 0;
			}

			if (isNovalue(storage)) {
				storage = 0;
			}
			System.out.println("The storage at previous step is:" + storage);

			outHMActualRainSlowReservoir = new HashMap<>();
			outHMActualRainFastReservoir = new HashMap<>();
			if (rain != 0) {
				if (storage >= pMinStorage && storage < pMaxStorage) {
					double normalizedStorage = (storage - pMinStorage) / (pMaxStorage - pMinStorage);
					double runoff1 = 0;
					if (storage + rain > pMaxStorage) {
						runoff1 = rain - pMaxStorage;
						rain = pMaxStorage - storage;
					}

					double runoff2 = Math.pow(normalizedStorage, pAlpha) * rain;
					outHMActualRainSlowReservoir.put(ID, new double[] { rain - runoff2 });
					outHMActualRainFastReservoir.put(ID, new double[] { runoff1 + runoff2 });
				} else if (storage >= pMaxStorage) {
					outHMActualRainSlowReservoir.put(ID, new double[] { 0 });
					outHMActualRainFastReservoir.put(ID, new double[] { rain });
				} else {
					double normalizedStorage = (pMinStorage) / (pMaxStorage - pMinStorage);
					double runoff = Math.pow(normalizedStorage, pAlpha) * rain;
					outHMActualRainSlowReservoir.put(ID, new double[] { rain - runoff });
					outHMActualRainFastReservoir.put(ID, new double[] { runoff });
				}

			} else {
				outHMActualRainSlowReservoir.put(ID, new double[] { 0 });
				outHMActualRainFastReservoir.put(ID, new double[] { 0 });
			}
		}

	}
}

