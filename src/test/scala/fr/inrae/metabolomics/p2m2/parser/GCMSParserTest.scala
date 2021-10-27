package fr.inrae.metabolomics.p2m2.parser

import fr.inrae.metabolomics.p2m2.tools.format.output.OutputGCMS.HeaderField
import utest.{TestSuite, Tests, test}

object GCMSParserTest extends TestSuite{
  val tests = Tests{
    test("getIndexLinesByCategories empty") {
      val toParse = ""
      assert( GCMSParser.getIndexLinesByCategories(toParse.split("\n").toList) == Map() )
    }

    test("getIndexLinesByCategories basic 1") {
      val toParse = "[H]"
      val v = GCMSParser.getIndexLinesByCategories(toParse.split("\n").toList)
      assert( v == Map("H"-> (0,1)) )
    }

    test("getIndexLinesByCategories basic 2") {
      val toParse =
      """
        |
        |[H]
        |""".stripMargin
      val v = GCMSParser.getIndexLinesByCategories(toParse.split("\n").toList)
      assert( v == Map("H"-> (2,3)) )
    }

    test("getIndexLinesByCategories basic 3") {
      val toParse =
        """
          |
          |[Header]
          |
          |[U]
          |""".stripMargin
      val v = GCMSParser.getIndexLinesByCategories(toParse.split("\n").toList)
      assert( v == Map("Header"-> (2,4),"U"->(4,5)) )
    }

    test("parseHeader - 1") {
      val toParse =
        """
          |
          |[Header]
          |Data File Name	C:\Users\ydellero\Desktop\Projets CR\P2M2\TQD\210510_13C_Younes\13CPROT2.qgd
          |Output Date	23/08/2021
          |Output Time	14:09:36
          |
          |[U]
          |""".stripMargin

      val v = GCMSParser.parseHeader(toParse.split("\n").toList)
      assert ( v == Map(
        HeaderField.Data_File_Name -> """C:\Users\ydellero\Desktop\Projets CR\P2M2\TQD\210510_13C_Younes\13CPROT2.qgd""",
        HeaderField.Output_Date -> "23/08/2021",
        HeaderField.Output_Time -> "14:09:36"
      ))
    }

    test("parseMSQuantitativeResults - 1") {
      val toParse =
        """
          |
          |[Header]
          |Data File Name	C:\Users\ydellero\Desktop\Projets CR\P2M2\TQD\210510_13C_Younes\13CPROT2.qgd
          |Output Date	23/08/2021
          |Output Time	14:09:36
          |
          |[U]
          |
          |[MS Quantitative Results]
          |ID#	Name	Type	ISTD Group#	Mass	Ret.Time	Start Time	End Time	A/H	Area	Height	Conc.	Mode	Peak#	Std.Ret.Time	Calibration Curve	3rd	2nd	1st	Constant	Ref.Ion Area	Ref.Ion Height	Ref.Ion Set Ratio	Ref.Ion Ratio	Recovery	SI	Ref.Ion1 m/z	Ref.Ion1 Area	Ref.Ion1 Height	Ref.Ion1 Set Ratio	Ref.Ion1 Ratio	Ref.Ion2 m/z	Ref.Ion2 Area	Ref.Ion2 Height	Ref.Ion2 Set Ratio	Ref.Ion2 Ratio	Ref.Ion3 m/z	Ref.Ion3 Area	Ref.Ion3 Height	Ref.Ion3 Set Ratio	Ref.Ion3 Ratio	Ref.Ion4 m/z	Ref.Ion4 Area	Ref.Ion4 Height	Ref.Ion4 Set Ratio	Ref.Ion4 Ratio	Ref.Ion5 m/z	Ref.Ion5 Area	Ref.Ion5 Height	Ref.Ion5 Set Ratio	Ref.Ion5 Ratio	Ret. Index	S/N	Unit	Description	Threshold
          |1	Glyoxylate (1MEOX) (1TMS )m0	Target	1	160.00	6.405	6.393	6.423	1.080	14	13	0.00029	Auto	3	6.400	Default	0	0	0	0	0	0	84.50	0.00	0.00	18	73.00	0	0	84.50	0.00	59.00	142	129	49.59	1014.29	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	1218	4.87	mg/L		0.00000
          |""".stripMargin
      val v = GCMSParser.parseMSQuantitativeResults(toParse.split("\n").toList)
      assert (v.length == 1 )
      assert (v(0).size == 56 )
    }

    test("parse 13CPROT1") {
      val v = GCMSParser.parse(getClass.getResource("/GCMS/13CPROT1.txt").getPath)
      assert (v.header.get(HeaderField.Data_File_Name).last == """C:\Users\ydellero\Desktop\Projets CR\P2M2\TQD\210510_13C_Younes\13CPROT1.qgd""" )
      assert (v.header.get(HeaderField.Output_Date).last == "23/08/2021")
      assert (v.header.get(HeaderField.Output_Time).last == "14:08:03")
      assert (v.ms_quantitative_results.length == 259 )
    }

    test("parse 13CPROT2") {
      val v = GCMSParser.parse(getClass.getResource("/GCMS/13CPROT2.txt").getPath)
      assert (v.header.get(HeaderField.Data_File_Name).last == """C:\Users\ydellero\Desktop\Projets CR\P2M2\TQD\210510_13C_Younes\13CPROT2.qgd""" )
      assert (v.header.get(HeaderField.Output_Date).last == "23/08/2021")
      assert (v.header.get(HeaderField.Output_Time).last == "14:09:36")
      assert (v.ms_quantitative_results.length == 262 )
    }

    test("parse 13CPROT3") {
      val v = GCMSParser.parse(getClass.getResource("/GCMS/13CPROT3.txt").getPath)
      assert (v.header.get(HeaderField.Data_File_Name).last == """C:\Users\ydellero\Desktop\Projets CR\P2M2\TQD\210510_13C_Younes\13CPROT3.qgd""" )
      assert (v.header.get(HeaderField.Output_Date).last == "23/08/2021")
      assert (v.header.get(HeaderField.Output_Time).last == "14:09:49")
      assert (v.ms_quantitative_results.length == 262 )
    }

    test("parse 13CPROT4") {
      val v = GCMSParser.parse(getClass.getResource("/GCMS/13CPROT4.txt").getPath)
      assert (v.header.get(HeaderField.Data_File_Name).last == """C:\Users\ydellero\Desktop\Projets CR\P2M2\TQD\210510_13C_Younes\13CPROT4.qgd""" )
      assert (v.header.get(HeaderField.Output_Date).last == "23/08/2021")
      assert (v.header.get(HeaderField.Output_Time).last == "14:10:14")
      assert (v.ms_quantitative_results.length == 262 )
    }
  }
}
