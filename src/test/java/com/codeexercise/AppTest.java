package com.codeexercise;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.HashSet;
import java.util.Set;

import static com.codeexercise.App.items;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
    private static final String SCENARIO_TEST_FILE = "files/scenario-test.txt";
    private static final String MAP_TEST_FILE = "files/map-test.xml";
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }



    public void testLoadScenario() {
        App.loadScenario(SCENARIO_TEST_FILE);

        assertNotNull("scenario's start room is null", App.startRoom);
        assertEquals("Room2", App.startRoom);

        assertNotNull("items to be collected is null", items);
        assertTrue("number of items to be collected is not 2", items.size() == 2);
        assertTrue(items.contains("Item3"));
    }

    public void testLoadMap() {
        App.loadMap(MAP_TEST_FILE);

        assertNotNull("game map is null", App.gameMap);
        assertNotNull("rooms in gameMap is null", App.gameMap.getRooms());
        assertTrue("size of gameMap rooms is not 3", App.gameMap.getRooms().size() == 3);
        assertEquals("Item3", App.gameMap.getRooms().get("Room3").getObject().getName());
        assertEquals("Room2", App.gameMap.getRooms().get("Room3").getNorth());
    }

    public void testPlay() {
        App.loadScenario(SCENARIO_TEST_FILE);
        App.loadMap(MAP_TEST_FILE);
        Set<String> itemsToFind = new HashSet<>();
        itemsToFind.addAll(App.items);
        assertEquals(2, itemsToFind.size());

        App.play(itemsToFind);

        assertEquals(0, itemsToFind.size());
    }
}
