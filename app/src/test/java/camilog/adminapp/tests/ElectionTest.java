package camilog.adminapp.tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import camilog.adminapp.elections.Candidate;
import camilog.adminapp.elections.Election;

/**
 * Created by stefano on 02-09-15.
 */
public class ElectionTest {
    private Election fullElection;
    private Election noServerElection;

    @Before
    public void setUp(){
        fullElection = new Election("Favourite Color?", "http://localhost:3030");
        noServerElection = new Election("Favorite animal?");
    }

    @Test
    public void testFullElection(){
        Assert.assertEquals("Favourite Color?", fullElection.getElectionName());
        Assert.assertEquals("http://localhost:3030", fullElection.getBBServer());

        Assert.assertEquals(0, fullElection.getNumberOfCandidates());
        Assert.assertFalse(fullElection.hasCandidates());

        fullElection.addCandidate(new Candidate());

        Assert.assertEquals(1, fullElection.getNumberOfCandidates());
        Assert.assertTrue(fullElection.hasCandidates());
    }

    @Test
    public void testNoServerElection(){
        Assert.assertTrue(noServerElection.getBBServer().isEmpty());
    }
}
