/*	Copyright 2007  Edwin Stang (edwinstang@gmail.com), 
                    Kovacs Zolt (kovacs.zsolt.85@gmail.com)

    This file is part of CoopNet.

    CoopNet is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    CoopNet is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with CoopNet.  If not, see <http://www.gnu.org/licenses/>.
*/

package jdplay;

public class JDPlay extends Object {
    
    /*
     *  Don't extend anything of this in Java!
     */
    
    private static boolean dllIsLoaded = false;
    
    public JDPlay(String urlToDLL, String playerName, String gameGUID, String hostIP, boolean iamhost, boolean enableDebug) throws Error {
        if(!dllIsLoaded){
            try {
                System.loadLibrary(urlToDLL);
                dllIsLoaded = true;
            } catch (UnsatisfiedLinkError er) {
                throw new Error("[JDPLAY] ERROR: loading jdplay dll failed, function calls will result in UnsatisfieldLinkError!");
            }
        }
        
        implementation = new Long(__c0(playerName, gameGUID, hostIP, iamhost, enableDebug));
    }
    
    public JDPlay(String playerName, String gameGUID, String hostIP, boolean iamhost, boolean enableDebug) {
        implementation = new Long(__c0(playerName, gameGUID, hostIP, iamhost, enableDebug));
    }
    private native long __c0(String playerName, String gameGUID, String hostIP, boolean iamhost, boolean enableDebug);
 
    public boolean isInitializedProperly() {
        boolean __retval = false;
        __retval = __m0(implementation.longValue());
        return __retval;
    }
    private native boolean __m0(long __imp);

    public void setMaxSearchRetries(int maxRetries) {
        __m1(implementation.longValue(), maxRetries);
    }
    private native void __m1(long __imp, int maxRetries);

    public void setPlayerName(String playerName) {
        __m2(implementation.longValue(), playerName);
    }
    private native void __m2(long __imp, String playerName);

    public boolean launch(boolean searchForSession) {
        boolean __retval = false;
        __retval = __m3(implementation.longValue(), searchForSession);
        return __retval;
    }
    private native boolean __m3(long __imp, boolean searchForSession);

// cxxwrap ctor, do not use
    public JDPlay(Long __imp) { implementation = __imp; }
    protected Long implementation = null;
    protected boolean extensible = false;
    public Long getCxxwrapImpl() { return implementation; }
    
// override equals() from Object, compare the implementation value
    public boolean equals(Object o) {
        if (!(o instanceof JDPlay)) return false;
        return implementation.equals(((JDPlay)o).implementation);
    }
    
// override hashCode() from Object, return the implementation values hashCode()
    public int hashCode() { return implementation.hashCode(); }
    public void delete() {
        
        __d(implementation.longValue());
        implementation = null;
    }
    private native void __d(long __imp);
};
