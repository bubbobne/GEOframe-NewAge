/*
 * GNU GPL v3 License
 *
 * Copyright 2021 Niccol� Tubini, Giuseppe Formetta, Riccardo Rigon
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

package it.geoframe.blogspot.geoframenewage.damtest;


import java.net.URISyntaxException;
import java.util.HashMap;

import org.hortonmachine.gears.io.timedependent.OmsTimeSeriesIteratorReader;
import org.hortonmachine.gears.io.timedependent.OmsTimeSeriesIteratorWriter;
import org.junit.Test;

import it.geoframe.blogspot.geoframenewage.dam.Dam;

/**
 * @author Niccol� Tubini, Giuseppe Formetta
 * 
 */
public class TestDam{

	@Test
	public void testLinear() throws Exception {

		String startDate = "1994-01-01 00:00";
		String endDate = "1994-02-01 00:00";
		int timeStepMinutes = 60;
		String fId = "ID";

		String inPathToPrec ="resources/Input/rainfall.csv";
		String inPathToEp ="resources/Input/ET.csv";
		String inPathToQin ="resources/Input/Qin.csv";
		String inPathToQout ="resources/Input/Qout.csv";
		
		
		String pathToLevel=  "resources/Output/dam/level.csv";
		String pathToVolume=  "resources/Output/dam/Volume.csv";
		String pathToArea=  "resources/Output/dam/Area.csv";
		String pathToQreservoir=  "resources/Output/dam/Qreservoir.csv";
		String pathToQweir=  "resources/Output/dam/Qweir.csv";


		
		OmsTimeSeriesIteratorReader JReader = getTimeseriesReader(inPathToPrec, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader EpReader = getTimeseriesReader(inPathToEp, fId, startDate, endDate, timeStepMinutes);

		OmsTimeSeriesIteratorReader QinReader = getTimeseriesReader(inPathToQin, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader QoutReader = getTimeseriesReader(inPathToQout, fId, startDate, endDate, timeStepMinutes);

		OmsTimeSeriesIteratorWriter writerLevel = new OmsTimeSeriesIteratorWriter();
		OmsTimeSeriesIteratorWriter writerVolume = new OmsTimeSeriesIteratorWriter();
		OmsTimeSeriesIteratorWriter writerArea = new OmsTimeSeriesIteratorWriter();
		OmsTimeSeriesIteratorWriter writerQreservoir = new OmsTimeSeriesIteratorWriter();
		OmsTimeSeriesIteratorWriter writerQweir = new OmsTimeSeriesIteratorWriter();



		writerLevel.file = pathToLevel;
		writerLevel.tStart = startDate;
		writerLevel.tTimestep = timeStepMinutes;
		writerLevel.fileNovalue="-9999";
		
		writerVolume.file = pathToVolume;
		writerVolume.tStart = startDate;
		writerVolume.tTimestep = timeStepMinutes;
		writerVolume.fileNovalue="-9999";
		
		writerArea.file = pathToArea;
		writerArea.tStart = startDate;
		writerArea.tTimestep = timeStepMinutes;
		writerArea.fileNovalue="-9999";
		
		writerQreservoir.file = pathToQreservoir;
		writerQreservoir.tStart = startDate;
		writerQreservoir.tTimestep = timeStepMinutes;
		writerQreservoir.fileNovalue="-9999";
		
		writerQweir.file = pathToQweir;
		writerQweir.tStart = startDate;
		writerQweir.tTimestep = timeStepMinutes;
		writerQweir.fileNovalue="-9999";
		

		
		Dam waterBudget= new Dam();


		while( JReader.doProcess ) {
		

			
			waterBudget.a = 5;
			waterBudget.b = 0;
			waterBudget.weirCoefficient = 0.75;
			waterBudget.weirWidth = 3;
			waterBudget.weirHeight = 1;
			waterBudget.timeStepMinutes = 60;
			
			

			
			JReader.nextRecord();
			
			HashMap<Integer, double[]> id2ValueMap = JReader.outData;
			waterBudget.inHMRain = id2ValueMap;
			
			EpReader.nextRecord();
			waterBudget.inHMEp = EpReader.outData;

			QinReader.nextRecord();
			waterBudget.inHMQin = QinReader.outData;

			QoutReader.nextRecord();
			waterBudget.inHMQout = QoutReader.outData;


            waterBudget.process();
                        
			writerLevel.inData = waterBudget.outHMFreeSurfaceLevel;
			writerLevel.writeNextLine();
			
			writerVolume.inData = waterBudget.outHMVolume;
			writerVolume.writeNextLine();
			
			writerArea.inData = waterBudget.outHMSurfaceArea;
			writerArea.writeNextLine();
			
			writerQreservoir.inData = waterBudget.outHMQ;
			writerQreservoir.writeNextLine();
			
			writerQweir.inData = waterBudget.outHMQweir;
			writerQweir.writeNextLine();
			
			if (pathToLevel != null) {
				writerLevel.close();
			}
			
			if (pathToVolume != null) {
				writerVolume.close();
			}

			if (pathToArea != null) {
				writerArea.close();
			}
			
			if (pathToQreservoir != null) {
				writerQreservoir.close();
			}
			
			if (pathToQweir != null) {
				writerQweir.close();
			}
            
		}
		JReader.close();

	}


	private OmsTimeSeriesIteratorReader getTimeseriesReader( String inPath, String id, String startDate, String endDate,
			int timeStepMinutes ) throws URISyntaxException {
		OmsTimeSeriesIteratorReader reader = new OmsTimeSeriesIteratorReader();
		reader.file = inPath;
		reader.idfield = "ID";
		reader.tStart = startDate;
		reader.tTimestep = 60;
		reader.tEnd = endDate;
		reader.fileNovalue = "-9999";
		reader.initProcess();
		return reader;
	}
}