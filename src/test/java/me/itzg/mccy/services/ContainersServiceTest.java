package me.itzg.mccy.services;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Geoff Bourne
 * @since 1/8/2016
 */
public class ContainersServiceTest {

    @Test
    public void testScrubContainerName() throws Exception {
        assertEquals("It_s_the_best_", ContainersService.scrubContainerName("It's the best!"));
    }
}