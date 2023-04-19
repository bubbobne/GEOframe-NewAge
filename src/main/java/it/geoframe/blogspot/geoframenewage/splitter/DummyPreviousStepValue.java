package it.geoframe.blogspot.geoframenewage.splitter;

import static org.hortonmachine.gears.libs.modules.HMConstants.isNovalue;

import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Out;

public class DummyPreviousStepValue {

	@In
	public HashMap<Integer, double[]> inHMValue;
	@In
	public HashMap<Integer, double[]> inHMRain;
	@In
	public double pInitialStorage;

	private double value = 0;
	private Integer id = 0;
	private int i = 0;
	@Out
	public HashMap<Integer, double[]> outHMValue;

	@Execute
	public void process() {
		outHMValue = new HashMap<>();
		System.out.println("enter dummy var");

		if (inHMValue != null || (i == 0 && inHMRain != null)) {
			Set<Entry<Integer, double[]>> entrySet;
			if (i != 0) {
				entrySet = inHMValue.entrySet();
				System.out.println("Initial condition");
			} else {
				entrySet = inHMRain.entrySet();
				System.out.println("Storage from rootzone");
			}
			for (Entry<Integer, double[]> entry : entrySet) {
				id = entry.getKey();
				value = i != 0 ? inHMValue.get(id)[0] : pInitialStorage / 2;
				if (isNovalue(value))
					value = 0;
				System.out.println("Value of storage" + value);

				outHMValue.put(id, new double[] { value });

			}
			i = 1 + 1;
		}

	}
}
