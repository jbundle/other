

/******************************************************************************\
*       This is a part of the Microsoft Source Code Samples. 
*       Copyright (C) 1993 Microsoft Corporation.
*       All rights reserved. 
*       This source code is only intended as a supplement to 
*       Microsoft Development Tools and/or WinHelp documentation.
*       See these sources for detailed information regarding the 
*       Microsoft samples programs.
\******************************************************************************/

//---------------------------------------------------------------------------
//
//  Module: tty.c
//
//  Purpose:
//     The sample application demonstrates the usage of the COMM
//     API.  It implements the new COMM API of Windows 3.1.
//
//     NOTE:  no escape sequences are translated, only
//            the necessary control codes (LF, CR, BS, etc.)
//
//  Description of functions:
//     Descriptions are contained in the function headers.
//
//---------------------------------------------------------------------------
//
//  Written by Microsoft Product Support Services, Windows Developer Support.
//  Copyright (c) 1991 Microsoft Corporation.  All Rights Reserved.
//
//---------------------------------------------------------------------------
#include "DGTerm.h"
#include "stdio.h"
#include "io.h"
//DonStart
#define false 0
#define true 1
#define Off 0
#define On 1

#define kCarriageReturn 13
#define kLineFeed 10
#define kEraseEOL 11
//#define kHome 8
#define kHomeAlso 30
//#define kClearScreen 12
#define kUp 23
#define kDown 26
#define kRight 24
#define kLeft 25
//#define kDimOn 28
//#define kDimOff 29
#define kPositionCursor 16
#define kFunctionHeader 30
#define kFunction1 33

#define kNull 0
#define kA1 1
#define kEndReverse 2
#define kA3 3
#define kA4 4
#define kReadCurPos 5
#define kA6 6
#define kBell 7
#define kHome 8
#define kTab 9
#define kNewLine 10
#define kEraseEol 11
#define kClearScreen 12
#define kCReturn 13
#define kStartBlink 14
#define kEndBlink 15
#define kPosCursor 16
#define kA17 17
#define kPageOff 18
#define kPageOn 19
#define kStartUnderline 20
#define kEndUnderline 21
#define kStartReverse 22
#define kUpCur 23
#define kRightCur 24
#define kLeftCur 25
#define kDownCur 26
#define kA27 27
#define kDimOn 28
#define kDimOff 29
#define kTermID1 30
#define kTermID2 67
#define kReverseOn2 68
#define kReverseOff2 69
#define kEndCur 1		// Ctrl A
#define kInsCur 5		// Ctrl E

#define kCHdr 30		// kFunctionHeader			// Hdr key for C1-C4
#define kC1Key 92		//25
#define kC2Key 93		// ]
#define kC3Key 94		//24
#define kC4Key 95		// _

#define kFuncHdr 30
#define kFun0 113
#define kFun1 97
#define kFun2 49
#define kFun3 33
		
#define kCurPosHdr 31
#define kFirstRowChar 0
#define kFirstColChar 0
#define kTermIDHdr 30
		
#define kDelete 127
#define kEscape 27
		
#define kReturnKey 13
#define kEnterKey 3
#define kClearKey 23	// {Actually, this key also produces Esckey...27}
#define kRightArrow 29
#define kLeftArrow 28
#define kDownArrow 31		
#define kUpArrow 30
#define kEscKey 27
#define kDeleteKey 8
		
#define kSOH 1
#define kEOT 4
#define kACK 6
#define kNAK 21
#define kCAN 24
#define kFIL 26		// Filler bytes

#define kNAM 2		// File Name in next block

#define USE_WIN_95	// Windows 95 Colors and labels

const COLORREF kWhite = RGB(255, 255, 255);
const COLORREF kLightGray = RGB(192, 192, 192);
const COLORREF kDarkGray = RGB(128, 128, 128);
const COLORREF kBlack = RGB(0, 0, 0);
const COLORREF kRed = RGB(255, 0, 0);
const COLORREF kDarkRed = RGB(128, 0, 0);
const COLORREF kGreen = RGB(0, 255, 0);
const COLORREF kDarkGreen = RGB(0, 128, 0);
const COLORREF kBlue = RGB(0, 0, 255);
const COLORREF kDarkBlue = RGB(0, 0, 128);
const COLORREF kCyan = RGB(0, 255, 255);
const COLORREF kDarkCyan = RGB(0, 128, 128);
const COLORREF kMagenta = RGB(255, 0, 255);
const COLORREF kDarkMagenta = RGB(128, 0, 128);
const COLORREF kYellow = RGB(255, 255, 0);
const COLORREF kDarkYellow = RGB(128, 128, 0);

#define kTerminalMode 0
#define kXSendMode 1
#define kXReceiveMode 2

DWORD gfXFerMode = kTerminalMode;
long gfBlock;
DWORD gnNAKCount;
HFILE ghFile;
char gszCharacterIn[128+10];
DWORD gnCharacterCount;
HWND ghDlg = NULL;
BOOL gKeepReceiving = FALSE;
NPTTYINFO glpTTYInfo = NULL;
//DonEnd

#define WIN95_THREAD_HACK	// Win95 Doesn't term threads correctly
#ifdef WIN32
#define REG_FONT_BINARY		// Register entire LOGFONT structure
#endif
//---------------------------------------------------------------------------
//  int PASCAL WinMain( HANDLE hInstance, HANDLE hPrevInstance,
//                      LPSTR lpszCmdLine, int nCmdShow )
//
//  Description:
//     This is the main window loop!
//
//  Parameters:
//     As documented for all WinMain() functions.
//
//  History:   Date       Author      Comment
//              5/ 8/91   BryanW      Wrote it.
//
//---------------------------------------------------------------------------

int PASCAL WinMain( HINSTANCE hInstance, HINSTANCE hPrevInstance,
                    LPSTR lpszCmdLine, int nCmdShow )
{
   HWND  hTTYWnd ;
   MSG   msg ;

   if (!hPrevInstance)
      if (!InitApplication( hInstance ))
         return ( FALSE ) ;

   if (NULL == (hTTYWnd = InitInstance( hInstance, nCmdShow )))
      return ( FALSE ) ;

   while (GetMessage( &msg, NULL, 0, 0 ))
   {
      if (!TranslateAccelerator( hTTYWnd, ghAccel, &msg ))
      {
         TranslateMessage( &msg ) ;
         DispatchMessage( &msg ) ;
      }
   }
   return ( (int) msg.wParam ) ;

} // end of WinMain()

//---------------------------------------------------------------------------
//  BOOL NEAR InitApplication( HANDLE hInstance )
//
//  Description:
//     First time initialization stuff.  This registers information
//     such as window classes.
//
//  Parameters:
//     HANDLE hInstance
//        Handle to this instance of the application.
//
//  History:   Date       Author      Comment
//              5/ 8/91   BryanW      Wrote it.
//             10/22/91   BryanW      Fixed background color problem.
//
//---------------------------------------------------------------------------

BOOL NEAR InitApplication( HANDLE hInstance )
{
   WNDCLASS  wndclass ;

   // register tty window class

   wndclass.style =         0 ;
   wndclass.lpfnWndProc =   TTYWndProc ;
   wndclass.cbClsExtra =    0 ;
   wndclass.cbWndExtra =    TTYEXTRABYTES ;
   wndclass.hInstance =     hInstance ;
   wndclass.hIcon =         LoadIcon( hInstance, MAKEINTRESOURCE( TTYICON ) );
   wndclass.hCursor =       LoadCursor( NULL, IDC_ARROW ) ;
   wndclass.hbrBackground = (HBRUSH) (COLOR_WINDOW + 1) ;
   wndclass.lpszMenuName =  MAKEINTRESOURCE( TTYMENU ) ;
   wndclass.lpszClassName = gszTTYClass ;

   return( RegisterClass( &wndclass ) ) ;

} // end of InitApplication()


//---------------------------------------------------------------------------
//  HWND NEAR InitRegistration( HANDLE hInstance, int nCmdShow )
//
//  Description:
//     Initializes instance specific information.
//
//  Parameters:
//     HANDLE hInstance
//        Handle to instance
//
//     int nCmdShow
//        How do we show the window?
//
//  History:   Date       Author      Comment
//              5/ 8/91   BryanW      Wrote it.
//
//---------------------------------------------------------------------------
#include <ShellAPI.h>
BOOL NEAR InitRegistration( NPTTYINFO npTTYInfo )
{
	HKEY hkProtocol;
	char szBuff[80];
	LONG cb;
#ifdef REG_FONT_BINARY
	DWORD dwType;
	BYTE* prgbyte;
	DWORD  dwDisposition;
#endif
	long lErrorCode;
	
#ifdef WIN32
	dwDisposition = 0;
	if (RegCreateKeyEx(HKEY_LOCAL_MACHINE,             /* root            */
	    "Software\\Superior Software\\DGTerm", /* protocol string */
    	0,	// reserved 
    "",	// address of class string 
    REG_OPTION_NON_VOLATILE,	// special options flag 
    KEY_ALL_ACCESS,	// desired security access 
    NULL,	// address of key security structure 
    &hkProtocol,	// address of buffer for opened handle  
    &dwDisposition) != ERROR_SUCCESS)          /* protocol key handle */
	        return FALSE;
#else
	if (RegCreateKey(HKEY_LOCAL_MACHINE,             /* root            */
	    "Software\\Superior Software\\DGTerm", /* protocol string */
	    &hkProtocol) != ERROR_SUCCESS)          /* protocol key handle */
	        return FALSE;
#endif

    cb = sizeof(szBuff);
#ifdef WIN32
    if (RegQueryValueEx(hkProtocol, "ComPort", NULL, &dwType, szBuff, &cb) != ERROR_SUCCESS)
#else
    if (RegQueryValue(hkProtocol, "ComPort", szBuff, &cb) != ERROR_SUCCESS)
#endif
    {
    	lstrcpy(szBuff, "COM1");
    	cb = lstrlen(szBuff);
#ifdef WIN32
		RegSetValueEx(hkProtocol, "ComPort", 0, REG_SZ, szBuff, cb);                         /* text string size               */
#else
		RegSetValue(hkProtocol, "ComPort", REG_SZ, szBuff, cb);                         /* text string size               */
#endif
	}
   PORT( npTTYInfo ) = szBuff[3] - '0' ;

    cb = sizeof(szBuff);
#ifdef WIN32
    if (RegQueryValueEx(hkProtocol, "BaudRate", NULL, &dwType, szBuff, &cb) != ERROR_SUCCESS)
#else
    if (RegQueryValue(hkProtocol, "BaudRate", szBuff, &cb) != ERROR_SUCCESS)
#endif
    {
    	lstrcpy(szBuff, "9600");
    	cb = lstrlen(szBuff);
#ifdef WIN32
		RegSetValueEx(hkProtocol, "BaudRate", 0, REG_SZ, szBuff, cb);                         /* text string size               */
#else
		RegSetValue(hkProtocol, "BaudRate", REG_SZ, szBuff, cb);                         /* text string size               */
#endif
	}
	switch (atoi(szBuff))
	{
	case 110:
		BAUDRATE(npTTYInfo) = CBR_110;break;
	case 300:
		BAUDRATE(npTTYInfo) = CBR_300;break;
	case 600:
		BAUDRATE(npTTYInfo) = CBR_600;break;
	case 1200:
		BAUDRATE(npTTYInfo) = CBR_1200;break;
	case 2400:
		BAUDRATE(npTTYInfo) = CBR_2400;break;
	case 4800:
		BAUDRATE(npTTYInfo) = CBR_4800;break;
	case 9600:
		BAUDRATE(npTTYInfo) = CBR_9600;break;
	case 14400:
		BAUDRATE(npTTYInfo) = CBR_14400;break;
	case 19200:
		BAUDRATE(npTTYInfo) = CBR_19200;break;
	case 38400:
		BAUDRATE(npTTYInfo) = CBR_38400;break;
	case 56000:
		BAUDRATE(npTTYInfo) = CBR_56000;break;
	case 128000:
		BAUDRATE(npTTYInfo) = CBR_128000;break;
	case 256000:
		BAUDRATE(npTTYInfo) = CBR_256000;break;
	default:
		BAUDRATE(npTTYInfo) = CBR_9600;break;
	}

	RegCloseKey(hkProtocol);     /* closes protocol key and subkeys    */
#ifdef WIN32
	dwDisposition = 0;
	if (RegCreateKeyEx(HKEY_CURRENT_USER, "Software\\Superior Software\\DGTerm", /* protocol string */
    	0,	// reserved 
    "",	// address of class string 
    REG_OPTION_NON_VOLATILE,	// special options flag 
    KEY_ALL_ACCESS,	// desired security access 
    NULL,	// address of key security structure 
    &hkProtocol,	// address of buffer for opened handle  
    &dwDisposition) != ERROR_SUCCESS)          /* protocol key handle */
	        return FALSE;
#else
	if (RegCreateKey(HKEY_CURRENT_USER, "Software\\Superior Software\\DGTerm", /* protocol string */
	    &hkProtocol) != ERROR_SUCCESS)          /* protocol key handle */
	        return FALSE;
#endif
    cb = sizeof(PHONENO(npTTYInfo));
#ifdef WIN32
    if (RegQueryValueEx(hkProtocol, "PhoneNo", NULL, &dwType, PHONENO(npTTYInfo), &cb) != ERROR_SUCCESS)
#else
    if (RegQueryValue(hkProtocol, "PhoneNo", PHONENO(npTTYInfo), &cb) != ERROR_SUCCESS)
#endif
    {
    	PHONENO(npTTYInfo)[0] = 0;
    	cb = 0;
#ifdef WIN32
		RegSetValueEx(hkProtocol, "PhoneNo", 0, REG_SZ, PHONENO(npTTYInfo), cb);                         /* text string size               */
#else
		RegSetValue(hkProtocol, "PhoneNo", REG_SZ, PHONENO(npTTYInfo), cb);                         /* text string size               */
#endif
	}

#ifdef REG_FONT_BINARY

    cb = sizeof(LOGFONT);
	dwType = REG_BINARY;
	prgbyte = (BYTE*)&LFTTYFONT( npTTYInfo );
//LFTTYFONT( npTTYInfo )
	if (RegQueryValueEx(hkProtocol, "FontInfo", NULL, &dwType, prgbyte, &cb) != ERROR_SUCCESS)
	{
		HFONT hGDIObject = (HFONT)GetStockObject(ANSI_FIXED_FONT);	// Windows fixed-pitch (monospace) system
		lErrorCode = GetObject(hGDIObject, cb, prgbyte);
		lErrorCode = RegSetValueEx(hkProtocol, "FontInfo", 0, REG_BINARY, prgbyte, cb);                         /* text string size               */
	}
#else
    cb = sizeof(LFTTYFONT( npTTYInfo ).lfFaceName);
    if (RegQueryValue(hkProtocol, "Font", LFTTYFONT( npTTYInfo ).lfFaceName, &cb) != ERROR_SUCCESS)
    {
    	lstrcpy(LFTTYFONT( npTTYInfo ).lfFaceName, "Terminal");
    	cb = lstrlen(LFTTYFONT( npTTYInfo ).lfFaceName);
#ifdef WIN32
		RegSetValueEx(hkProtocol, "Font", 0, REG_SZ, LFTTYFONT( npTTYInfo ).lfFaceName, cb);                         /* text string size               */
#else
		RegSetValue(hkProtocol, "Font", REG_SZ, LFTTYFONT( npTTYInfo ).lfFaceName, cb);                         /* text string size               */
#endif
	}

    cb = sizeof(szBuff);
#ifdef WIN32
    if (RegQueryValueEx(hkProtocol, "FontSize", NULL, &dwType, szBuff, &cb) != ERROR_SUCCESS)
#else
    if (RegQueryValue(hkProtocol, "FontSize", szBuff, &cb) != ERROR_SUCCESS)
#endif
    {
    	lstrcpy(szBuff, "8");
    	cb = lstrlen(szBuff);
#ifdef WIN32
		RegSetValueEx(hkProtocol, "FontSize", 0, REG_SZ, szBuff, cb);                         /* text string size               */
#else
		RegSetValue(hkProtocol, "FontSize", REG_SZ, szBuff, cb);                         /* text string size               */
#endif
	}
	LFTTYFONT( npTTYInfo ).lfHeight = atoi(szBuff);
#endif
	RegCloseKey(hkProtocol);     /* closes protocol key and subkeys    */
	
	return true;
} // end of InitApplication()

long NEAR ConvertString( char* chString, long cb )
{
	int iSource, iDest = 0;
	if (cb != -1)
		chString[cb] = 0;
	for (iSource = 0; iSource < 80; iSource++, iDest++)
	{
		chString[iDest] = chString[iSource];
		if (chString[iSource] == 0)
			return iDest;	// Length
		if (chString[iSource] == '<')
		{
			int iBinary = 0;
			iBinary += (chString[++iSource] - '0') * 100;
			iBinary += (chString[++iSource] - '0') * 10;
			iBinary += chString[++iSource] - '0';
			chString[iDest] = iBinary;
			iSource++;
		}
	}
}
//---------------------------------------------------------------------------
//  HWND NEAR InitInstance( HANDLE hInstance, int nCmdShow )
//
//  Description:
//     Initializes instance specific information.
//
//  Parameters:
//     HANDLE hInstance
//        Handle to instance
//
//     int nCmdShow
//        How do we show the window?
//
//  History:   Date       Author      Comment
//              5/ 8/91   BryanW      Wrote it.
//
//---------------------------------------------------------------------------

HWND NEAR InitInstance( HANDLE hInstance, int nCmdShow )
{
   HWND  hTTYWnd ;

   // load accelerators
//DonStart
   ghAccel = NULL;	// LoadAccelerators( hInstance, MAKEINTRESOURCE( TTYACCEL ) ) ;
//DonEnd

   // create the TTY window
   hTTYWnd = CreateWindow( gszTTYClass, gszAppName,
                           WS_OVERLAPPEDWINDOW,
                           CW_USEDEFAULT, CW_USEDEFAULT,
                           CW_USEDEFAULT, CW_USEDEFAULT,
                           NULL, NULL, hInstance, NULL ) ;

   if (NULL == hTTYWnd)
      return ( NULL ) ;

   ShowWindow( hTTYWnd, nCmdShow ) ;
   UpdateWindow( hTTYWnd ) ;

   return ( hTTYWnd ) ;

} // end of InitInstance()

//---------------------------------------------------------------------------
//  LRESULT FAR PASCAL TTYWndProc( HWND hWnd, UINT uMsg,
//                                 WPARAM wParam, LPARAM lParam )
//
//  Description:
//     This is the TTY Window Proc.  This handles ALL messages
//     to the tty window.
//
//  Parameters:
//     As documented for Window procedures.
//
//  Win-32 Porting Issues:
//     - WM_HSCROLL and WM_VSCROLL packing is different under Win-32.
//     - Needed LOWORD() of wParam for WM_CHAR messages.
//
//  History:   Date       Author      Comment
//              5/ 9/91   BryanW      Wrote it.
//              6/15/92   BryanW      Kill focus before disconnecting.
//              6/15/92   BryanW      Ported to Win-32.
//
//---------------------------------------------------------------------------

