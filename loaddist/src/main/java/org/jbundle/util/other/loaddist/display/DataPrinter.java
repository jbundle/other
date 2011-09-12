/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.util.other.loaddist.display;

import java.net.*;
import java.io.*;

public class DataPrinter extends Thread {

    /**
     * Constructor.
     */
    public DataPrinter ()
    {
    }
	/*
	 * Testing only
	 */
    public static void main(String args[]) {
		DataPrinter datagram = new DataPrinter();
		byte[] by = new byte[100];
		by[0] = (byte)'1';
		by[1] = (byte)'2';
		datagram.printData(by);
    }
    /**
     * Print this data.
     */
    public void printData(byte[] rgByte)
    {
		int iLength = rgByte.length;
		this.printData(rgByte, iLength);
	}
    /**
     * Print this data.
     */
    public void printData(byte[] rgByte, int iLength)
    {
        this.printData(rgByte, 0, iLength);
    }
    /**
     * Print this data.
     */
    public void printData(byte[] rgByte, int iStart, int iLength)
    {
		PrintStream out = null;
		FileOutputStream dataOut = null;
//		try	{
    		out = System.out;
			dataOut = null;
//			out = new PrintStream(new FileOutputStream("outfile"));
//			dataOut = new FileOutputStream("datafile");
//		} catch (FileNotFoundException ex)	{
//			ex.printStackTrace();
//		}
		this.printData(rgByte, iStart, iLength, out, dataOut);
//		out.close();
	}
    public void printData(byte[] rgByte, int iStart, int iLength, PrintStream out, FileOutputStream dataOut)
    {
		String strChar, strOctalChar;
		String strAlpha = null;
		String strOctal = null;
		String strHex = null;
		int iAddress = 0;
		int iColumn = 0;
		if (dataOut != null)
		{
			try	{
				dataOut.write(rgByte);
				dataOut.close();
			} catch (IOException ex)	{
				ex.printStackTrace();
			}
		}
		for (int i = iStart; i < iLength; i++)
		{
			if (iColumn == 0)
			{
				String strAddress = Integer.toString(iAddress);
				while (strAddress.length() < 6)
				{
					strAddress = ' ' + strAddress;
				}
				out.print(strAddress + ": ");
				strOctal = "^ ";
				strAlpha = "| ";
				strHex = "";
			}
			byte by = rgByte[i];

			char ch = this.getHex(by >> 4);
			strHex += ch;
			ch = this.getHex(by);
			strHex += ch;
			strHex += ' ';

			strChar = new String(rgByte, i, 1);
			char charValue = strChar.charAt(0);
			if ((!Character.isLetterOrDigit(charValue))
				&& (!Character.isWhitespace(charValue)))
					charValue = ' ';
            if ((charValue == '\n')
                || (charValue == '\r'))
                    charValue = ' ';
			strAlpha += charValue;
			
			int x = by;
			if (x < 0)
				x += 256;
			strOctalChar = Integer.toString(x);
			while (strOctalChar.length() < 3)
			{
				strOctalChar = '0' + strOctalChar;
			}
			strOctal += strOctalChar + " ";

			if (iColumn++ == 7)
			{
				iColumn = 0;
				out.print(strHex);
				out.print(strOctal);
				out.println(strAlpha);
			}
			iAddress++;
		}
		out.print(strHex);
		out.print(strOctal);
		out.println(strAlpha);
    }
	/*
	 *
	 */
	public byte[] getData()
	{
		FileInputStream in = null;
		try	{
			in = new FileInputStream("datafile");
			byte[] rgByte = new byte[4096];
			int iLen = in.read(rgByte);
			byte[] rgReturn = new byte[iLen];
			for (int i = 0; i < iLen; i++)
				rgReturn[i] = rgByte[i];
			return rgReturn;
		} catch (FileNotFoundException ex)	{
			ex.printStackTrace();
		} catch (IOException ex)	{
			ex.printStackTrace();
		}
		return null;
	}
	/*
	 * Convert this (4 bit) value to a hex character.
	 */
	public char getHex(int i)
	{
		return m_rgchHex[i & 15];
	}
	
	public char[] m_rgchHex = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
}

