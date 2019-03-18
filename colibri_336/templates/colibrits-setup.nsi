; Script generated by the HM NIS Edit Script Wizard.

; HM NIS Edit Wizard helper defines
!define PRODUCT_NAME "ColibriTS"
!define PRODUCT_VERSION "1.6.0_384"
!define PRODUCT_PUBLISHER "Christian Eugster"
!define PRODUCT_UNINST_KEY "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_NAME}"
!define PRODUCT_UNINST_ROOT_KEY "HKLM"

; MUI 1.67 compatible ------
!include "MUI.nsh"

; MUI Settings
!define MUI_ABORTWARNING
!define MUI_ICON "${NSISDIR}\Contrib\Graphics\Icons\modern-install.ico"
!define MUI_UNICON "${NSISDIR}\Contrib\Graphics\Icons\modern-uninstall.ico"

; Welcome page
!insertmacro MUI_PAGE_WELCOME
; License page
;!define MUI_LICENSEPAGE_RADIOBUTTONS
;!insertmacro MUI_PAGE_LICENSE "ColibriTS\LICENSE-2.0.txt"
; Directory page
!insertmacro MUI_PAGE_DIRECTORY
; Instfiles page
!insertmacro MUI_PAGE_INSTFILES
; Finish page
!insertmacro MUI_PAGE_FINISH
!define MUI_FINISHPAGE_RUN '"$INSTDIR\runtime\jre6\bin\javaw.exe"'
!define MUI_FINISHPAGE_RUN_PARAMETERS '-Duser.timezone="runtime\jre6\lib\zi\Europe\Berlin" -jar "$INSTDIR\admin.jar"'

; Uninstaller pages
!insertmacro MUI_UNPAGE_INSTFILES

; Language files
!insertmacro MUI_LANGUAGE "German"

Name "${PRODUCT_NAME} ${PRODUCT_VERSION}"
OutFile "${PRODUCT_NAME}-${PRODUCT_VERSION}-setup.exe"
InstallDir "$PROGRAMFILES\ColibriTS"
ShowInstDetails show
ShowUnInstDetails show