LRESULT FAR PASCAL TTYWndProc( HWND hWnd, UINT uMsg,
                               WPARAM wParam, LPARAM lParam )
{
	BOOL shift;
	BOOL control;
	char nChar;
	int offset;
#ifdef SAVEREG
	HKEY hkProtocol;
	char szBuff[80];
	LONG cb;	
#ifdef REG_FONT_BINARY
	DWORD dwType;
#endif
#endif
//DonStart
	BYTE	bOldPort, bNewPort;
//DonEnd
   switch (uMsg)
   {
      case WM_CREATE:
         return ( CreateTTYInfo( hWnd ) ) ;

      case WM_COMMAND:
      {
         switch ( LOWORD( wParam ) )
         {
            case IDM_CONNECT:
               if (!OpenConnection( hWnd ))
                  MessageBox( hWnd, "Connection failed.", gszAppName,
                              MB_ICONEXCLAMATION ) ;
               break ;

            case IDM_DISCONNECT:
               KillTTYFocus( hWnd ) ;
               CloseConnection( hWnd ) ;
               break ;

            case IDM_SETTINGS:
            {
               NPTTYINFO  npTTYInfo ;

               if (NULL == (npTTYInfo = GETNPTTYINFO( hWnd )))
                  return ( FALSE ) ;
//DonStart
	gfXFerMode = kTerminalMode;		// Make sure XSend/XReceive aren't going
	if (ghFile != (HFILE)NULL)
		ghFile = _lclose(ghFile);
	bOldPort = PORT( npTTYInfo );
//DonEnd
               GoModalDialogBoxParam( GETHINST( hWnd ),
                                      MAKEINTRESOURCE( SETTINGSDLGBOX ), hWnd,
                                      (DLGPROC) SettingsDlgProc,
                                      (LPARAM) (LPSTR) npTTYInfo ) ;

               // if fConnected, set new COM parameters

//DonStart
	bNewPort = PORT( npTTYInfo );
	if (bOldPort != bNewPort)
	{
        CloseConnection( hWnd ) ;
		if (!OpenConnection( hWnd ))
		  MessageBox( hWnd, "Connection failed.", gszAppName,
		              MB_ICONEXCLAMATION ) ;
	}
   // get baud rate selection
//DonEnd
               if (CONNECTED( npTTYInfo ))
               {
                  if (!SetupConnection( hWnd ))
                     MessageBox( hWnd, "Settings failed!", gszAppName,
                                 MB_ICONEXCLAMATION ) ;
               }
            }
            break ;

            case IDM_XSEND:
            {
               NPTTYINFO  npTTYInfo ;

               if (NULL == (npTTYInfo = GETNPTTYINFO( hWnd )))
                  return ( FALSE ) ;
               XSend( hWnd ) ;
            }
            break ;

            case IDM_XRECEIVE:
            {
               NPTTYINFO  npTTYInfo ;

               if (NULL == (npTTYInfo = GETNPTTYINFO( hWnd )))
                  return ( FALSE ) ;
               XReceive( hWnd ) ;
            }
            break ;

            case IDM_MULTIRECEIVE:
				gKeepReceiving = TRUE;
			// Fall through
            case IDM_AUTORECEIVE:
            {
               NPTTYINFO  npTTYInfo ;
               if (NULL == (npTTYInfo = GETNPTTYINFO( hWnd )))
                  return ( FALSE ) ;
				ghFile = (HFILE)NULL;	// File is not open yet
               XRecStart( hWnd ) ;
               GoModalDialogBoxParam ( GETHINST( hWnd ),
                                       MAKEINTRESOURCE( AUTORECEIVEBOX ),
                                       hWnd,
                                       (DLGPROC) AutoRecProc, 
                                       (LPARAM) (LPSTR) npTTYInfo ) ;
            }
            break ;

            case IDM_ABOUT:
           {
               NPTTYINFO  npTTYInfo ;
               if (NULL == (npTTYInfo = GETNPTTYINFO( hWnd )))
                  return ( FALSE ) ;
               GoModalDialogBoxParam ( GETHINST( hWnd ),
                                       MAKEINTRESOURCE( ABOUTDLGBOX ),
                                       hWnd,
                                       (DLGPROC) AboutDlgProc,
                                       (LPARAM) (LPSTR) npTTYInfo ) ;
            }
               break;

//DonStart
            case IDM_DIAL:		// Auto-dial
            {
				long i, cb;	
               NPTTYINFO  npTTYInfo ;
               if (NULL == (npTTYInfo = GETNPTTYINFO( hWnd )))
                  return ( FALSE ) ;
				ProcessTTYCharacter( hWnd, 'A' );
				ProcessTTYCharacter( hWnd, 'T' );
				ProcessTTYCharacter( hWnd, 'D' );
				ProcessTTYCharacter( hWnd, 'T' );

		    	cb = lstrlen(PHONENO(npTTYInfo));
			    for (i = 0; i < cb; i++)
			    {
					ProcessTTYCharacter( hWnd, PHONENO(npTTYInfo)[i] );
			    }

				ProcessTTYCharacter( hWnd, kReturnKey );
                break;
            }
            case IDM_PHONE:		// Auto-dial
           {
               NPTTYINFO  npTTYInfo ;
               if (NULL == (npTTYInfo = GETNPTTYINFO( hWnd )))
                  return ( FALSE ) ;
               GoModalDialogBoxParam ( GETHINST( hWnd ),
                                       MAKEINTRESOURCE( PHONEBOX ),
                                       hWnd,
                                       (DLGPROC) PhoneDlgProc,
                                       (LPARAM) (LPSTR) npTTYInfo ) ;
 				break;
            }
           case IDM_SMALL:
		{
			NPTTYINFO  npTTYInfo ;

			if (NULL == (npTTYInfo = GETNPTTYINFO( hWnd )))
			  return ( FALSE ) ;
#if defined USE_WIN_95
				LFTTYFONT( npTTYInfo ).lfHeight =         -11;
				LFTTYFONT( npTTYInfo ).lfWidth =          0 ;
				LFTTYFONT( npTTYInfo ).lfEscapement =     0 ;
				LFTTYFONT( npTTYInfo ).lfOrientation =    0 ;
				LFTTYFONT( npTTYInfo ).lfWeight =         400 ;
				LFTTYFONT( npTTYInfo ).lfItalic =         0 ;
				LFTTYFONT( npTTYInfo ).lfUnderline =      0 ;
				LFTTYFONT( npTTYInfo ).lfStrikeOut =      0 ;
				LFTTYFONT( npTTYInfo ).lfCharSet =        ANSI_CHARSET ;
				LFTTYFONT( npTTYInfo ).lfOutPrecision =   OUT_STROKE_PRECIS ;
				LFTTYFONT( npTTYInfo ).lfClipPrecision =  CLIP_STROKE_PRECIS ;
				LFTTYFONT( npTTYInfo ).lfQuality =        DRAFT_QUALITY ;
				LFTTYFONT( npTTYInfo ).lfPitchAndFamily = FIXED_PITCH | FF_MODERN ;
				lstrcpy( LFTTYFONT( npTTYInfo ).lfFaceName, "Courier New" ) ;
#ifdef SAVEREG
#ifdef WIN32
	RegOpenKeyEx(HKEY_CURRENT_USER, "Software\\Superior Software\\DGTerm", 0, KEY_ALL_ACCESS, &hkProtocol);
#else
	RegOpenKey(HKEY_CURRENT_USER, "Software\\Superior Software\\DGTerm", &hkProtocol);
#endif
#ifdef REG_FONT_BINARY
    cb = sizeof(LFTTYFONT( npTTYInfo ));
	dwType = REG_BINARY;
//LFTTYFONT( npTTYInfo )
	RegSetValueEx(hkProtocol, "FontInfo", 0, dwType, (BYTE*)&LFTTYFONT( npTTYInfo ), cb);                         /* text string size               */
#else
	cb = lstrlen(LFTTYFONT( npTTYInfo ).lfFaceName);
	RegSetValue(hkProtocol, "Font", REG_SZ, LFTTYFONT( npTTYInfo ).lfFaceName, cb);                         /* text string size               */
	cb = wsprintf(szBuff, "%d", (short)LFTTYFONT( npTTYInfo ).lfHeight);		// Convert to a string
	RegSetValue(hkProtocol, "FontSize", REG_SZ, szBuff, cb);                         /* text string size               */
#endif
	RegCloseKey(hkProtocol);     /* closes protocol key and subkeys    */
#endif
#else
				LFTTYFONT( npTTYInfo ).lfHeight =         8 ;
				LFTTYFONT( npTTYInfo ).lfWidth =          0 ;
				LFTTYFONT( npTTYInfo ).lfEscapement =     0 ;
				LFTTYFONT( npTTYInfo ).lfOrientation =    0 ;
				LFTTYFONT( npTTYInfo ).lfWeight =         0 ;
				LFTTYFONT( npTTYInfo ).lfItalic =         0 ;
				LFTTYFONT( npTTYInfo ).lfUnderline =      0 ;
				LFTTYFONT( npTTYInfo ).lfStrikeOut =      0 ;
				LFTTYFONT( npTTYInfo ).lfCharSet =        OEM_CHARSET ;
				LFTTYFONT( npTTYInfo ).lfOutPrecision =   OUT_DEFAULT_PRECIS ;
				LFTTYFONT( npTTYInfo ).lfClipPrecision =  CLIP_DEFAULT_PRECIS ;
				LFTTYFONT( npTTYInfo ).lfQuality =        DEFAULT_QUALITY ;
				LFTTYFONT( npTTYInfo ).lfPitchAndFamily = FIXED_PITCH | FF_MODERN ;
				lstrcpy( LFTTYFONT( npTTYInfo ).lfFaceName, "Terminal" ) ;
#endif
				// set TTYInfo handle before any further message processing.

				SETNPTTYINFO( hWnd, npTTYInfo ) ;

				// reset the character information, etc.
				ResetTTYScreen( hWnd, npTTYInfo ) ;
				KillTTYFocus( hWnd ) ;
		        SetTTYFocus( hWnd ) ;
		}
               break;

            case IDM_MEDIUM:
		{
			NPTTYINFO  npTTYInfo ;

			if (NULL == (npTTYInfo = GETNPTTYINFO( hWnd )))
			  return ( FALSE ) ;
				LFTTYFONT( npTTYInfo ).lfHeight =         -13 ;
				LFTTYFONT( npTTYInfo ).lfWidth =          0 ;
				LFTTYFONT( npTTYInfo ).lfEscapement =     0 ;
				LFTTYFONT( npTTYInfo ).lfOrientation =    0 ;
				LFTTYFONT( npTTYInfo ).lfWeight =         400 ;
				LFTTYFONT( npTTYInfo ).lfItalic =         0 ;
				LFTTYFONT( npTTYInfo ).lfUnderline =      0 ;
				LFTTYFONT( npTTYInfo ).lfStrikeOut =      0 ;
				LFTTYFONT( npTTYInfo ).lfCharSet =        ANSI_CHARSET ;
				LFTTYFONT( npTTYInfo ).lfOutPrecision =   OUT_STROKE_PRECIS ;
				LFTTYFONT( npTTYInfo ).lfClipPrecision =  CLIP_STROKE_PRECIS ;
				LFTTYFONT( npTTYInfo ).lfQuality =        DRAFT_QUALITY ;
				LFTTYFONT( npTTYInfo ).lfPitchAndFamily = FIXED_PITCH | FF_MODERN ;
				lstrcpy( LFTTYFONT( npTTYInfo ).lfFaceName, "Courier New" ) ;
#ifdef SAVEREG
#ifdef WIN32
	RegOpenKeyEx(HKEY_CURRENT_USER, "Software\\Superior Software\\DGTerm", 0, KEY_ALL_ACCESS, &hkProtocol);
#else
	RegOpenKey(HKEY_CURRENT_USER, "Software\\Superior Software\\DGTerm", &hkProtocol);
#endif
#ifdef REG_FONT_BINARY
    cb = sizeof(LFTTYFONT( npTTYInfo ));
	dwType = REG_BINARY;
//LFTTYFONT( npTTYInfo )
	RegSetValueEx(hkProtocol, "FontInfo", 0, dwType, (BYTE*)&LFTTYFONT( npTTYInfo ), cb);                         /* text string size               */
#else
	cb = lstrlen(LFTTYFONT( npTTYInfo ).lfFaceName);
	RegSetValue(hkProtocol, "Font", REG_SZ, LFTTYFONT( npTTYInfo ).lfFaceName, cb);                         /* text string size               */
	cb = wsprintf(szBuff, "%d", (short)LFTTYFONT( npTTYInfo ).lfHeight);		// Convert to a string
	RegSetValue(hkProtocol, "FontSize", REG_SZ, szBuff, cb);                         /* text string size               */
#endif
	RegCloseKey(hkProtocol);     /* closes protocol key and subkeys    */
#endif

				// set TTYInfo handle before any further message processing.

				SETNPTTYINFO( hWnd, npTTYInfo ) ;

				// reset the character information, etc.
				ResetTTYScreen( hWnd, npTTYInfo ) ;
				KillTTYFocus( hWnd ) ;
				SetTTYFocus( hWnd ) ;
		}
               break;

            case IDM_LARGE:
		{
			NPTTYINFO  npTTYInfo ;

			if (NULL == (npTTYInfo = GETNPTTYINFO( hWnd )))
			  return ( FALSE ) ;
#if defined USE_WIN_95
				LFTTYFONT( npTTYInfo ).lfHeight =         -16;
				LFTTYFONT( npTTYInfo ).lfWidth =          0 ;
				LFTTYFONT( npTTYInfo ).lfEscapement =     0 ;
				LFTTYFONT( npTTYInfo ).lfOrientation =    0 ;
				LFTTYFONT( npTTYInfo ).lfWeight =         400 ;
				LFTTYFONT( npTTYInfo ).lfItalic =         0 ;
				LFTTYFONT( npTTYInfo ).lfUnderline =      0 ;
				LFTTYFONT( npTTYInfo ).lfStrikeOut =      0 ;
				LFTTYFONT( npTTYInfo ).lfCharSet =        ANSI_CHARSET ;
				LFTTYFONT( npTTYInfo ).lfOutPrecision =   OUT_STROKE_PRECIS ;
				LFTTYFONT( npTTYInfo ).lfClipPrecision =  CLIP_STROKE_PRECIS ;
				LFTTYFONT( npTTYInfo ).lfQuality =        DRAFT_QUALITY ;
				LFTTYFONT( npTTYInfo ).lfPitchAndFamily = FIXED_PITCH | FF_MODERN ;
				lstrcpy( LFTTYFONT( npTTYInfo ).lfFaceName, "Courier New" ) ;
#ifdef SAVEREG
#ifdef WIN32
	RegOpenKeyEx(HKEY_CURRENT_USER, "Software\\Superior Software\\DGTerm", 0, KEY_ALL_ACCESS, &hkProtocol);
#else
	RegOpenKey(HKEY_CURRENT_USER, "Software\\Superior Software\\DGTerm", &hkProtocol);
#endif
#ifdef REG_FONT_BINARY
    cb = sizeof(LFTTYFONT( npTTYInfo ));
	dwType = REG_BINARY;
//LFTTYFONT( npTTYInfo )
	RegSetValueEx(hkProtocol, "FontInfo", 0, dwType, (BYTE*)&LFTTYFONT( npTTYInfo ), cb);                         /* text string size               */
#else
	cb = lstrlen(LFTTYFONT( npTTYInfo ).lfFaceName);
	RegSetValue(hkProtocol, "Font", REG_SZ, LFTTYFONT( npTTYInfo ).lfFaceName, cb);                         /* text string size               */
	cb = wsprintf(szBuff, "%d", (short)LFTTYFONT( npTTYInfo ).lfHeight);		// Convert to a string
	RegSetValue(hkProtocol, "FontSize", REG_SZ, szBuff, cb);                         /* text string size               */
#endif
	RegCloseKey(hkProtocol);     /* closes protocol key and subkeys    */
#endif
#else
				LFTTYFONT( npTTYInfo ).lfHeight =         12 ;
				LFTTYFONT( npTTYInfo ).lfWidth =          0 ;
				LFTTYFONT( npTTYInfo ).lfEscapement =     0 ;
				LFTTYFONT( npTTYInfo ).lfOrientation =    0 ;
				LFTTYFONT( npTTYInfo ).lfWeight =         0 ;
				LFTTYFONT( npTTYInfo ).lfItalic =         0 ;
				LFTTYFONT( npTTYInfo ).lfUnderline =      0 ;
				LFTTYFONT( npTTYInfo ).lfStrikeOut =      0 ;
				LFTTYFONT( npTTYInfo ).lfCharSet =        ANSI_CHARSET ;
				LFTTYFONT( npTTYInfo ).lfOutPrecision =   OUT_DEFAULT_PRECIS ;
				LFTTYFONT( npTTYInfo ).lfClipPrecision =  CLIP_DEFAULT_PRECIS ;
				LFTTYFONT( npTTYInfo ).lfQuality =        DEFAULT_QUALITY ;
				LFTTYFONT( npTTYInfo ).lfPitchAndFamily = FIXED_PITCH | FF_MODERN ;
				lstrcpy( LFTTYFONT( npTTYInfo ).lfFaceName, "FixedSys" ) ;
#endif
				// set TTYInfo handle before any further message processing.

				SETNPTTYINFO( hWnd, npTTYInfo ) ;

				// reset the character information, etc.
				ResetTTYScreen( hWnd, npTTYInfo ) ;
				KillTTYFocus( hWnd ) ;
				SetTTYFocus( hWnd ) ;
		}
               break;

//DonEnd
            case IDM_EXIT:
               PostMessage( hWnd, WM_CLOSE, 0, 0L ) ;
               break ;
         }
      }
      break ;

      case WM_COMMNOTIFY:
         ProcessCOMMNotification( hWnd, wParam, lParam ) ;
         break ;

      case WM_PAINT:
         PaintTTY( hWnd ) ;
         break ;

      case WM_SIZE:
//DonStart	//Hack to keep scroll bars out on minimize, then maximize
	  	switch (wParam)
	  	{
	  	case SIZE_MINIMIZED:	// Don't call SizeTTY (messes up scroll bars)
			break;
	  	case SIZE_MAXIMIZED:
			SizeTTY( hWnd, HIWORD( lParam ), LOWORD( lParam ) ) ;
			break;
	  	case SIZE_RESTORED:
			SizeTTY( hWnd, HIWORD( lParam ), LOWORD( lParam ) ) ;
			break;
		default:
         SizeTTY( hWnd, HIWORD( lParam ), LOWORD( lParam ) ) ;
         break ;
		}
//DonEnd

      case WM_HSCROLL:
#ifdef WIN32
         ScrollTTYHorz( hWnd, LOWORD( wParam ), HIWORD( wParam ) ) ;
#else
         ScrollTTYHorz( hWnd, (WORD) wParam, LOWORD( lParam ) ) ;
#endif
         break ;

      case WM_VSCROLL:
#ifdef WIN32
         ScrollTTYVert( hWnd, LOWORD( wParam ), HIWORD( wParam ) ) ;
#else
         ScrollTTYVert( hWnd, (WORD) wParam, LOWORD( lParam ) ) ;
#endif
         break ;

      case WM_CHAR:
#ifdef WIN32
		nChar = LOBYTE( LOWORD( wParam ) );
#else
		nChar = LOBYTE( wParam );
#endif
	if ((nChar != kCReturn) && (nChar != VK_BACK))
	{	// Handled in WM_KEYDOWN
#ifdef WIN32
         ProcessTTYCharacter( hWnd, LOBYTE( LOWORD( wParam ) ) ) ;
#else
         ProcessTTYCharacter( hWnd, LOBYTE( wParam ) ) ;
#endif
	}
         break ;
//DonStart
 	   case WM_SYSKEYDOWN:
#ifdef WIN32
		nChar = LOBYTE( LOWORD( wParam ) );
#else
		nChar = LOBYTE( wParam );
#endif
	if (nChar != VK_F10)
		return( DefWindowProc( hWnd, uMsg, wParam, lParam ) ) ;
// else handle the F10 the same as any other key (fall through)
 
      case WM_KEYDOWN:
//      case WM_KEYUP:
#ifdef WIN32
		nChar = LOBYTE( LOWORD( wParam ) );
#else
		nChar = LOBYTE( wParam );
#endif
    switch( nChar )
	{
		case VK_HOME:
	        ProcessTTYCharacter( hWnd, kHome ) ;
			break;
		case VK_LEFT:
	        ProcessTTYCharacter( hWnd, kLeftCur ) ;
			break;
		case VK_RIGHT:
	        ProcessTTYCharacter( hWnd, kRightCur ) ;
			break;
		case VK_DOWN:
	        ProcessTTYCharacter( hWnd, kDownCur ) ;
			break;
		case VK_UP:
	        ProcessTTYCharacter( hWnd, kUpCur ) ;
			break;
		case VK_ESCAPE:
//	        ProcessTTYCharacter( hWnd, kEscape ) ;	// Processed in WM_CHAR
			break;
		case VK_BACK:
	        ProcessTTYCharacter( hWnd, kDelete ) ;
			break;
		case VK_DELETE:
	        ProcessTTYCharacter( hWnd, kDelete ) ;
			break;
		case VK_TAB:
//	        ProcessTTYCharacter( hWnd, kTab ) ;		//Processed in WM_CHAR
			break;
		case VK_CLEAR:
	        ProcessTTYCharacter( hWnd, kClearScreen ) ;
			break;
		case VK_RETURN:
			if (GetKeyState(VK_SHIFT) & 0x8000)
		        ProcessTTYCharacter( hWnd, kCReturn ) ;
			else
		        ProcessTTYCharacter( hWnd, kNewLine ) ;
			break;
		case VK_SPACE:
//	        ProcessTTYCharacter( hWnd, ' ' ) ;		//Processed in WM_CHAR
			break;
		case VK_END:
//	        ProcessTTYCharacter( hWnd, kEndCur ) ;		// Ctrl A
	        ProcessTTYCharacter( hWnd, kTermID1 ) ;		// Ctrl A
	        ProcessTTYCharacter( hWnd, kTermID2 ) ;		// Ctrl A
			break;
		case VK_INSERT:
	        ProcessTTYCharacter( hWnd, kInsCur ) ;		// Ctrl E
			break;
		case VK_PRIOR:
		{
			if (kCHdr != 0)
		        ProcessTTYCharacter( hWnd, kCHdr ) ;
	        ProcessTTYCharacter( hWnd, kC2Key ) ;	// Page up
		}
			break;
		case VK_NEXT:
		{
			if (kCHdr != 0)
		        ProcessTTYCharacter( hWnd, kCHdr ) ;
	        ProcessTTYCharacter( hWnd, kC4Key ) ;	// Page down
		}
			break;
		case VK_F1:
		case VK_F2:
		case VK_F3:
		case VK_F4:
		case VK_F5:
		case VK_F6:
		case VK_F7:
		case VK_F8:
		case VK_F9:
		case VK_F10:
		case VK_F11:
		case VK_F12:
			shift = false;
			control = false;
			if (GetKeyState(VK_SHIFT) & 0x8000)
				shift = true;
			if (GetKeyState(VK_CONTROL) & 0x8000)
				control = true;
			offset = kFun0;
			if (shift)
				offset = kFun1;
			if (control)
				offset = kFun2;
			if (shift && control)
				offset = kFun3;
	        ProcessTTYCharacter( hWnd, kFuncHdr ) ;
	        ProcessTTYCharacter( hWnd, (char)(offset + nChar - VK_F1) ) ;
		}
		break;
 
       case WM_SYSCOMMAND:
		switch (wParam)
		{
		case SC_MAXIMIZE:
		{
			NPTTYINFO  npTTYInfo ;

			if (NULL == (npTTYInfo = GETNPTTYINFO( hWnd )))
			  return ( FALSE ) ;
			if ((LFTTYFONT( npTTYInfo ).lfHeight < 11) && (LFTTYFONT( npTTYInfo ).lfHeight > -11))
			{
				LFTTYFONT( npTTYInfo ).lfHeight =         12 ;
				LFTTYFONT( npTTYInfo ).lfWidth =          0 ;
				LFTTYFONT( npTTYInfo ).lfEscapement =     0 ;
				LFTTYFONT( npTTYInfo ).lfOrientation =    0 ;
				LFTTYFONT( npTTYInfo ).lfWeight =         0 ;
				LFTTYFONT( npTTYInfo ).lfItalic =         0 ;
				LFTTYFONT( npTTYInfo ).lfUnderline =      0 ;
				LFTTYFONT( npTTYInfo ).lfStrikeOut =      0 ;
				LFTTYFONT( npTTYInfo ).lfCharSet =        ANSI_CHARSET ;
				LFTTYFONT( npTTYInfo ).lfOutPrecision =   OUT_DEFAULT_PRECIS ;
				LFTTYFONT( npTTYInfo ).lfClipPrecision =  CLIP_DEFAULT_PRECIS ;
				LFTTYFONT( npTTYInfo ).lfQuality =        DEFAULT_QUALITY ;
				LFTTYFONT( npTTYInfo ).lfPitchAndFamily = FIXED_PITCH | FF_MODERN ;
				lstrcpy( LFTTYFONT( npTTYInfo ).lfFaceName, "FixedSys" ) ;

				// set TTYInfo handle before any further message processing.

				SETNPTTYINFO( hWnd, npTTYInfo ) ;

				// reset the character information, etc.

			}
				ResetTTYScreen( hWnd, npTTYInfo ) ;
				KillTTYFocus( hWnd ) ;
				SetTTYFocus( hWnd ) ;
				break;	// Don't maximize, resetTTY changes screen to correct size
		}
//case SC_RESTORE:
//	return( DefWindowProc( hWnd, uMsg, wParam, lParam ) ) ;
//	break;
		default:
			return( DefWindowProc( hWnd, uMsg, wParam, lParam ) ) ;
		}
		break ;
//DonEnd

      case WM_SETFOCUS:
         SetTTYFocus( hWnd ) ;
         break ;

      case WM_KILLFOCUS:
         KillTTYFocus( hWnd ) ;
         break ;

      case WM_DESTROY:
         DestroyTTYInfo( hWnd ) ;
         PostQuitMessage( 0 ) ;
         break ;

      case WM_CLOSE:
			shift = false;	  	
//DonStart
//         if (IDOK != MessageBox( hWnd, "OK to close window?", "Data General",
//                                 MB_ICONQUESTION | MB_OKCANCEL ))
//            break ;
//DonEnd
         // fall through

      default:
         return( DefWindowProc( hWnd, uMsg, wParam, lParam ) ) ;
   }
   return 0L ;

} // end of TTYWndProc()

