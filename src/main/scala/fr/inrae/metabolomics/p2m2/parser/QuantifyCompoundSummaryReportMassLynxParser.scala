package fr.inrae.metabolomics.p2m2.parser

import fr.inrae.metabolomics.p2m2.tools.format.output.OutputQuantifyCompoundSummaryReportMassLynx
import fr.inrae.metabolomics.p2m2.tools.format.output.OutputQuantifyCompoundSummaryReportMassLynx.Header
import fr.inrae.metabolomics.p2m2.tools.format.output.OutputQuantifyCompoundSummaryReportMassLynx.HeaderField.HeaderField

import scala.io.Source

object QuantifyCompoundSummaryReportMassLynxParser extends Parser[OutputQuantifyCompoundSummaryReportMassLynx] {
  val separator = "\t"

  def parseHeader( toParse : Seq[String] ) : Header =
    {
      val start = toParse.indices.find( id => toParse(id).trim == "Quantify Compound Summary Report" )
      val end = toParse.indices.find(id => toParse(id).trim.startsWith("Compound"))

      start match {
        case Some(s) => end match {
          case Some(e) => Header(toParse.slice(s, e)
            .filter( _.startsWith("Printed") )
            .map( _.replace("Printed","").trim)
            .find(_.nonEmpty))
          case None =>
            Header(toParse.slice(s, toParse.length)
              .filter( _.startsWith("Printed") )
              .map( _.replace("Printed","").trim)
              .find(_.nonEmpty))
        }
        case None => Header()
      }
    }
  def parseResults( toParse : Seq[String] ) : Seq[(String,Seq[Map[HeaderField,String]])] = {
    val listCompoundIndexLine = toParse.zipWithIndex.filter( _._1.trim.startsWith("Compound")).map(_._2)

    listCompoundIndexLine
      .zipWithIndex.map(
      zip => {
        val idx_line = zip._1
        val iStart = listCompoundIndexLine(zip._2)+1
        val iEnd = zip._2 match {
          case v if v == listCompoundIndexLine.length-1 => toParse.length
          case idx => listCompoundIndexLine(idx+1)
        }
        val compoundName : String = """Compound\s\d+:\s+(.*)""".r.findFirstMatchIn(toParse(idx_line)) match {
          case Some(v) => v.group(1)
          case None => throw new Exception("Can not parse Compound name:"+toParse(idx_line))
        }
        (compoundName,parseArrayCompound(toParse.slice(iStart,iEnd)))
      })
  }

  def parseArrayCompound( toParse : Seq[String] ) :Seq[Map[HeaderField,String]] = {
      toParse.filter(_.trim.nonEmpty).find(_.trim.startsWith("Name")) match {
        case Some(headerString) => {
          /* first value of array in the number corresponding to the injection*/
          val header :Seq[String] = "Num. Injection" +: headerString.trim.split(separator)
          toParse
            .map(_.trim)
            .filter(_.nonEmpty)
            .filter( ! _.startsWith("Name") )
            // split String => List[String] "List of Fields"
            .map( (line : String) => line.split(separator) )
            .map( mapLine => {
              mapLine
                .zipWithIndex.flatMap {  case (value, index) =>
                OutputQuantifyCompoundSummaryReportMassLynx.getHeaderField(header(index)) match {
                  case Some(k) if value.nonEmpty => Some(k -> value)
                  case _ => None
                }
              }.toMap
            })
        }
        case None => Seq()
      }
  }

  def get(filename : String, toParse : Seq[String]) : OutputQuantifyCompoundSummaryReportMassLynx = {
    OutputQuantifyCompoundSummaryReportMassLynx(
      origin = filename,
      header = parseHeader(toParse),
      results = parseResults(toParse)
    )
  }

  def parse(filename : String) : OutputQuantifyCompoundSummaryReportMassLynx = {

    val s = Source.fromFile(filename)
    val lines = s.getLines().toList
    s.close()

    get(
      filename,
      lines
        .map( _.trim )
        .filter( _.nonEmpty)
    )
  }

}
