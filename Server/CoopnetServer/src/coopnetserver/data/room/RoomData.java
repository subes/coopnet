/*  Copyright 2007  Edwin Stang (edwinstang@gmail.com),
 *                  Kovacs Zsolt (kovacs.zsolt.85@gmail.com)
 *
 *  This file is part of Coopnet.
 *
 *  Coopnet is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Coopnet is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Coopnet.  If not, see <http://www.gnu.org/licenses/>.
 */
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