//---------------------------------------------------------------------------
//  LRESULT NEAR CreateTTYInfo( HWND hWnd )
//
//  Description:
//     Creates the tty information structure and sets
//     menu option availability.  Returns -1 if unsuccessful.
//
//  Parameters:
//     HWND  hWnd
//        Handle to main window.
//
//  Win-32 Porting Issues:
//     - Needed to initialize TERMWND( npTTYInfo ) for secondary thread.
//     - Needed to create/initialize overlapped structures used in reads &
//       writes to COMM device.
//
//  History:   Date       Author      Comment
//             10/18/91   BryanW      Pulled from tty window proc.
//              1/13/92   BryanW      Fixed bug with invalid handle
//                                    caused by WM_SIZE sent by
//                                    ResetTTYScreen().
//
//---------------------------------------------------------------------------

LRESULT NEAR CreateTTYInfo( HWND hWnd )
{
   HMENU       hMenu ;
   NPTTYINFO   npTTYInfo ;

   if (NULL == (npTTYInfo =
                   (NPTTYINFO) LocalAlloc( LPTR, sizeof( TTYINFO ) )))
      return ( (LRESULT) -1 ) ;

   // initialize TTY info structure

   COMDEV( npTTYInfo )        = 0 ;
   CONNECTED( npTTYInfo )     = FALSE ;
   CURSORSTATE( npTTYInfo )   = CS_HIDE ;
   LOCALECHO( npTTYInfo )     = FALSE ;
   NEWLINE( npTTYInfo )      = TRUE ;
   AUTOWRAP( npTTYInfo )      = TRUE ;
   PORT( npTTYInfo )          = 1 ;
   BAUDRATE( npTTYInfo )      = CBR_9600 ;
   BYTESIZE( npTTYInfo )      = 8 ;
   FLOWCTRL( npTTYInfo )      = FC_XONXOFF ;
   PARITY( npTTYInfo )        = NOPARITY ;
   STOPBITS( npTTYInfo )      = ONESTOPBIT ;
   XONXOFF( npTTYInfo )       = TRUE ;
   XSIZE( npTTYInfo )         = 0 ;
   YSIZE( npTTYInfo )         = 0 ;
   XSCROLL( npTTYInfo )       = 0 ;
   YSCROLL( npTTYInfo )       = 0 ;
   XOFFSET( npTTYInfo )       = 0 ;
   YOFFSET( npTTYInfo )       = 0 ;
   COLUMN( npTTYInfo )        = 0 ;
   ROW( npTTYInfo )           = 0 ;
   HTTYFONT( npTTYInfo )      = NULL ;
   FGCOLOR( npTTYInfo )       = RGB( 0, 0, 0 ) ;
   USECNRECEIVE( npTTYInfo )  = TRUE ;
   DISPLAYERRORS( npTTYInfo ) = TRUE ;

//DonStart
   REVERSE( npTTYInfo ) = Off ;
   DIM( npTTYInfo ) = Off ;
   UNDERLINE( npTTYInfo ) = Off ;
   BLINK( npTTYInfo ) = Off ;
   _fmemset( AREVERSE( npTTYInfo ), Off, MAXROWS * MAXCOLS ) ;
   _fmemset( ADIM( npTTYInfo ), Off, MAXROWS * MAXCOLS ) ;
   _fmemset( AUNDERLINE( npTTYInfo ), Off, MAXROWS * MAXCOLS ) ;
   _fmemset( ABLINK( npTTYInfo ), Off, MAXROWS * MAXCOLS ) ;
   PAGE( npTTYInfo ) = 0 ;
   POSITIONFLAG( npTTYInfo ) = 0 ;
   FUNHDR( npTTYInfo ) = 0 ;
#if defined USE_WIN_95
   TEXTCOLOR( npTTYInfo ) = GetSysColor( COLOR_WINDOWTEXT );
   BACKCOLOR( npTTYInfo ) = GetSysColor( COLOR_WINDOW );
   TEXTREVERSECOLOR( npTTYInfo ) = GetSysColor( COLOR_WINDOW );
   BACKREVERSECOLOR( npTTYInfo ) = GetSysColor( COLOR_WINDOWTEXT );
   TEXTDIMCOLOR( npTTYInfo )= GetSysColor( COLOR_HIGHLIGHT	);
   TEXTBLINKCOLOR( npTTYInfo ) = GetSysColor( COLOR_HIGHLIGHTTEXT	);
   TEXTBLINKDIMCOLOR( npTTYInfo ) = GetSysColor( COLOR_HIGHLIGHTTEXT	);
#else
   TEXTCOLOR( npTTYInfo ) = kBlack; //GetSysColor( COLOR_WINDOWTEXT );
   BACKCOLOR( npTTYInfo ) = kDarkGray; //GetSysColor( COLOR_WINDOW );
   TEXTREVERSECOLOR( npTTYInfo ) = kRed; //GetSysColor( COLOR_WINDOW );
   BACKREVERSECOLOR( npTTYInfo ) = kGreen; //GetSysColor( COLOR_WINDOWTEXT );
   TEXTDIMCOLOR( npTTYInfo )= kYellow; //GetSysColor( COLOR_HIGHLIGHT	);
   TEXTBLINKCOLOR( npTTYInfo ) = kGreen; //GetSysColor( COLOR_HIGHLIGHTTEXT	);
   TEXTBLINKDIMCOLOR( npTTYInfo ) = kRed; //GetSysColor( COLOR_HIGHLIGHTTEXT	);
#endif
//DonEnd
#ifdef WIN32
   TERMWND( npTTYInfo ) =       hWnd ;

   // create I/O event used for overlapped reads / writes

   READ_OS( npTTYInfo ).hEvent = CreateEvent( NULL,    // no security
                                              TRUE,    // explicit reset req
                                              FALSE,   // initial event reset
                                              NULL ) ; // no name
   if (READ_OS( npTTYInfo ).hEvent == NULL)
   {
      LocalFree( npTTYInfo ) ;
      return ( -1 ) ;
   }
   WRITE_OS( npTTYInfo ).hEvent = CreateEvent( NULL,    // no security
                                               TRUE,    // explicit reset req
                                               FALSE,   // initial event reset
                                               NULL ) ; // no name
   if (NULL == WRITE_OS( npTTYInfo ).hEvent)
   {
      CloseHandle( READ_OS( npTTYInfo ).hEvent ) ;
      LocalFree( npTTYInfo ) ;
      return ( -1 ) ;
   }

   // create "posted notification" event

   POSTEVENT( npTTYInfo ) = CreateEvent( NULL,     // no security
                                         TRUE,     // manual reset
                                         TRUE,     // initial event is set
                                         NULL ) ;  // no name

   if (POSTEVENT( npTTYInfo ) == NULL)
   {
      CloseHandle( READ_OS( npTTYInfo ).hEvent ) ;
      CloseHandle( WRITE_OS( npTTYInfo ).hEvent ) ;
      LocalFree( npTTYInfo ) ;
      return ( -1 ) ;
   }
#endif

   // clear screen space

   _fmemset( SCREEN( npTTYInfo ), ' ', MAXROWS * MAXCOLS ) ;

   // setup default font information

   LFTTYFONT( npTTYInfo ).lfHeight =         8 ;
   LFTTYFONT( npTTYInfo ).lfWidth =          0 ;
   LFTTYFONT( npTTYInfo ).lfEscapement =     0 ;
   LFTTYFONT( npTTYInfo ).lfOrientation =    0 ;
   LFTTYFONT( npTTYInfo ).lfWeight =         0 ;
   LFTTYFONT( npTTYInfo ).lfItalic =         0 ;
   LFTTYFONT( npTTYInfo ).lfUnderline =      0 ;
   LFTTYFONT( npTTYInfo ).lfStrikeOut =      0 ;
   LFTTYFONT( npTTYInfo ).lfCharSet =        OEM_CHARSET ;
   LFTTYFONT( npTTYInfo ).lfOutPrecision =   OUT_DEFAULT_PRECIS ;
   LFTTYFONT( npTTYInfo ).lfClipPrecision =  CLIP_DEFAULT_PRECIS ;
   LFTTYFONT( npTTYInfo ).lfQuality =        DEFAULT_QUALITY ;
   LFTTYFONT( npTTYInfo ).lfPitchAndFamily = FIXED_PITCH | FF_MODERN ;
//DonStart
//   lstrcpy( LFTTYFONT( npTTYInfo ).lfFaceName, "FixedSys" ) ;
   lstrcpy( LFTTYFONT( npTTYInfo ).lfFaceName, "Terminal" ) ;
//DonEnd

   // set TTYInfo handle before any further message processing.

   SETNPTTYINFO( hWnd, npTTYInfo ) ;

	InitRegistration(npTTYInfo);	// Read registration (.INI) info

   // reset the character information, etc.

   ResetTTYScreen( hWnd, npTTYInfo ) ;

   hMenu = GetMenu( hWnd ) ;
//DonStart
	if (!OpenConnection( hWnd ))
	  MessageBox( hWnd, "Connection failed.", gszAppName,
	              MB_ICONEXCLAMATION ) ;
//   EnableMenuItem( hMenu, IDM_DISCONNECT,
//                   MF_GRAYED | MF_DISABLED | MF_BYCOMMAND ) ;
//   EnableMenuItem( hMenu, IDM_CONNECT, MF_ENABLED | MF_BYCOMMAND ) ;
   EnableMenuItem( hMenu, IDM_DISCONNECT,
                   MF_ENABLED | MF_BYCOMMAND ) ;
   EnableMenuItem( hMenu, IDM_CONNECT, MF_GRAYED | MF_DISABLED | MF_BYCOMMAND ) ;
//DonEnd


   return ( (LRESULT) TRUE ) ;

} // end of CreateTTYInfo()

//---------------------------------------------------------------------------
//  BOOL NEAR DestroyTTYInfo( HWND hWnd )
//
//  Description:
//     Destroys block associated with TTY window handle.
//
//  Parameters:
//     HWND hWnd
//        handle to TTY window
//
//  Win-32 Porting Issues:
//     - Needed to clean up event objects created during initialization.
//
//  History:   Date       Author      Comment
//              5/ 8/91   BryanW      Wrote it.
//              6/15/92   BryanW      Ported to Win-32.
//
//---------------------------------------------------------------------------

BOOL NEAR DestroyTTYInfo( HWND hWnd )
{
   NPTTYINFO npTTYInfo ;

   if (NULL == (npTTYInfo = GETNPTTYINFO( hWnd )))
      return ( FALSE ) ;

   // force connection closed (if not already closed)

   if (CONNECTED( npTTYInfo ))
      CloseConnection( hWnd ) ;

#ifdef WIN32
   // clean up event objects

   CloseHandle( READ_OS( npTTYInfo ).hEvent ) ;
   CloseHandle( WRITE_OS( npTTYInfo ).hEvent ) ;
   CloseHandle( POSTEVENT( npTTYInfo ) ) ;
#endif

   DeleteObject( HTTYFONT( npTTYInfo ) ) ;

   LocalFree( npTTYInfo ) ;
   return ( TRUE ) ;

} // end of DestroyTTYInfo()

//---------------------------------------------------------------------------
//  BOOL NEAR ResetTTYScreen( HWND hWnd, NPTTYINFO npTTYInfo )
//
//  Description:
//     Resets the TTY character information and causes the
//     screen to resize to update the scroll information.
//
//  Parameters:
//     NPTTYINFO  npTTYInfo
//        pointer to TTY info structure
//
//  History:   Date       Author      Comment
//             10/20/91   BryanW      Wrote it.
//
//---------------------------------------------------------------------------

BOOL NEAR ResetTTYScreen( HWND hWnd, NPTTYINFO npTTYInfo )
{
   HDC         hDC ;
   TEXTMETRIC  tm ;
   RECT        rcWindow ;
//DonStart
	DWORD		rcWidth, rcHeight, screenHeight, screenWidth;
//DonEnd

   if (NULL == npTTYInfo)
      return ( FALSE ) ;

   if (NULL != HTTYFONT( npTTYInfo ))
      DeleteObject( HTTYFONT( npTTYInfo ) ) ;

   HTTYFONT( npTTYInfo ) = CreateFontIndirect( &LFTTYFONT( npTTYInfo ) ) ;
   hDC = GetDC( hWnd ) ;
   SelectObject( hDC, HTTYFONT( npTTYInfo ) ) ;
   GetTextMetrics( hDC, &tm ) ;
   ReleaseDC( hWnd, hDC ) ;

   XCHAR( npTTYInfo ) = tm.tmAveCharWidth  ;
   YCHAR( npTTYInfo ) = tm.tmHeight + tm.tmExternalLeading ;
   UNDERLINEPOS( npTTYInfo ) = tm.tmHeight;

   // a slimy hack to force the scroll position, region to
   // be recalculated based on the new character sizes

   GetWindowRect( hWnd, &rcWindow ) ;
//DonStart
	rcWidth = (MAXCOLS * XCHAR( npTTYInfo )) + GetSystemMetrics(SM_CXFRAME) * 2;
	rcHeight = (MAXROWS * YCHAR( npTTYInfo )) + GetSystemMetrics(SM_CYFRAME) * 2 + GetSystemMetrics(SM_CYMENU) + GetSystemMetrics(SM_CYCAPTION);
	screenWidth = GetSystemMetrics(SM_CXSCREEN) + GetSystemMetrics(SM_CXFRAME) * 2;
	screenHeight = GetSystemMetrics(SM_CYSCREEN) + GetSystemMetrics(SM_CYFRAME) * 2;
	if (rcWidth > screenWidth)
		rcWidth = screenWidth;
	if (rcHeight > screenHeight)
		rcHeight = screenHeight;
	if (rcWindow.left + rcWidth > screenWidth)
		rcWindow.left = screenWidth - rcWidth - GetSystemMetrics(SM_CXFRAME);
	if (rcWindow.top + rcHeight > (ULONG)GetSystemMetrics(SM_CYSCREEN))
		rcWindow.top = screenHeight - rcHeight - GetSystemMetrics(SM_CYFRAME);
   MoveWindow( hWnd, rcWindow.left, rcWindow.top, rcWidth, rcHeight, true ) ;
//DonEnd
//   SendMessage( hWnd, WM_SIZE, SIZENORMAL,
//                (LPARAM) MAKELONG( rcWindow.right - rcWindow.left,
//                                   rcWindow.bottom - rcWindow.top ) ) ;

   return ( TRUE ) ;

} // end of ResetTTYScreen()

//---------------------------------------------------------------------------
//  BOOL NEAR PaintTTY( HWND hWnd )
//
//  Description:
//     Paints the rectangle determined by the paint struct of
//     the DC.
//
//  Parameters:
//     HWND hWnd
//        handle to TTY window (as always)
//
//  History:   Date       Author      Comment
//              5/ 9/91   BryanW      Wrote it.
//             10/22/91   BryanW      Problem with background color
//                                    and "off by one" fixed.
//
//              2/25/92   BryanW      Off-by-one not quite fixed...
//                                    also resolved min/max problem
//                                    for windows extended beyond
//                                    the "TTY display".
//
//---------------------------------------------------------------------------

BOOL NEAR PaintTTY( HWND hWnd )
{
   int          nRow, nCol, nEndRow, nEndCol, nCount, nHorzPos, nVertPos ;
   HDC          hDC ;
   HFONT        hOldFont ;
   NPTTYINFO    npTTYInfo ;
   PAINTSTRUCT  ps ;
   RECT         rect ;
//DonStart
	COLORREF	cTextColor, cBackColor, cTempColor;
	DWORD		nStartCol, nLastCol;
	BOOL		fReverse;
	BOOL		fDim;
	BOOL		fUnderline;
	BOOL		fBlink;
//DonEnd

   if (NULL == (npTTYInfo = GETNPTTYINFO( hWnd )))
      return ( FALSE ) ;

   hDC = BeginPaint( hWnd, &ps ) ;
   hOldFont = SelectObject( hDC, HTTYFONT( npTTYInfo ) ) ;
   SetTextColor( hDC, FGCOLOR( npTTYInfo ) ) ;
   SetBkColor( hDC, GetSysColor( COLOR_WINDOW ) ) ;
   rect = ps.rcPaint ;
   nRow =
      min( MAXROWS - 1,
           max( 0, (rect.top + YOFFSET( npTTYInfo )) / YCHAR( npTTYInfo ) ) ) ;
   nEndRow =
      min( MAXROWS - 1,
           ((rect.bottom + YOFFSET( npTTYInfo ) - 1) / YCHAR( npTTYInfo ) ) ) ;
//DonStart
   nStartCol =
//DonEnd
      min( MAXCOLS - 1,
           max( 0, (rect.left + XOFFSET( npTTYInfo )) / XCHAR( npTTYInfo ) ) ) ;
   nEndCol =
      min( MAXCOLS - 1,
           ((rect.right + XOFFSET( npTTYInfo ) - 1) / XCHAR( npTTYInfo ) ) ) ;
   nCount = nEndCol - nCol + 1 ;
   for (; nRow <= nEndRow; nRow++)
   {
//DonStart
	nCol = nStartCol;
	while (nCol <= nEndCol)
	{	// Until entire line is processed
        fReverse = *(AREVERSE( npTTYInfo ) + nRow * MAXCOLS + nCol);
        fDim = *(ADIM( npTTYInfo ) + nRow * MAXCOLS + nCol);
        fUnderline = *(AUNDERLINE( npTTYInfo ) + nRow * MAXCOLS + nCol);
        fBlink = *(ABLINK( npTTYInfo ) + nRow * MAXCOLS + nCol);

		cTextColor = TEXTCOLOR( npTTYInfo );
		cBackColor = BACKCOLOR( npTTYInfo );
		if (fDim)
			cTextColor = TEXTDIMCOLOR( npTTYInfo );
		if (fUnderline)
		  	;
		if (fBlink)
			cTextColor = TEXTBLINKCOLOR( npTTYInfo );
		if (fBlink && fDim)
			cTextColor = TEXTBLINKDIMCOLOR( npTTYInfo );;
		if (fReverse)
		{		// Swap the colors
			cTempColor = cTextColor;
			cTextColor = cBackColor;	// TEXTREVERSECOLOR( npTTYInfo );
			cBackColor = cTempColor;	// BACKREVERSECOLOR( npTTYInfo );
		}

	   for (nLastCol = nCol + 1; nLastCol <= (DWORD)nEndCol; nLastCol++)
	   {		// Find first place status changes
	        if ((fReverse != *(AREVERSE( npTTYInfo ) + nRow * MAXCOLS + nLastCol))
	        || (fDim != *(ADIM( npTTYInfo ) + nRow * MAXCOLS + nLastCol))
	        || (fUnderline != *(AUNDERLINE( npTTYInfo ) + nRow * MAXCOLS + nLastCol))
	        || (fBlink != *(ABLINK( npTTYInfo ) + nRow * MAXCOLS + nLastCol)))
				break;		// Status changed, output up to here
	   }
	   nLastCol--;		// Last char in this block
	   nCount = nLastCol - nCol + 1 ;
//DonEnd
		nVertPos = (nRow * YCHAR( npTTYInfo )) - YOFFSET( npTTYInfo ) ;
		nHorzPos = (nCol * XCHAR( npTTYInfo )) - XOFFSET( npTTYInfo ) ;
		rect.top = nVertPos ;
		rect.bottom = nVertPos + YCHAR( npTTYInfo ) ;
		rect.left = nHorzPos ;
		rect.right = nHorzPos + XCHAR( npTTYInfo ) * nCount ;
		SetBkMode( hDC, OPAQUE ) ;
//DonStart
		SetTextColor(hDC, cTextColor);
		SetBkColor(hDC, cBackColor);
//DonEnd
		ExtTextOut( hDC, nHorzPos, nVertPos, ETO_OPAQUE | ETO_CLIPPED, &rect,
		          (LPSTR)( SCREEN( npTTYInfo ) + nRow * MAXCOLS + nCol ),
		          nCount, NULL ) ;
//DonStart
		if (fUnderline)
		{
			rect.top += UNDERLINEPOS( npTTYInfo ) - 1;
			rect.bottom = rect.top + 1;
			InvertRect( hDC, &rect);
		}
		nCol = nLastCol + 1 ;
//DonEnd
	 }
   }
   SelectObject( hDC, hOldFont ) ;
   EndPaint( hWnd, &ps ) ;
   MoveTTYCursor( hWnd ) ;
   return ( TRUE ) ;

} // end of PaintTTY()

