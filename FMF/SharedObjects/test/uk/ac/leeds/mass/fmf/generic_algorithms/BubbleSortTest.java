/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.leeds.mass.fmf.generic_algorithms;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author geo8kh
 */
public class BubbleSortTest {

    public BubbleSortTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testIntegerSort(){

        int[] input = {5,2,3,7,9,1,10,4,8,6};
        int[][] expected = new int[2][10];
        //sorted values of the orignal array
        expected[1][0] = 1;
        expected[1][1] = 2;
        expected[1][2] = 3;
        expected[1][3] = 4;
        expected[1][4] = 5;
        expected[1][5] = 6;
        expected[1][6] = 7;
        expected[1][7] = 8;
        expected[1][8] = 9;
        expected[1][9] = 10;

        //index of the original location in the array
        expected[0][0] = 5;
        expected[0][1] = 1;
        expected[0][2] = 2;
        expected[0][3] = 7;
        expected[0][4] = 0;
        expected[0][5] = 9;
        expected[0][6] = 3;
        expected[0][7] = 8;
        expected[0][8] = 4;
        expected[0][9] = 6;
     
        Assert.assertArrayEquals(expected, BubbleSort.sort(input));

    }

    @Test
    public void testIntegerSortNegativeNumbers(){

        int[] input = {-5,-2,-3,-7,-9,-1,-10,-4,-8,-6};
        int[][] expected = new int[2][10];
        //sorted values of the orignal array
        expected[1][0] = -10;
        expected[1][1] = -9;
        expected[1][2] = -8;
        expected[1][3] = -7;
        expected[1][4] = -6;
        expected[1][5] = -5;
        expected[1][6] = -4;
        expected[1][7] = -3;
        expected[1][8] = -2;
        expected[1][9] = -1;

        //index of the original location in the array
        expected[0][0] = 6;
        expected[0][1] = 4;
        expected[0][2] = 8;
        expected[0][3] = 3;
        expected[0][4] = 9;
        expected[0][5] = 0;
        expected[0][6] = 7;
        expected[0][7] = 2;
        expected[0][8] = 1;
        expected[0][9] = 5;

        Assert.assertArrayEquals(expected, BubbleSort.sort(input));
    }

    @Test
    public void testIntegerSortMixedNumbersPassThrough0(){

        int[] input = {-5,-2,-3,2,4,-1,5,-4,3,1,0};
        int[][] expected = new int[2][11];
        //sorted values of the orignal array
        expected[1][0] = -5;
        expected[1][1] = -4;
        expected[1][2] = -3;
        expected[1][3] = -2;
        expected[1][4] = -1;
        expected[1][5] = 0;
        expected[1][6] = 1;
        expected[1][7] = 2;
        expected[1][8] = 3;
        expected[1][9] = 4;
        expected[1][10] = 5;

        //index of the original location in the array
        expected[0][0] = 0;
        expected[0][1] = 7;
        expected[0][2] = 2;
        expected[0][3] = 1;
        expected[0][4] = 5;
        expected[0][5] = 10;
        expected[0][6] = 9;
        expected[0][7] = 3;
        expected[0][8] = 8;
        expected[0][9] = 4;
        expected[0][10] = 6;

        Assert.assertArrayEquals(expected, BubbleSort.sort(input));
    }

    @Test
    public void testIntegerSortDuplicateValuesNotConsectutive(){

        int[] input = {-4,-2,-3,2,4,-1,5,-4,2,1,0};
        int[][] expected = new int[2][11];
        //sorted values of the orignal array
        expected[1][0] = -4;
        expected[1][1] = -4;
        expected[1][2] = -3;
        expected[1][3] = -2;
        expected[1][4] = -1;
        expected[1][5] = 0;
        expected[1][6] = 1;
        expected[1][7] = 2;
        expected[1][8] = 2;
        expected[1][9] = 4;
        expected[1][10] = 5;

        //index of the original location in the array
        expected[0][0] = 0;
        expected[0][1] = 7;
        expected[0][2] = 2;
        expected[0][3] = 1;
        expected[0][4] = 5;
        expected[0][5] = 10;
        expected[0][6] = 9;
        expected[0][7] = 3;
        expected[0][8] = 8;
        expected[0][9] = 4;
        expected[0][10] = 6;

        Assert.assertArrayEquals(expected, BubbleSort.sort(input));
    }