Section "Hauptgruppe" SEC01
  SetOverwrite ifnewer
  SetOutPath "$INSTDIR"
  
  ; jars to run
  File ..\..\colibri_336\build\source\*.*

  ; source code
  SetOutPath $INSTDIR\src
  File /r /x .* ..\..\colibri_336\src\*.*
  ; database scripts
  SetOutPath $INSTDIR\db
  File /r /x .* ..\..\colibri_336\db\*.*
  ; documentation
  SetOutPath $INSTDIR\doc
  File /r /x .* ..\..\colibri_336\doc\*.*
  ; icons
  SetOutPath $INSTDIR\icons
  File /r /x .* ..\..\colibri_336\icons\*.*
  ; configuration
  SetOutPath $INSTDIR\properties
  File /r /x .* ..\..\colibri_336\properties\*.*
  ; readme
  SetOutPath $INSTDIR\readme
  File /r /x .* ..\..\colibri_336\readme\*.*
  ; reports
  SetOutPath $INSTDIR\reports
  File /r /x .* ..\..\colibri_336\reports\*.*
  ; java runtime (currently jre6
  SetOutPath $INSTDIR\runtime\jre6
  File /r /x .* ..\..\colibri_336\runtime\jre6\*.*
  SetOutPath $INSTDIR\runtime\jre6\bin
  File /r /x .* ..\..\colibri_336\runtime\jre6\bin\*.*
  SetOutPath $INSTDIR\runtime\jre6\lib
  File /r /x .* ..\..\colibri_336\runtime\jre6\lib\*.*
  SetOutPath $INSTDIR\runtime\jre6\lib\ext
  File /r /x .* ..\..\colibri_336\runtime\jre6\lib\ext\*.*
  File /x .* ..\..\colibri_336\lib\*.*
  SetOutPath $INSTDIR\runtime\jre6\bin
  File ..\..\colibri_336\javac.exe
  File ..\..\colibri_336\swt-win32-2136.dll
  File ..\..\colibri_336\swt-win32-3139.dll
  File ..\..\colibri_336\win32com.dll
  SetOutPath $INSTDIR\win32
  File /r /x .* ..\..\colibri_336\win32\com4j-x86.dll

  CreateDirectory "$INSTDIR\export"
  CreateDirectory "$INSTDIR\import"
  CreateDirectory "$INSTDIR\lock"
  CreateDirectory "$INSTDIR\log"
  CreateDirectory "$INSTDIR\save"

  SetOutPath "$INSTDIR"

  CreateShortCut "$STARTMENU.lnk" '"$INSTDIR\*"'

  CreateShortCut "$INSTDIR\ColibriTS Kassenprogramm.lnk" '"$INSTDIR\runtime\jre6\bin\javaw.exe" -Dswing.metalTheme=DefaultMetal -Duser.timezone="runtime/jre6/lib/zi/Europe/Berlin" -jar "$INSTDIR\colibri.jar"'
  CreateShortCut "$INSTDIR\ColibriTS Auswertungen.lnk" '"$INSTDIR\runtime\jre6\bin\javaw.exe" -Duser.timezone="Europe/Berlin" -jar "$INSTDIR\statistics.jar"'
  CreateShortCut "$INSTDIR\ColibriTS Administrator.lnk" '"$INSTDIR\runtime\jre6\bin\javaw.exe" -Duser.timezone="Europe/Berlin" -jar "$INSTDIR\admin.jar"'

  WriteUninstaller '"$INSTDIR\uninst.exe"'

SectionEnd

Section -AdditionalIcons
  SetShellVarContext "all"
  CreateDirectory "$SMPROGRAMS\ColibriTS"
  CreateShortCut "$SMPROGRAMS\ColibriTS\ColibriTS Kassenprogramm.lnk" '"$INSTDIR\runtime\jre6\bin\javaw.exe" -Dswing.metalTheme=DefaultMetal -Duser.timezone="Europe/Berlin" -jar "$INSTDIR\colibri.jar"'
  CreateShortCut "$SMPROGRAMS\ColibriTS\ColibriTS Auswertungen.lnk" '"$INSTDIR\runtime\jre6\bin\javaw.exe" -Duser.timezone="Europe/Berlin" -jar "$INSTDIR\statistics.jar"'
  CreateShortCut "$SMPROGRAMS\ColibriTS\ColibriTS Administrator.lnk" '"$INSTDIR\runtime\jre6\bin\javaw.exe" -Duser.timezone="Europe/Berlin" -jar "$INSTDIR\admin.jar"'
  CreateShortCut "$SMPROGRAMS\ColibriTS\Uninstall.lnk" '"$INSTDIR\uninst.exe"'
SectionEnd

Section -Post
  Exec 'regsvr32.exe /s "$INSTDIR\win32\com4j-x86.dll"'

  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "UninstallString" '"$INSTDIR\uninst.exe"'
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayVersion" "${PRODUCT_VERSION}"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "Publisher" "${PRODUCT_PUBLISHER}"
SectionEnd


Function un.onUninstSuccess
  HideWindow
  MessageBox MB_ICONINFORMATION|MB_OK "ColibriTS wurde erfolgreich deinstalliert."
FunctionEnd

Function un.onInit
  MessageBox MB_ICONQUESTION|MB_YESNO|MB_DEFBUTTON2 "M�chten Sie ColibriTS und alle seinen Komponenten deinstallieren?" IDYES +2
  Abort
FunctionEnd

Section Uninstall
  Delete "$INSTDIR\uninst.exe"
  Delete "$INSTDIR\*"

  Delete "$SMPROGRAMS\ColibriTS\Uninstall.lnk"
  Delete "$STARTMENU.lnk"

  RMDir "$SMPROGRAMS\ColibriTS"
  RMDir /r "$INSTDIR"

  Exec 'regsvr32.exe /s /u "$INSTDIR\win32\com4j-x86.dll"'

  DeleteRegKey ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}"
  SetAutoClose true
SectionEnd