//---------------------------------------------------------------------------
//  BOOL NEAR SizeTTY( HWND hWnd, WORD wVertSize, WORD wHorzSize )
//
//  Description:
//     Sizes TTY and sets up scrolling regions.
//
//  Parameters:
//     HWND hWnd
//        handle to TTY window
//
//     WORD wVertSize
//        new vertical size
//
//     WORD wHorzSize
//        new horizontal size
//
//  History:   Date       Author      Comment
//              5/ 8/ 91  BryanW      Wrote it
//
//---------------------------------------------------------------------------

BOOL NEAR SizeTTY( HWND hWnd, WORD wVertSize, WORD wHorzSize )
{
   int        nScrollAmt ;
   NPTTYINFO  npTTYInfo ;
   if (NULL == (npTTYInfo = GETNPTTYINFO( hWnd )))
      return ( FALSE ) ;
   YSIZE( npTTYInfo ) = (int) wVertSize ;
   YSCROLL( npTTYInfo ) = max( 0, (MAXROWS * YCHAR( npTTYInfo )) -
                               YSIZE( npTTYInfo ) ) ;
   nScrollAmt = min( YSCROLL( npTTYInfo ), YOFFSET( npTTYInfo ) ) -
                     YOFFSET( npTTYInfo ) ;
   ScrollWindow( hWnd, 0, -nScrollAmt, NULL, NULL ) ;

   YOFFSET( npTTYInfo ) = YOFFSET( npTTYInfo ) + nScrollAmt ;
   SetScrollPos( hWnd, SB_VERT, YOFFSET( npTTYInfo ), FALSE ) ;
   SetScrollRange( hWnd, SB_VERT, 0, YSCROLL( npTTYInfo ), TRUE ) ;

   XSIZE( npTTYInfo ) = (int) wHorzSize ;
   XSCROLL( npTTYInfo ) = max( 0, (MAXCOLS * XCHAR( npTTYInfo )) -
                                XSIZE( npTTYInfo ) ) ;
   nScrollAmt = min( XSCROLL( npTTYInfo ), XOFFSET( npTTYInfo )) -
                     XOFFSET( npTTYInfo ) ;
   ScrollWindow( hWnd, 0, -nScrollAmt, NULL, NULL ) ;
   XOFFSET( npTTYInfo ) = XOFFSET( npTTYInfo ) + nScrollAmt ;
   SetScrollPos( hWnd, SB_HORZ, XOFFSET( npTTYInfo ), FALSE ) ;
   SetScrollRange( hWnd, SB_HORZ, 0, XSCROLL( npTTYInfo ), TRUE ) ;

   InvalidateRect( hWnd, NULL, TRUE ) ;

   return ( TRUE ) ;

} // end of SizeTTY()

//---------------------------------------------------------------------------
//  BOOL NEAR ScrollTTYVert( HWND hWnd, WORD wScrollCmd, WORD wScrollPos )
//
//  Description:
//     Scrolls TTY window vertically.
//
//  Parameters:
//     HWND hWnd
//        handle to TTY window
//
//     WORD wScrollCmd
//        type of scrolling we're doing
//
//     WORD wScrollPos
//        scroll position
//
//  History:   Date       Author      Comment
//              5/ 8/91   BryanW      Wrote it.
//
//---------------------------------------------------------------------------

BOOL NEAR ScrollTTYVert( HWND hWnd, WORD wScrollCmd, WORD wScrollPos )
{
   int        nScrollAmt ;
   NPTTYINFO  npTTYInfo ;

   if (NULL == (npTTYInfo = GETNPTTYINFO( hWnd )))
      return ( FALSE ) ;

   switch (wScrollCmd)
   {
      case SB_TOP:
         nScrollAmt = -YOFFSET( npTTYInfo ) ;
         break ;

      case SB_BOTTOM:
         nScrollAmt = YSCROLL( npTTYInfo ) - YOFFSET( npTTYInfo ) ;
         break ;

      case SB_PAGEUP:
         nScrollAmt = -YSIZE( npTTYInfo ) ;
         break ;

      case SB_PAGEDOWN:
         nScrollAmt = YSIZE( npTTYInfo ) ;
         break ;

      case SB_LINEUP:
         nScrollAmt = -YCHAR( npTTYInfo ) ;
         break ;

      case SB_LINEDOWN:
         nScrollAmt = YCHAR( npTTYInfo ) ;
         break ;

      case SB_THUMBPOSITION:
         nScrollAmt = wScrollPos - YOFFSET( npTTYInfo ) ;
         break ;

      default:
         return ( FALSE ) ;
   }
   if ((YOFFSET( npTTYInfo ) + nScrollAmt) > YSCROLL( npTTYInfo ))
      nScrollAmt = YSCROLL( npTTYInfo ) - YOFFSET( npTTYInfo ) ;
   if ((YOFFSET( npTTYInfo ) + nScrollAmt) < 0)
      nScrollAmt = -YOFFSET( npTTYInfo ) ;
   ScrollWindow( hWnd, 0, -nScrollAmt, NULL, NULL ) ;
   YOFFSET( npTTYInfo ) = YOFFSET( npTTYInfo ) + nScrollAmt ;
   SetScrollPos( hWnd, SB_VERT, YOFFSET( npTTYInfo ), TRUE ) ;

   return ( TRUE ) ;

} // end of ScrollTTYVert()

//---------------------------------------------------------------------------
//  BOOL NEAR ScrollTTYHorz( HWND hWnd, WORD wScrollCmd, WORD wScrollPos )
//
//  Description:
//     Scrolls TTY window horizontally.
//
//  Parameters:
//     HWND hWnd
//        handle to TTY window
//
//     WORD wScrollCmd
//        type of scrolling we're doing
//
//     WORD wScrollPos
//        scroll position
//
//  History:   Date       Author      Comment
//              5/ 8/91   BryanW      Wrote it.
//
//---------------------------------------------------------------------------

BOOL NEAR ScrollTTYHorz( HWND hWnd, WORD wScrollCmd, WORD wScrollPos )
{
   int        nScrollAmt ;
   NPTTYINFO  npTTYInfo ;

   if (NULL == (npTTYInfo = GETNPTTYINFO( hWnd )))
      return ( FALSE ) ;

   switch (wScrollCmd)
   {
      case SB_TOP:
         nScrollAmt = -XOFFSET( npTTYInfo ) ;
         break ;

      case SB_BOTTOM:
         nScrollAmt = XSCROLL( npTTYInfo ) - XOFFSET( npTTYInfo ) ;
         break ;

      case SB_PAGEUP:
         nScrollAmt = -XSIZE( npTTYInfo ) ;
         break ;

      case SB_PAGEDOWN:
         nScrollAmt = XSIZE( npTTYInfo ) ;
         break ;

      case SB_LINEUP:
         nScrollAmt = -XCHAR( npTTYInfo ) ;
         break ;

      case SB_LINEDOWN:
         nScrollAmt = XCHAR( npTTYInfo ) ;
         break ;

      case SB_THUMBPOSITION:
         nScrollAmt = wScrollPos - XOFFSET( npTTYInfo ) ;
         break ;

      default:
         return ( FALSE ) ;
   }
   if ((XOFFSET( npTTYInfo ) + nScrollAmt) > XSCROLL( npTTYInfo ))
      nScrollAmt = XSCROLL( npTTYInfo ) - XOFFSET( npTTYInfo ) ;
   if ((XOFFSET( npTTYInfo ) + nScrollAmt) < 0)
      nScrollAmt = -XOFFSET( npTTYInfo ) ;
   ScrollWindow( hWnd, -nScrollAmt, 0, NULL, NULL ) ;
   XOFFSET( npTTYInfo ) = XOFFSET( npTTYInfo ) + nScrollAmt ;
   SetScrollPos( hWnd, SB_HORZ, XOFFSET( npTTYInfo ), TRUE ) ;

   return ( TRUE ) ;

} // end of ScrollTTYHorz()

//---------------------------------------------------------------------------
//  BOOL NEAR SetTTYFocus( HWND hWnd )
//
//  Description:
//     Sets the focus to the TTY window also creates caret.
//
//  Parameters:
//     HWND hWnd
//        handle to TTY window
//
//  History:   Date       Author      Comment
//              5/ 9/91   BryanW      Wrote it.
//
//---------------------------------------------------------------------------

BOOL NEAR SetTTYFocus( HWND hWnd )
{
   NPTTYINFO  npTTYInfo ;

   if (NULL == (npTTYInfo = GETNPTTYINFO( hWnd )))
      return ( FALSE ) ;

   if (CONNECTED( npTTYInfo ) && (CURSORSTATE( npTTYInfo ) != CS_SHOW))
   {
      CreateCaret( hWnd, NULL, XCHAR( npTTYInfo ), YCHAR( npTTYInfo ) ) ;
      ShowCaret( hWnd ) ;
      CURSORSTATE( npTTYInfo ) = CS_SHOW ;
   }
   MoveTTYCursor( hWnd ) ;
   return ( TRUE ) ;

} // end of SetTTYFocus()

//---------------------------------------------------------------------------
//  BOOL NEAR KillTTYFocus( HWND hWnd )
//
//  Description:
//     Kills TTY focus and destroys the caret.
//
//  Parameters:
//     HWND hWnd
//        handle to TTY window
//
//  History:   Date       Author      Comment
//              5/ 9/91   BryanW      Wrote it.
//
//---------------------------------------------------------------------------

BOOL NEAR KillTTYFocus( HWND hWnd )
{
   NPTTYINFO  npTTYInfo ;

   if (NULL == (npTTYInfo = GETNPTTYINFO( hWnd )))
      return ( FALSE ) ;

   if (CONNECTED( npTTYInfo ) && (CURSORSTATE( npTTYInfo ) != CS_HIDE))
   {
      HideCaret( hWnd ) ;
      DestroyCaret() ;
      CURSORSTATE( npTTYInfo ) = CS_HIDE ;
   }
   return ( TRUE ) ;

} // end of KillTTYFocus()

//---------------------------------------------------------------------------
//  BOOL NEAR MoveTTYCursor( HWND hWnd )
//
//  Description:
//     Moves caret to current position.
//
//  Parameters:
//     HWND hWnd
//        handle to TTY window
//
//  History:   Date       Author      Comment
//              5/ 9/91   BryanW      Wrote it.
//
//---------------------------------------------------------------------------

BOOL NEAR MoveTTYCursor( HWND hWnd )
{
   NPTTYINFO  npTTYInfo ;

   if (NULL == (npTTYInfo = GETNPTTYINFO( hWnd )))
      return ( FALSE ) ;

   if (CONNECTED( npTTYInfo ) && (CURSORSTATE( npTTYInfo ) & CS_SHOW))
      SetCaretPos( (COLUMN( npTTYInfo ) * XCHAR( npTTYInfo )) -
                   XOFFSET( npTTYInfo ),
                   (ROW( npTTYInfo ) * YCHAR( npTTYInfo )) -
                   YOFFSET( npTTYInfo ) ) ;
   
   return ( TRUE ) ;

} // end of MoveTTYCursor()

//---------------------------------------------------------------------------
//  BOOL NEAR ProcessCOMMNotification( HWND hWnd,
//                                     WPARAM wParam, LPARAM lParam ) ;
//
//  Description:
//     Processes the WM_COMMNOTIFY message from the COMM.DRV.
//
//  Parameters:
//     HWND hWnd
//        handle to TTY window
//
//     WPARAM wParam
//        specifes the device (nCid)
//
//     LPARAM lParam
//        LOWORD contains event trigger
//        HIWORD is NULL
//
//  Win-32 Porting Issues:
//     - Function was constrained by WORD and LONG declarations.
//     - Processing under Win-32 is much simpler and additionally
//       requires the "posted message" flag to be cleared.
//
//  History:   Date       Author      Comment
//              5/10/91   BryanW      Wrote it.
//             10/18/91   BryanW      Updated to verify the event.
//              6/15/92   BryanW      Removed WORD and LONG constraints.
//              6/15/92   BryanW      Ported to Win-32.
//
//---------------------------------------------------------------------------

BOOL NEAR ProcessCOMMNotification( HWND hWnd, WPARAM wParam, LPARAM lParam )
{
   int        nLength ;
   BYTE       abIn[ MAXBLOCK + 1] ;
   NPTTYINFO  npTTYInfo ;
   MSG        msg ;
#ifndef WIN32		// for Win 3.1 only
//?	COMSTAT	ComStat;
	int		lastError;
#endif

   if (NULL == (npTTYInfo = GETNPTTYINFO( hWnd )))
      return ( FALSE ) ;

   if (!CONNECTED( npTTYInfo ))
      return ( FALSE ) ;

#ifdef WIN32
   // verify that it is a COMM event sent by our thread

   if (CN_EVENT & LOWORD( lParam ) != CN_EVENT)
      return ( FALSE ) ;

   // We loop here since it is highly likely that the buffer
   // can been filled while we are reading this block.  This
   // is especially true when operating at high baud rates
   // (e.g. >= 9600 baud).

   do
   {
      if (nLength = ReadCommBlock( hWnd, (LPSTR) abIn, MAXBLOCK ))
      {
	  	if (gfXFerMode == kTerminalMode)
		{
        	WriteTTYBlock( hWnd, (LPSTR) abIn, nLength ) ;
	         // force a paint
        	UpdateWindow( hWnd ) ;
		}
		else if (gfXFerMode == kXSendMode)
			ProcessXSend( hWnd, abIn, nLength );
		else if (gfXFerMode == kXReceiveMode)
			ProcessXReceive( hWnd, abIn, nLength );
      }
   }
   while (!PeekMessage( &msg, NULL, 0, 0, PM_NOREMOVE) || (nLength > 0)) ;
//?   while (nLength > 0) ;

   // clear our "posted notification" flag

   SetEvent( POSTEVENT( npTTYInfo ) ) ;
#else
   if (!USECNRECEIVE( npTTYInfo ))
   {
      // verify that it is a COMM event specified by our mask

      if (CN_EVENT & LOWORD( lParam ) != CN_EVENT)
         return ( FALSE ) ;

      // For Windows 3.1, rested reset the event word so we are notified
      // when the next event occurs

      GetCommEventMask( COMDEV( npTTYInfo ), EV_RXCHAR ) ;

      // We loop here since it is highly likely that the buffer
      // can been filled while we are reading this block.  This
      // is especially true when operating at high baud rates
      // (e.g. >= 9600 baud).

      do
      {
         if (nLength = ReadCommBlock( hWnd, (LPSTR) abIn, MAXBLOCK ))
         {
		  	if (gfXFerMode == kTerminalMode)
			{
	        	WriteTTYBlock( hWnd, (LPSTR) abIn, nLength ) ;
		         // force a paint
	        	UpdateWindow( hWnd ) ;
			}
			else if (gfXFerMode == kXSendMode)
				ProcessXSend( hWnd, abIn, nLength );
			else if (gfXFerMode == kXReceiveMode)
				ProcessXReceive( hWnd, abIn, nLength );
         }
      }
      while (!PeekMessage( &msg, NULL, 0, 0, PM_NOREMOVE) || (nLength > 0)) ;
   }
   else
   {
      // verify that it is a receive event

      if (CN_RECEIVE & LOWORD( lParam ) != CN_RECEIVE)
         return ( FALSE ) ;

      do
      {
         if (nLength = ReadCommBlock( hWnd, (LPSTR) abIn, MAXBLOCK ))
         {
		  	if (gfXFerMode == kTerminalMode)
			{
	        	WriteTTYBlock( hWnd, (LPSTR) abIn, nLength ) ;
		         // force a paint
	        	UpdateWindow( hWnd ) ;
			}
			else if (gfXFerMode == kXSendMode)
				ProcessXSend( hWnd, abIn, nLength );
			else if (gfXFerMode == kXReceiveMode)
				ProcessXReceive( hWnd, abIn, nLength );
         }
//?         else
//?         {	// nLength = 0 (Could be an error)
//?         	ComStat.cbInQue = 0;	// Be safe
//?         	lastError = GetCommError(COMDEV( npTTYInfo ), &ComStat);
//?         }
      }
      while (!PeekMessage( &msg, NULL, 0, 0, PM_NOREMOVE) || (nLength > 0)) ;
//?      while (!PeekMessage( &msg, NULL, 0, 0, PM_NOREMOVE ) ||
//?            (ComStat.cbInQue >= MAXBLOCK)) ;
   }
#endif

   return ( TRUE ) ;

} // end of ProcessCOMMNotification()

//---------------------------------------------------------------------------
//  BOOL NEAR ProcessTTYCharacter( HWND hWnd, BYTE bOut )
//
//  Description:
//     This simply writes a character to the port and echos it
//     to the TTY screen if fLocalEcho is set.  Some minor
//     keyboard mapping could be performed here.
//
//  Parameters:
//     HWND hWnd
//        handle to TTY window
//
//     BYTE bOut
//        byte from keyboard
//
//  History:   Date       Author      Comment
//              5/11/91   BryanW      Wrote it.
//
//---------------------------------------------------------------------------

BOOL NEAR ProcessTTYCharacter( HWND hWnd, BYTE bOut )
{
   NPTTYINFO  npTTYInfo ;

   if (NULL == (npTTYInfo = GETNPTTYINFO( hWnd )))
      return ( FALSE ) ;

   if (!CONNECTED( npTTYInfo ))
      return ( FALSE ) ;

   WriteCommByte( hWnd, bOut ) ;
   if (LOCALECHO( npTTYInfo ))
      WriteTTYBlock( hWnd, &bOut, 1 ) ;

   return ( TRUE ) ;

} // end of ProcessTTYCharacter()

//---------------------------------------------------------------------------
//  BOOL NEAR OpenConnection( HWND hWnd )
//
//  Description:
//     Opens communication port specified in the TTYINFO struct.
//     It also sets the CommState and notifies the window via
//     the fConnected flag in the TTYINFO struct.
//
//  Parameters:
//     HWND hWnd
//        handle to TTY window
//
//  Win-32 Porting Issues:
//     - OpenComm() is not supported under Win-32.  Use CreateFile()
//       and setup for OVERLAPPED_IO.
//     - Win-32 has specific communication timeout parameters.
//     - Created the secondary thread for event notification.
//
//  History:   Date       Author      Comment
//              5/ 9/91   BryanW      Wrote it.
//
//---------------------------------------------------------------------------

