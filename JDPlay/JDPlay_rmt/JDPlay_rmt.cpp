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

/* JDPlay_rmt.cpp
 *
 *	This is a remote controlled program for launching DirectPlay games via RippleLaunch.
 *	It is supposed to give the functionality of the JDPLay_jni.dll with an environment like wine or cedega.
 *
 */

/****************************************************************************************************************/

#include "..\\JDPlay\\JDPlay.h"
#include <string>
#include <sstream>
#include <conio.h>

using namespace std;

// *** Variable declarations ***
char* playername;
char* gameGUID;
char* hostIP;
bool iamhost;
bool enableDebug;
bool doSearch;
int maxSearchRetries;

JDPlay* jdplay;

// *** Method declarations ***
void waitForCommand();
void launch();
void readArgs(int argc, char* argv[]);
void printHelp();

// *** Method implementations ***
int main(int argc, char* argv[]){
	//Read args
	readArgs(argc, argv);

	//Init JDPlay
	jdplay = new JDPlay(playername, gameGUID, hostIP, iamhost, enableDebug);

	//Check init
	if(jdplay->isInitializedProperly()){
		if(doSearch){
			//Set maxSearchRetries
			jdplay->setMaxSearchRetries(maxSearchRetries);
		}
		
		waitForCommand();
	}else{
		cout << "ERR init"<< endl;
		fflush(stdout);
	}
}

void waitForCommand(){
	//Flush stdin
	while(kbhit()){
		_getch();
	}
	
	//Now ready for command
	cout << "RDY" << endl;
	fflush(stdout);

	//Read the command
	char in[256]; //Dunno why 256 here, but it should be enough
	gets(in);

	string input(in);

	if(!input.compare("LAUNCH")){
		//Launch game
		cout << "ACK" << endl;
		fflush(stdout);
		launch();
	}else
	if(!input.substr(0,11).compare("PLAYERNAME ")){
		//Set new playername
		cout << "ACK" << endl;
		fflush(stdout);
		string newname = input.substr(11, input.length());
		strcpy(playername, newname.c_str());
	}else{
		//Unknown command
		cout << "NAK" << endl;
		fflush(stdout);
	}

	waitForCommand();
}

void launch(){
	bool ret = jdplay->launch(doSearch);
	if(!ret){
		cout << "ERR launch" << endl;
		fflush(stdout);
	}else{
		cout << "FIN" << endl;
		fflush(stdout);
	}

	waitForCommand();
}

void readArgs(int argc, char* argv[]){
	//Set default settings
	hostIP = "127.0.0.1";
	iamhost = true;
	enableDebug = false;
	doSearch = false;
	maxSearchRetries = 5;

	//Go through arguments
	bool playerfound = false;
	bool gamefound = false;

	for(int i = 0; i < argc; i++){
		if(!strcmp(argv[i], "--player")){
			if(!playerfound){
				playerfound = true;
				if(argc > i+1){
					playername = argv[i+1];
					i++;
				}else{
					cout << "ERROR: --player was used without a <VALUE>" << endl;
					fflush(stdout);
					printHelp();
					exit(1);
				}
			}else{
				cout << "ERROR: --player was given more than once" << endl;
				fflush(stdout);
				printHelp();
				exit(1);
			}
		}else
		if(!strcmp(argv[i], "--game")){
			if(!gamefound){
				gamefound = true;
				if(argc > i+1){
					gameGUID = argv[i+1];
					i++;
				}else{
					cout << "ERROR: --game was used without a <VALUE>" << endl;
					fflush(stdout);
					printHelp();
					exit(1);
				}
			}else{
				cout << "ERROR: --game was given more than once" << endl;
				fflush(stdout);
				printHelp();
				exit(1);
			}
		}else
		if(!strcmp(argv[i], "--host")){
			if(argc > i+1){
				iamhost = false;
				hostIP = argv[i+1];
				i++;
			}else{
				cout << "ERROR: --host was used without a <VALUE>" << endl;
				fflush(stdout);
				printHelp();
				exit(1);
			}
		}else
		if(!strcmp(argv[i], "--search")){
			if(argc > i+1){
				doSearch = true;
				//Convert char* to int
				istringstream istr(argv[i+1]);
				if(!(istr >> maxSearchRetries)){
					cout << "ERROR: the <VALUE> of --search is not a number" << endl;
					fflush(stdout);
				}
				i++;
			}else{
				cout << "ERROR: --search was used without a <VALUE>" << endl;
				fflush(stdout);
				printHelp();
				exit(1);
			}
		}else
		if(!strcmp(argv[i], "--help")){
			printHelp();
			exit(0);
		}else
		if(!strcmp(argv[i], "--debug")){
			enableDebug = true;
		}
	}

	//Check for missing arguments
	if(!playerfound){
		cout << "ERROR: --player was not given" << endl;
		fflush(stdout);
		printHelp();
		exit(1);
	}
	if(!gamefound){
		cout << "ERROR: --game was not given" << endl;
		fflush(stdout);
		printHelp();
		exit(1);
	}
}

