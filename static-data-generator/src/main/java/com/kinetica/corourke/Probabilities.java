package com.kinetica.corourke;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;

public class Probabilities {
    /*
     The category_probabilities table indexes the categories with numbers increasing from zero to one, such
     that a random number can be compared
    */

    private ArrayList<BigDecimal> probabilities;
    private BigDecimal scale_max;

    public Probabilities() {
        probabilities = new ArrayList<>(); // relative probabilities of selection
        scale_max = new BigDecimal(BigInteger.ZERO); // sum of all probability intervals
    }

    /**
     * @param p
     * Add a selection probability to the list
     */
    public void add(BigDecimal p) {
        scale_max = scale_max.add(p, MathContext.DECIMAL128);
        probabilities.add(scale_max);
    }
    public void add(Integer i) {
        add(BigDecimal.valueOf(i));
    }
    public void add(Double d) {
        add(BigDecimal.valueOf(d));
    }

    public Integer get_random_index() {
        BigDecimal rand = scale_max.multiply(BigDecimal.valueOf(Math.random()));
        return get_index(rand);
    }

    // Mostly for testing purposes
    public Integer get_index(BigDecimal rand) {
        for (int p = 0; p < probabilities.size(); p++) {
            if (probabilities.get(p).compareTo(rand) == 1) { // means rand is less than this table entry
                return p;
            }
        }
        throw new IllegalArgumentException("Random number given exceeds sum of probabilities.");
    }
    public Integer get_index(Double rand) {
        return get_index(BigDecimal.valueOf(rand));
    }
    public Integer get_index(Integer i) {
        return get_index(BigDecimal.valueOf(i));
    }

    public String toString() {
        return Arrays.toString(probabilities.toArray());
    }
}