BOOL NEAR OpenConnection( HWND hWnd )
{            
   char       szPort[ 15 ], szTemp[ 10 ] ;
   BOOL       fRetVal ;
   HCURSOR    hOldCursor, hWaitCursor ;
   HMENU      hMenu ;
   NPTTYINFO  npTTYInfo ;

#ifdef WIN32
   HANDLE        hCommWatchThread ;
   DWORD         dwThreadID ;
   COMMTIMEOUTS  CommTimeOuts ;
#endif

   if (NULL == (npTTYInfo = GETNPTTYINFO( hWnd )))
      return ( FALSE ) ;

   // show the hourglass cursor
   hWaitCursor = LoadCursor( NULL, IDC_WAIT ) ;
   hOldCursor = SetCursor( hWaitCursor ) ;

//DonStart
	gKeepReceiving = FALSE;			// Reset this flag
	if (ghFile != (HFILE)NULL)
		ghFile = _lclose(ghFile);
	gfXFerMode = kTerminalMode;		// Make sure all characters received are sent to this terminal
//DonEnd
#ifdef WIN32
   // HACK!  This checks for the PORT number defined by
   // the combo box selection.  If it is greater than the
   // maximum number of ports, assume TELNET.

   if (PORT( npTTYInfo ) > MAXPORTS)
      lstrcpy( szPort, "\\\\.\\TELNET" ) ;
   else
   {
      // load the COM prefix string and append port number
   
      LoadString( GETHINST( hWnd ), IDS_COMPREFIX, szTemp, sizeof( szTemp ) ) ;
      wsprintf( szPort, "%s%d", (LPSTR) szTemp, PORT( npTTYInfo ) ) ;
   }
#else
   // load the COM prefix string and append port number

   LoadString( GETHINST( hWnd ), IDS_COMPREFIX, szTemp, sizeof( szTemp ) ) ;
   wsprintf( szPort, "%s%d", (LPSTR) szTemp, PORT( npTTYInfo ) ) ;
#endif

   // open COMM device

#ifdef WIN32
   if ((COMDEV( npTTYInfo ) =
      CreateFile( szPort, GENERIC_READ | GENERIC_WRITE,
                  0,                    // exclusive access
                  NULL,                 // no security attrs
                  OPEN_EXISTING,
                  FILE_ATTRIBUTE_NORMAL | 
                  FILE_FLAG_OVERLAPPED, // overlapped I/O
                  NULL )) == (HANDLE) -1 )
      return ( FALSE ) ;
   else
   {
      // get any early notifications

      SetCommMask( COMDEV( npTTYInfo ), EV_RXCHAR ) ;

      // setup device buffers

      SetupComm( COMDEV( npTTYInfo ), 4096, 4096 ) ;

      // purge any information in the buffer

      // HACK! HACK! Not really needed... the buffers are allocated
      // and clear.  If TELNET is active, the buffer will contain
      // immediate data.

//      PurgeComm( COMDEV( npTTYInfo ), PURGE_TXABORT | PURGE_RXABORT |
//                                      PURGE_TXCLEAR | PURGE_RXCLEAR ) ;

      // set up for overlapped non-blocking I/O

      CommTimeOuts.ReadIntervalTimeout = 0xFFFFFFFF ;
      CommTimeOuts.ReadTotalTimeoutMultiplier = 0 ;
      CommTimeOuts.ReadTotalTimeoutConstant = 0 ;
      CommTimeOuts.WriteTotalTimeoutMultiplier = 0 ;
      CommTimeOuts.WriteTotalTimeoutConstant = 5000 ;
      SetCommTimeouts( COMDEV( npTTYInfo ), &CommTimeOuts ) ;
   }
#else
   if ((COMDEV( npTTYInfo ) = OpenComm( szPort, RXQUEUE, TXQUEUE )) < 0)
      return ( FALSE ) ;
#endif

   fRetVal = SetupConnection( hWnd ) ;

   if (fRetVal)
   {
      CONNECTED( npTTYInfo ) = TRUE ;

#ifdef WIN32
      // In the case of Win32, we create a secondary thread
      // to watch for an event.

//		fRetVal = CommWatchProc((LPSTR)npTTYInfo);
//		fRetVal = true;
//		if (fRetVal)
      if (NULL == (hCommWatchThread =
                      CreateThread( (LPSECURITY_ATTRIBUTES) NULL,
                                    0, 
                                    (LPTHREAD_START_ROUTINE) CommWatchProc,
                                    (LPVOID) npTTYInfo,
                                    0, &dwThreadID )))
      {
         CONNECTED( npTTYInfo ) = FALSE ;
         CloseHandle( COMDEV( npTTYInfo ) ) ;
         fRetVal = FALSE ;
      }
      else
      {
         THREADID( npTTYInfo ) = dwThreadID ;
         HTHREAD( npTTYInfo ) = hCommWatchThread ;

         // Adjust thread priority
        
         SetThreadPriority( hCommWatchThread, THREAD_PRIORITY_BELOW_NORMAL ) ;
         ResumeThread( hCommWatchThread ) ;

         // assert DTR

         EscapeCommFunction( COMDEV( npTTYInfo ), SETDTR ) ;

         SetTTYFocus( hWnd ) ;

         hMenu = GetMenu( hWnd ) ;
//DonStart
//         EnableMenuItem( hMenu, IDM_DISCONNECT,
//                        MF_ENABLED | MF_BYCOMMAND ) ;
//         EnableMenuItem( hMenu, IDM_CONNECT,
//                        MF_GRAYED | MF_DISABLED | MF_BYCOMMAND ) ;
//DonEnd

         // kick TELNET into operation

         ProcessCOMMNotification( hWnd, (WPARAM) COMDEV( npTTYInfo ), 
                                  MAKELONG( CN_EVENT, 0 ) ) ;

      }
#else
      // Under Windows 3.1, we set up notifications from COMM.DRV

      if (!USECNRECEIVE( npTTYInfo ))
      {
         // In this case we really are only using the notifications
         // for the received characters - it could be expanded to
         // cover the changes in CD or other status lines.

         SetCommEventMask( COMDEV( npTTYInfo ), EV_RXCHAR ) ;

         // Enable notifications for events only.
      
         // NB:  This method does not use the specific
         // in/out queue triggers.

         EnableCommNotification( COMDEV( npTTYInfo ), hWnd, -1, -1 ) ;
      }
      else
      {
         // Enable notification for CN_RECEIVE events.

         EnableCommNotification( COMDEV( npTTYInfo ), hWnd, MAXBLOCK, -1 ) ;
      }

      // assert DTR

      EscapeCommFunction( COMDEV( npTTYInfo ), SETDTR ) ;

      SetTTYFocus( hWnd ) ;

      hMenu = GetMenu( hWnd ) ;
//DonStart
//      EnableMenuItem( hMenu, IDM_DISCONNECT,
//                      MF_ENABLED | MF_BYCOMMAND ) ;
//      EnableMenuItem( hMenu, IDM_CONNECT,
//                      MF_GRAYED | MF_DISABLED | MF_BYCOMMAND ) ;
//DonEnd
#endif
   }
   else
   {
      CONNECTED( npTTYInfo ) = FALSE ;

#ifdef WIN32
      CloseHandle( COMDEV( npTTYInfo ) ) ;
#else
      CloseComm( COMDEV( npTTYInfo ) ) ;
#endif
   }

   // restore cursor

   SetCursor( hOldCursor ) ;

   return ( fRetVal ) ;

} // end of OpenConnection()

//---------------------------------------------------------------------------
//  BOOL NEAR SetupConnection( HWND hWnd )
//
//  Description:
//     This routines sets up the DCB based on settings in the
//     TTY info structure and performs a SetCommState().
//
//  Parameters:
//     HWND hWnd
//        handle to TTY window
//
//  Win-32 Porting Issues:
//     - Win-32 requires a slightly different processing of the DCB.
//       Changes were made for configuration of the hardware handshaking
//       lines.
//
//  History:   Date       Author      Comment
//              5/ 9/91   BryanW      Wrote it.
//
//---------------------------------------------------------------------------

BOOL NEAR SetupConnection( HWND hWnd )
{
   BOOL       fRetVal ;
   BYTE       bSet ;
   DCB        dcb ;
   NPTTYINFO  npTTYInfo ;

   if (NULL == (npTTYInfo = GETNPTTYINFO( hWnd )))
      return ( FALSE ) ;

#ifdef WIN32
   dcb.DCBlength = sizeof( DCB ) ;
#endif

   GetCommState( COMDEV( npTTYInfo ), &dcb ) ;

   dcb.BaudRate = BAUDRATE( npTTYInfo ) ;
   dcb.ByteSize = BYTESIZE( npTTYInfo ) ;
   dcb.Parity = PARITY( npTTYInfo ) ;
   dcb.StopBits = STOPBITS( npTTYInfo ) ;

   // setup hardware flow control

   bSet = (BYTE) ((FLOWCTRL( npTTYInfo ) & FC_DTRDSR) != 0) ;
#ifdef WIN32
   dcb.fOutxDsrFlow = bSet ;
   if (bSet)
      dcb.fDtrControl = DTR_CONTROL_HANDSHAKE ;
   else
      dcb.fDtrControl = DTR_CONTROL_ENABLE ;
#else
   dcb.fOutxDsrFlow = dcb.fDtrflow = bSet ;
   dcb.DsrTimeout = (bSet) ? 30 : 0 ;
#endif

   bSet = (BYTE) ((FLOWCTRL( npTTYInfo ) & FC_RTSCTS) != 0) ;
#ifdef WIN32
   dcb.fOutxCtsFlow = bSet ;
   if (bSet)
      dcb.fRtsControl = RTS_CONTROL_HANDSHAKE ;
   else
      dcb.fRtsControl = RTS_CONTROL_ENABLE ;
#else
   dcb.fOutxCtsFlow = dcb.fRtsflow = bSet ;
   dcb.CtsTimeout = (bSet) ? 30 : 0 ;
#endif

   // setup software flow control

   bSet = (BYTE) ((FLOWCTRL( npTTYInfo ) & FC_XONXOFF) != 0) ;

   dcb.fInX = dcb.fOutX = bSet ;
//DonStart
   dcb.fOutX = 0;		// Can't use this
//DonEnd
   dcb.XonChar = ASCII_XON ;
   dcb.XoffChar = ASCII_XOFF ;
   dcb.XonLim = 100 ;
   dcb.XoffLim = 100 ;

   // other various settings

   dcb.fBinary = TRUE ;
   dcb.fParity = TRUE ;

#ifndef WIN32
   dcb.fRtsDisable = FALSE ;
   dcb.fDtrDisable = FALSE ;
   fRetVal = !(SetCommState( &dcb ) < 0) ;
#else
   fRetVal = SetCommState( COMDEV( npTTYInfo ), &dcb ) ;
#endif

   return ( fRetVal ) ;

} // end of SetupConnection()

//---------------------------------------------------------------------------
//  BOOL NEAR CloseConnection( HWND hWnd )
//
//  Description:
//     Closes the connection to the port.  Resets the connect flag
//     in the TTYINFO struct.
//
//  Parameters:
//     HWND hWnd
//        handle to TTY window
//
//  Win-32 Porting Issues:
//     - Needed to stop secondary thread.  SetCommMask() will signal the
//       WaitCommEvent() event and the thread will halt when the
//       CONNECTED() flag is clear.
//     - Use new PurgeComm() API to clear communications driver before
//       closing device.
//
//  History:   Date       Author      Comment
//              5/ 9/91   BryanW      Wrote it.
//              6/15/92   BryanW      Ported to Win-32.
//
//---------------------------------------------------------------------------

BOOL NEAR CloseConnection( HWND hWnd )
{
   HMENU      hMenu ;
   NPTTYINFO  npTTYInfo ;
#ifdef WIN32
   HANDLE       hCommWatchThread ;
   DWORD		lExitCode;
   BOOL			bFlag;
#endif
   if (NULL == (npTTYInfo = GETNPTTYINFO( hWnd )))
      return ( FALSE ) ;

   // set connected flag to FALSE

   CONNECTED( npTTYInfo ) = FALSE ;

#ifdef WIN32
   // disable event notification and wait for thread
   // to halt

   SetCommMask( COMDEV( npTTYInfo ), 0 ) ;

   // block until thread has been halted

#if defined WIN95_THREAD_HACK		// HACK - Win95 Doesn't seem to  want to kill this thread!
									// So I'm gonna do it explicity!!!
   hCommWatchThread = HTHREAD( npTTYInfo );
   if (hCommWatchThread != NULL)
   {
		bFlag = GetExitCodeThread(hCommWatchThread, &lExitCode);
		if (bFlag)
			ExitThread(lExitCode);		// HACK - Win95 Doesn't seem to  want to kill this thread!
	}
#endif
   while (THREADID( npTTYInfo ) != 0) ;
   
#else
   // Disable event notification.  Using a NULL hWnd tells
   // the COMM.DRV to disable future notifications.

   EnableCommNotification( COMDEV( npTTYInfo ), NULL, -1, -1 ) ;
#endif

   // kill the focus

   KillTTYFocus( hWnd ) ;

#ifdef WIN32
   // drop DTR

   EscapeCommFunction( COMDEV( npTTYInfo ), CLRDTR ) ;

   // purge any outstanding reads/writes and close device handle

   PurgeComm( COMDEV( npTTYInfo ), PURGE_TXABORT | PURGE_RXABORT |
                                   PURGE_TXCLEAR | PURGE_RXCLEAR ) ;
   CloseHandle( COMDEV( npTTYInfo ) ) ;
#else
   // drop DTR

   EscapeCommFunction( COMDEV( npTTYInfo ), CLRDTR ) ;

   // close comm connection

   CloseComm( COMDEV( npTTYInfo ) ) ;
#endif

   // change the selectable items in the menu

   hMenu = GetMenu( hWnd ) ;
//DonStart
//   EnableMenuItem( hMenu, IDM_DISCONNECT,
//                   MF_GRAYED | MF_DISABLED | MF_BYCOMMAND ) ;
//   EnableMenuItem( hMenu, IDM_CONNECT,
//                   MF_ENABLED | MF_BYCOMMAND ) ;
//DonEnd

   return ( TRUE ) ;

} // end of CloseConnection()

//---------------------------------------------------------------------------
//  int NEAR ReadCommBlock( HWND hWnd, LPSTR lpszBlock, int nMaxLength )
//
//  Description:
//     Reads a block from the COM port and stuffs it into
//     the provided block.
//
//  Parameters:
//     HWND hWnd
//        handle to TTY window
//
//     LPSTR lpszBlock
//        block used for storage
//
//     int nMaxLength
//        max length of block to read
//
//  Win-32 Porting Issues:
//     - ReadComm() has been replaced by ReadFile() in Win-32.
//     - Overlapped I/O has been implemented.
//
//  History:   Date       Author      Comment
//              5/10/91   BryanW      Wrote it.
//
//---------------------------------------------------------------------------

int NEAR ReadCommBlock( HWND hWnd, LPSTR lpszBlock, int nMaxLength )
{
#ifdef WIN32
   BOOL       fReadStat ;
   COMSTAT    ComStat ;
   DWORD      dwErrorFlags, dwLength ;
#else
   int        nError, nLength ;
#endif

   char       szError[ 10 ] ;
   NPTTYINFO  npTTYInfo ;

   if (NULL == (npTTYInfo = GETNPTTYINFO( hWnd )))
      return ( FALSE ) ;

#ifdef WIN32
   ClearCommError( COMDEV( npTTYInfo ), &dwErrorFlags, &ComStat ) ;
   if ((dwErrorFlags > 0) && DISPLAYERRORS( npTTYInfo ))
   {
      wsprintf( szError, "<CE-%u>", dwErrorFlags ) ;
      WriteTTYBlock( hWnd, szError, lstrlen( szError ) ) ;
   }

   dwLength = min( (DWORD) nMaxLength, ComStat.cbInQue ) ;

   if (dwLength > 0)
   {
      fReadStat = ReadFile( COMDEV( npTTYInfo ), lpszBlock,
                            dwLength, &dwLength, &READ_OS( npTTYInfo ) ) ;
      if (!fReadStat)
      {
         if (GetLastError() == ERROR_IO_PENDING)
         {
            // wait for a second for this transmission to complete

            if (WaitForSingleObject( READ_OS( npTTYInfo ).hEvent, 1000 ))
               dwLength = 0 ;
            else
            {
               GetOverlappedResult( COMDEV( npTTYInfo ),
                                    &READ_OS( npTTYInfo ),
                                    &dwLength, FALSE ) ;
               READ_OS( npTTYInfo ).Offset += dwLength ;
            }
         }
         else
            // some other error occurred
		{		//?
		   ClearCommError( COMDEV( npTTYInfo ), &dwErrorFlags, &ComStat ) ;
		   if ((dwErrorFlags > 0) && DISPLAYERRORS( npTTYInfo ))
		   {
		      wsprintf( szError, "<CE-%u>", dwErrorFlags ) ;
		      WriteTTYBlock( hWnd, szError, lstrlen( szError ) ) ;
		   }

		   dwLength = min( (DWORD) nMaxLength, ComStat.cbInQue ) ;

            dwLength = 0 ;
		}
      }
   }

   return ( dwLength ) ;
#else
   nLength = ReadComm( COMDEV( npTTYInfo ), lpszBlock, nMaxLength ) ;

   if (nLength < 0)
   {
      nLength *= -1 ;
      while (nError = GetCommError( COMDEV( npTTYInfo ), NULL ))
      {
         if (DISPLAYERRORS( npTTYInfo ))
         {
            wsprintf( szError, "<CE-%d>", nError ) ;
            WriteTTYBlock( hWnd, szError, lstrlen( szError ) ) ;
         }
      }
   }
   return ( nLength ) ;
#endif


} // end of ReadCommBlock()

//---------------------------------------------------------------------------
//  BOOL NEAR WriteCommByte( HWND hWnd, BYTE bByte )
//
//  Description:
//     Writes a byte to the COM port specified in the associated
//     TTY info structure.
//
//  Parameters:
//     HWND hWnd
//        handle to TTY window
//
//     BYTE bByte
//        byte to write to port
//
//  Win-32 Porting Issues:
//     - WriteComm() has been replaced by WriteFile() in Win-32.
//     - Overlapped I/O has been implemented.
//
//  History:   Date       Author      Comment
//              5/10/91   BryanW      Wrote it.
//
//---------------------------------------------------------------------------

BOOL NEAR WriteCommByte( HWND hWnd, BYTE bByte )
{
#ifdef WIN32
   BOOL        fWriteStat ;
   DWORD       dwBytesWritten ;
#endif
   NPTTYINFO  npTTYInfo ;

   if (NULL == (npTTYInfo = GETNPTTYINFO( hWnd )))
      return ( FALSE ) ;

#ifdef WIN32
   fWriteStat = WriteFile( COMDEV( npTTYInfo ), (LPSTR) &bByte, 1,
                           &dwBytesWritten, &WRITE_OS( npTTYInfo ) ) ;
   if (!fWriteStat && (GetLastError() == ERROR_IO_PENDING))
   {
      // wait for a second for this transmission to complete

      if (WaitForSingleObject( WRITE_OS( npTTYInfo ).hEvent, 1000 ))
         dwBytesWritten = 0 ;
      else
      {
         fWriteStat = GetOverlappedResult( COMDEV( npTTYInfo ),
                              &WRITE_OS( npTTYInfo ),
                              &dwBytesWritten, FALSE ) ;
		if (!fWriteStat)
		{
			dwBytesWritten = GetLastError();
			return true;
		}
         WRITE_OS( npTTYInfo ).Offset += dwBytesWritten ;
      }
   }
#else
   WriteComm( COMDEV( npTTYInfo ), (LPSTR) &bByte, 1 ) ;
#endif
   return ( TRUE ) ;

} // end of WriteCommByte()

//---------------------------------------------------------------------------
//  BOOL NEAR WriteTTYBlock( HWND hWnd, LPSTR lpBlock, int nLength )
//
//  Description:
//     Writes block to TTY screen.  Nothing fancy - just
//     straight TTY.
//
//  Parameters:
//     HWND hWnd
//        handle to TTY window
//
//     LPSTR lpBlock
//        far pointer to block of data
//
//     int nLength
//        length of block
//
//  History:   Date       Author      Comment
//              5/ 9/91   BryanW      Wrote it.
//              5/20/91   BryanW      Modified... not character based,
//                                    block based now.  It was processing
//                                    per char.
//
//---------------------------------------------------------------------------

