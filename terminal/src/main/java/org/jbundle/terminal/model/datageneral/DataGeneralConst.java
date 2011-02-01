/*
 * Copyright (c) 2000 jbundle.org. All Rights Reserved.
 *		don@tourgeek.com
*/
package org.jbundle.terminal.model.datageneral;

/**
 * Shared constants.
 */
interface DataGeneralConst
{
	static final char kCarriageReturn = 13;
	static final char kLineFeed = 10;
	static final char kEraseEOL = 11;
	//static final char kHome = 8;
	static final char kHomeAlso = 30;
	//static final char kClearScreen = 12;
	//static final char kUp = '\027';	// 23;
	//static final char kRight = '\030';	// 24;
	//static final char kLeft = '\031';	// 25;
	//static final char kDown = '\032';	// 26;
	//static final char kDimOn = 28;
	//static final char kDimOff = 29;
	static final char kPositionCursor = 16;
	static final char kFunctionHeader = 30;
	static final char kFunction1 = 33;
	
	static final char kNull = 0;
	static final char kA1 = 1;
	static final char kEndReverse = 2;
	static final char kA3 = 3;
	static final char kA4 = 4;
	static final char kReadCurPos = 5;
	static final char kA6 = 6;
	static final char kBell = 7;
	static final char kHome = 8;
	static final char kTab = 9;
	static final char kNewLine = 10;
	static final char kEraseEol = 11;
	static final char kClearScreen = 12;
	static final char kCReturn = 13;
	static final char kStartBlink = 14;
	static final char kEndBlink = 15;
	static final char kPosCursor = 16;
	static final char kA17 = 17;
	static final char kPageOff = 18;
	static final char kPageOn = 19;
	static final char kStartUnderline = 20;
	static final char kEndUnderline = 21;
	static final char kStartReverse = 22;
	static final char kUp = 23;
	static final char kRight = 24;
	static final char kLeft = 25;
	static final char kDown = 26;
	static final char kA27 = 27;
	static final char kDimOn = 28;
	static final char kDimOff = 29;
	static final char kTermID1 = 30;
	static final char kTermID2 = 67;
	static final char kReverseOn2 = 68;
	static final char kReverseOff2 = 69;
	static final char kEndCur = 1;		// Ctrl A
	static final char kInsCur = 5;		// Ctrl E
	
	static final char kCHdr = 30;		// kFunctionHeader			// Hdr key for C1-C4
	static final char kC1Key = 92;		//25
	static final char kC2Key = 93;		// ]
	static final char kC3Key = 94;		//24
	static final char kC4Key = 95;		// _
	
	static final char kFuncHdr = 30;
	static final char kFun0 = 113;
	static final char kFun1 = 97;
	static final char kFun2 = 49;
	static final char kFun3 = 33;
			
	static final char kCurPosHdr = 31;
	static final char kFirstRowChar = 0;
	static final char kFirstColChar = 0;
	static final char kTermIDHdr = 30;
			
	static final char kDelete = 127;
	static final char kEscape = 27;
			
	static final char kReturnKey = 13;
	static final char kEnterKey = 3;
	static final char kClearKey = 23;	// {Actually, this key also produces Esckey...27}
	static final char kRightArrow = 29;
	static final char kLeftArrow = 28;
	static final char kDownArrow = 31;		
	static final char kUpArrow = 30;
	static final char kEscKey = 27;
	static final char kDeleteKey = 8;
			
	static final char kSOH = 1;
	static final char kEOT = 4;
	static final char kACK = 6;
	static final char kNAK = 21;
	static final char kCAN = 24;
	static final char kFIL = 26;		// Filler bytes
	
	static final char kNAM = 2;		// File Name in next block
}