<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" exclude-result-prefixes="xsl xs">
  <xsl:output method="html" omit-xml-declaration="yes" indent="yes" />
  <xsl:strip-space elements="*"/>  

  <xsl:variable name="smallCase" select="'abcdefghijklmnopqrstuvwxyz'"/>
  <xsl:variable name="upperCase" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'"/>

  <xsl:template match="/xs:schema/xs:element">
    <h2>SelfIssue Configuration Editor</h2>
    <p><xsl:value-of select="xs:annotation/xs:documentation[1]" /></p>
    <img src="images/{translate(@name,$upperCase,$smallCase)}.png" />
    <p><xsl:copy-of select="xs:annotation/xs:documentation[2]" /></p>
    <xsl:apply-templates />
  </xsl:template>

  <xsl:template match="xs:element[@name='UI']|xs:element[@name='Systems']|xs:element[@name='Admin']">
       <xsl:apply-templates mode="tabs"/>
  </xsl:template>

  <xsl:template match="xs:element">
       <h3><xsl:value-of select="@name"/></h3>
       <p><pre style="white-space: pre-wrap;"><xsl:value-of select="xs:annotation/xs:documentation" /></pre></p>
       <img src="images/{translate(@name,$upperCase,$smallCase)}.png" />
       <p><ul>
       <xsl:apply-templates select="*[position() &gt; 1]" mode="list"/>
       </ul></p>
  </xsl:template>

  <xsl:template match="xs:element" mode="tabs">
    <h3><xsl:value-of select="../../../@name" /> - <xsl:value-of select="@name" /></h3>
    <p><pre style="white-space: pre-wrap;"><xsl:value-of select="xs:annotation/xs:documentation" /></pre></p>
    <img src="images/{translate(../../../@name,$upperCase,$smallCase)}-{translate(@name,$upperCase,$smallCase)}.png" />
    <p><ul>
    <xsl:apply-templates select="*[position() &gt; 1]" mode="list"/>
    </ul></p>
  </xsl:template>

  <xsl:template match="xs:documentation" mode="list">
    <li><b><xsl:value-of select="../../@name"/></b>: <pre style="white-space: pre-wrap;"><xsl:value-of select="text()" /></pre></li>
  </xsl:template>

  <xsl:template match="xs:documentation" mode="tabs">
  </xsl:template>

  <xsl:template match="xs:documentation">
  </xsl:template>

  <xsl:template match="*">
    <xsl:apply-templates />
  </xsl:template>

</xsl:stylesheet>