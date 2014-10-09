/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eqtlmappingpipeline.ase;

import cern.colt.list.tint.IntArrayList;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author molgenis
 */
public class AseMlePerGroupNGTest {

    private AseMlePerGroup mlePerGroup;
    private AseMle mle;
    

    public AseMlePerGroupNGTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod
    public void setUpMethod() throws Exception {
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
    }

    @Test
    public void testMethod() {

//        fail("The test case is a prototype.");
        final IntArrayList a1CountsGroup = new IntArrayList();
        final IntArrayList a2CountsGroup = new IntArrayList();
        final IntArrayList a1Counts = new IntArrayList();
        final IntArrayList a2Counts = new IntArrayList();

        final ArrayList sampleIds = new ArrayList<String>();
        final HashMap<String, ArrayList<String>> testMap = new HashMap<String, ArrayList<String>>();
        final ArrayList<String> sampleList1 = new ArrayList<String>();
        final ArrayList<String> sampleList2 = new ArrayList<String>();
   

        a1CountsGroup.add(10);  //brain
        a1CountsGroup.add(12);  //liver
        a1CountsGroup.add(10);  //liver
        a1CountsGroup.add(15);  //brain

        a2CountsGroup.add(20);  //brain
        a2CountsGroup.add(20);  //liver
        a2CountsGroup.add(22);  //liver
        a2CountsGroup.add(22);  //brain

        sampleIds.add("s1");
        sampleIds.add("s2");
        sampleIds.add("s6");
        sampleIds.add("s5");

        sampleList1.add("s1");
        sampleList1.add("s2"); 

        sampleList2.add("s3");
        sampleList2.add("s4");

        testMap.put("liver", sampleList1);
//        testMap.put("brain", sampleList2);

        final SamplesToGroups samplesToGroups = new SamplesToGroups(testMap);

        a1Counts.add(10);
        a1Counts.add(12);
//        a1Counts.add(10);
//        a1Counts.add(15);
        
        a2Counts.add(20);
        a2Counts.add(20);
//        a2Counts.add(22);
//        a2Counts.add(22);

     
        AseMlePerGroup aseMlePerGroup = new AseMlePerGroup(a1CountsGroup, a2CountsGroup, sampleIds, samplesToGroups);
        AseMle aseMle = new AseMle(a1Counts, a2Counts);
        
        assertEquals(aseMlePerGroup.getSumLikelihoodPerGroup(), aseMle.getMaxLikelihood(), 0.00001);
        assertEquals(aseMlePerGroup.getRatioD(), aseMle.getRatioD(), 0.00001);
        assertEquals(aseMlePerGroup.getRatioP(), aseMle.getRatioP(), 0.00001); 
    }

}
