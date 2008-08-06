/*	Copyright 2007 	Edwin Stang (edwinstang@gmail.com)

    This file is part of JDPlay.

    JDPlay is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    JDPlay is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with JDPlay.  If not, see <http://www.gnu.org/licenses/>.
*/

/****************************************************************************************************************/

/* JDPlay.h
 *
 *	JDPlay, a class to the DirectPlay RippleLaunch technology for easy use.
 *
 *	Needs to be linked to: "dplayx.lib dxguid.lib"
 */

/****************************************************************************************************************/

//includes
#include <iostream>
#include <fstream>
#include <windows.h>
#include <windowsx.h>	//GlobalAllocPtr
#include <dplay.h>
#include <dplobby.h>
#include <comdef.h>
#include <atlbase.h>	//STRING TO GUID
#include <atlstr.h>
#include <time.h>

using namespace std;

//Don't know how to make it a member function of JDPlay
BOOL FAR PASCAL EnumSessionsCallback(LPCDPSESSIONDESC2 lpThisSD, LPDWORD lpdwTimeOut, DWORD dwFlags, LPVOID lpContext);

class JDPlay{
	public:
		static DPSESSIONDESC2	dpSessionDesc;		// session description
		static DPLCONNECTION	dpConn;				// connection description
		static bool				foundLobby;
	private:
		bool					debug;
		int						curRetry;
		int						maxSearchRetries;
		bool					isInitialized;
		bool					lpDPIsOpen;
		LPDIRECTPLAY3A			lpDP;        // directplay interface pointer
		LPDIRECTPLAYLOBBY3A		lpDPLobby;   // lobby interface pointer
		
		DPNAME					dpName;				// player description
		
		DPID					dPid;				// player ID (currently unused)
		DWORD					appID;				// game process ID
		DWORD					sessionFlags;	//either Host or Join Session
		DWORD					playerFlags;	//either Host or not

	public:
		JDPlay(char* playerName, int maxSearchRetries, bool debug);
		bool initialize(char* gameGUID, char* hostIP, bool isHost);
		void updatePlayerName(char* playerName);
		bool launch(bool searchForSession);
		~JDPlay();
	private:
		void deInitialize();
		char* getDPERR(HRESULT hr);
};
