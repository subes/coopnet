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

/* JDPlay.cpp
 *
 *	JDPlay, a class to the DirectPlay RippleLaunch technology for easy use.
 *
 *	Needs to be linked to: "dplayx.lib dxguid.lib"
 */

/****************************************************************************************************************/

#include "JDPlay.h"

DPSESSIONDESC2 JDPlay::dpsDesc;
DPLCONNECTION JDPlay::dpConn;
bool JDPlay::foundLobby;

JDPlay::JDPlay(char* playerName, char* gameGUID, char* hostIP, bool iamhost, bool enableDebug){

	debug = enableDebug;

	time_t tStart = NULL;

	if(debug){
		tStart = time(NULL);
		clock_t tClock = clock();
		cout << "JDPlay: [DEBUG@obj@sec:" << (tClock/CLOCKS_PER_SEC) << "] ++ started init ++" << endl;
		fflush(stdout);
		ost.open("jdplay_last.log");
		ost << "JDPlay: [DEBUG@obj@sec:" << (tClock/CLOCKS_PER_SEC) << "] ++ started init ++" << endl;
		ost.flush();
	}

	isInitialized = false;

	HRESULT hr;
	
	// create GUID Object **************************************************************************
	GUID gameID;
	
	USES_CONVERSION;
	LPOLESTR lpoleguid = A2OLE(gameGUID);
	hr = CLSIDFromString(lpoleguid, &gameID);

	if(hr != S_OK){
		if(debug){
			cout << "JDPlay: [ERROR] invalid GUID" << endl;
			fflush(stdout);
			ost << "JDPlay: [ERROR] invalid GUID" << endl;
			ost.flush();
		}
		return;
	}

	if(debug){
		time_t tNow = time(NULL);
		cout << "JDPlay: [DEBUG@func@sec:" << (tNow-tStart) << "] initialized GUID" << endl;
		fflush(stdout);
		ost << "JDPlay: [DEBUG@func@sec:" << (tNow-tStart) << "] initialized GUID" << endl;
		ost.flush();
	}
	
	// create TCP connection ***********************************************************************
	LPDIRECTPLAYLOBBYA old_lpdplobbyA = NULL;    // old lobby pointer
	DPCOMPOUNDADDRESSELEMENT  address[2];        // to create compound addr
	DWORD     addressSize = 0;					// size of compound address
	LPVOID    lpConnection = NULL;				// pointer to make connection

	// registering COM
	hr = CoInitialize(NULL);
	
	if(hr != S_OK){
		if(debug){
			cout << "JDPlay: [ERROR] failed to initialize COM" << endl;
			fflush(stdout);
			ost << "JDPlay: [ERROR] failed to initialize COM" << endl;
			ost.flush();
		}
		return;
	}

	if(debug){
		time_t tNow = time(NULL);
		cout << "JDPlay: [DEBUG@func@sec:" << (tNow-tStart) << "] initialized COM" << endl;
		fflush(stdout);
		ost << "JDPlay: [DEBUG@func@sec:" << (tNow-tStart) << "] initialized COM" << endl;
		ost.flush();
	}

	// creating directplay object
	hr = CoCreateInstance(CLSID_DirectPlay, NULL, CLSCTX_INPROC_SERVER, IID_IDirectPlay3A,(LPVOID*)&lpdp );
	
	if(hr != S_OK){
		if(debug){
			cout << "JDPlay: [ERROR] failed to initialize DirectPlay" << endl;
			fflush(stdout);
			ost << "JDPlay: [ERROR] failed to initialize DirectPlay" << endl;
			ost.flush();
		}
		return;
	}

	CoUninitialize();  // unregister the COM

	if(debug){
		time_t tNow = time(NULL);
		cout << "JDPlay: [DEBUG@func@sec:" << (tNow-tStart) << "] initialized DirectPlay and deinitialized COM" << endl;
		fflush(stdout);
		ost << "JDPlay: [DEBUG@func@sec:" << (tNow-tStart) << "] initialized DirectPlay and deinitialized COM" << endl;
		ost.flush();
	}

	// creating lobby object
	hr = DirectPlayLobbyCreate(NULL, &old_lpdplobbyA, NULL, NULL, 0);

	if(hr != S_OK){
		if(debug){
			cout << "JDPlay: [" << getDPERR(hr) <<"] failed to create lobby object" << endl;
			fflush(stdout);
			ost << "JDPlay: [" << getDPERR(hr) <<"] failed to create lobby object" << endl;
			ost.flush();
		}
		return;
	}

	// get new interface of lobby
	hr = old_lpdplobbyA->QueryInterface(IID_IDirectPlayLobby3A, (LPVOID *)&lpdplobby);
	
	if(hr != S_OK){
		if(debug){
			cout << "JDPlay: [" << getDPERR(hr) << "] failed to get new lobby interface" << endl;
			fflush(stdout);
			ost << "JDPlay: [" << getDPERR(hr) << "] failed to get new lobby interface" << endl;
			ost.flush();
		}
		return;
	}

	// release old interface since we have new one
	hr = old_lpdplobbyA->Release();

	if(hr != S_OK){
		if(debug){
			cout << "JDPlay: [" << getDPERR(hr) << "] failed to release old lobby interface" << endl;
			fflush(stdout);
			ost << "JDPlay: [" << getDPERR(hr) << "] failed to release old lobby interface" << endl;
			ost.flush();
		}
		return;
	}

	if(debug){
		time_t tNow = time(NULL);
		cout << "JDPlay: [DEBUG@func@sec:" << (tNow-tStart) << "] initialized lobby" << endl;
		fflush(stdout);
		ost << "JDPlay: [DEBUG@func@sec:" << (tNow-tStart) << "] initialized lobby" << endl;
		ost.flush();
	}

	// fill in data for address
	address[0].guidDataType = DPAID_ServiceProvider;
	address[0].dwDataSize   = sizeof(GUID);
	address[0].lpData       = (LPVOID)&DPSPGUID_TCPIP;  // TCP ID

	address[1].guidDataType = DPAID_INet;
	address[1].dwDataSize   = strlen(hostIP)+1;
	address[1].lpData       = hostIP;

	// get size to create address
	// this method will return DPERR_BUFFERTOOSMALL not an error
	hr = lpdplobby->CreateCompoundAddress(address, 2, NULL, &addressSize);

	if(hr != S_OK && hr != DPERR_BUFFERTOOSMALL){
		if(debug){
			cout << "JDPlay: [" << getDPERR(hr) << "] failed to get size for CompoundAddress" << endl;
			fflush(stdout);
			ost << "JDPlay: [" << getDPERR(hr) << "] failed to get size for CompoundAddress" << endl;
			ost.flush();
		}
		return;
	}

	lpConnection = GlobalAllocPtr(GHND, addressSize);  // allocating mem

	// now creating the address
	hr = lpdplobby->CreateCompoundAddress(address, 2, lpConnection, &addressSize);

	if(hr != S_OK){
		if(debug){
			cout << "JDPlay: [" << getDPERR(hr) << "] failed to create CompoundAddress" << endl;
			fflush(stdout);
			ost << "JDPlay: [" << getDPERR(hr) << "] failed to create CompoundAddress" << endl;
			ost.flush();
		}
		return;
	}

	// initialize the tcp connection
	hr = lpdp->InitializeConnection(lpConnection, 0);

	if(hr != S_OK){
		if(debug){
			cout << "JDPlay: [" << getDPERR(hr) << "] failed to initialize TCP connection" << endl;
			fflush(stdout);
			ost << "JDPlay: [" << getDPERR(hr) << "] failed to initialize TCP connection" << endl;
			ost.flush();
		}
		return;
	}

	if(debug){
		time_t tNow = time(NULL);
		cout << "JDPlay: [DEBUG@func@sec:" << (tNow-tStart) << "] initialized TCP connection" << endl;
		fflush(stdout);
		ost << "JDPlay: [DEBUG@func@sec:" << (tNow-tStart) << "] initialized TCP connection" << endl;
		ost.flush();
	}

	// create session description ****************************************************************** 
	ZeroMemory(&dpsDesc, sizeof(DPSESSIONDESC2));
	dpsDesc.dwSize = sizeof(DPSESSIONDESC2);
	dpsDesc.dwFlags = 0;									// optional: DPSESSION_MIGRATEHOST
	dpsDesc.guidApplication = gameID;						// Game GUID
	dpsDesc.guidInstance = gameID;							// ID for the session instance
	dpsDesc.lpszSessionName = NULL;			// ANSI name of the session
	dpsDesc.lpszSessionNameA = NULL;			// ANSI name of the session
	dpsDesc.dwMaxPlayers = 0;								// Maximum # players allowed in session
	dpsDesc.dwCurrentPlayers = 0;							// Current # players in session (read only)
	dpsDesc.lpszPasswordA = NULL;							// ANSI password of the session (optional)
	dpsDesc.dwReserved1 = 0;								// Reserved for future M$ use.
	dpsDesc.dwReserved2 = 0;
	dpsDesc.dwUser1 = 0;									// For use by the application
	dpsDesc.dwUser2 = 0;
	dpsDesc.dwUser3 = 0;
	dpsDesc.dwUser4 = 0;

	// create player object ************************************************************************
	ZeroMemory(&dpname,sizeof(DPNAME));		// clear out structure
	dpname.dwSize = sizeof(DPNAME);
	dpname.dwFlags = 0;						// Not used. Must be zero.
	dpname.lpszShortNameA = playerName;		// nickname of the user
	dpname.lpszLongNameA = playerName;

	// create connection info **********************************************************************
	ZeroMemory( &dpConn, sizeof(DPLCONNECTION) );
	dpConn.dwSize = sizeof(DPLCONNECTION);
	dpConn.lpSessionDesc = &dpsDesc;		// Pointer to session desc to use on connect
	dpConn.lpPlayerName = &dpname;			// Pointer to Player name structure
	dpConn.guidSP = DPSPGUID_TCPIP;			// GUID of the DPlay SP to use
	dpConn.lpAddress = lpConnection;		// Address for service provider
	dpConn.dwAddressSize = addressSize;		// Size of address data
	if(iamhost){
		dpConn.dwFlags = DPLCONNECTION_CREATESESSION;
	}else{
		dpConn.dwFlags = DPLCONNECTION_JOINSESSION;
	}

	if(debug){
		time_t tNow = time(NULL);
		cout << "JDPlay: [DEBUG@func@sec:" << (tNow-tStart) << "] initialized session info" << endl;
		fflush(stdout);
		ost << "JDPlay: [DEBUG@func@sec:" << (tNow-tStart) << "] initialized session info" << endl;
		ost.flush();
	}

	// set other vars
	if(iamhost){
		sessionFlags = DPOPEN_CREATE;
		playerFlags = DPPLAYER_SERVERPLAYER;
	}else{
		sessionFlags = DPOPEN_JOIN;
		playerFlags = 0;
	}

	maxRetries = 5;
	lpdpIsOpen = false;
	isInitialized = true;

	if(debug){
		clock_t tClock = clock();
		cout << "JDPlay: [DEBUG@obj@sec:" << (tClock/CLOCKS_PER_SEC) << "] -- finished init --" << endl;
		fflush(stdout);
		ost << "JDPlay: [DEBUG@obj@sec:" << (tClock/CLOCKS_PER_SEC) << "] -- finished init --" << endl;
		ost.flush();
	}

}

