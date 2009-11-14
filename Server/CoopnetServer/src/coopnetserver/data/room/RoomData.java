package coopnetserver.data.room;

import java.util.HashMap;

public class RoomData {
    
    private static HashMap<Long,Room> roomPool = new HashMap<Long,Room>();
    private static Long lastID;

    static{
        lastID = 0l;
    }

    public static void registerRoom(Room room){
        if(!roomPool.containsValue(room)){
            Long ID = generateID();
            room.setID(ID);
            roomPool.put(ID,room);
        }
    }
    
    public static Room getRoomByID(Long ID){
        return roomPool.get(ID);
    }
    
    public static void unRegisterRoom(Room room){
        roomPool.remove(room.getID());
    }
    
    private static Long generateID(){
        long newID = lastID++;
        return newID;
    }
    
}
