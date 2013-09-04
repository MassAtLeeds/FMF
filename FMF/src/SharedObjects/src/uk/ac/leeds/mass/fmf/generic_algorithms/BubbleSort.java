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

package uk.ac.leeds.mass.fmf.generic_algorithms;

/**
 *
 * @author geo8kh
 */
public class BubbleSort {

    /**
     * This is a helper method to sort integer arrays so that the elements are ordered by size in the 
     * seconed element of the two dimensional array returned.  The first dimension contains the original integer index of elements position.
     * The smallest element will be contained in the return array element [1][0] while the maximum value will be
     * stored in the return array's last element [1][returnArray[1].length-1]. The original index of each element is contained in the
     * first dimension [0][0] contains the original array index for the value in element [1][0].
     * The sort methodology is a simple bubble sort which cycles throught the array swapping elements based on relative size.
     * The sorting ends when no exchanges are made on one complete pass through the array.  This may not be the most efficient
     * way to sort for large arrays.
     *
     * @param arrayToSort is a single dimension integer array of any size
     * @return is a new two dimensional integer array with the second dimension of equal size to the input array.  
     * It contains all of the same elements sorted from low to high and the original index values of where the element 
     * was located in the arrayToSort.
     */
    static public int[][] sort(int[] arrayToSort){

        int[][] returnArray = new int[2][arrayToSort.length];
        double[] arrayToSortParameter = new double[arrayToSort.length];

        //convert the array into a double array
        for (int i = 0; i < arrayToSort.length; i++) {
            arrayToSortParameter[i] = arrayToSort[i];
        }

       //pass the new double array into the double array sort method
       double[][] returned = sort(arrayToSortParameter);

        //Convert the returned double array into an int array
        for (int i = 0; i < returned[0].length; i++) {
            returnArray[0][i] = new Double(returned[0][i]).intValue();
            returnArray[1][i] = new Double(returned[1][i]).intValue();
        }
        
        return returnArray; 
    }

    
    
    /**
     * This is a helper method to sort double arrays so that the elements are ordered by size in the 
     * seconed element of the two dimensional array returned.  The first dimension contains the original integer index of elements position.
     * The smallest element will be contained in the return array element [1][0] while the maximum value will be
     * stored in the return array's last element [1][returnArray[1].length-1]. The original index of each element is contained in the
     * first dimension [0][0] contains the original array index for the value in element [1][0].
     * The sort methodology is a simple bubble sort which cycles throught the array swapping elements based on relative size.
     * The sorting ends when no exchanges are made on one complete pass through the array.  This may not be the most efficient
     * way to sort for large arrays.
     *
     * @param arrayToSort is a single dimension double array of any size
     * @return is a new two dimensional double array with the second dimension of equal size to the input array.  
     * It contains all of the same elements sorted from low to high and the original index values of where the element 
     * was located in the arrayToSort.
     */
    static public double[][] sort(double[] arrayToSort){
        boolean changeMade = true;
        double[][] returnArray = new double[2][arrayToSort.length];
        
        //put all of the elements in the array for sorting in a new two dimensional array with the index values.
        for (int i = 0; i < arrayToSort.length; i++) {
            returnArray[0][i] = i;
            returnArray[1][i] = arrayToSort[i];
        }
        //cycle through the array until no swaps have been made
        while (changeMade) {

            changeMade = false;

            for (int i = 0; i < returnArray[1].length-1; i++) {
                //swap the elements in the array if the element at 1,i is less than the element at 1,i+1
                if ( returnArray[1][i] > returnArray[1][i+1] ){
                    //store the values at i in two variables
                    double value = returnArray[1][i];
                    double index = returnArray[0][i];
                    //swap the values at i+1 into i
                    returnArray[1][i] = returnArray[1][i+1];
                    returnArray[0][i] = returnArray[0][i+1];
                    //put the stored values from i into i+1
                    returnArray[1][i+1] = value;
                    returnArray[0][i+1] = index;
                    //record that a change has been made
                    changeMade = true;
                }
            }

        }
        
        return returnArray;
    }

    /**
     * This is a helper method to reorder arrays previous sorted by the sort methods above back into the original
     * order that they were passed in.  The input array needs to be a two dimensional array.
     * The first element of the first dimension [0][i] contains the index of the elements, this is the order that the returned
     * array will be ordered in.  The second element of the first dimension [1][i] contains the values that will be put in the array returned.
     *
     * @param arrayToReorder two dimentional int array. First element of the first dimension contains the index order
     * for the values.  Second element of the first dimension contains the values to be returned.
     * @return single dimesional double array containing the reordered values from the second element of the first dimension
     * of the input array.
     */
    static public int[] returnToOriginalOrder(int[][] arrayToReorder) throws RuntimeException{


        if (arrayToReorder.length < 2){
            throw new RuntimeException(new Throwable(
                    "arrayToReorder does not contain enough elements in the first dimension. " +
                    "Class: BubbleSort " +
                    "Method: returnToOriginalOrder " +
                    "int overload."));
        }

        int[] reordered = new int[arrayToReorder[0].length];

        //cycle through the array that is to be returned to original order
        for (int i = 0; i < arrayToReorder[0].length; i++) {
            //put the values in the second dimension of the array into the order contained in the first dimension
            reordered[arrayToReorder[0][i]] = arrayToReorder[1][i];
        }

        return reordered;
    }
    

    /**
     * This is a helper method to reorder arrays previous sorted by the sort methods above back into the original
     * order that they were passed in.  The input array needs to be a two dimensional array.
     * The first element of the first dimension [0][i] contains the index of the elements, this is the order that the returned
     * array will be ordered in.  The second element of the first dimension [1][i] contains the values that will be put in the array returned.
     *
     * @param arrayToReorder two dimentional double array. First element of the first dimension contains the index order
     * for the values.  Second element of the first dimension contains the values to be returned.
     * @return single dimesional double array containing the reordered values from the second element of the first dimension
     * of the input array.
     */
    static public double[] returnToOriginalOrder(double[][] arrayToReorder) throws RuntimeException{

        if (arrayToReorder.length < 2){
            throw new RuntimeException(new Throwable(
                    "arrayToReorder does not contain enough elements in the first dimension. " +
                    "Class: BubbleSort " +
                    "Method: returnToOriginalOrder " +
                    "double overload."));
        }

        double[] reordered = new double[arrayToReorder[0].length];

        //cycle through the array that is to be returned to original order
        for (int i = 0; i < arrayToReorder[0].length; i++) {
            //put the values in the second dimension of the array into the order contained in the first dimension
            reordered[new Double(arrayToReorder[0][i]).intValue()] = arrayToReorder[1][i];
        }

        return reordered;
    }
}