bool JDPlay::isInitializedProperly(){
	return isInitialized;
}

void JDPlay::setMaxSearchRetries(int maxRetries){

	this->maxRetries = maxRetries;
}

void JDPlay::setPlayerName(char* playerName){

	dpname.lpszShortNameA = playerName;
	dpname.lpszLongNameA = playerName;
}

BOOL FAR PASCAL EnumSessionsCallback(LPCDPSESSIONDESC2 lpThisSD, LPDWORD lpdwTimeOut, DWORD dwFlags, LPVOID lpContext){

	if(lpThisSD){
		//so that dplay also joins sessions created ingame
		JDPlay::dpsDesc.dwSize = lpThisSD->dwSize;
		JDPlay::dpsDesc.dwFlags = lpThisSD->dwFlags;
		JDPlay::dpsDesc.guidInstance = lpThisSD->guidInstance;
		JDPlay::dpsDesc.guidApplication = lpThisSD->guidApplication;
		JDPlay::dpsDesc.dwMaxPlayers = lpThisSD->dwMaxPlayers;
		JDPlay::dpsDesc.dwCurrentPlayers = lpThisSD->dwCurrentPlayers;
		JDPlay::dpsDesc.dwReserved1 = lpThisSD->dwReserved1;
		JDPlay::dpsDesc.dwReserved2 = lpThisSD->dwReserved2;
		JDPlay::dpsDesc.dwUser1 = lpThisSD->dwUser1;
		JDPlay::dpsDesc.dwUser2 = lpThisSD->dwUser2;
		JDPlay::dpsDesc.dwUser3 = lpThisSD->dwUser3;
		JDPlay::dpsDesc.dwUser4 = lpThisSD->dwUser4;
		JDPlay::dpsDesc.lpszSessionName = lpThisSD->lpszSessionName;
		JDPlay::dpsDesc.lpszSessionNameA = lpThisSD->lpszSessionNameA;
		JDPlay::dpsDesc.lpszPassword = lpThisSD->lpszPassword;
		JDPlay::dpsDesc.lpszPasswordA = lpThisSD->lpszPasswordA;
		
		JDPlay::dpConn.lpSessionDesc = &JDPlay::dpsDesc;

		JDPlay::foundLobby = true;
	}

  return 0;
}

