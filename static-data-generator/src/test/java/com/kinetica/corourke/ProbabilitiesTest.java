package com.kinetica.corourke;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
            Integer ri = test_prob.get_random_index();
            assertTrue(ri >= 0 && ri <= 2, "Expecting index to be 0, 1, 2 only");
            results[ri] += 1;
        }
        assertTrue(results[1] > 5900 && results[0] < 6100, "Expect distribution to be close to probability -- try running test again");
    }

    @Test
    void exception_on_large_rand() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> test_prob.get_index(150));
    }
}