    @Test
    public void testOriginalIntArrayNotChanged(){

        int[] input = {5,4,3,2,1};
        int[] inputCopy = {5,4,3,2,1};
        int[][] expected = new int [2][5];
        //sorted values of the orignal array
        expected[1][0] = 1;
        expected[1][1] = 2;
        expected[1][2] = 3;
        expected[1][3] = 4;
        expected[1][4] = 5;

        //index of the original location in the array
        expected[0][0] = 4;
        expected[0][1] = 3;
        expected[0][2] = 2;
        expected[0][3] = 1;
        expected[0][4] = 0;

        Assert.assertArrayEquals(input, inputCopy);
        Assert.assertArrayEquals(expected, BubbleSort.sort(input));
        Assert.assertArrayEquals(input, inputCopy);
    }

    @Test
    public void testDoubleSort(){

        double[] input = {1.1,0.8,-0.1,-1.0,1.099999999999};
        double[][] expected = new double[2][5];

        //sorted values of the orignal array
        expected[1][0] = -1.0;
        expected[1][1] = -0.1;
        expected[1][2] = 0.8;
        expected[1][3] = 1.099999999999;
        expected[1][4] = 1.1;

        //index of the original location in the array
        expected[0][0] = 3;
        expected[0][1] = 2;
        expected[0][2] = 1;
        expected[0][3] = 4;
        expected[0][4] = 0;

        double[][] returnedTest = BubbleSort.sort(input);
        Assert.assertArrayEquals(returnedTest, expected);

        double[][] reordered = new double[1][returnedTest[0].length];
        for (int i = 0; i < returnedTest[0].length; i++) {
            reordered[0][new Double(returnedTest[0][i]).intValue()] = returnedTest[1][i];
        }
        double[][] testInput = new double[1][input.length];
        testInput[0] = input;
        Assert.assertArrayEquals(reordered, testInput);

    }

    @Test
    public void testReorderDouble(){

        double[][] expected = new double[1][5];
        expected[0] = new double[] {1.1,0.8,-0.1,-1.0,1.099999999999};

        double[][] input = new double[2][5];
        //index of the original location in the array
        input[0] = new double[]{3,2,1,4,0};
        //sorted values of the orignal array
        input[1] = new double[]{-1.0,-0.1,0.8,1.099999999999,1.1};

        double[][] testArray = new double[1][];
        testArray[0] = BubbleSort.returnToOriginalOrder(input);

        Assert.assertArrayEquals(expected, testArray);
    }

    @Test(expected = RuntimeException.class )
    public void testReorderDoubleTwoFewElements(){

        double[][] input = new double[1][5];
        //index of the original location in the array
        input[0] = new double[]{3,2,1,4,0};

        double[] testArray = null;
        testArray = BubbleSort.returnToOriginalOrder(input);

    }


    @Test
    public void testReorderInteger(){

        int[] expected = new int[] {3,1,-0,-1,2};

        int[][] input = new int[2][5];
        //index of the original location in the array
        input[0] = new int[]{3,2,1,4,0};
        //sorted values of the orignal array
        input[1] = new int[]{-1,0,1,2,3};

        int[] testArray = null;
        testArray = BubbleSort.returnToOriginalOrder(input);

        Assert.assertArrayEquals(expected, testArray);
    }

    @Test(expected = RuntimeException.class )
    public void testReorderIntegerTwoFewElements(){

        int[][] input = new int[1][5];
        //index of the original location in the array
        input[0] = new int[]{3,2,1,4,0};

        int[] testArray = null;
        testArray = BubbleSort.returnToOriginalOrder(input);

    }

}