BOOL NEAR WriteTTYBlock( HWND hWnd, LPSTR lpBlock, int nLength )
{
   int        i ;
   NPTTYINFO  npTTYInfo ;
   RECT       rect ;
	short col;
	short row;

   if (NULL == (npTTYInfo = GETNPTTYINFO( hWnd )))
      return ( FALSE ) ;

   for (i = 0 ; i < nLength; i++)
   {
//DonStart
	if (FUNHDR( npTTYInfo ))
	{
		if (lpBlock[ i ] == kTermID2)
		{
			ProcessTTYCharacter( hWnd, kTermIDHdr );
			ProcessTTYCharacter( hWnd, 'o' );
			ProcessTTYCharacter( hWnd, '#' );
	
			ProcessTTYCharacter( hWnd, '1' );
			ProcessTTYCharacter( hWnd, 'T' );
			ProcessTTYCharacter( hWnd, 'y' );

//			ProcessTTYCharacter( hWnd, 'e' );
//			ProcessTTYCharacter( hWnd, 'R' );
//			ProcessTTYCharacter( hWnd, 'Y' );
		}
		else if (lpBlock[ i ] == kReverseOn2)
		{
			REVERSE( npTTYInfo ) = On;
		}
		else if (lpBlock[ i ] == kReverseOff2)
		{
			REVERSE( npTTYInfo ) = Off;
		}
//		Find out what the protocol is for read term id	}
//		{I have to review the documentation to see why this works}
		FUNHDR( npTTYInfo ) = Off;
	}
	else if (POSITIONFLAG( npTTYInfo ))
	{		// Char position mode
		if (POSITIONFLAG( npTTYInfo ) == 2)
            COLUMN( npTTYInfo ) = lpBlock[ i ];
		if (POSITIONFLAG( npTTYInfo ) == 1)
            ROW( npTTYInfo ) = lpBlock[ i ];
		POSITIONFLAG( npTTYInfo )--;
		if (POSITIONFLAG( npTTYInfo ) == 0)
            MoveTTYCursor( hWnd ) ;
	}
	else
	{
      switch (lpBlock[ i ])
      {
		case kNull:
            break ;
		case kA1:
            break ;
		case kEndReverse:
			REVERSE( npTTYInfo ) = Off;
            break ;
		case kA3:
            break ;
		case kA4:
            break ;
		case kReadCurPos:
			ProcessTTYCharacter( hWnd, kCurPosHdr );
			ProcessTTYCharacter( hWnd, (char)COLUMN( npTTYInfo ) );
			ProcessTTYCharacter( hWnd, (char)ROW( npTTYInfo ) );
            break ;
		case kA6:
            break ;
		case kBell:
//         case ASCII_BEL:
            // Bell
            MessageBeep( 0 ) ;
            break ;
		case kHome:
            ROW( npTTYInfo ) = 0 ;
            COLUMN( npTTYInfo ) = 0 ;
            MoveTTYCursor( hWnd ) ;
            break ;
		case kTab:
//			{Ignore tab received}
            break ;
		case kNewLine:
//         case ASCII_LF:
            	// Line feed
//			Col := 1;
//			Row := Row + 1;
            if (NEWLINE( npTTYInfo ))
               WriteTTYBlock( hWnd, "\r", 1 ) ;

            if (ROW( npTTYInfo )++ == MAXROWS - 1)
            {
	            if (PAGE( npTTYInfo ))
				{
	               ROW( npTTYInfo ) = 0;
				}
			   else
			   {
	               _fmemmove( (LPSTR) (SCREEN( npTTYInfo )),
	                          (LPSTR) (SCREEN( npTTYInfo ) + MAXCOLS),
	                          (MAXROWS - 1) * MAXCOLS ) ;
	               _fmemset( (LPSTR) (SCREEN( npTTYInfo ) + (MAXROWS - 1) * MAXCOLS),
	                         ' ', MAXCOLS ) ;

	               _fmemmove( (LPSTR) (AREVERSE( npTTYInfo )),
	                          (LPSTR) (AREVERSE( npTTYInfo ) + MAXCOLS),
	                          (MAXROWS - 1) * MAXCOLS ) ;
	               _fmemset( (LPSTR) (AREVERSE( npTTYInfo ) + (MAXROWS - 1) * MAXCOLS),
	                         Off, MAXCOLS ) ;
	               _fmemmove( (LPSTR) (ADIM( npTTYInfo )),
	                          (LPSTR) (ADIM( npTTYInfo ) + MAXCOLS),
	                          (MAXROWS - 1) * MAXCOLS ) ;
	               _fmemset( (LPSTR) (ADIM( npTTYInfo ) + (MAXROWS - 1) * MAXCOLS),
	                         Off, MAXCOLS ) ;
	               _fmemmove( (LPSTR) (ABLINK( npTTYInfo )),
	                          (LPSTR) (ABLINK( npTTYInfo ) + MAXCOLS),
	                          (MAXROWS - 1) * MAXCOLS ) ;
	               _fmemset( (LPSTR) (ABLINK( npTTYInfo ) + (MAXROWS - 1) * MAXCOLS),
	                         Off, MAXCOLS ) ;
	               _fmemmove( (LPSTR) (AUNDERLINE( npTTYInfo )),
	                          (LPSTR) (AUNDERLINE( npTTYInfo ) + MAXCOLS),
	                          (MAXROWS - 1) * MAXCOLS ) ;
	               _fmemset( (LPSTR) (AUNDERLINE( npTTYInfo ) + (MAXROWS - 1) * MAXCOLS),
	                         Off, MAXCOLS ) ;

	               InvalidateRect( hWnd, NULL, FALSE ) ;
	               ROW( npTTYInfo )-- ;
			   }
            }
            MoveTTYCursor( hWnd ) ;
            break ;
		case kEraseEol:
            rect.left = (COLUMN( npTTYInfo ) * XCHAR( npTTYInfo )) -
                        XOFFSET( npTTYInfo ) ;
	        rect.right = ((MAXCOLS) * XCHAR( npTTYInfo )) -
                        XOFFSET( npTTYInfo )  - 1;
            rect.top = (ROW( npTTYInfo ) * YCHAR( npTTYInfo )) -
                       YOFFSET( npTTYInfo ) ;
	        rect.bottom = rect.top + YCHAR( npTTYInfo ) ;
			for (col = COLUMN( npTTYInfo ); col <= MAXCOLS - 1; col++)
			{

	            *(SCREEN( npTTYInfo ) + ROW( npTTYInfo ) * MAXCOLS +
	                col) = ' ' ;
	            *(AREVERSE( npTTYInfo ) + ROW( npTTYInfo ) * MAXCOLS + col) = Off;
	            *(ADIM( npTTYInfo ) + ROW( npTTYInfo ) * MAXCOLS + col) = Off;
	            *(AUNDERLINE( npTTYInfo ) + ROW( npTTYInfo ) * MAXCOLS + col) = Off;
	            *(ABLINK( npTTYInfo ) + ROW( npTTYInfo ) * MAXCOLS + col) = Off;
			}
	        InvalidateRect( hWnd, &rect, FALSE ) ;
            break ;
		case kClearScreen:
			for (row = 0; row <= MAXROWS - 1; row++)
			for (col = 0; col <= MAXCOLS - 1; col++)
			{
	            *(SCREEN( npTTYInfo ) + row * MAXCOLS + col) = ' ' ;
	            *(AREVERSE( npTTYInfo ) + row * MAXCOLS + col) = Off;
	            *(ADIM( npTTYInfo ) + row * MAXCOLS + col) = Off;
	            *(AUNDERLINE( npTTYInfo ) + row * MAXCOLS + col) = Off;
	            *(ABLINK( npTTYInfo ) + row * MAXCOLS + col) = Off;
			}
            REVERSE( npTTYInfo ) = Off;
            DIM( npTTYInfo ) = Off;
            UNDERLINE( npTTYInfo ) = Off;
            BLINK( npTTYInfo ) = Off;
            ROW( npTTYInfo ) = 0 ;
            COLUMN( npTTYInfo ) = 0 ;
	        InvalidateRect( hWnd, NULL, FALSE ) ;
			break;
		case kCReturn:
//         case ASCII_CR:
            // Carriage return
            COLUMN( npTTYInfo ) = 0 ;
            MoveTTYCursor( hWnd ) ;
//x            if (NEWLINE( npTTYInfo ))
//x               WriteTTYBlock( hWnd, "\n", 1 ) ;
            break;
 		case kStartBlink:
			BLINK( npTTYInfo ) = On;
            break ;
		case kEndBlink:
			BLINK( npTTYInfo ) = Off;
            break ;
		case kPosCursor:
//		case kPositionCursor:
			POSITIONFLAG( npTTYInfo )  = 2;
			break;
		case kA17:
            break ;
		case kPageOff:
			PAGE( npTTYInfo ) = Off;
            break ;
		case kPageOn:
			PAGE( npTTYInfo ) = On;
            break ;
		case kStartUnderline:
			UNDERLINE( npTTYInfo ) = On;
            break ;
		case kEndUnderline:
			UNDERLINE( npTTYInfo ) = Off;
            break ;
		case kStartReverse:
			REVERSE( npTTYInfo ) = On;
            break ;
		case kUpCur:
            if (ROW( npTTYInfo ) > 0)
               ROW( npTTYInfo ) -- ;
            MoveTTYCursor( hWnd ) ;
            break ;
		case kRightCur:
//		case kRight:
            if (COLUMN( npTTYInfo ) < MAXCOLS - 1)
               COLUMN( npTTYInfo )++ ;
            else if (AUTOWRAP( npTTYInfo ))
               WriteTTYBlock( hWnd, "\r\n", 2 ) ;
            MoveTTYCursor( hWnd ) ;
			break;
		case kLeftCur:
//         case ASCII_BS:
            // Backspace
            if (COLUMN( npTTYInfo ) > 0)
               COLUMN( npTTYInfo ) -- ;
            MoveTTYCursor( hWnd ) ;
            break ;
		case kDownCur:
            if (ROW( npTTYInfo )++ == MAXROWS - 1)
 	            ROW( npTTYInfo ) = 0;
            MoveTTYCursor( hWnd ) ;
            break ;
		case kA27:
            break ;
		case kDimOn:
			DIM( npTTYInfo ) = On;
            break ;
		case kDimOff:
			DIM( npTTYInfo ) = Off;
            break ;
		case kTermID1:
			FUNHDR( npTTYInfo ) = On;
            break ;
//DonEnd

            default:
            *(SCREEN( npTTYInfo ) + ROW( npTTYInfo ) * MAXCOLS +
                COLUMN( npTTYInfo )) = lpBlock[ i ] ;

//DonStart
		  *(AREVERSE( npTTYInfo ) + ROW( npTTYInfo ) * MAXCOLS + COLUMN( npTTYInfo )) = REVERSE( npTTYInfo );
		  *(ADIM( npTTYInfo ) + ROW( npTTYInfo ) * MAXCOLS + COLUMN( npTTYInfo )) = DIM( npTTYInfo );
		  *(AUNDERLINE( npTTYInfo ) + ROW( npTTYInfo ) * MAXCOLS + COLUMN( npTTYInfo )) = UNDERLINE( npTTYInfo );
		  *(ABLINK( npTTYInfo ) + ROW( npTTYInfo ) * MAXCOLS + COLUMN( npTTYInfo )) = BLINK( npTTYInfo );
//DonEnd

            rect.left = (COLUMN( npTTYInfo ) * XCHAR( npTTYInfo )) -
                        XOFFSET( npTTYInfo ) ;
            rect.right = rect.left + XCHAR( npTTYInfo ) ;
            rect.top = (ROW( npTTYInfo ) * YCHAR( npTTYInfo )) -
                       YOFFSET( npTTYInfo ) ;
            rect.bottom = rect.top + YCHAR( npTTYInfo ) ;
            InvalidateRect( hWnd, &rect, FALSE ) ;

            // Line wrap
            if (COLUMN( npTTYInfo ) < MAXCOLS - 1)
               COLUMN( npTTYInfo )++ ;
            else if (AUTOWRAP( npTTYInfo ))
               WriteTTYBlock( hWnd, "\r\n", 2 ) ;
            break;
		}
    }
   };
   return ( TRUE ) ;

} // end of WriteTTYBlock()

//---------------------------------------------------------------------------
//  VOID NEAR GoModalDialogBoxParam( HINSTANCE hInstance,
//                                   LPCSTR lpszTemplate, HWND hWnd,
//                                   DLGPROC lpDlgProc, LPARAM lParam )
//
//  Description:
//     It is a simple utility function that simply performs the
//     MPI and invokes the dialog box with a DWORD paramter.
//
//  Parameters:
//     similar to that of DialogBoxParam() with the exception
//     that the lpDlgProc is not a procedure instance
//
//  History:   Date       Author      Comment
//              5/10/91   BryanW      Wrote it.
//
//---------------------------------------------------------------------------

VOID NEAR GoModalDialogBoxParam( HINSTANCE hInstance, LPCSTR lpszTemplate,
                                 HWND hWnd, DLGPROC lpDlgProc, LPARAM lParam )
{
   DLGPROC  lpProcInstance ;

   lpProcInstance = (DLGPROC) MakeProcInstance( (FARPROC) lpDlgProc,
                                                hInstance ) ;
   DialogBoxParam( hInstance, lpszTemplate, hWnd, lpProcInstance, lParam ) ;
   FreeProcInstance( (FARPROC) lpProcInstance ) ;

} // end of GoModalDialogBoxParam()

//---------------------------------------------------------------------------
//  BOOL FAR PASCAL AboutDlgProc( HWND hDlg, UINT uMsg,
//                                WPARAM wParam, LPARAM lParam )
//
//  Description:
//     Simulates the Windows System Dialog Box.
//
//  Parameters:
//     Same as standard dialog procedures.
//
//  History:   Date       Author      Comment
//             10/19/91   BryanW      Added this little extra.
//      
//---------------------------------------------------------------------------

BOOL FAR PASCAL AboutDlgProc( HWND hDlg, UINT uMsg,
                              WPARAM wParam, LPARAM lParam )
{
   switch (uMsg)
   {
      case WM_INITDIALOG:
      {
#ifdef WIN32
         char         szBuffer[ MAXLEN_TEMPSTR ], szTemp[ MAXLEN_TEMPSTR ];
         WORD         wRevision, wVersion ;
#else
         int          idModeString ;
         char         szBuffer[ MAXLEN_TEMPSTR ], szTemp[ MAXLEN_TEMPSTR ] ;
         DWORD        dwFreeMemory, dwWinFlags ;
         WORD         wFreeResources, wRevision, wVersion ;
#endif

#ifdef ABOUTDLG_USEBITMAP
         // if we are using the bitmap, hide the icon

         ShowWindow( GetDlgItem( hDlg, IDD_ABOUTICON ), SW_HIDE ) ;
#endif
         // sets up the version number for Windows

         wVersion = LOWORD( GetVersion() ) ;
         wRevision = HIBYTE( wVersion ) ;
         wVersion = LOBYTE( wVersion ) ;

         GetDlgItemText( hDlg, IDD_TITLELINE, szTemp, sizeof( szTemp ) ) ;
         wsprintf( szBuffer, szTemp, wVersion, wRevision ) ;
         SetDlgItemText( hDlg, IDD_TITLELINE, szBuffer ) ;
//#ifdef WIN32
//		Sleep(0);		// Release timeslice to display screen
//#else
//		WaitMessage();
//#endif

         // sets up version number for TTY

         GetDlgItemText( hDlg, IDD_VERSION, szTemp, sizeof( szTemp ) ) ;
         wsprintf( szBuffer, szTemp, VER_MAJOR, VER_MINOR, VER_BUILD ) ;
         SetDlgItemText( hDlg, IDD_VERSION, (LPSTR) szBuffer ) ;

         // get by-line

         LoadString( GETHINST( hDlg ), IDS_BYLINE, szBuffer,
                     sizeof( szBuffer ) ) ;
         SetDlgItemText( hDlg, IDD_BYLINE, szBuffer ) ;

#ifndef WIN32
         // set windows mode information

         dwWinFlags = GetWinFlags() ;
         if (dwWinFlags & WF_ENHANCED)
            idModeString = IDS_MODE_ENHANCED ;
         else if (dwWinFlags & WF_STANDARD)
            idModeString = IDS_MODE_STANDARD ;
         else if (dwWinFlags & WF_WLO)
            idModeString = IDS_MODE_WLO ;
         else
            idModeString = IDS_MODE_UNDEF ;

         LoadString( GETHINST( hDlg ), idModeString, szBuffer,
                     sizeof( szBuffer ) ) ;
         SetDlgItemText( hDlg, IDD_WINDOWSMODE, szBuffer ) ;
#else
         SetDlgItemText( hDlg, IDD_WINDOWSMODE, "NT Mode" ) ;
#endif

#ifndef WIN32
         // get free memory information

         dwFreeMemory = GetFreeSpace( 0 ) / 1024L ;
         GetDlgItemText( hDlg, IDD_FREEMEM, szTemp, sizeof( szTemp ) ) ;
         wsprintf( szBuffer, szTemp, dwFreeMemory ) ;
         SetDlgItemText( hDlg, IDD_FREEMEM, (LPSTR) szBuffer ) ;

         // get free resources information

         wFreeResources = GetFreeSystemResources( 0 ) ;
         GetDlgItemText( hDlg, IDD_RESOURCES, szTemp, sizeof( szTemp ) ) ;
         wsprintf( szBuffer, szTemp, wFreeResources ) ;
         SetDlgItemText( hDlg, IDD_RESOURCES, (LPSTR) szBuffer ) ;
#endif
      }
      return ( TRUE ) ;

#ifdef ABOUTDLG_USEBITMAP
      // used to paint the bitmap

      case WM_PAINT:
      {
         HBITMAP      hBitMap ;
         HDC          hDC, hMemDC ;
         PAINTSTRUCT  ps ;

         // load bitmap and display it

         hDC = BeginPaint( hDlg, &ps ) ;
         if (NULL != (hMemDC = CreateCompatibleDC( hDC )))
         {
            hBitMap = LoadBitmap( GETHINST( hDlg ),
                                  MAKEINTRESOURCE( TTYBITMAP ) ) ;
            hBitMap = SelectObject( hMemDC, hBitMap ) ;
            BitBlt( hDC, 10, 10, 64, 64, hMemDC, 0, 0, SRCCOPY ) ;
            DeleteObject( SelectObject( hMemDC, hBitMap ) ) ;
            DeleteDC( hMemDC ) ;
         }
         EndPaint( hDlg, &ps ) ;
      }
      break ;
#endif

      case WM_COMMAND:
         if (LOWORD( wParam ) == IDD_OK)
         {
            EndDialog( hDlg, TRUE ) ;
            return ( TRUE ) ;
         }
         break;
   }
   return ( FALSE ) ;

} // end of AboutDlgProc()

//DonStart
BOOL FAR PASCAL AutoRecProc( HWND hDlg, UINT uMsg,
                              WPARAM wParam, LPARAM lParam )
{
   switch (uMsg)
   {
      case WM_INITDIALOG:
      {
	  	ghDlg = hDlg;
         break;
	  }
      case WM_COMMAND:
         if (LOWORD( wParam ) == IDD_CANCEL)
         {
//?			fFlag = WriteCommByte((HANDLE)hWnd, kCAN);	//		{Send a 'Block ok' to DG}
			gKeepReceiving = FALSE;			// Reset this flag
			if (ghFile != (HFILE)NULL)
				ghFile = _lclose(ghFile);
			gfXFerMode = kTerminalMode;		// All Done, Don't capture any more characters!
            EndDialog( hDlg, TRUE ) ;
	  		ghDlg = NULL;
            return ( TRUE ) ;
         }
         break;
   }
   return ( FALSE ) ;

} // end of AboutDlgProc()
//DonEnd

//---------------------------------------------------------------------------
//  VOID NEAR FillComboBox( HINSTANCE hInstance, HWND hCtrlWnd, int nIDString,
//                          WORD NEAR *npTable, WORD wTableLen,
//                          WORD wCurrentSetting )
//
//  Description:
//     Fills the given combo box with strings from the resource
//     table starting at nIDString.  Associated items are
//     added from given table.  The combo box is notified of
//     the current setting.
//
//  Parameters:
//     HINSTANCE hInstance
//        handle to application instance
//
//     HWND hCtrlWnd
//        handle to combo box control
//
//     int nIDString
//        first resource string id
//
//     DWORD NEAR *npTable
//        near point to table of associated values
//
//     WORD wTableLen
//        length of table
//
//     DWORD dwCurrentSetting
//        current setting (for combo box selection)
//
//  History:   Date       Author      Comment
//             10/20/91   BryanW      Pulled from the init procedure.
//
//---------------------------------------------------------------------------

VOID NEAR FillComboBox( HINSTANCE hInstance, HWND hCtrlWnd, int nIDString,
                        DWORD NEAR *npTable, WORD wTableLen,
                        DWORD dwCurrentSetting )
{
   char  szBuffer[ MAXLEN_TEMPSTR ] ;
   WORD  wCount, wPosition ;

   for (wCount = 0; wCount < wTableLen; wCount++)
   {
      // load the string from the string resources and
      // add it to the combo box

      LoadString( hInstance, nIDString + wCount, szBuffer, sizeof( szBuffer ) ) ;
      wPosition = LOWORD( SendMessage( hCtrlWnd, CB_ADDSTRING, 0,
                                       (LPARAM) (LPSTR) szBuffer ) ) ;

      // use item data to store the actual table value

      SendMessage( hCtrlWnd, CB_SETITEMDATA, (WPARAM) wPosition,
                   (LPARAM) *(npTable + wCount) ) ;

      // if this is our current setting, select it

      if (*(npTable + wCount) == dwCurrentSetting)
         SendMessage( hCtrlWnd, CB_SETCURSEL, (WPARAM) wPosition, 0L ) ;
   }

} // end of FillComboBox()

//---------------------------------------------------------------------------
//  BOOL NEAR SettingsDlgInit( HWND hDlg )
//
//  Description:
//     Puts current settings into dialog box (via CheckRadioButton() etc.)
//
//  Parameters:
//     HWND hDlg
//        handle to dialog box
//
//  Win-32 Porting Issues:
//     - Constants require DWORD arrays for baud rate table, etc.
//     - There is no "MAXCOM" function in Win-32.  Number of COM ports
//       is assumed to be 4.
//
//  History:   Date       Author      Comment
//              5/11/91   BryanW      Wrote it.
//             10/20/91   BryanW      Dialog revision.
//             10/24/91   BryanW      Fixed bug with EscapeCommFunction().
//              6/15/92   BryanW      Ported to Win-32.
//
//---------------------------------------------------------------------------