bool JDPlay::launch(bool searchForSession){

	time_t tStart = NULL;

	if(debug){
		tStart = time(NULL);
		clock_t tClock = clock();
		cout << "JDPlay: [DEBUG@obj@sec:" << (tClock/CLOCKS_PER_SEC) << "] ++ started launch ++" << endl;
		fflush(stdout);
		ost << "JDPlay: [DEBUG@obj@sec:" << (tClock/CLOCKS_PER_SEC) << "] ++ started launch ++" << endl;
		ost.flush();
	}

	HRESULT hr;

	if(searchForSession){
		// join/host session ***************************************************************************
		if(lpdp && sessionFlags == DPOPEN_JOIN){
			if(debug){
				time_t tNow = time(NULL);
				cout << "JDPlay: [DEBUG@func@sec:" << (tNow-tStart) << "] searching for a session .";
				fflush(stdout);
				ost << "JDPlay: [DEBUG@func@sec:" << (tNow-tStart) << "] searching for a session .";
				ost.flush();
			}
			
			for(retry = 1; retry < maxRetries; retry++){
		
				if(debug){
					cout << ".";
					fflush(stdout);
					ost << ".";
					ost.flush();
				}

				hr = lpdp->EnumSessions(&dpsDesc, 0, EnumSessionsCallback, NULL, 0);
				if(hr != S_OK){
					if(debug){
						cout << endl << "JDPlay: [" << getDPERR(hr) << "] failed to enumerate sessions" << endl;
						fflush(stdout);
						ost << endl << "JDPlay: [" << getDPERR(hr) << "] failed to enumerate sessions" << endl;
						ost.flush();
					}
					return false;
				}

				if(foundLobby){
					break;
				}
			}

			if(debug){
				if(!foundLobby){
					cout << " FAILURE after " << retry << ". try!" << endl;
					fflush(stdout);
					ost << " FAILURE after " << retry << ". try!" << endl;
					ost.flush();
				}else{
					cout << " SUCCESS at " << retry << ". try!" << endl;
					fflush(stdout);
					ost << " SUCCESS at " << retry << ". try!" << endl;
					ost.flush();
				}
			}

			if(debug){
				time_t tNow = time(NULL);
				if(foundLobby){
					cout << "JDPlay: [DEBUG@func@sec:" << (tNow-tStart) << "] session found" << endl;
					fflush(stdout);
					ost << "JDPlay: [DEBUG@func@sec:" << (tNow-tStart) << "] session found" << endl;
					ost.flush();
				}else{
					cout << "JDPlay: [DEBUG@func@sec:" << (tNow-tStart) << "] search failed" << endl;
					fflush(stdout);
					ost << "JDPlay: [DEBUG@func@sec:" << (tNow-tStart) << "] search failed" << endl;
					ost.flush();
				}
			}

			if(!foundLobby){
				return false;
			}

		}

		if(lpdp){

			hr = lpdp->Open(&dpsDesc, sessionFlags | DPOPEN_RETURNSTATUS);
			if(hr != S_OK){
				if(debug){
					cout << "JDPlay: [" << getDPERR(hr) << "] failed to open DirectPlay session" << endl;
					fflush(stdout);
					ost << "JDPlay: [" << getDPERR(hr) << "] failed to open DirectPlay session" << endl;
					ost.flush();
				}
				return false;
			}

			lpdpIsOpen = true;

			// create player *******************************************************************************
			hr = lpdp->CreatePlayer(&dpid, &dpname, NULL, NULL, 0, playerFlags);

			if(hr != S_OK){
				if(debug){
					cout << "JDPlay: [" << getDPERR(hr) << "] failed to create local player" << endl;
					fflush(stdout);
					ost << "JDPlay: [" << getDPERR(hr) << "] failed to create local player" << endl;
					ost.flush();
				}
				return false;
			}

			if(debug){
				time_t tNow = time(NULL);
				cout << "JDPlay: [DEBUG@func@sec:" << (tNow-tStart) << "] opened session and initialized player" << endl;
				fflush(stdout);
				ost << "JDPlay: [DEBUG@func@sec:" << (tNow-tStart) << "] opened session and initialized player" << endl;
				ost.flush();
			}
		}
	}else{
		if(debug){
			time_t tNow = time(NULL);
			cout << "JDPlay: [DEBUG@func@sec:" << (tNow-tStart) << "] skipping session search" << endl;
			fflush(stdout);
			ost << "JDPlay: [DEBUG@func@sec:" << (tNow-tStart) << "] skipping session search" << endl;
			ost.flush();
		}
	}

	// release temporary directplay interface ***************************************************************
	if(lpdp){

		if(lpdpIsOpen){
			hr = lpdp->Close();		//close dplay interface
			if(hr != S_OK){
				if(debug){
					cout << "JDPlay: [" << getDPERR(hr) << "] failed to close DirectPlay interface" << endl;
					fflush(stdout);
					ost << "JDPlay: [" << getDPERR(hr) << "] failed to close DirectPlay interface" << endl;
					ost.flush();
				}
				return false;
			}
		}
		
		hr = lpdp->Release();	//release dplay interface
		if(hr != S_OK){
			if(debug){
				cout << "JDPlay: [" << getDPERR(hr) << "] failed to release DirectPlay interface" << endl;
				fflush(stdout);
				ost << "JDPlay: [" << getDPERR(hr) << "] failed to release DirectPlay interface" << endl;
				ost.flush();
			}
			return false;
		}
		
		lpdp = NULL;  // set to NULL, safe practice here

		if(debug){
			time_t tNow = time(NULL);
			cout << "JDPlay: [DEBUG@func@sec:" << (tNow-tStart) << "] deinitialized DirectPlay" << endl;
			fflush(stdout);
			ost << "JDPlay: [DEBUG@func@sec:" << (tNow-tStart) << "] deinitialized DirectPlay" << endl;
			ost.flush();
		}
	}

	// launch game *********************************************************************************
	hr = lpdplobby->RunApplication( 0, &appID, &dpConn, 0);
	
	if(hr != S_OK){
		if(debug){
			cout << "JDPlay: [" << getDPERR(hr) << "] failed to launch the game, maybe it's not installed properly" << endl;
			fflush(stdout);
			ost << "JDPlay: [" << getDPERR(hr) << "] failed to launch the game, maybe it's not installed properly" << endl;
			ost.flush();
		}
		return false;
	}

	if(debug){
		time_t tNow = time(NULL);
		cout << "JDPlay: [DEBUG@func@sec:" << (tNow-tStart) << "] started game, ProcessID = " << appID << endl;
		fflush(stdout);
		ost << "JDPlay: [DEBUG@func@sec:" << (tNow-tStart) << "] started game, ProcessID = " << appID << endl;
		ost.flush();
	}

	// wait until game exits ***********************************************************************
	HANDLE appHandle = OpenProcess(PROCESS_QUERY_INFORMATION, FALSE, appID);
	if(appHandle == NULL){
		if(debug){
			cout << "JDPlay: [ERROR] failed to open game process" << endl;
			fflush(stdout);
			ost << "JDPlay: [ERROR] failed to open game process" << endl;
			ost.flush();
		}
		return false;
	}

	DWORD exitCode = NULL;
	while(GetExitCodeProcess(appHandle, &exitCode)){   // process is running
		if(exitCode != STILL_ACTIVE){
			break;
		}
		Sleep(1000);		
	}

	if(debug){
		clock_t tClock = clock();
		cout << "JDPlay: [DEBUG@obj@sec:" << (tClock/CLOCKS_PER_SEC) << "] -- game closed, finished launch --" << endl;
		fflush(stdout);
		ost << "JDPlay: [DEBUG@obj@sec:" << (tClock/CLOCKS_PER_SEC) << "] -- game closed, finished launch --" << endl;
		ost.flush();
	}

	return true;
}

