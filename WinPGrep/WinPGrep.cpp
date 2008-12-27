
/*	Copyright 2007  Edwin Stang (edwinstang@gmail.com),
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

#include <iostream>
#include <string.h>
#include <windows.h>
#include <psapi.h>
#include <stdio.h>
#include <tchar.h>

using namespace std;

bool processRunning(char* exeName);
void printHelp();

int main(int argc, char** argv) {

    if(argc <= 1 || !strcmp(argv[1], "--help")){
        printHelp();
        exit(1);
    }

    if(processRunning(argv[1])){
        return EXIT_SUCCESS;
    }else{
        return EXIT_FAILURE;
    }
}

bool processRunning(char* exeName){
    bool foundProcess = false;

    // Get the list of process identifiers.
    DWORD aProcesses[1024], cbNeeded, cProcesses;
    unsigned int i;

    if ( !EnumProcesses( aProcesses, sizeof(aProcesses), &cbNeeded ) )
        return false;

    // Calculate how many process identifiers were returned.
    cProcesses = cbNeeded / sizeof(DWORD);

    // Print the name and process identifier for each process.
    for ( i = 0; i < cProcesses; i++ ){
        if( aProcesses[i] != 0 ){
            TCHAR szProcessName[MAX_PATH] = TEXT("<unknown>");

            // Get a handle to the process.
            HANDLE hProcess = OpenProcess( PROCESS_QUERY_INFORMATION |
                                           PROCESS_VM_READ,
                                           FALSE, aProcesses[i] );

            // Get the process name.
            if (NULL != hProcess )
            {
                HMODULE hMod;
                DWORD cbNeeded;

                if ( EnumProcessModules( hProcess, &hMod, sizeof(hMod),
                     &cbNeeded) )
                {
                    GetModuleBaseName( hProcess, hMod, szProcessName,
                                       sizeof(szProcessName)/sizeof(TCHAR) );
                }
            }

            //Print PID
            if(!strcmpi(szProcessName, exeName)){
                cout << aProcesses[i] << endl;
                foundProcess = true;
            }

            CloseHandle( hProcess );
        }
    }

    return foundProcess;

}

void printHelp(){
    cout << endl << "WinPGrep usage:" << endl
         << "    winpgrep.exe <EXENAME>" << endl
         << endl
         << "Example:" << endl
         << "    winpgrep.exe BOS.exe" << endl
         << endl
         << "    --help          print this help and exit" << endl
         << endl
         << "This tool is similar to the linux tool pgrep," << endl
         << "though it is stripped down to the basic function." << endl
         << "It will try to find the given process and print its pid." << endl
         << "If the process specified is running, the return code will be 0." << endl
         << "If the process is not running, it will return 1." << endl
         << "That's all." << endl
         << endl;
    fflush(stdout);
}
