package com.serverless;

import org.junit.Test;

import static org.junit.Assert.*;

public class FractionalRatioSimplifierTest {

    @Test
    public void calculate_equal_right_multiple() {
        String result = FractionalRatioSimplifier.calculate("4/6", "2/3");
        assertEquals("4/6 : 2/3\n" +
                "4/6 : 4/6\n" +
                "1 : 1\n", result);
    }
    @Test
    public void calculate_equal_left_multiple() {
        String result = FractionalRatioSimplifier.calculate("2/3","4/6" );
        assertEquals("2/3 : 4/6\n" +
                "4/6 : 4/6\n" +
                "1 : 1\n", result);
    }
    @Test
    public void calculate_case_1() {
        String result = FractionalRatioSimplifier.calculate("1/3","2/3" );
        assertEquals("1/3 : 2/3\n" +
                "1 : 2\n", result);
    }
}