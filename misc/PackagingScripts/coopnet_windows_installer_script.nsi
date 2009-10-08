!include "LogicLib.nsh"

; The name of the installer; Version gets replaced by ant
Name "CoopnetClient ${VERSION}"

; The file to write
OutFile "${OUTPUT_FILE}"

; The default installation directory
InstallDir $PROGRAMFILES\Coopnet

; Registry key to check for directory (so if you install again, it will 
; overwrite the old one automatically)
InstallDirRegKey HKLM "Software\Coopnet" "Install_Dir"

; Request application privileges for Windows Vista
RequestExecutionLevel admin

;--------------------------------

; Pages

Page directory
Page components
Page instfiles

UninstPage uninstConfirm
UninstPage instfiles

;--------------------------------

;show message to user

Function .onInit
ReadRegStr $0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" CurrentVersion
${If} $0 == ""
  MessageBox MB_OK "This software requires Java 1.6 or newer! Download it at http://www.java.com!"
${EndIf}
${If} $0 == "1.4"
  MessageBox MB_OK "This software requires Java 1.6 or newer! Download it at http://www.java.com!"
${EndIf}
${If} $0 == "1.5"
  MessageBox MB_OK "This software requires Java 1.6 or newer! Download it at http://www.java.com!"
${EndIf}
FunctionEnd

;-----------------------------------

; The stuff to install
Section "CoopnetClient (required)"

  SectionIn RO
  
  ; Set output path to the installation directory.
  SetOutPath $INSTDIR
  
  ; when creating the isntaller the nsi msut be on the same level of Coopnet folder which has the release files
  File /r Coopnet\*.*
  
  ; Write the installation path into the registry
  WriteRegStr HKLM SOFTWARE\Coopnet "Install_Dir" "$INSTDIR"
  
  ; Write the uninstall keys for Windows
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Coopnet" "DisplayName" "Coopnet Client"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Coopnet" "UninstallString" '"$INSTDIR\uninstall.exe"'
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Coopnet" "NoModify" 1
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Coopnet" "NoRepair" 1
  WriteUninstaller "uninstall.exe"
  
SectionEnd

; Optional section (can be disabled by the user)
Section "Start Menu Shortcuts"

  CreateDirectory "$SMPROGRAMS\Coopnet"
  CreateShortCut "$SMPROGRAMS\Coopnet\Uninstall.lnk" "$INSTDIR\uninstall.exe" "" "$INSTDIR\uninstall.exe" 0
  CreateShortCut "$SMPROGRAMS\Coopnet\CoopnetClient.lnk" "$INSTDIR\CoopnetClient.jar" "" "$INSTDIR\coopnet.ico" 0
  CreateShortCut "$SMPROGRAMS\Coopnet\CoopnetClient (Safe Mode).lnk" "$INSTDIR\CoopnetClient.jar" "--safemode" "$INSTDIR\coopnet.ico" 0
  
SectionEnd


; Optional section (can be disabled by the user)
Section "Desktop Shortcut"

  CreateShortCut "$DESKTOP\CoopnetClient.lnk" "$INSTDIR\CoopnetClient.jar" "" "$INSTDIR\coopnet.ico" 0
  
SectionEnd

;--------------------------------

; Uninstaller

Section "Uninstall"
  
  ; Remove registry keys
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Coopnet"
  DeleteRegKey HKLM SOFTWARE\Coopnet

  ; Remove installation

  ;remove install-root folder files
  Delete "$INSTDIR\CHANGELOG.txt"
  Delete "$INSTDIR\coopnet.ico"
  Delete "$INSTDIR\CoopnetClient.jar"
  Delete "$INSTDIR\COPYRIGHT.txt"
  Delete "$INSTDIR\debug.bat"
  Delete "$INSTDIR\debug.sh"
  Delete "$INSTDIR\LICENSE.txt"
  Delete "$INSTDIR\README.txt"
  Delete "$INSTDIR\CoopnetUpdater.jar"
  Delete "$INSTDIR\uninstall.exe"

  ;remove all data
  RMDir  /r "$INSTDIR\lib"
  RMDir  /r "$INSTDIR\data"
  RMDir  /r "$INSTDIR\UPDATER_TMP"
  ;remove install dir if possible
  RMDir "$INSTDIR"
  
  ; Remove start menu shortcuts, if any
  RMDir /r "$SMPROGRAMS\Coopnet"
  Delete "$DESKTOP\Coopnet Client.lnk"
  
SectionEnd
