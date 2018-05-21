import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class DataExtractor {
    private Document doc;
    private String name;
    private Map<String, MeaQuantity> quantities;
    private Map<String, String> idToUnit;
    private Map<String, String> idToQuantity;

    public DataExtractor(String Filename) throws Exception {
        SAXReader sr = new SAXReader();
        this.doc = sr.read(new File(Filename));
        this.quantities = new HashMap<String, MeaQuantity>();
        this.idToQuantity = new HashMap<String, String>();
        this.idToUnit = new HashMap<String, String>();
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
        private Number sum = 0;

        private void parseShort(ByteBuffer buffer) {
            for (int i = 0; i < this.length; i++) {
                int offset = this.startOffset + i * blocksize + valueOffset;
                Short num = buffer.getShort(offset);
                this.data[i] = num;
                sum = sum.shortValue() + num;
            }
        }

        private void parseLong(ByteBuffer buffer) {
            for (int i = 0; i < this.length; i++) {
                int offset = this.startOffset + i * blocksize + valueOffset;
                Integer num = buffer.getInt(offset);
                this.data[i] = num;
                sum = sum.intValue() + num;
            }
        }

        private void parseFloat(ByteBuffer buffer) {
            for (int i = 0; i < this.length; i++) {
                int offset = this.startOffset + i * blocksize + valueOffset;
                Float num = buffer.getFloat(offset);
                this.data[i] = num;
                sum = sum.floatValue() + num;
            }
        }

        private void parseDouble(ByteBuffer buffer) {
            for (int i = 0; i < this.length; i++) {
                int offset = this.startOffset + i * blocksize + valueOffset;
                Double num = buffer.getDouble(offset);
                this.data[i] = num;
                sum = sum.doubleValue() + num;
            }
        }

        private Number[] calculate() {
            Number[] temp = this.data.clone();
            Arrays.sort(temp);
            this.min = temp[0];
            this.max = temp[this.length - 1];
            return temp;
        }

        private void calculateShort() {
            Number[] temp = this.calculate();
            if (this.length % 2 == 0) {
                this.med = (temp[this.length / 2 - 1].shortValue() + temp[this.length / 2].shortValue()) / 2;
            } else {
                this.med = temp[this.length / 2];
            }
            this.avg = this.sum.shortValue() / this.length;
        }

        private void calculateLong() {
            Number[] temp = this.calculate();
            if (this.length % 2 == 0) {
                this.med = (temp[this.length / 2 - 1].intValue() + temp[this.length / 2].intValue()) / 2;
            } else {
                this.med = temp[this.length / 2];
            }
            this.avg = this.sum.shortValue() / this.length;
        }

        private void calculateFloat() {
            Number[] temp = this.calculate();
            if (this.length % 2 == 0) {
                this.med = (temp[this.length / 2 - 1].floatValue() + temp[this.length / 2].floatValue()) / 2;
            } else {
                this.med = temp[this.length / 2];
            }
            this.avg = this.sum.shortValue() / this.length;
        }

        private void calculateDouble() {
            Number[] temp = this.calculate();
            if (this.length % 2 == 0) {
                this.med = (temp[this.length / 2 - 1].doubleValue() + temp[this.length / 2].doubleValue()) / 2;
            } else {
                this.med = temp[this.length / 2];
            }
            this.avg = this.sum.shortValue() / this.length;
        }
    }

    public void parse() {
        try {
            Node subMatrix = this.doc.selectSingleNode("//atfx_file/instance_data/SubMatrix");
            List<Node> MeaQuantities = this.doc.selectNodes("//atfx_file/instance_data/MeaQuantity");
            List<Node> extComp = this.doc.selectNodes("//atfx_file/instance_data/ExternalComponent");
            List<Node> units = this.doc.selectNodes("//atfx_file/instance_data/Unit");
            List<Node> quantities = this.doc.selectNodes("//atfx_file/instance_data/Quantity");
            Map<String, String> LocalColumnToName = new HashMap<String, String>();
            this.name = subMatrix.selectSingleNode("Name").getText();
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
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            for (String s : this.quantities.keySet()) {
                MeaQuantity mq = this.quantities.get(s);
                switch (mq.type) {
                    case "DT_SHORT":
                        mq.parseShort(buffer);
                        mq.calculateShort();
                        break;
                    case "DT_LONG":
                        mq.parseLong(buffer);
                        mq.calculateLong();
                        break;
                    case "DT_FLOAT":
                        mq.parseFloat(buffer);
                        mq.calculateFloat();
                        break;
                    case "DT_DOUBLE":
                        mq.parseDouble(buffer);
                        mq.calculateDouble();
                        break;
                    default:
                        throw new Exception("Unknown data type!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeXlsx(String fileName) {
        FileOutputStream out = null;
        try {
            Workbook workBook = new XSSFWorkbook();
            Sheet sheet = workBook.createSheet(this.name);
            sheet.setDefaultColumnWidth(20);

            Row row1 = sheet.createRow(0);
            Row row2 = sheet.createRow(1);
            int i = 0;
            for (String s : this.quantities.keySet()) {
                MeaQuantity mq = this.quantities.get(s);
                Cell cell = row1.createCell(i);
                cell.setCellValue(mq.name);
                cell = row2.createCell(i);
                cell.setCellValue(this.idToUnit.get(mq.unitID));
                i++;
            }

            out = new FileOutputStream(new File(fileName));
            workBook.write(out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