JDPlay::~JDPlay(){

	time_t tStart = NULL;

	if(debug){
		tStart = time(NULL);
		clock_t tClock = clock();
		cout << "JDPlay: [DEBUG@obj@sec:" << (tClock/CLOCKS_PER_SEC) << "] ++ started cleanup ++" << endl;
		fflush(stdout);
		ost << "JDPlay: [DEBUG@obj@sec:" << (tClock/CLOCKS_PER_SEC) << "] ++ started cleanup ++" << endl;
		ost.flush();
	}

	HRESULT hr;

	if(lpdp){

		if(lpdpIsOpen){
			hr = lpdp->Close();		//close dplay interface
			if(hr != S_OK){
				if(debug){
					cout << "JDPlay: [" << getDPERR(hr) << "] failed to close DirectPlay interface" << endl;
					fflush(stdout);
					ost << "JDPlay: [" << getDPERR(hr) << "] failed to close DirectPlay interface" << endl;
					ost.flush();
				}
			}
		}
		
		hr = lpdp->Release();	//release dplay interface
		if(hr != S_OK){
			if(debug){
				cout << "JDPlay: [" << getDPERR(hr) << "] failed to release DirectPlay interface" << endl;
				fflush(stdout);
				ost << "JDPlay: [" << getDPERR(hr) << "] failed to release DirectPlay interface" << endl;
				ost.flush();
			}
		}
		
		lpdp = NULL;  // set to NULL, safe practice here

		if(debug){
			time_t tNow = time(NULL);
			cout << "JDPlay: [DEBUG@func@sec:" << (tNow-tStart) << "] deinitialized DirectPlay" << endl;
			fflush(stdout);
			ost << "JDPlay: [DEBUG@func@sec:" << (tNow-tStart) << "] deinitialized DirectPlay" << endl;
			ost.flush();
		}
		
	}

	if(lpdplobby){
		
		hr = lpdplobby->Release(); //release lobby
		if(hr != S_OK){
			if(debug){
				cout << "JDPlay: [" << getDPERR(hr) << "] failed to release lobby interface" << endl;
				fflush(stdout);
				ost << "JDPlay: [" << getDPERR(hr) << "] failed to release lobby interface" << endl;
				ost.flush();
			}
		}
		lpdplobby = NULL;

		if(debug){
			time_t tNow = time(NULL);
			cout << "JDPlay: [DEBUG@func@sec:" << (tNow-tStart) << "] deinitialized lobby" << endl;
			fflush(stdout);
			ost << "JDPlay: [DEBUG@func@sec:" << (tNow-tStart) << "] deinitialized lobby" << endl;
			ost.flush();
		}
	}

	if(debug){
		clock_t tClock = clock();
		cout << "JDPlay: [DEBUG@obj@sec:" << (tClock/CLOCKS_PER_SEC) << "] -- finished cleanup --" << endl;
		fflush(stdout);
		ost << "JDPlay: [DEBUG@obj@sec:" << (tClock/CLOCKS_PER_SEC) << "] -- finished cleanup --" << endl;
		ost.close();
	}

}

