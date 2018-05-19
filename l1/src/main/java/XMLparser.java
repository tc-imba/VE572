import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class XMLparser {
    public static void main(String[] args) {
        try {
            SAXReader sr = new SAXReader();
            Document doc = sr.read(new File("decoding.xml"));
            List<Node>  MeaQuantities = doc.selectNodes("//atfx_file/instance_data/MeaQuantity");
            System.out.println(MeaQuantities.size());
            Map<String, String> nameMap = new HashMap<String, String>();
            Map<String, int[]> format = new HashMap<String, int[]>();
            Map<String, Vector<Double>> data = new HashMap<String, Vector<Double>>();
            Vector<Integer> x_axis = new Vector<Integer>();
            for (Node node : MeaQuantities) {
                nameMap.put(node.selectSingleNode("Name").getText(), node.selectSingleNode("LocalColumns").getText());
            }
            List<Node> extComp = doc.selectNodes("//atfx_file/instance_data/ExternalComponent");
            for (Node node : extComp) {
                int[] temp = {Integer.parseInt(node.selectSingleNode("StartOffset").getText()),
                        Integer.parseInt(node.selectSingleNode("Blocksize").getText()), Integer.parseInt(node.selectSingleNode("Length").getText())};
                format.put(node.selectSingleNode("LocalColumn").getText(), temp);
            }
            byte[] bin = Files.readAllBytes(Paths.get("data_1.bin"));
            for (String s : nameMap.keySet()) {
                int[] l = format.get(nameMap.get(s));
                int offset = l[0];
                int blocksize = l[1];
                int length = l[2];
                if (blocksize == 4) {
                    for (int i = offset; i < offset + (length - 1) * blocksize; i += blocksize) {
                        byte[] temp = Arrays.copyOfRange(bin, i, i + blocksize);
                        for (int j = 0; j <= blocksize / 2; j++) {
                            byte b = temp[j];
                            temp[j] = temp[blocksize - 1 - j];
                            temp[blocksize - 1 - j] = b;
                        }
                        x_axis.add(ByteBuffer.wrap(temp).getInt());
                    }
                }
                else {
                    Vector<Double> v = new Vector<Double>();
                    for (int i = offset; i < offset + (length - 1) * blocksize; i += blocksize) {
                        byte[] temp = Arrays.copyOfRange(bin, i, i + blocksize);
                        for (int j = 0; j <= blocksize / 2; j++) {
                            byte b = temp[j];
                            temp[j] = temp[blocksize- 1 - j];
                            temp[blocksize - 1 - j] = b;
                        }
                        double d = ByteBuffer.wrap(temp).getDouble();
//                        System.out.println(d);
                        v.add(d);
                    }
                    data.put(s, v);
                }
            }
            double min = Collections.min(data.get("CHANNEL06"));
            System.out.println("The local column of CHANNEL06 is " + min);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
