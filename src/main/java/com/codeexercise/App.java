package com.codeexercise;

import com.codeexercise.model.GameMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

/**
 * A-Maze-ingly Retro Route Puzzle
 *
 */
public class App {
    private static final String SCENARIO_FILE = "files/scenario.txt";
    private static final String MAP_FILE = "files/map.xml";
    private static final ClassLoader CLASS_LOADER  = Thread.currentThread().getContextClassLoader();
    private static final Logger LOGGER = Logger.getLogger(CLASS_LOADER.getClass().getSimpleName());
    private static int MAX_STEPS = 10000; // for now
    private static int random = 0; // used by heuristic to get next room

    // package-private to allow unit testing
    static String startRoom;
    static final Set<String> items = new HashSet<>();
    static GameMap gameMap;

    public static void main(String[] args) {
        if (args.length == 1 && Integer.parseInt(args[0]) < MAX_STEPS) {
            MAX_STEPS = Integer.parseInt(args[0]);
        } else {
            LOGGER.warning("Running without limit on iterations; to set limit, run with: java -jar <jar-file> <valid-limit>");
        }

        // initialize startRoom and items fields
        loadScenario(SCENARIO_FILE);
        // initialize gameMap field
        loadMap(MAP_FILE);

        // run game with a copy of items
        final Set<String> itemsToFind = new HashSet<>();
        itemsToFind.addAll(items);
        play(itemsToFind);
    }

    // package-private to allow unit testing
    static void play(final Set<String> neededItems) {
        // defensive, get own copy of map model
        final Map<String, GameMap.Room> map = new HashMap<>();
        map.putAll(gameMap.getRooms());
        // check for valid start room
        if (!map.containsKey(startRoom)) return;

        int countItemsCollected = 0;
        int countSteps = 0;
        GameMap.Room room = map.get(startRoom);
        while (room != null && countSteps < MAX_STEPS) {
            System.out.printf("In the %s%n", room.getName());

            if (room.getObject() != null && neededItems.contains(room.getObject().getName())) {
                neededItems.remove(room.getObject().getName());
                System.out.printf("I collect the %s, that's item # %d !!!!!%n",
                        room.getObject().getName(), ++countItemsCollected);
            }

            // done?
            if (neededItems.isEmpty()) break;

            // marshall available next rooms and directions
            final List<String[]> availableNextSteps = new ArrayList<>();
            if (room.getEast() != null) availableNextSteps.add(new String[] {room.getEast(), "east"});
            if (room.getWest() != null) availableNextSteps.add(new String[] {room.getWest(), "west"});
            if (room.getNorth() != null) availableNextSteps.add(new String[] {room.getNorth(), "north"});
            if (room.getSouth() != null) availableNextSteps.add(new String[] {room.getSouth(), "south"});

            // pick next room using heuristic
            room = getNextRoom(room, availableNextSteps, map);
            if (room != null) {
                final String nextDirection = availableNextSteps.get(random)[1];
                System.out.printf("I go %s%n", nextDirection);
                countSteps++;
            }
        }
        // performance tuning use
        LOGGER.info(countSteps + " steps to find " + countItemsCollected + " items");
    }

    // heuristic: random, but avoid two rooms being visited alternately twice e.g. R -> S -> R, want to avoid -> S again
    private static GameMap.Room getNextRoom(final GameMap.Room room, final List<String[]> availableNextSteps,
                                            final Map<String, GameMap.Room> map) {
        int heuristicLimit = 40; // arbitrary, there are max 4 directions for next step so just use 4 * 10 for now
        GameMap.Room nextRoom = null;

        while (heuristicLimit > 0) {
            random = ThreadLocalRandom.current().nextInt(0, availableNextSteps.size());
            final String nextRoomId = availableNextSteps.get(random)[0];
            nextRoom = map.get(nextRoomId);
            if (!nextRoomId.equals(room.getLastRoomVisited()) ||
                    // defensive null check
                    nextRoom == null || !room.getId().equals(nextRoom.getLastRoomVisited()) ||
                    // limit reached so just take the pick
                    heuristicLimit == 1) {
                // ok got next room, first update model data
                room.setLastRoomVisited(nextRoomId);
                if (nextRoom != null) nextRoom.setLastRoomVisited(room.getId());
                break;
            }
            heuristicLimit--;
        }

        return nextRoom;
    }

    // package-private to allow unit testing
    static void loadScenario(final String fileName) {
        try (InputStream is = CLASS_LOADER.getResourceAsStream(fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            if ((startRoom = reader.readLine()) != null) {
                String line;
                while ((line = reader.readLine()) != null) {
                    items.add(line);
                }
            }
        } catch (IOException e) {
            LOGGER.severe("Snap! Exception thrown trying to read data file " + fileName);
            e.printStackTrace();
        }
    }

    // package-private to allow unit testing
    @SuppressWarnings("unchecked")
    static void loadMap(final String fileName) {
        ObjectMapper xmlMapper = new XmlMapper();
        try (InputStream is = CLASS_LOADER.getResourceAsStream(fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            final StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            gameMap = xmlMapper.readValue(sb.toString(), GameMap.class);

        } catch (IOException e) {
            LOGGER.severe("Snap! Exception thrown trying to read data file " + fileName);
            e.printStackTrace();
        }
    }
}
