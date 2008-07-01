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
		static DPSESSIONDESC2			dpsDesc;			// session description
		static DPLCONNECTION			dpConn;				// connection description
		static bool foundLobby;
	private:
		bool debug;
		bool isInitialized;
		int retry;
		int maxRetries;
		bool lpdpIsOpen;
		LPDIRECTPLAY3A			lpdp;        // directplay interface pointer
		LPDIRECTPLAYLOBBY3A		lpdplobby;   // lobby interface pointer
		
		DPNAME					dpname;				// player description
		DPID					dpid;				// player ID (currently unused)
		DWORD					appID;				// game process ID
		DWORD					sessionFlags;	//either Host or Join Session
		DWORD					playerFlags;	//either Host or not

		ofstream				ost;

	public:
		JDPlay(char* playerName, char* gameGUID, char* hostIP, bool iamhost, bool enableDebug);
		bool isInitializedProperly();
		void setMaxSearchRetries(int maxRetries);
		void setPlayerName(char* playerName);
		bool launch(bool searchForSession);
		~JDPlay();
	private:
		char* getDPERR(HRESULT hr);
};
