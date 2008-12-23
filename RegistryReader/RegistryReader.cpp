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
#include <windows.h>
#include <string>
#include <conio.h>

#define MAX_DONE_COUNT 10

using namespace std;

char* readEntry(char* fullEntryPath);
void printHelp();

int main(int argc, char** argv) {

    if(argc > 1){
        printHelp();
        exit(1);
    }

    int doneCount = 0;

    while(true){

        //Flush stdin
        char prev = 'a';
        int sameCharCount = 0;
	while(_kbhit()){
            char cur = _getch();
            if(prev == cur){
                sameCharCount++;
                if(sameCharCount >= MAX_DONE_COUNT*5){
                    exit(1);
                }
            }else{
                prev = cur;
            }
	}

        char in[MAX_PATH];
        gets(in);

        string input(in);

        cout << input.length() << endl;
        fflush(stdout);

        if(!input.compare("DONE") || input.length() < 1){
            doneCount++;

            if(doneCount >= MAX_DONE_COUNT){
                exit(1);
            }
        }else{
            doneCount = 0;
            cout << readEntry(in) << endl;
            fflush(stdout);
        }

        gets(in);
        if(strcmp(in, "DONE")){
            cout << "Your forgot to write DONE after each command!" << endl;
            fflush(stdout);
        }
    }
    
    return (EXIT_SUCCESS);
}

char* readEntry(char* fullEntryPath) {

    // Convert to String
    string s_fullEntryPath(fullEntryPath);

    // Get HKEY
    string s_hKey = s_fullEntryPath.substr(0,
            s_fullEntryPath.find_first_of('\\'));

    HKEY hKey;

    if (!s_hKey.compare("HKEY_CLASSES_ROOT")) {
        hKey = HKEY_CLASSES_ROOT;
    } else
        if (!s_hKey.compare("HKEY_CURRENT_USER")) {
        hKey = HKEY_CURRENT_USER;
    } else
        if (!s_hKey.compare("HKEY_LOCAL_MACHINE")) {
        hKey = HKEY_LOCAL_MACHINE;
    } else
        if (!s_hKey.compare("HKEY_USERS")) {
        hKey = HKEY_USERS;
    } else
        if (!s_hKey.compare("HKEY_CURRENT_CONFIG")) {
        hKey = HKEY_CURRENT_CONFIG;
    } else
        if (!s_hKey.compare("HKEY_DYN_DATA")) {
        hKey = HKEY_DYN_DATA;
    } else
        if (!s_hKey.compare("HKEY_PERFORMANCE_DATA")) {
        hKey = HKEY_PERFORMANCE_DATA;
    } else {
        return "ERR invalid hkey";
    }

    // Get SUBKEY
    const char* subkey = s_fullEntryPath.substr(
            s_fullEntryPath.find_first_of('\\') + 1,
            s_fullEntryPath.find_last_of('\\') - s_fullEntryPath.find_first_of('\\') - 1
            ).c_str();

    // Get ENTRY
    const char* entry = s_fullEntryPath.substr(
            s_fullEntryPath.find_last_of('\\') + 1
            ).c_str();

    //do the actual reading
    HKEY final_hKey = 0;
    static char buf[MAX_PATH] = {0};
    DWORD dwType = 0;
    DWORD dwBufSize = sizeof (buf);

    if (RegOpenKey(hKey, subkey, &final_hKey) == ERROR_SUCCESS) {
        char* ret;

        dwType = REG_SZ;
        if (RegQueryValueEx(final_hKey, entry, 0, &dwType, (BYTE*) buf, &dwBufSize) == ERROR_SUCCESS) {
            ret = buf;
        } else {
            ret = "ERR entry not found";
        }
        RegCloseKey(final_hKey);

        return ret;
    } else {
        return "ERR path not found";
    }
}

void printHelp(){
    cout << endl << "RegistryReader usage:" << endl
         << "    registryreader.exe [--help]" << endl
         << endl
         << "    --help          print this help and exit" << endl
         << endl
         << "This is a remote controlled program for reading windows registry values." << endl
         << "To read registry values, type in the complete path of the entry like this:" << endl
         << endl
         << "Pattern: " << endl
         << "  <HKEY>\\<PATH>\\<ENTRY>" << endl
         << "Example: " << endl
         << "  HKEY_LOCAL_MACHINE\\Software\\14 Degrees East\\Fallout Tactics\\ProgramFolder" << endl
         << endl
         << "If the entry is found, the value will get printed." << endl
         << "If not, there will be an error message:" << endl
         << endl
         << "  ERR <DETAILS>" << endl
         << endl
         << "To ensure this application closes when the host application closes, you should print DONE after each command." << endl
         << "This is a fix for running this application from java via wine on linux and java closing unexpectedly." << endl
         << "Any command has to end with a newline." << endl
         << endl;
    fflush(stdout);
}