void printHelp(){
	cout << endl << "JDPlay_rmt usage:" << endl
	     << "    JDPlay_rmt.exe --player <NAME> --game <GUID> [--host <IP>]" << endl 
		 << "                   [--search <MAXRETRIES>] [--debug]" << endl
		 << endl
		 << "    --help          print this help and exit" << endl
		 << "    --player        the name of the player" << endl
		 << "    --game          the GUID of the game to launch" << endl
		 << "    --host          the IP of the Host" << endl
		 << "    --search        search for a sesson to join before launching" << endl
		 << "                        default for <MAXRETRIES> is 5" << endl
		 << "    --debug         print debug messages" << endl
		 << endl
		 << "This is a remote controlled program for launching DirectPlay games via RippleLaunch. "
		 << "It is supposed to give the functionality of the JDPLay_jni.dll with an environment like wine or cedega." << endl
		 << endl
		 << "Example for hosting:" << endl
		 << "  JDPlay_rmt.exe --player <NAME> --game <GUID>" << endl
		 << "Example for joining with instant launch:" << endl
		 << "  JDPlay_rmt.exe --player <NAME> --game <GUID> --host <IP>" << endl
		 << "Example for joining with lobby search:" << endl
		 << "  JDPlay_rmt.exe --player <NAME> --game <GUID> --host <IP> --search <MAXRETRIES>" << endl
		 << endl
		 << "Explanation of remote control:" << endl
		 << "  # JDPlay was started and is initialized properly" << endl
		 << "    OUT: RDY" << endl
		 << "  # remote app wants to launch the game" << endl
		 << "    IN:  LAUNCH" << endl
		 << "  # JDPlay understood the command and launches" << endl
		 << "    OUT: ACK" << endl
		 << "  # game has been closed" << endl
		 << "    OUT: FIN" << endl
		 << "    OUT: RDY" << endl
		 << "  # now, the game could be relaunched by sending LAUNCH again" << endl
		 << endl
		 << "  # there's also the possibility to change the playername after a RDY" << endl
		 << "    OUT: RDY" << endl
		 << "    IN:  PLAYERNAME <NAME>" << endl
		 << "    OUT: ACK" << endl
		 << "    OUT: RDY" << endl
		 << endl
		 << "  # this happens when gibberish is read" << endl
		 << "    OUT: RDY" << endl
		 << "    IN:  jsdakfsdfkh" << endl
		 << "    OUT: NAK" << endl
		 << "    OUT: RDY" << endl
		 << endl
		 << "  # errors are printed like this" << endl
		 << "    OUT: ERR <MESSAGE>" << endl
		 << endl
		 << "Every command ends with and is recognized after an endline (\\n). Commands have to be written in UPPERCASE. "
		 << "Before a RDY is written, stdin gets flushed, so theres no possibility that old text is read. JDPlay may print stuff that can be ignored while remote controlling."
		 << "To launch a different game or to connect to a different host, JDPlay has to be restarted with different arguments. "
		 << endl;
	fflush(stdout);
}
