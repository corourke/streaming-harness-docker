package com.github.corourke;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Maintains an array of probabilities or frequencies for which each index into the array should be
 * randomly chosen. For example, say we have the following list of car makes and frequencies:
 *
 *         Map<String, Integer> cars = new LinkedHashMap<>();
 *         cars.put("ford", 75);
 *         cars.put("bmw", 24);
 *         cars.put("ferrari", 1);
 *
 * Create a Probabilities instance and then populate it:
 *
 *         Probabilities car_frequencies = new Probabilities();
 *         cars.forEach((car, probability) -> car_frequencies.add(probability));
 *
 * Generate weighted indexes into the list:
 *
 *         int[] results = new int[3];
 *         for(int i=0; i<10000; i++) {
 *             Integer ri = car_frequencies.get_weighted_index();
 *             results[ri] += 1;
 *         }
 */
public class Probabilities {
    /*

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

    /**
     * @return An index into the
     */
    public Integer get_weighted_index() {
        BigDecimal rand = scale_max.multiply(BigDecimal.valueOf(Math.random()));
        return get_index(rand);
    }

    // Public mostly for testing purposes
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
