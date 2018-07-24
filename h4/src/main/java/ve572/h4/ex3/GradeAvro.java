package ve572.h4.ex3;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import ve572.h4.ex3.avro.Grade;

import java.io.*;
import java.util.StringTokenizer;

import static java.lang.Integer.parseInt;

public class GradeAvro {

    public static void Serialize(String csvName, String avroName) throws IOException {
        DatumWriter<Grade> gradeDatumWriter = new SpecificDatumWriter<>(Grade.class);
        DataFileWriter<Grade> dataFileWriter = new DataFileWriter<>(gradeDatumWriter);
        dataFileWriter.create(new Grade().getSchema(), new File(avroName));

        FileInputStream inputStream = new FileInputStream(csvName);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String str;
        while ((str = bufferedReader.readLine()) != null) {
            StringTokenizer tokenizer = new StringTokenizer(str, ",");
            Grade grade = new Grade();
            grade.setName(tokenizer.nextToken());
            grade.setId(tokenizer.nextToken());
            grade.setScore(parseInt(tokenizer.nextToken()));
            dataFileWriter.append(grade);
        }

        dataFileWriter.close();
        bufferedReader.close();
        inputStream.close();
    }

    public static void Deserialize(String avroName, String csvName) throws IOException {
        DatumReader<Grade> gradeDatumReader = new SpecificDatumReader<>(Grade.class);
        DataFileReader<Grade> dataFileReader = new DataFileReader<>(new File(avroName), gradeDatumReader);

        FileOutputStream outputStream = new FileOutputStream(csvName);
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

        Grade grade = null;
        while (dataFileReader.hasNext()) {
            grade = dataFileReader.next(grade);
            String str = grade.getName() + "," + grade.getId() + "," + grade.getScore().toString() + '\n';
            bufferedWriter.write(str);
        }

        dataFileReader.close();
        bufferedWriter.close();
        outputStream.close();
    }

    public static void main(String[] args) throws Exception {
        Serialize("grades.csv", "grades.avro");
        Deserialize("grades.avro", "grades.1.csv");
    }

}
