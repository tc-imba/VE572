import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class XMLParser {
    private Document doc;
    private Map<String, MeaQuantity> quantities;
    private Map<String, String> idToUnit;
    private Map<String, String> idToQuantity;

    public XMLParser(String Filename) throws Exception {
        SAXReader sr = new SAXReader();
        this.doc = sr.read(new File(Filename));
        this.quantities = new HashMap<String, MeaQuantity>();
        this.idToQuantity = new HashMap<String, String>();
        this.idToUnit = new HashMap<String, String>();
    }

    public static void main(String[] args) {
        try {
            XMLParser parser = new XMLParser("validate.xml");
            parser.parse();
            parser.readBinary("validate.bin");
            System.out.println(parser.query("Cilarry_Harrinton", "MAX"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class MeaQuantity {
        private String name;
        private String quantityID;
        private String type;
        private int blocksize;
        private int startOffset;
        private int valueOffset;
        private String unitID;
        private int length;
        private Number[] data;
        private Number min;
        private Number max;
        private Number med;
        private Number avg;
        private Number sum;

        private void parseShort(ByteBuffer buffer) throws Exception {
            for (int i = 0; i < this.length; i++) {
                int offset = this.startOffset + i * blocksize + valueOffset;
                this.data[i] = buffer.getShort(offset);
            }
        }

        private void parseLong(ByteBuffer buffer) throws Exception {
            for (int i = 0; i < this.length; i++) {
                int offset = this.startOffset + i * blocksize + valueOffset;
                this.data[i] = buffer.getInt(offset);
            }
        }

        private void parseFloat(ByteBuffer buffer) throws Exception {
            for (int i = 0; i < this.length; i++) {
                int offset = this.startOffset + i * blocksize + valueOffset;
                this.data[i] = buffer.getFloat(offset);
            }
        }

        private void parseDouble(ByteBuffer buffer) throws Exception {
            for (int i = 0; i < this.length; i++) {
                int offset = this.startOffset + i * blocksize + valueOffset;
                this.data[i] = buffer.getDouble(offset);
            }
        }

        private void calculate() throws Exception {
            Number[] temp = this.data.clone();
            Arrays.sort(temp);
            this.min = temp[0];
            this.max = temp[1];
            Number a = temp[this.length / 2];
            Number b = temp[this.length / 2];
            if (this.length % 2 == 0) {
                a = temp[this.length / 2 - 1];
            }
            switch (this.type) {
                case "DT_SHORT":
                    this.med = (a.shortValue() + b.shortValue()) / 2;
                    
                    break;
                case "DT_LONG":
                    this.med = (a.intValue() + b.intValue()) / 2;

                    break;
                case "DT_FLOAT":
                    this.med = (a.floatValue() + b.floatValue()) / 2;

                    break;
                case "DT_DOUBLE":
                    this.med = (a.doubleValue() + b.doubleValue()) / 2;

                    break;
                default:
                    throw new Exception("Unknown data type!");
            }
        }


//        public void prepare(Vector<T> v) {
//            Collections.sort(v);
//            this.min = v.firstElement();
//            this.max = v.lastElement();
//            if (this.length % 2 == 0) {
//                this.med = v.elementAt(this.length / 2 - 1) + v.elementAt(this.length / 2 )
//            }
//        }
    }

/*    Arrays.sort(shortList);
    mq.min = shortList[0];
    mq.max = shortList[mq.length - 1];
                        if (mq.length % 2 == 0) {
        mq.med = (shortList[mq.length / 2 - 1] + shortList[mq.length / 2]) / 2;
    } else {
        mq.med = shortList[mq.length / 2];
    }
    int shortSum = IntStream.of(shortList).sum();
    mq.sum = shortSum;
    mq.avg = shortSum / mq.length;*/

    public void parse() {
        try {
            List<Node> MeaQuantities = this.doc.selectNodes("//atfx_file/instance_data/MeaQuantity");
            List<Node> extComp = this.doc.selectNodes("//atfx_file/instance_data/ExternalComponent");
            List<Node> units = this.doc.selectNodes("//atfx_file/instance_data/Unit");
            List<Node> quantities = this.doc.selectNodes("//atfx_file/instance_data/Quantity");
            Map<String, String> LocalColumnToName = new HashMap<String, String>();
            for (Node node : MeaQuantities) {
                MeaQuantity mq = new MeaQuantity();
                mq.name = node.selectSingleNode("Name").getText();
                mq.type = node.selectSingleNode("DataType").getText();
                mq.quantityID = node.selectSingleNode("Quantity").getText();
                mq.unitID = node.selectSingleNode("Unit").getText();
                this.quantities.put(mq.name, mq);
                LocalColumnToName.put(node.selectSingleNode("LocalColumns").getText(), mq.name);
            }
            for (Node node : extComp) {
                MeaQuantity mq = this.quantities.get(LocalColumnToName.get(node.selectSingleNode("LocalColumn").getText()));
                mq.length = Integer.parseInt(node.selectSingleNode("Length").getText());
                mq.startOffset = Integer.parseInt(node.selectSingleNode("StartOffset").getText());
                mq.valueOffset = Integer.parseInt(node.selectSingleNode("ValueOffset").getText());
                mq.blocksize = Integer.parseInt(node.selectSingleNode("Blocksize").getText());
                mq.data = new Number[mq.length];
            }
            for (Node node : units) {
                this.idToUnit.put(node.selectSingleNode("Id").getText(), node.selectSingleNode("Name").getText());
            }
            for (Node node : quantities) {
                this.idToQuantity.put(node.selectSingleNode("Id").getText(), node.selectSingleNode("Name").getText());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void leo2beo(byte[] b) {
        int len = b.length;
        for (int j = 0; j <= len / 2; j++) {
            byte temp = b[j];
            b[j] = b[len - 1 - j];
            b[len - 1 - j] = temp;
        }
    }

    public String query(String name, String Op) {
        MeaQuantity mq = this.quantities.get(name);
        String head = "RESULT " + name + " OF " + this.idToQuantity.get(mq.quantityID) + " ";
        String tail = " " + this.idToUnit.get(mq.unitID) + " FROM " + mq.length + " POINTS";
        String result;
        switch (Op) {
            case "MIN":
                result = mq.min.toString();
                break;
            case "MAX":
                result = mq.max.toString();
                break;
            case "MEDIAN":
                result = mq.med.toString();
                break;
            case "SUM":
                result = mq.sum.toString();
                break;
            case "AVG":
                result = mq.avg.toString();
                break;
            default:
                return Op + " not available!";
        }
        return head + result + tail;
    }

    public void readBinary(String fileName) {
        try {
            byte[] bin = Files.readAllBytes(Paths.get(fileName));
            ByteBuffer buffer = ByteBuffer.wrap(bin);
            buffer.order(ByteOrder.BIG_ENDIAN);
            for (String s : this.quantities.keySet()) {
                MeaQuantity mq = this.quantities.get(s);
//                Vector v;
                switch (mq.type) {
                    case "DT_SHORT":
                        mq.parseShort(buffer);
                        break;
                    case "DT_LONG":
                        mq.parseLong(buffer);
                        break;
                    case "DT_FLOAT":
                        mq.parseFloat(buffer);
                        break;
                    case "DT_DOUBLE":
                        mq.parseDouble(buffer);
                        break;
                    default:
                        throw new Exception("Unknown data type!");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
