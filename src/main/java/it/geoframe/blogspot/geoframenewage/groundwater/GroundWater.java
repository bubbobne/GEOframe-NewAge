/*
 * GNU GPL v3 License
 *
 * Copyright 2021 Niccol� Tubini
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.geoframe.blogspot.geoframenewage.groundwater;

import static org.hortonmachine.gears.libs.modules.HMConstants.isNovalue;

import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import it.geoframe.blogspot.numerical.ode.NestedNewton;
import oms3.annotations.Description;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Out;

/**
 * @author Niccol� Tubini, Giuseppe Formetta
 * 
 */

public class GroundWater {

	@Description("Input recharge Hashmap")
	@In
	public HashMap<Integer, double[]> inHMRecharge;

	@Description("Input CI Hashmap")
	@In
	public HashMap<Integer, double[]>initialConditionS_i;


	@Description("Time Step simulation")
	@In
	public int timeStep;
	

	@Description("Coefficient of the non-linear Reservoir model ")
	@In
	public double coefficientGroundWaterDischarge ;


	@Description("Exponent of non-linear reservoir")
	@In
	public double exponentGroundWaterDischarge;

	@Description("The area of the HRUs in km2")
	@In
	public double area;

	@Description("Smax")
	@In
	public double storageMaxGroundWater=10;



	@Description("The output HashMap with the Water Storage")
	@Out
	public HashMap<Integer, double[]> outHMStorage= new HashMap<Integer, double[]>() ;

	@Description("The output HashMap with the discharge")
	@Out
	public HashMap<Integer, double[]> outHMDischarge= new HashMap<Integer, double[]>() ;
	
	@Description("The output HashMap with the discharge expressed in mm. This is meant to be easily compare with storage and rain")
	@Out
	public HashMap<Integer, double[]> outHMDischarge_mm = new HashMap<Integer, double[]>() ;

	private int step;
	
	private double recharge;
	private double storage;
	private double discharge;

	private Set<Entry<Integer, double[]>> entrySet;

	private ODE ode;

	private NestedNewton newton;
	
	double CI;
	
	/**
	 * Process: reading of the data, computation of the
	 * storage and outflows
	 *
	 * @throws Exception the exception
	 */
	@Execute
	public void process() throws Exception {

		// reading the ID of all the stations 
		entrySet = inHMRecharge.entrySet();


		// iterate over the station
		for( Entry<Integer, double[]> entry : entrySet ) {
			Integer ID = entry.getKey();

			if(step==0){
				
				if(initialConditionS_i!=null){
					storage = initialConditionS_i.get(ID)[0];	
					if (isNovalue(storage)) storage = 0;	
					
				}else{
					storage = 0;	
				}
				
				ode = new ODE();
				newton = new NestedNewton();

			}


			/**Input data reading*/
			recharge = inHMRecharge.get(ID)[0];
			if (isNovalue(recharge)) recharge= 0;

			ode.set(storage, recharge, coefficientGroundWaterDischarge, exponentGroundWaterDischarge, storageMaxGroundWater);

			storage = newton.solve(storageMaxGroundWater*0.9, ode);
			discharge = coefficientGroundWaterDischarge*Math.pow(Math.min(storage,storageMaxGroundWater), exponentGroundWaterDischarge) + Math.max(0,  storage - storageMaxGroundWater);
			storage = storage - Math.max(0,  storage - storageMaxGroundWater);
//			System.out.println(storage + "\t" + recharge + "\t" + discharge );


			outHMStorage.put(ID, new double[]{storage});
			outHMDischarge.put(ID, new double[]{discharge/1000*area*Math.pow(10, 6)/(60*timeStep)});
			outHMDischarge_mm.put(ID, new double[]{discharge});

			//initialConditionS_i.put(ID,new double[]{waterStorage});



		}


		step++;

	}

}
