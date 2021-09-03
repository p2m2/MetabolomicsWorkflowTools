package fr.inrae.metabolomics.p2m2.converter

import fr.inrae.metabolomics.p2m2.parser.GCMSParser
import fr.inrae.metabolomics.p2m2.tools.format.output.OutputGCMS
import fr.inrae.metabolomics.p2m2.tools.format.output.OutputGCMS.HeaderField

object GCMS2Isocor extends App {
      println("hello world")
      if (args.length>1) {
            val filename_output_isocor = args(0)
            println("output:"+filename_output_isocor)
            //GCMS2Isocor(args.drop(1))
      } else {

      }
}

case class GCMSOutputFiles2IsocorInput( resolution : Int = 2000, separator_name : String = "_" ) {

      def export(gcms_inputfiles : Array[String]) = {
            println(gcms_inputfiles.mkString("\n"))

            gcms_inputfiles.map(
                  fileName => GCMSParser.parse(fileName)
            )
      }


      def transform( gcms : OutputGCMS ) : List[String] = {
            val sample = gcms.header.get(HeaderField.Data_File_Name) match {
                  case Some(value) => value.split("[/\\\\]").last
                  case None => throw new Exception("Can not retrieve sample (end of 'Data File Name' value) origin:"+gcms.origin)
            }

            gcms.ms_quantitative_results
              .flatMap {
                  case (mapResults : Map[String,String]) => {
                        val id = mapResults.get("ID#") match {
                              case Some(v) => v
                              case None => "unknown"
                        }
                         (mapResults.get("Name") match {
                                    case Some(v) => v
                                    case None => {
                                          throw new Exception("Can not parse 'Name' field origin:" + gcms.origin +
                                            ", id:"+id )
                                    }
                              }).split(separator_name) match {
                                    case tokens if tokens.length == 3 => {
                                          val (metabolite,derivative,isotopologue) = (tokens(0),tokens(1),tokens(2))
                                          val area = mapResults.get("Area") match {
                                                case Some(v) => v
                                                case None => throw new Exception("Can not parse 'Area' field "+
                                                  ", origin:" + gcms.origin + ", id:"+id )
                                          }
                                          Some(List(sample, metabolite, derivative, isotopologue,area, resolution).mkString("\t"))
                                    }
                                    case _ => {
                                          System.err.println(
                                                "Name should be formatted [Metabolite]_[Derivative]_[Isotopologue] " +
                                                  ", origin:" + gcms.origin + ", id:" + id )
                                          None
                                    }
                        }


                  }
            }
      }
}