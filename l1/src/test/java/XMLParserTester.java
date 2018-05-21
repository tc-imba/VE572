public class XMLParserTester {

    public static void main(String[] args) {
        try {
            XMLParser parser = new XMLParser("data/validate.xml");
            parser.parse();
            parser.readBinary("data/validate.bin");
            System.out.println(parser.query("Cilarry_Harrinton", "MAX"));
            parser.writeText("data/validate.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
