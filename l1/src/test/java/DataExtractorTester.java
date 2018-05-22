public class DataExtractorTester {

    public static void main(String[] args) {
        try {
            DataExtractor parser = new DataExtractor("data/validate.xml");
            parser.parse();
            parser.readBinary("data/validate.bin");
            System.out.println(parser.query("Cilarry_Harrinton", "MAX"));
            parser.writeXlsx("data/validate.xlsx");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
