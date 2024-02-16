#
# This ProGuard configuration file illustrates how to process applications.
# Usage:
#     java -jar proguard.jar @applications.pro
#

# Specify the input jars, output jars, and library jars.

-injars  C:\jcodes\dev\jchon.jar
-outjars C:\jcodes\dev\appservers\ecims\lib\ext\loghon.jar
-dontwarn jxl.CellView
-dontwarn jxl.Workbook
-dontwarn jxl.WorkbookSettings
-dontwarn jxl.format.Colour
-dontwarn jxl.format.UnderlineStyle
-dontwarn jxl.write.Label
-dontwarn jxl.write.Number
-dontwarn jxl.write.WritableCellFormat
-dontwarn jxl.write.WritableFont
-dontwarn jxl.write.WritableSheet
-dontwarn jxl.write.WritableWorkbook
-dontwarn jxl.write.WriteException
-dontwarn jxl.write.biff.RowsExceededException
-dontwarn jxl.write.WritableFont$BoldStyle
-dontwarn jxl.write.WritableFont$FontName

-libraryjars <java.home>/lib/rt.jar
-libraryjars C:\jcodes\dev\appservers\ecims\lib\ext\jxl.jar
-keepclasseswithmembers public class * {
      public static void main(java.lang.String[]);
      public boolean crossCheckUserSession(java.lang.Long);
  }