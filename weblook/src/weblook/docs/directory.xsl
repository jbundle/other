<?xml version="1.0" encoding="ISO-8859-1"?>
<!-- Edited with XML Spy v4.2 -->
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/">
  <html>
  <body>
  <h2><xsl:value-of select="directory/name"/></h2>
    <table border="1">
      <tr bgcolor="#9acd32">
        <th align="left">Name</th>
        <th align="left">Size</th>
      </tr>
      <xsl:for-each select="directory/file">
      <tr>
				<xsl:element name="a">
					<xsl:attribute name="href">
						?file=<xsl:value-of select="name"/>
					</xsl:attribute>
        <td><xsl:value-of select="name"/></td>
				</xsl:element>
        <td><xsl:value-of select="size"/></td>
      </tr>
      </xsl:for-each>
    </table>
  </body>
  </html>
</xsl:template>
</xsl:stylesheet>
