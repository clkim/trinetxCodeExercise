package com.codeexercise.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import javax.xml.bind.annotation.XmlElement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Authored by clkim.
 */
@JacksonXmlRootElement(localName = "map")
public class GameMap {
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "room")
    private List<Room> rooms = null;

    private Map<String, Room> roomsMap;
    public Map<String, Room> getRooms() {
        if (roomsMap == null && rooms != null) {
            roomsMap = new HashMap<>();
            rooms.forEach(r -> roomsMap.put(r.getId(), r));
        }
        return roomsMap;
    }

    @JacksonXmlRootElement
    public static class Room {
        @JacksonXmlProperty(isAttribute = true)
        private String id;

        @JacksonXmlProperty(isAttribute = true)
        private String name;

        @JacksonXmlProperty(isAttribute = true)
        private String north;

        @JacksonXmlProperty(isAttribute = true)
        private String south;

        @JacksonXmlProperty(isAttribute = true)
        private String east;

        @JacksonXmlProperty(isAttribute = true)
        private String west;

        @XmlElement
        @JacksonXmlProperty
        private GameObject object;

        private String lastRoomVisited;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNorth() {
            return north;
        }

        public void setNorth(String north) {
            this.north = north;
        }

        public String getSouth() {
            return south;
        }

        public void setSouth(String south) {
            this.south = south;
        }

        public String getEast() {
            return east;
        }

        public void setEast(String east) {
            this.east = east;
        }

        public String getWest() {
            return west;
        }

        public void setWest(String west) {
            this.west = west;
        }

        public GameObject getObject() {
            return object;
        }

        public void setObject(GameObject object) {
            this.object = object;
        }

        public String getLastRoomVisited() {
            return lastRoomVisited;
        }

        public void setLastRoomVisited(String lastRoomVisited) {
            this.lastRoomVisited = lastRoomVisited;
        }
    }

    @JacksonXmlRootElement(localName = "object")
    public static class GameObject {
        @JacksonXmlProperty(isAttribute = true)
        private String name;

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
