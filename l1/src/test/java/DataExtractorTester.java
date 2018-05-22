public class DataExtractorTester {

    public static void main(String[] args) {
        try {
            DataExtractor parser = new DataExtractor("specs/validate.xml");
            parser.parse();
            parser.readBinary("specs/validate.bin");
//            System.out.println(parser.query("Cilarry_Harrinton", "MAX"));
            parser.writeXlsx("specs/validate.xlsx");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
