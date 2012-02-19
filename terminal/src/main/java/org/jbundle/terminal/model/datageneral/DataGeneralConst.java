/*
 *		don@tourgeek.com
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.terminal.model.datageneral;

/**
 * Shared constants.
 */
interface DataGeneralConst
{
	static final char CARRIAGE_RETURN = 13;
	static final char LINE_FEED = 10;
	//static final char ERASE_EOL = 11;
	//static final char HOME = 8;
	static final char HOME_ALSO = 30;
	//static final char CLEAR_SCREEN = 12;
	//static final char UP = '\027';	// 23;
	//static final char RIGHT = '\030';	// 24;
	//static final char LEFT = '\031';	// 25;
	//static final char DOWN = '\032';	// 26;
	//static final char DIM_ON = 28;
	//static final char DIM_OFF = 29;
	static final char POSITION_CURSOR = 16;
	static final char FUNCTION_HEADER = 30;
	static final char FUNCTION_1 = 33;
	
	static final char NULL = 0;
	static final char A1 = 1;
	static final char END_REVERSE = 2;
	static final char A3 = 3;
	static final char A4 = 4;
	static final char READ_CUR_POS = 5;
	static final char A6 = 6;
	static final char BELL = 7;
	static final char HOME = 8;
	static final char TAB = 9;
	static final char NEW_LINE = 10;
	static final char ERASE_EOL = 11;
	static final char CLEAR_SCREEN = 12;
	static final char C_RETURN = 13;
	static final char START_BLINK = 14;
	static final char END_BLINK = 15;
	static final char POS_CURSOR = 16;
	static final char A17 = 17;
	static final char PAGE_OFF = 18;
	static final char PAGE_ON = 19;
	static final char START_UNDERLINE = 20;
	static final char END_UNDERLINE = 21;
	static final char START_REVERSE = 22;
	static final char UP = 23;
	static final char RIGHT = 24;
	static final char LEFT = 25;
	static final char DOWN = 26;
	static final char A27 = 27;
	static final char DIM_ON = 28;
	static final char DIM_OFF = 29;
	static final char TERM_ID1 = 30;
	static final char TERM_ID2 = 67;
	static final char REVERSE_ON_2 = 68;
	static final char REVERSE_OFF_2 = 69;
	static final char END_CUR = 1;		// Ctrl A
	static final char INS_CUR = 5;		// Ctrl E
	
	static final char C_HDR = 30;		// FUNCTION_HEADER			// Hdr key for C1-C4
	static final char C1_KEY = 92;		//25
	static final char C2_KEY = 93;		// ]
	static final char C3_KEY = 94;		//24
	static final char C4_KEY = 95;		// _
	
	static final char FUNC_HDR = 30;
	static final char FUN_0 = 113;
	static final char FUN_1 = 97;
	static final char FUN_2 = 49;
	static final char FUN_3 = 33;
			
	static final char CUR_POS_HDR = 31;
	static final char FIRST_ROW_CHAR = 0;
	static final char FIRST_COL_CHAR = 0;
	static final char TERM_ID_HDR = 30;
			
	static final char DELETE = 127;
	static final char ESCAPE = 27;
			
	static final char RETURN_KEY = 13;
	static final char ENTER_KEY = 3;
	static final char CLEAR_KEY = 23;	// {Actually, this key also produces Esckey...27}
	static final char RIGHT_ARROW = 29;
	static final char LEFT_ARROW = 28;
	static final char DOWN_ARROW = 31;		
	static final char UP_ARROW = 30;
	static final char ESC_KEY = 27;
	static final char DELETE_KEY = 8;
			
	static final char kSOH = 1;
	static final char kEOT = 4;
	static final char kACK = 6;
	static final char kNAK = 21;
	static final char kCAN = 24;
	static final char kFIL = 26;		// Filler bytes
	
	static final char kNAM = 2;		// File Name in next block
}
