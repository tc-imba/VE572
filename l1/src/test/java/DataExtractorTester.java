public class DataExtractorTester {

    public static void main(String[] args) {
        try {
            DataExtractor parser = new DataExtractor("data/decoding.xml");
            parser.parse();
            parser.readBinary("data/decoding.bin");
            System.out.println(parser.query("CHANNEL08", "MAX"));
            parser.writeXlsx("data/decoding.xlsx");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
