/*
 *   The Flexible Modelling Framework is a Social Science application for 
 *   synthesising individual level populations
 *   Copyright (C) 2013  Kirk Harland
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   Contact email: k.harland@leeds.ac.uk
 */
package tooltemplates.toolcommunication;

import javax.swing.*;
import java.util.*;
import uk.ac.leeds.mass.fmf.shared_objects.*;

/**
 * This class provides a singleton that allows for Observer/Observable like communication.
 * Objects can register with the singleton and request all listeners are alerted.
 * @version 1.0
 * @author <A href="http://www.geog.leeds.ac.uk/people/a.evans/">Andy Evans</A>
 */
public class StorageSingleton {

	/**
	* Singleton object.
	**/
	private static StorageSingleton storageSingleton = null;
	
	/**
	* Data observers/observables might want to set/get.
	**/
	private FMFTable fmfTable = null;
	
	/**
	* List of data observers.
	**/
	private ArrayList<DataChangeListener> listeners = new ArrayList<DataChangeListener>();
	
	
	
	
	/**
	* Standard singleton private constructor.
	**/
	private StorageSingleton() {}
	
	
	
	
	/**
	* Initiates and/or returns the singleton object.
	* @return the singleton object for access to methods.
	**/
	public static StorageSingleton getInstance() {
	
		if (storageSingleton == null) {
			storageSingleton = new StorageSingleton();
		}
		
		return storageSingleton;
	
	}
	
	
	
	
	/**
	* Sets the data that observers might want.
	* @param fmfTable the data.
	**/
	public void setData (FMFTable fmfTable) {
	
		this.fmfTable = fmfTable;
	
	}
	
	
	
	
	/**
	* Gets the data that observers might want.
	* @return the data.
	**/
	public FMFTable getData() {
	
		return fmfTable;
	
	}
	
	
	
	
	/**
	* Register an observer.
	* @param listener the observer.
	**/
	public void registerDataChangeListener(DataChangeListener listener) {
	
		listeners.add(listener);
	
	}
	
	
	
	
	/**
	* Remove an observer from the list.
	* @param listener the observer.
	**/
	public void removeDataChangeListener(DataChangeListener listener) {
	
		listeners.remove(listener);
	
	}

	
	
	
	/**
	* Alert all the observers.
	**/
	public void alertDataChangeListeners() {
	
		for (DataChangeListener dcl : listeners) dcl.dataChanged();
	
	}
	
	
}