public class XMLParserTester {

    public static void main(String[] args) {
        try {
            XMLParser parser = new XMLParser("data/decoding.xml");
            parser.parse();
            parser.readBinary("data/decoding.bin");
            System.out.println(parser.query("CHANNEL08", "MAX"));
            parser.writeXlsx("data/decoding.xlsx");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
