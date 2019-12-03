package com.github.corourke;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;



class ProbabilitiesTest {

    private Probabilities test_prob;

    @BeforeEach
    void setUp() {
        test_prob = new Probabilities();
    }

    @Test
    void add_doubles_not_ordered() {
        test_prob.add(.75D);
        test_prob.add(.25D);
        assertEquals(test_prob.get_index(0.5D), 0);
        assertEquals(test_prob.get_index(0.8D), 1);
    }

    @Test
    void add_integers() {
        test_prob.add(60);
        test_prob.add(20);
        assertEquals(test_prob.get_index(40), 0);
        assertEquals(test_prob.get_index(70), 1);
    }

    @Test
    void check_distribution() {
        test_prob.add(20);
        test_prob.add(60);
        test_prob.add(20);
        int[] results = new int[3];
        for(int i=0; i<10000; i++) {
            Integer ri = test_prob.get_weighted_index();
            assertTrue(ri >= 0 && ri <= 2, "Expecting index to be 0, 1, 2 only");
            results[ri] += 1;
        }
        assertTrue(results[1] > 5900 && results[0] < 6100, "Expect distribution to be close to probability -- try running test again");
    }

    @Test
    void exception_on_large_rand() {
        assertThrows(IllegalArgumentException.class, () -> test_prob.get_index(150));
    }

    @Test
    void complete_example() {
        Map<String, Integer> cars = new LinkedHashMap<>();
        // Note probabilities do not need to add up to 100, just doing this to make results easy to validate
        cars.put("ford", 75);
        cars.put("bmw", 24);
        cars.put("ferrari", 1);

        Probabilities car_frequencies = new Probabilities();
        cars.forEach((car, probability) -> car_frequencies.add(probability));

        int[] results = new int[3];
        for(int i=0; i<10000; i++) {
            Integer ri = car_frequencies.get_weighted_index();
            results[ri] += 1;
        }

        assertTrue(results[2] > 85 && results[2] < 115,
                "Expect distribution of %1 of 10000 to be near 100, and got " + results[2]);

    }
}