<?xml version="1.0" encoding="ISO-8859-1"?>
<!-- Edited with XML Spy v4.2 -->
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/">
  <html>
  <body>
  <h2><xsl:value-of select="file/name"/></h2>
    <xsl:value-of select="file/directory"/>/<xsl:value-of select="file/name"/>

		<xsl:element name="form">
				<xsl:attribute name="action">
					LookServlet
				</xsl:attribute>
				<xsl:attribute name="method">
					get
				</xsl:attribute>
			<xsl:element name="input">
				<xsl:attribute name="type">hidden</xsl:attribute>
				<xsl:attribute name="name">file</xsl:attribute>
				<xsl:attribute name="value"><xsl:value-of select="/file/name"/></xsl:attribute>
			</xsl:element>
			<br/>include extensions (+):
      <xsl:for-each select="file/extensions/extension">
			<xsl:element name="input">
				<xsl:attribute name="type">checkbox</xsl:attribute>
				<xsl:attribute name="name">plus</xsl:attribute>
				<xsl:attribute name="value"><xsl:value-of select="."/></xsl:attribute>
			</xsl:element>
			<xsl:value-of select="."/>
			/
      </xsl:for-each>
			<br/>exclude extensions (-):
      <xsl:for-each select="file/extensions/extension">
			<xsl:element name="input">
				<xsl:attribute name="type">checkbox</xsl:attribute>
				<xsl:attribute name="name">minus</xsl:attribute>
				<xsl:attribute name="value"><xsl:value-of select="."/></xsl:attribute>
			</xsl:element>
			<xsl:value-of select="."/>
			/
      </xsl:for-each>
			<br/>
			<input type="checkbox" name="params" value="yes"/> Analyze params?
			<input type="checkbox" name="noparams" value="yes" checked="true"/> Exclude params?
			<br/>
    		<input type="submit" value="Send"/>
			<input type="reset"/>
		</xsl:element>
	<p/>

    <table border="1">
      <tr bgcolor="#9acd32">
        <th align="left">Count</th>
        <th align="left">Name</th>
      </tr>
      <xsl:for-each select="file/data">
      <tr>
        <td><xsl:value-of select="count"/></td>
		<td>
		<xsl:element name="a">
					<xsl:attribute name="href">
						?file=<xsl:value-of select="url"/>
					</xsl:attribute>
        	<xsl:value-of select="url"/>
		</xsl:element>
		</td>
      </tr>
      </xsl:for-each>
    </table>
  </body>
  </html>
</xsl:template>
</xsl:stylesheet>