BOOL NEAR SettingsDlgInit( HWND hDlg )
{
   char       szBuffer[ MAXLEN_TEMPSTR ], szTemp[ MAXLEN_TEMPSTR ] ;
   NPTTYINFO  npTTYInfo ;
   WORD       wCount, wMaxCOM, wPosition ;

   if (NULL == (npTTYInfo = (NPTTYINFO) GET_PROP( hDlg, ATOM_TTYINFO )))
      return ( FALSE ) ;

#ifdef WIN32
   wMaxCOM = MAXPORTS ;
#else
   wMaxCOM = LOWORD( EscapeCommFunction( NULL, GETMAXCOM ) ) + 1 ;
#endif

   // load the COM prefix from resources

   LoadString( GETHINST( hDlg ), IDS_COMPREFIX, szTemp, sizeof( szTemp ) ) ;

   // fill port combo box and make initial selection

   for (wCount = 0; wCount < wMaxCOM; wCount++)
   {
      wsprintf( szBuffer, "%s%d", (LPSTR) szTemp, wCount + 1 ) ;
      SendDlgItemMessage( hDlg, IDD_PORTCB, CB_ADDSTRING, 0,
                          (LPARAM) (LPSTR) szBuffer ) ;
   }

#ifdef WIN32 
   SendDlgItemMessage( hDlg, IDD_PORTCB, CB_ADDSTRING, 0,
                       (LPARAM) (LPSTR) "TELNET" ) ;
#endif
   SendDlgItemMessage( hDlg, IDD_PORTCB, CB_SETCURSEL,
                       (WPARAM) (PORT( npTTYInfo ) - 1), 0L ) ;

   // disable COM port combo box if connection has already been
   // established (e.g. OpenComm() already successful)

//DonStart
//   EnableWindow( GetDlgItem( hDlg, IDD_PORTCB ), !CONNECTED( npTTYInfo ) ) ;
//DonEnd

   // fill baud combo box and make initial selection

   FillComboBox( GETHINST( hDlg ), GetDlgItem( hDlg, IDD_BAUDCB ),
                 IDS_BAUD110, BaudTable,
                 sizeof( BaudTable ) / sizeof( BaudTable[ 0 ] ),
                 BAUDRATE( npTTYInfo ) ) ;

   // fill data bits combo box and make initial selection

   for (wCount = 5; wCount < 9; wCount++)
   {
      wsprintf( szBuffer, "%d", wCount ) ;
      wPosition = LOWORD( SendDlgItemMessage( hDlg, IDD_DATABITSCB,
                                              CB_ADDSTRING, 0,
                                              (LPARAM) (LPSTR) szBuffer ) ) ;

      // if current selection, tell the combo box

      if (wCount == BYTESIZE( npTTYInfo ))
         SendDlgItemMessage( hDlg, IDD_DATABITSCB, CB_SETCURSEL,
                             (WPARAM) wPosition, 0L ) ;
   }

   // fill parity combo box and make initial selection

   FillComboBox( GETHINST( hDlg ), GetDlgItem( hDlg, IDD_PARITYCB ),
                 IDS_PARITYNONE, ParityTable,
                 sizeof( ParityTable ) / sizeof( ParityTable[ 0 ] ),
                 PARITY( npTTYInfo ) ) ;

   // fill stop bits combo box and make initial selection

   FillComboBox( GETHINST( hDlg ), GetDlgItem( hDlg, IDD_STOPBITSCB ),
                 IDS_ONESTOPBIT, StopBitsTable,
                 sizeof( StopBitsTable ) / sizeof ( StopBitsTable ),
                 STOPBITS( npTTYInfo ) ) ;

   // initalize the flow control settings

   CheckDlgButton( hDlg, IDD_DTRDSR,
                   (FLOWCTRL( npTTYInfo ) & FC_DTRDSR) > 0 ) ;
   CheckDlgButton( hDlg, IDD_RTSCTS,
                   (FLOWCTRL( npTTYInfo ) & FC_RTSCTS) > 0 ) ;
   CheckDlgButton( hDlg, IDD_XONXOFF,
                   (FLOWCTRL( npTTYInfo ) & FC_XONXOFF) > 0 ) ;

   // other TTY settings

   CheckDlgButton( hDlg, IDD_AUTOWRAP, AUTOWRAP( npTTYInfo ) ) ;
   CheckDlgButton( hDlg, IDD_NEWLINE, NEWLINE( npTTYInfo ) ) ;
   CheckDlgButton( hDlg, IDD_LOCALECHO, LOCALECHO( npTTYInfo ) ) ;

   // control options

#ifdef WIN32
   // "Use CN_RECEIVE" is not valid under Win-32

   EnableWindow( GetDlgItem( hDlg, IDD_USECNRECEIVE ), FALSE ) ;
#else
   CheckDlgButton( hDlg, IDD_USECNRECEIVE, USECNRECEIVE( npTTYInfo ) ) ;

   // disable Use CN_RECEIVE option if connection has already been
   // established (e.g. OpenComm() already successful)

   EnableWindow( GetDlgItem( hDlg, IDD_USECNRECEIVE ),
                 !CONNECTED( npTTYInfo ) ) ;
#endif

   CheckDlgButton( hDlg, IDD_DISPLAYERRORS, DISPLAYERRORS( npTTYInfo ) ) ;

   return ( TRUE ) ;

} // end of SettingsDlgInit()

//---------------------------------------------------------------------------
//  BOOL NEAR SelectTTYFont( HWND hDlg )
//
//  Description:
//     Selects the current font for the TTY screen.
//     Uses the Common Dialog ChooseFont() API.
//
//  Parameters:
//     HWND hDlg
//        handle to settings dialog
//
//  History:   Date       Author      Comment
//             10/20/91   BryanW      Wrote it.
//
//---------------------------------------------------------------------------

BOOL NEAR SelectTTYFont( HWND hDlg )
{
   CHOOSEFONT  cfTTYFont ;
   NPTTYINFO   npTTYInfo ;
#ifdef SAVEREG
	HKEY hkProtocol;
	char szBuff[80];
	LONG cb;
#ifdef REG_FONT_BINARY
	DWORD dwType;
#endif
	
#ifdef WIN32
	RegOpenKeyEx(HKEY_CURRENT_USER, "Software\\Superior Software\\DGTerm", 0, KEY_ALL_ACCESS, &hkProtocol);
#else
	RegOpenKey(HKEY_CURRENT_USER, "Software\\Superior Software\\DGTerm", &hkProtocol);
#endif
#endif

   if (NULL == (npTTYInfo = (NPTTYINFO) GET_PROP( hDlg, ATOM_TTYINFO )))
      return ( FALSE ) ;

   cfTTYFont.lStructSize    = sizeof( CHOOSEFONT ) ;
   cfTTYFont.hwndOwner      = hDlg ;
   cfTTYFont.hDC            = NULL ;
   cfTTYFont.rgbColors      = FGCOLOR( npTTYInfo ) ;
   cfTTYFont.lpLogFont      = &LFTTYFONT( npTTYInfo ) ;
   cfTTYFont.Flags          = CF_SCREENFONTS | CF_FIXEDPITCHONLY |
                              CF_EFFECTS | CF_INITTOLOGFONTSTRUCT ;
   cfTTYFont.lCustData      = 0 ;
   cfTTYFont.lpfnHook       = NULL ;
   cfTTYFont.lpTemplateName = NULL ;
   cfTTYFont.hInstance      = GETHINST( hDlg ) ;

   if (ChooseFont( &cfTTYFont ))
   {
     FGCOLOR( npTTYInfo ) = cfTTYFont.rgbColors ;
     ResetTTYScreen( GetParent( hDlg ), npTTYInfo ) ;
   }

#ifdef SAVEREG
#ifdef REG_FONT_BINARY
    cb = sizeof(LFTTYFONT( npTTYInfo ));
	dwType = REG_BINARY;
//LFTTYFONT( npTTYInfo )
	RegSetValueEx(hkProtocol, "FontInfo", 0, dwType, (BYTE*)&LFTTYFONT( npTTYInfo ), cb);                         /* text string size               */
#else
	cb = lstrlen(LFTTYFONT( npTTYInfo ).lfFaceName);
	RegSetValue(hkProtocol, "Font", REG_SZ, LFTTYFONT( npTTYInfo ).lfFaceName, cb);                         /* text string size               */
	cb = wsprintf(szBuff, "%d", (short)LFTTYFONT( npTTYInfo ).lfHeight);		// Convert to a string
	RegSetValue(hkProtocol, "FontSize", REG_SZ, szBuff, cb);                         /* text string size               */
#endif
#endif

#ifdef SAVEREG
	RegCloseKey(hkProtocol);     /* closes protocol key and subkeys    */
#endif
   return ( TRUE ) ;

} // end of SelectTTYFont()

//---------------------------------------------------------------------------
//  BOOL NEAR SettingsDlgTerm( HWND hDlg )
//
//  Description:
//     Puts dialog contents into TTY info structure.
//
//  Parameters:
//     HWND hDlg
//        handle to settings dialog
//
//  Win-32 Porting Issues:
//     - Baud rate requires DWORD values.
//
//  History:   Date       Author      Comment
//              5/11/91   BryanW      Wrote it.
//              6/15/92   BryanW      Ported to Win-32.
//
//---------------------------------------------------------------------------

BOOL NEAR SettingsDlgTerm( HWND hDlg )
{
   NPTTYINFO  npTTYInfo ;
   WORD       wSelection ;
#ifdef SAVEREG
	HKEY hkProtocol;
	char szBuff[80];
	LONG cb;
	
#ifdef WIN32
	RegOpenKeyEx(HKEY_LOCAL_MACHINE, "Software\\Superior Software\\DGTerm", 0, KEY_ALL_ACCESS, &hkProtocol);
#else
	RegOpenKey(HKEY_LOCAL_MACHINE, "Software\\Superior Software\\DGTerm", &hkProtocol);
#endif
#endif

   if (NULL == (npTTYInfo = (NPTTYINFO) GET_PROP( hDlg, ATOM_TTYINFO )))
      return ( FALSE ) ;

   // get port selection
	PORT( npTTYInfo ) = LOBYTE( LOWORD( SendDlgItemMessage( hDlg, IDD_PORTCB,
                                          CB_GETCURSEL,
                                          0, 0L ) ) + 1 ) ;
#ifdef SAVEREG
    lstrcpy(szBuff, "COM1");
    szBuff[3] = '0' + PORT( npTTYInfo );
    cb = lstrlen(szBuff);
#ifdef WIN32
	RegSetValueEx(hkProtocol, "ComPort", 0, REG_SZ, szBuff, cb);                         /* text string size               */
#else
	RegSetValue(hkProtocol, "ComPort", REG_SZ, szBuff, cb);                         /* text string size               */
#endif
#endif
   wSelection =
      LOWORD( SendDlgItemMessage( hDlg, IDD_BAUDCB, CB_GETCURSEL,
                                  0, 0L ) ) ;
#ifdef WIN32
   BAUDRATE( npTTYInfo ) =
      SendDlgItemMessage( hDlg, IDD_BAUDCB, CB_GETITEMDATA,
                          (WPARAM) wSelection, 0L ) ;
#else
   BAUDRATE( npTTYInfo ) =
      LOWORD( SendDlgItemMessage( hDlg, IDD_BAUDCB, CB_GETITEMDATA,
                                  (WPARAM) wSelection, 0L ) ) ;
#endif
#ifdef SAVEREG
	switch (BAUDRATE( npTTYInfo ))
	{
	case CBR_110:
		lstrcpy(szBuff, "110");break;
	case CBR_300:
		lstrcpy(szBuff, "300");break;
	case CBR_600:
		lstrcpy(szBuff, "600");break;
	case CBR_1200:
		lstrcpy(szBuff, "1200");break;
	case CBR_2400:
		lstrcpy(szBuff, "2400");break;
	case CBR_4800:
		lstrcpy(szBuff, "4800");break;
	case CBR_9600:
		lstrcpy(szBuff, "9600");break;
	case CBR_14400:
		lstrcpy(szBuff, "14400");break;
	case CBR_19200:
		lstrcpy(szBuff, "19200");break;
	case CBR_38400:
		lstrcpy(szBuff, "38400");break;
	case CBR_56000:
		lstrcpy(szBuff, "56000");break;
	case CBR_128000:
		lstrcpy(szBuff, "128000");break;
	case CBR_256000:
		lstrcpy(szBuff, "256000");break;
	default:
		lstrcpy(szBuff, "9600");break;
	}
    cb = lstrlen(szBuff);
#ifdef WIN32
	RegSetValueEx(hkProtocol, "BaudRate", 0, REG_SZ, szBuff, cb);                         /* text string size               */
#else
	RegSetValue(hkProtocol, "BaudRate", REG_SZ, szBuff, cb);                         /* text string size               */
#endif
#endif

   // get data bits selection

   BYTESIZE( npTTYInfo ) =
      LOBYTE( LOWORD( SendDlgItemMessage( hDlg, IDD_DATABITSCB,
                                          CB_GETCURSEL,
                                          0, 0L ) ) + 5 ) ;

   // get parity selection

   wSelection =
      LOWORD( SendDlgItemMessage( hDlg, IDD_PARITYCB, CB_GETCURSEL,
                                  0, 0L ) ) ;
   PARITY( npTTYInfo ) =
      LOBYTE( LOWORD( SendDlgItemMessage( hDlg, IDD_PARITYCB,
                                          CB_GETITEMDATA,
                                          (WPARAM) wSelection,
                                          0L ) ) ) ;

   // get stop bits selection

   wSelection =
      LOWORD( SendDlgItemMessage( hDlg, IDD_STOPBITSCB, CB_GETCURSEL,
                                  0, 0L ) ) ;
   STOPBITS( npTTYInfo ) =
      LOBYTE( LOWORD( SendDlgItemMessage( hDlg, IDD_STOPBITSCB,
                                          CB_GETITEMDATA,
                                          (WPARAM) wSelection, 0L ) ) ) ;

   // get flow control settings

   FLOWCTRL( npTTYInfo ) = 0 ;
   if (IsDlgButtonChecked( hDlg, IDD_DTRDSR ))
      FLOWCTRL( npTTYInfo ) |= FC_DTRDSR ;
   if (IsDlgButtonChecked( hDlg, IDD_RTSCTS ))
      FLOWCTRL( npTTYInfo ) |= FC_RTSCTS ;
   if (IsDlgButtonChecked( hDlg, IDD_XONXOFF ))
      FLOWCTRL( npTTYInfo ) |= FC_XONXOFF ;

   // get other various settings

   AUTOWRAP( npTTYInfo ) = IsDlgButtonChecked( hDlg, IDD_AUTOWRAP ) ;
   NEWLINE( npTTYInfo ) = IsDlgButtonChecked( hDlg, IDD_NEWLINE ) ;
   LOCALECHO( npTTYInfo ) = IsDlgButtonChecked( hDlg, IDD_LOCALECHO ) ;

   // control options

   USECNRECEIVE( npTTYInfo ) = IsDlgButtonChecked( hDlg, IDD_USECNRECEIVE ) ;
   DISPLAYERRORS( npTTYInfo ) = IsDlgButtonChecked( hDlg, IDD_DISPLAYERRORS ) ;

#ifdef SAVEREG
	RegCloseKey(hkProtocol);     /* closes protocol key and subkeys    */
#endif
   return ( TRUE ) ;

} // end of SettingsDlgTerm()

//---------------------------------------------------------------------------
//  BOOL FAR PASCAL SettingsDlgProc( HWND hDlg, UINT uMsg,
//                                   WPARAM wParam, LPARAM lParam )
//
//  Description:
//     This handles all of the user preference settings for
//     the TTY.
//
//  Parameters:
//     same as all dialog procedures
//
//  Win-32 Porting Issues:
//     - npTTYInfo is a DWORD in Win-32.
//
//  History:   Date       Author      Comment
//              5/10/91   BryanW      Wrote it.
//             10/20/91   BryanW      Now uses window properties to
//                                    store TTYInfo handle.  Also added
//                                    font selection.
//              6/15/92   BryanW      Ported to Win-32.
//
//---------------------------------------------------------------------------

BOOL FAR PASCAL SettingsDlgProc( HWND hDlg, UINT uMsg,
                                 WPARAM wParam, LPARAM lParam )
{
   switch (uMsg)
   {
      case WM_INITDIALOG:
      {
         NPTTYINFO  npTTYInfo ;

         // get & save pointer to TTY info structure

#ifdef WIN32
         npTTYInfo = (NPTTYINFO) lParam ;
#else
         npTTYInfo = (NPTTYINFO) LOWORD( lParam ) ;
#endif

         SET_PROP( hDlg, ATOM_TTYINFO, (HANDLE) npTTYInfo ) ;

         return ( SettingsDlgInit( hDlg ) ) ;
      }

      case WM_COMMAND:
         switch ( LOWORD( wParam ))
         {
            case IDD_FONT:
               return ( SelectTTYFont( hDlg ) ) ;

            case IDD_OK:
               // Copy stuff into structure
               SettingsDlgTerm( hDlg ) ;
               EndDialog( hDlg, TRUE ) ;
               return ( TRUE ) ;

            case IDD_CANCEL:
               // Just end
               EndDialog( hDlg, TRUE ) ;
               return ( TRUE ) ;
         }
         break;

      case WM_DESTROY:
         REMOVE_PROP( hDlg, ATOM_TTYINFO ) ;
         break ;
   }
   return ( FALSE ) ;

} // end of SettingsDlgProc()

BOOL FAR PASCAL PhoneDlgProc( HWND hDlg, UINT uMsg,
                                 WPARAM wParam, LPARAM lParam )
{
	DWORD dwDisposition;
	switch (uMsg)
	{
		case WM_INITDIALOG:
		{
         NPTTYINFO  npTTYInfo ;

         // get & save pointer to TTY info structure

#ifdef WIN32
         npTTYInfo = (NPTTYINFO) lParam ;
#else
         npTTYInfo = (NPTTYINFO) LOWORD( lParam ) ;
#endif
		if (hDlg != NULL)
		{
	         SetDlgItemText( hDlg, IDD_FILENAME, (LPSTR) PHONENO(npTTYInfo) ) ;
	         glpTTYInfo = npTTYInfo;
		}

         return ( true ) ;
      }

      case WM_COMMAND:
         switch ( LOWORD( wParam ))
         {
            case IDD_OK:
            {
               // Copy stuff into structure
		if (hDlg != NULL)
		{
			HKEY hkProtocol;
			LONG cb;
	         NPTTYINFO  npTTYInfo ;
	         npTTYInfo = glpTTYInfo ;
	         glpTTYInfo = NULL;

	         GetDlgItemText( hDlg, IDD_FILENAME, (LPSTR) PHONENO(npTTYInfo), 30 ) ;
	
#ifdef WIN32
	dwDisposition = 0;
	if (RegCreateKeyEx(HKEY_CURRENT_USER, "Software\\Superior Software\\DGTerm", /* protocol string */
    	0,	// reserved 
    "",	// address of class string 
    REG_OPTION_NON_VOLATILE,	// special options flag 
    KEY_ALL_ACCESS,	// desired security access 
    NULL,	// address of key security structure 
    &hkProtocol,	// address of buffer for opened handle  
    &dwDisposition) != ERROR_SUCCESS)          /* protocol key handle */
	        return FALSE;
#else
	if (RegCreateKey(HKEY_CURRENT_USER, "Software\\Superior Software\\DGTerm", /* protocol string */
	    &hkProtocol) != ERROR_SUCCESS)          /* protocol key handle */
	        return FALSE;
#endif
		
	    	cb = lstrlen(PHONENO(npTTYInfo));
#ifdef WIN32
	RegSetValueEx(hkProtocol, "PhoneNo", 0, REG_SZ, PHONENO(npTTYInfo), cb);                         /* text string size               */
#else
	RegSetValue(hkProtocol, "PhoneNo", REG_SZ, PHONENO(npTTYInfo), cb);                         /* text string size               */
#endif
			RegCloseKey(hkProtocol);     /* closes protocol key and subkeys    */
		}

               EndDialog( hDlg, TRUE ) ;
               return ( TRUE ) ;
             }
            case IDD_CANCEL:
               // Just end
               EndDialog( hDlg, TRUE ) ;
               return ( TRUE ) ;
         }
         break;

      case WM_DESTROY:
         REMOVE_PROP( hDlg, ATOM_TTYINFO ) ;
         break ;
   }
   return ( FALSE ) ;

} // end of PhoneDlgProc()

#ifdef WIN32

//************************************************************************
//  DWORD FAR PASCAL CommWatchProc( LPSTR lpData )
//
//  Description:
//     A secondary thread that will watch for COMM events.
//
//  Parameters:
//     LPSTR lpData
//        32-bit pointer argument
//
//  Win-32 Porting Issues:
//     - Added this thread to watch the communications device and
//       post notifications to the associated window.
//
//  History:   Date       Author      Comment
//             12/31/91   BryanW      Wrote it.
//              6/12/92   BryanW      CommWaitEvent() now uses
//                                    an overlapped structure.
//
//************************************************************************

DWORD FAR PASCAL CommWatchProc( LPSTR lpData )
{
   DWORD       dwTransfer, dwEvtMask ;
   NPTTYINFO   npTTYInfo = (NPTTYINFO) lpData ;
   OVERLAPPED  os ;

   memset( &os, 0, sizeof( OVERLAPPED ) ) ;

   // create I/O event used for overlapped read

   os.hEvent = CreateEvent( NULL,    // no security
                            TRUE,    // explicit reset req
                            FALSE,   // initial event reset
                            NULL ) ; // no name
   if (os.hEvent == NULL)
   {
      MessageBox( NULL, "Failed to create event for thread!", "TTY Error!",
                  MB_ICONEXCLAMATION | MB_OK ) ;
      return ( FALSE ) ;
   }

   if (!SetCommMask( COMDEV( npTTYInfo ), EV_RXCHAR ))
      return ( FALSE ) ;

   while ( CONNECTED( npTTYInfo ) )
   {
      dwEvtMask = 0 ;

      if (!WaitCommEvent( COMDEV( npTTYInfo ), &dwEvtMask, &os ))
      {
         if (ERROR_IO_PENDING == GetLastError())
         {
            GetOverlappedResult( COMDEV( npTTYInfo ), &os, &dwTransfer, TRUE ) ;
            os.Offset += dwTransfer ;
         }
      }

      if ((dwEvtMask & EV_RXCHAR) == EV_RXCHAR)
      {
         // wait for "posted notification" flag to clear

         WaitForSingleObject( POSTEVENT( npTTYInfo ), 0xFFFFFFFF ) ;

         // reset event

         ResetEvent( POSTEVENT( npTTYInfo ) ) ;

         // last message was processed, O.K. to post

         PostMessage( TERMWND( npTTYInfo ), WM_COMMNOTIFY,
                      (WPARAM) COMDEV( npTTYInfo ),
                      MAKELONG( CN_EVENT, 0 ) ) ;
      }
   }

   // get rid of event handle

   CloseHandle( os.hEvent ) ;

   // clear information in structure (kind of a "we're done flag")

   THREADID( npTTYInfo ) = 0 ;
   HTHREAD( npTTYInfo ) = NULL ;

   return( TRUE ) ;

} // end of CommWatchProc()

#endif

//DonStart  
//**************************XSend****************************
//			Send data using the Xmodem protocol
// ***************************************************************}

