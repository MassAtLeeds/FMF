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


package FlatFile;

/**
 *
 * @author geo8kh
 */
public class FlatFileConfigurationException extends RuntimeException{
    FlatFileConfigurationException(){ super(); }

    FlatFileConfigurationException(String message){ super(message); }

 	FlatFileConfigurationException (String message, Throwable cause){ super(message, cause); }

 	FlatFileConfigurationException(Throwable cause){ super(cause); }

}