char* JDPlay::getDPERR(HRESULT hr){
	if(DP_OK) return "DP_OK";
	if(DPERR_ALREADYINITIALIZED) return "DPERR_ALREADYINITIALIZED";
	if(DPERR_ACCESSDENIED) return "DPERR_ACCESSDENIED";
	if(DPERR_ACTIVEPLAYERS) return "DPERR_ACTIVEPLAYERS";
	if(DPERR_BUFFERTOOSMALL) return "DPERR_BUFFERTOOSMALL";
	if(DPERR_CANTADDPLAYER) return "DPERR_CANTADDPLAYER";
	if(DPERR_CANTCREATEGROUP) return "DPERR_CANTCREATEGROUP";
	if(DPERR_CANTCREATEPLAYER) return "DPERR_CANTCREATEPLAYER";
	if(DPERR_CANTCREATESESSION) return "DPERR_CANTCREATESESSION";
	if(DPERR_CAPSNOTAVAILABLEYET) return "DPERR_CAPSNOTAVAILABLEYET";
	if(DPERR_EXCEPTION) return "DPERR_CAPSNOTAVAILABLEYET";
	if(DPERR_GENERIC) return "DPERR_GENERIC";
	if(DPERR_INVALIDFLAGS) return "DPERR_INVALIDFLAGS";
	if(DPERR_INVALIDOBJECT) return "DPERR_INVALIDOBJECT";
	if(DPERR_INVALIDPARAMS) return "DPERR_INVALIDPARAMS";
	if(DPERR_INVALIDPLAYER) return "DPERR_INVALIDPLAYER";
	if(DPERR_INVALIDGROUP) return "DPERR_INVALIDGROUP";
	if(DPERR_NOCAPS) return "DPERR_NOCAPS";
	if(DPERR_NOCONNECTION) return "DPERR_NOCONNECTION";
	if(DPERR_NOMEMORY) return "DPERR_NOMEMORY";
	if(DPERR_OUTOFMEMORY) return "DPERR_OUTOFMEMORY";
	if(DPERR_NOMESSAGES) return "DPERR_NOMESSAGES";
	if(DPERR_NONAMESERVERFOUND) return "DPERR_NONAMESERVERFOUND";
	if(DPERR_NOPLAYERS) return "DPERR_NOPLAYERS";
	if(DPERR_NOSESSIONS) return "DPERR_NOSESSIONS";
	if(DPERR_PENDING) return "DPERR_PENDING";
	if(DPERR_SENDTOOBIG) return "DPERR_SENDTOOBIG";
	if(DPERR_TIMEOUT) return "DPERR_TIMEOUT";
	if(DPERR_UNAVAILABLE) return "DPERR_UNAVAILABLE";
	if(DPERR_UNSUPPORTED) return "DPERR_UNSUPPORTED";
	if(DPERR_BUSY) return "DPERR_BUSY";
	if(DPERR_USERCANCEL) return "DPERR_USERCANCEL";
	if(DPERR_NOINTERFACE) return "DPERR_NOINTERFACE";
	if(DPERR_CANNOTCREATESERVER) return "DPERR_CANNOTCREATESERVER";
	if(DPERR_PLAYERLOST) return "DPERR_PLAYERLOST";
	if(DPERR_SESSIONLOST) return "DPERR_SESSIONLOST";
	if(DPERR_UNINITIALIZED) return "DPERR_UNINITIALIZED";
	if(DPERR_NONEWPLAYERS) return "DPERR_NONEWPLAYERS";
	if(DPERR_INVALIDPASSWORD) return "DPERR_INVALIDPASSWORD";
	if(DPERR_CONNECTING) return "DPERR_CONNECTING";
	if(DPERR_CONNECTIONLOST) return "DPERR_CONNECTIONLOST";
	if(DPERR_UNKNOWNMESSAGE) return "DPERR_UNKNOWNMESSAGE";
	if(DPERR_CANCELFAILED) return "DPERR_CANCELFAILED";
	if(DPERR_INVALIDPRIORITY) return "DPERR_INVALIDPRIORITY";
	if(DPERR_NOTHANDLED) return "DPERR_NOTHANDLED";
	if(DPERR_CANCELLED) return "DPERR_CANCELLED";
	if(DPERR_ABORTED) return "DPERR_ABORTED";
	if(DPERR_BUFFERTOOLARGE) return "DPERR_BUFFERTOOLARGE";
	if(DPERR_CANTCREATEPROCESS) return "DPERR_CANTCREATEPROCESS";
	if(DPERR_APPNOTSTARTED) return "DPERR_APPNOTSTARTED";
	if(DPERR_INVALIDINTERFACE) return "DPERR_INVALIDINTERFACE";
	if(DPERR_NOSERVICEPROVIDER) return "DPERR_NOSERVICEPROVIDER";
	if(DPERR_UNKNOWNAPPLICATION) return "DPERR_UNKNOWNAPPLICATION";
	if(DPERR_NOTLOBBIED) return "DPERR_NOTLOBBIED";
	if(DPERR_SERVICEPROVIDERLOADED) return "DPERR_SERVICEPROVIDERLOADED";
	if(DPERR_ALREADYREGISTERED) return "DPERR_ALREADYREGISTERED";
	if(DPERR_NOTREGISTERED) return "DPERR_NOTREGISTERED";
	if(DPERR_AUTHENTICATIONFAILED) return "DPERR_AUTHENTICATIONFAILED";
	if(DPERR_CANTLOADSSPI) return "DPERR_CANTLOADSSPI";
	if(DPERR_ENCRYPTIONFAILED) return "DPERR_ENCRYPTIONFAILED";
	if(DPERR_SIGNFAILED) return "DPERR_SIGNFAILED";
	if(DPERR_CANTLOADSECURITYPACKAGE) return "DPERR_CANTLOADSECURITYPACKAGE";
	if(DPERR_ENCRYPTIONNOTSUPPORTED) return "DPERR_ENCRYPTIONNOTSUPPORTED";
	if(DPERR_CANTLOADCAPI) return "DPERR_CANTLOADCAPI";
	if(DPERR_NOTLOGGEDIN) return "DPERR_NOTLOGGEDIN";
	if(DPERR_LOGONDENIED) return "DPERR_LOGONDENIED";

	return "ERROR";
}