BOOL NEAR PASCAL XSend( HWND hWnd )
{
	OPENFILENAME ofn;
	char szDirName[256];
	char szFile[256];
	char szFileTitle[256];
#ifdef WIN32
	DWORD lastError;
#endif
	OFSTRUCT ofsOpenBuff;
	NPTTYINFO npTTYInfo ;
	BOOL fFlag;

//   if (NULL == (npTTYInfo = GETNPTTYINFO( hWnd )))
//      return ( FALSE ) ;

	ghFile = (HFILE)NULL;
	memset( &ofn, 0, sizeof( OPENFILENAME ) ) ;

	GetSystemDirectory(szDirName, sizeof(szDirName));
	szFile[0] = '\0';

	ofn.lStructSize = sizeof(OPENFILENAME);
	ofn.hwndOwner = hWnd;
	ofn.lpstrFilter = "All Files (*.*)\0*.*\0Text Files (*.TXT)\0*.TXT\0Word Files (*.DOC *.DOT)\0*.DOC;*.DOT\0WordPerfect (*.WPF)\0*.WPF\0\0";
	ofn.nFilterIndex = 1;
	ofn.lpstrFile = szFile;
	ofn.nMaxFile = sizeof(szFile);
	ofn.lpstrFileTitle = szFileTitle;
	ofn.nMaxFileTitle = sizeof(szFileTitle);
	ofn.lpstrInitialDir = NULL;	//szDirName;	// Current directory
	ofn.lpstrTitle        = "Send a File from PC";
   	ofn.lpstrDefExt       = "*.txt";
	ofn.Flags = OFN_SHOWHELP | OFN_OVERWRITEPROMPT | OFN_PATHMUSTEXIST | OFN_FILEMUSTEXIST;
	if (!GetOpenFileName( &ofn))
	{
#ifdef WIN32
		lastError = GetLastError();
#endif
		return false;	// Error return
	}

	ghFile = OpenFile(szFile, &ofsOpenBuff, OF_READ | OF_SHARE_COMPAT);
	if (ghFile == HFILE_ERROR)
	{
		MessageBox( hWnd, "File open failed.", NULL, MB_OK );
		return FALSE;
	}

   if (NULL == (npTTYInfo = GETNPTTYINFO( hWnd )))
      return ( FALSE ) ;
#ifdef WIN32
	if (!PurgeComm(COMDEV( npTTYInfo ), PURGE_RXCLEAR))	// Purge the receive buffer
	{
		lastError = GetLastError();
		return false;	// Error return
	}
#endif

	fFlag = WriteCommByte(hWnd, kNAK);	// Send some junk to get things going
	gfBlock = 1;					// Initialize block number
	gnNAKCount = 0;				// Reset bad block count
	gfXFerMode = kXSendMode;	// Grab Incomming characters (In ProcessXSend)
	return TRUE;				// Wait for NAK before starting send

}

//**************************SetUpBlock****************************
// ProcessXSend - In Send Mode, process this incomming character (ie., NAK or ACK)
//***************************************************************}
BOOL NEAR ProcessXSend( HWND hWnd, LPSTR lpBlock, DWORD nLength )
{
	char	chChar;
	BOOL	fSendOK;
	BOOL	fFlag;

	fSendOK = FALSE;
	if (nLength)
		chChar = *(((char*)lpBlock) + nLength - 1);		// Last character is one of concern
	else
		chChar = '0';		// Timeout (len = 0)
	if (chChar == kACK)
	{
		fSendOK = TRUE;
		gfBlock++;					// Bump to next block no
		gnNAKCount = 0;				// Reset bad block count
	}
	if (chChar == kNAK)
	{
		fSendOK = TRUE;				// Resend block
		gnNAKCount++;				// Only process 10 bad blocks!!!
	}
	fFlag = TRUE;		// Init (Don't quit)
	if (gnNAKCount >= 10)
	{
		fSendOK = FALSE;	// Don't resend block
		fFlag = FALSE;		// Stop send
		MessageBox( hWnd, "Too many retries", NULL, MB_OK );
	}
	if (fSendOK)
		fFlag = WriteXSendBlock(hWnd);				// Send the next block!
	if (fFlag == FALSE)
	{
		gKeepReceiving = FALSE;			// Reset this flag
		if (ghFile != (HFILE)NULL)
			ghFile = _lclose(ghFile);
		gfXFerMode = kTerminalMode;		// All Done, Don't capture any more characters!
	}

	return TRUE;
}

//**************************SetUpBlock****************************
// WriteXSendBlock
//***************************************************************}
BOOL NEAR WriteXSendBlock( HWND hWnd )
{
	DWORD	nCharacters, nCharactersRead;
	LPVOID	pCharPointer;
	DWORD	nCount;
	BOOL	fFlag;
	DWORD	dwNewPointer, dwFilePointer;
#ifdef WIN32
	DWORD	lastError;
#endif

	if (gfBlock == -1)
	{	// Our EOT was ACKed
		ghFile = _lclose(ghFile);
		return false;	// Stop (successful send)
	}

	nCharacters = 128;
	dwFilePointer = (gfBlock - 1) * 128;
	pCharPointer = gszCharacterIn + 4;		// SOH + Length byte + n(s) +n(^s)}
#ifdef WIN32
	dwNewPointer = SetFilePointer((HANDLE)ghFile, dwFilePointer, 0, FILE_BEGIN);
#else
	dwNewPointer = lseek((HANDLE)ghFile, dwFilePointer, SEEK_SET);
#endif
	nCharactersRead = 0;
	if (dwNewPointer == dwFilePointer)
	{
#ifdef WIN32
		fFlag = ReadFile((HANDLE)ghFile, pCharPointer, nCharacters, &nCharactersRead, NULL);
#else
		nCharactersRead = read((HANDLE)ghFile, pCharPointer, nCharacters);
		fFlag = TRUE;
#endif
		if (fFlag == FALSE)
		{
#ifdef WIN32
			lastError = GetLastError();
#endif
			MessageBox( hWnd, "File Write Failed.", NULL, MB_OK );
			return FALSE;
		}
		if (nCharactersRead == 0)
			gfBlock = -2;		// At EOF
		else if (nCharactersRead != nCharacters)
		{
			for (nCount = nCharactersRead + 4; nCount <= 131; nCount++)
				gszCharacterIn[nCount] = kFIL;	// Filler characters
		}
	}
	else
		gfBlock = -2;		// At EOF
	if (gfBlock == -2)
	{   		// At EOF, Send EOT, then after rec ACK, close
		gszCharacterIn[0] = kEOT;		// Send the EOT}
		fFlag = WriteCommBlock( hWnd, gszCharacterIn, 1);
		return true;	// Successful return
	}

	SetUpBlock(gszCharacterIn);
	SendBlock(hWnd, gszCharacterIn);
	return true;	// Successful return
}

//**************************SetUpBlock****************************
//			Set up the Block for Xsend
//***************************************************************}
BOOL NEAR PASCAL SetUpBlock( char* szCharacterIn )
{
	DWORD nCheckSum;
	DWORD nCount;

	szCharacterIn[1] = kSOH;
	szCharacterIn[2] = (char)(gfBlock & 255);
	szCharacterIn[3] = (char)~szCharacterIn[2];
	nCheckSum = 0;
	for (nCount = 4; nCount <= 131; nCount++)
		nCheckSum += (unsigned char)szCharacterIn[nCount];

	szCharacterIn[132] = (unsigned char)nCheckSum;
	return true;
}

//**************************SendBlock****************************
//			Send the block (CharacterIn) out until it's right
//***************************************************************}
BOOL NEAR PASCAL SendBlock( HWND hWnd, char* szCharacterIn )
{
	DWORD	nCharacters;
	LPVOID	pCharPointer;
	BOOL	fFlag;

	nCharacters = 132;
	pCharPointer= szCharacterIn + 1;
	fFlag = WriteCommBlock( hWnd, pCharPointer, nCharacters); 
	return TRUE;
}

//**************************CheckBlock****************************
//			Check the block (CharacterIn) to see if it is valid
//Returns: bool: Yes=Block is okay (Xmodem format)
//***************************************************************}

BOOL NEAR PASCAL CheckBlock( char* szCharacterIn )
{
	DWORD nCheckSum, nCount;
	BOOL fCheckBlock;
	
	fCheckBlock = TRUE;
	if (gszCharacterIn[1] != kSOH)
		fCheckBlock = FALSE;
	if (gszCharacterIn[1] == kNAM) if (ghFile == (HFILE)NULL)
		fCheckBlock = TRUE;
	if (szCharacterIn[2] != (char)(255 & gfBlock))
		fCheckBlock = FALSE;
	if ((char)(255 & ~szCharacterIn[3]) != (char)(255 & gfBlock))
		fCheckBlock = FALSE;
	nCheckSum = 0;
	for (nCount = 4; nCount <= 131; nCount++)
	{
		nCheckSum += (unsigned char)szCharacterIn[nCount];
	}

	if ((255 & nCheckSum) != (unsigned char)szCharacterIn[132])
		fCheckBlock = FALSE;
	return fCheckBlock;
}

//---------------------------------------------------------------------------
//  BOOL NEAR WriteCommByte( HWND hWnd, BYTE bByte )
//
//  Description:
//     Writes a byte to the COM port specified in the associated
//     TTY info structure.
//
//  Parameters:
//     HWND hWnd
//        handle to TTY window
//
//     BYTE bByte
//        byte to write to port
//
//  Win-32 Porting Issues:
//     - WriteComm() has been replaced by WriteFile() in Win-32.
//     - Overlapped I/O has been implemented.
//
//  History:   Date       Author      Comment
//              5/10/91   BryanW      Wrote it.
//
//---------------------------------------------------------------------------

BOOL NEAR WriteCommBlock( HWND hWnd, LPVOID cBlock, DWORD nBlockSize )
{
#ifdef WIN32
   BOOL        fWriteStat ;
   DWORD       dwBytesWritten ;
#endif
   NPTTYINFO  npTTYInfo ;

   if (NULL == (npTTYInfo = GETNPTTYINFO( hWnd )))
      return ( FALSE ) ;

#ifdef WIN32
   fWriteStat = WriteFile( COMDEV( npTTYInfo ), cBlock, nBlockSize,
                           &dwBytesWritten, &WRITE_OS( npTTYInfo ) ) ;
   if (!fWriteStat && (GetLastError() == ERROR_IO_PENDING))
   {
      // wait for a second for this transmission to complete

      if (WaitForSingleObject( WRITE_OS( npTTYInfo ).hEvent, 1000 ))
         dwBytesWritten = 0 ;
      else
      {
         GetOverlappedResult( COMDEV( npTTYInfo ),
                              &WRITE_OS( npTTYInfo ),
                              &dwBytesWritten, FALSE ) ;
         WRITE_OS( npTTYInfo ).Offset += dwBytesWritten ;
      }
   }
#else
   WriteComm( COMDEV( npTTYInfo ), cBlock, nBlockSize ) ;
#endif
   return ( TRUE ) ;

} // end of WriteCommByte()

BOOL NEAR ProcessXReceive( HWND hWnd, LPSTR lpBlock, DWORD nLength )
{
	BOOL	fFlag, fBlockOK;

	DWORD	nCharacters, nCharactersRead;
	LPVOID	pCharPointer;
#ifdef WIN32
	DWORD	lastError;
#else
	OFSTRUCT OfStruc;
	int i;
#endif
	if (nLength == 0)
	{	// Timeout, send another NAK to try to get it going
		if (gnNAKCount >= 10)
		{
			MessageBox( hWnd, "Too many retries", NULL, MB_OK );
			gKeepReceiving = FALSE;			// Reset this flag
			if (ghFile != (HFILE)NULL)
				ghFile = _lclose(ghFile);
			gfXFerMode = kTerminalMode;		// All Done, Don't capture any more characters!
			return false;	// Stop (unsuccessful send)
		}
		fFlag = WriteCommByte(hWnd, kNAK);	//		{Send a NAK to DG}
		gnCharacterCount = 0;				// Reset character receive buffer
		gnNAKCount++;				// Only process 10 bad blocks!!!
		return TRUE;		// Continue
	}
	if (gnCharacterCount == 0)
	{		// If the first char is not a control char, advance to a control char
		while (nLength > 0)
		{
			if ((lpBlock[0] == kEOT) || (lpBlock[0] == kCAN))
				if (nLength == 1)
					break;
			if (lpBlock[0] == kNAM)
				if (ghFile == (HFILE)NULL)
					break;	// Supplying a file name
			if (lpBlock[0] == kSOH)
			{
				if (gfBlock > 1)
				{	// Write the last block
					nCharacters = 128;
					pCharPointer = gszCharacterIn + 4;		// {Length byte + n(s) +n(^s)}
#ifdef WIN32
					fFlag = WriteFile((HANDLE)ghFile, pCharPointer, nCharacters, &nCharactersRead, NULL);
#else
					nCharactersRead = write((HANDLE)ghFile, pCharPointer, nCharacters);
					fFlag = TRUE;
#endif
					if (fFlag == FALSE)
					{
#ifdef WIN32
						lastError = GetLastError();
#endif
						MessageBox( hWnd, "File Write Failed.", NULL, MB_OK );
						return FALSE;
					}
					if ((nCharactersRead == 0) || (nCharactersRead != nCharacters))
					{
#ifdef WIN32
						lastError = GetLastError();
#endif
						MessageBox( hWnd, "File Write Failed.", NULL, MB_OK );
						return FALSE;
					}
				}
				break;
			}
			nLength--;
			lpBlock++;
		}
		if (nLength == 0)
			return TRUE;	// Keep trying until you get an SOH
	}
	if (gnCharacterCount + nLength > 132)
	{		// Too many characters, start again
		gnCharacterCount = 0;	// Reset character receive buffer
		return TRUE;			// Continue
	}
	memcpy(gszCharacterIn + gnCharacterCount + 1, lpBlock, nLength);
	gnCharacterCount += nLength;		// New count
	if ((gszCharacterIn[1] == kEOT) || (gszCharacterIn[1] == kCAN))
	{
		fFlag = WriteCommByte((HANDLE)hWnd, kACK);	//		{Send a 'Block ok' to DG}

 		nCharacters = 128;
		pCharPointer = gszCharacterIn + 4;		// {Length byte + n(s) +n(^s)}
		for (nCharacters = 128; nCharacters >= 1; nCharacters--)
		{
			pCharPointer = gszCharacterIn + 4 + (nCharacters - 1);
			if (((char*)pCharPointer)[0] != kFIL)
				break;
		}
		pCharPointer = gszCharacterIn + 4;		// {Length byte + n(s) +n(^s)}
#ifdef WIN32
		fFlag = WriteFile((HANDLE)ghFile, pCharPointer, nCharacters, &nCharactersRead, NULL);
#else
		nCharactersRead = write((HANDLE)ghFile, pCharPointer, nCharacters);
		fFlag = TRUE;
#endif
		if (ghFile != (HFILE)NULL)
			ghFile = _lclose(ghFile);
		if (gKeepReceiving)
		{
			XRecStart(hWnd);
			return TRUE;		// Keep going
		}
		gfXFerMode = kTerminalMode;		// All Done, Don't capture any more characters!
		if (ghDlg != NULL)
		{
            EndDialog( ghDlg, TRUE ) ;
		  	ghDlg = NULL;
		}
		return false;	// Stop (successful send)
	}
	if (gszCharacterIn[1] == kSOH) if (gnCharacterCount < 132)
		return TRUE;		// Haven't gotten all of them yet
	if (gszCharacterIn[1] == kNAM) if (gnCharacterCount < 132)
		return TRUE;		// Haven't gotten all of them yet

	fBlockOK = CheckBlock(gszCharacterIn);
	if (fBlockOK)
	{
		if (gszCharacterIn[1] == kNAM) if (ghFile == (HFILE)NULL)
		{	// File name supplied
			char *szFile;
			szFile = gszCharacterIn + 4;	// Start of file name
#ifdef WIN32
			ghFile = (HFILE)CreateFile(szFile, GENERIC_WRITE, 0, NULL, CREATE_ALWAYS, FILE_ATTRIBUTE_NORMAL, NULL);
#else
// Fix the filename to a DOS filename
			szFile[12] = 0;		// In case it is too long
			for (i = 0; i <= 8; i++)
			{
				if (szFile[i] == '.')
				{
					szFile[i+4] = 0;	// Three chars after the dot
					break;
				}
			};
			if (i >= 8)
				szFile[8] = 0;	// No dot 
    		ghFile = OpenFile(szFile, &OfStruc, OF_CREATE);
#endif
			if (ghDlg != NULL)
			{
		         SetDlgItemText( ghDlg, IDD_FILENAME, (LPSTR) szFile ) ;
			}
		// Delete if already exists
			if (ghFile == HFILE_ERROR)
			{
				gKeepReceiving = FALSE;			// Reset this flag
				if (ghFile != (HFILE)NULL)
					ghFile = _lclose(ghFile);
				gfXFerMode = kTerminalMode;		// All Done, Don't capture any more characters!
				fFlag = WriteCommByte(hWnd, kCAN);		//	Stop, error				
				MessageBox( hWnd, "File open failed.", NULL, MB_OK );
				return FALSE;
			}
			gfBlock--;	// Don't count this block
		}
		gfBlock++;
		gnCharacterCount = 0;			// Character receive buffer
		fFlag = WriteCommByte(hWnd, kACK);		//	{Send a 'Block ok' to DG}				
		gnNAKCount = 0;				// Reset bad block count
		if (ghDlg != NULL)
		{
			DWORD dwBytesMoved;
			char szBuffer[50];

//x            hPercentLine = GetDlgItem( ghDlg, IDD_BYTESIN ) ;
			dwBytesMoved = gfBlock * 128;
         	wsprintf( szBuffer, "Moved: %ld", dwBytesMoved ) ;
	         SetDlgItemText( ghDlg, IDD_BYTESIN, (LPSTR) szBuffer ) ;
		}
	}
	else
	{
		if (gszCharacterIn[0] == kSOH) if (gnCharacterCount == 132)	// Bad block or junk?
		{
			fFlag = WriteCommByte(hWnd, kNAK);	//		{Send a NAK to DG}
			gnNAKCount++;				// Only process 10 bad blocks!!!
		}
	}
	gnCharacterCount = 0;					// Reset character receive buffer
	return TRUE;		// Continue
}

BOOL NEAR PASCAL XReceive( HWND hWnd )
{
	OPENFILENAME ofn;
	char szDirName[256];
	char szFile[256];
	char szFileTitle[256];
#ifdef WIN32
	DWORD lastError;
#else
	OFSTRUCT OfStruc;
#endif

//   if (NULL == (npTTYInfo = GETNPTTYINFO( hWnd )))
//      return ( FALSE ) ;

	gfBlock = 1;				// Initialize block number
	gnNAKCount = 0;				// Reset bad block count
	memset( &ofn, 0, sizeof( OPENFILENAME ) ) ;

	GetSystemDirectory(szDirName, sizeof(szDirName));
	szFile[0] = '\0';

	ofn.lStructSize = sizeof(OPENFILENAME);
	ofn.hwndOwner = hWnd;
	ofn.lpstrFilter = "All Files (*.*)\0*.*\0Text Files (*.TXT)\0*.TXT\0Word Files (*.DOC *.DOT)\0*.DOC;*.DOT\0WordPerfect (*.WPF)\0*.WPF\0\0";
	ofn.nFilterIndex = 1;
	ofn.lpstrFile = szFile;
	ofn.nMaxFile = sizeof(szFile);
	ofn.lpstrFileTitle = szFileTitle;
	ofn.nMaxFileTitle = sizeof(szFileTitle);
	ofn.lpstrInitialDir = NULL;	//szDirName;	// Current directory
	ofn.lpstrTitle        = "Receive a file to PC";
   	ofn.lpstrDefExt       = "*.txt";
	ofn.Flags = OFN_SHOWHELP | OFN_OVERWRITEPROMPT;

	if (!GetSaveFileName( &ofn))
	{
#ifdef WIN32
		lastError = GetLastError();
#endif
		return false;	// Error return
	}

	ghFile = (HFILE)NULL;
#ifdef WIN32
	ghFile = (HFILE)CreateFile(szFile, GENERIC_WRITE, 0, NULL, CREATE_ALWAYS, FILE_ATTRIBUTE_NORMAL, NULL);
#else
    ghFile = OpenFile(szFile, &OfStruc, OF_CREATE);
#endif
// Delete if already exists
	if (ghFile == HFILE_ERROR)
	{
		MessageBox( hWnd, "File open failed.", NULL, MB_OK );
		return FALSE;
	}

   return XRecStart( hWnd );
}

BOOL NEAR PASCAL XRecStart( HWND hWnd )
{
	BOOL	fFlag;
	NPTTYINFO npTTYInfo ;
#ifdef WIN32
   COMMTIMEOUTS  CommTimeOuts ;
	DWORD lastError;
#endif

   if (NULL == (npTTYInfo = GETNPTTYINFO( hWnd )))
      return ( FALSE ) ;
#ifdef WIN32
   	if (!PurgeComm(COMDEV( npTTYInfo ), PURGE_RXCLEAR))	// Purge the receive buffer
	{
		lastError = GetLastError();
		return false;	// Error return
	}       
#endif
	fFlag = WriteCommByte(hWnd, kNAK);	//		{Send a NAK to Start send}
	gfBlock = 1;					// Initialize block number
	gnCharacterCount = 0;			// Character receive buffer
	gfXFerMode = kXReceiveMode;	// Grab Incomming characters (In ProcessXSend)

#ifdef WIN32
      CommTimeOuts.ReadIntervalTimeout = 0xFFFFFFFF ;
      CommTimeOuts.ReadTotalTimeoutMultiplier = 0 ;
      CommTimeOuts.ReadTotalTimeoutConstant = 5 ;
      CommTimeOuts.WriteTotalTimeoutMultiplier = 0 ;
      CommTimeOuts.WriteTotalTimeoutConstant = 5000 ;
// I don't know why this doesn't work!
//?      fFlag = SetCommTimeouts( COMDEV( npTTYInfo ), &CommTimeOuts ) ;
#else
//?   if ((COMDEV( npTTYInfo ) = OpenComm( szPort, RXQUEUE, TXQUEUE )) < 0)
//?      return ( FALSE ) ;
#endif

	return true;	// Successful return
}
 
//DonEnd
//---------------------------------------------------------------------------
//  End of File: tty.c
//---------------------------------------------------------------------------



