package base;

import java.io.*;

public class PostResult2File {

    public static void flush(String fileName) throws IOException {
        FileWriter fw = new FileWriter(fileName);
        fw.flush();
        fw.close();
    }

    public static void writedata(String inputData, String fileName) {

        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, true)));
            out.write(inputData);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean readlinesfromtestresultfile(String fileName) throws IOException {

        BufferedReader dataReader = null;
        boolean isFailed = false;
        dataReader = new BufferedReader(new FileReader(fileName));
        String line = dataReader.readLine();
        if (line == null) {
                isFailed = true;
        }
            return isFailed;
    }
    //get json string from json file
    public static String ReadFile(String Path){
        String tempString ="";
        String line;
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(Path)),
                    "UTF-8"));

            while((line = reader.readLine()) != null){
                if (line== null){
                    break;
                }
                tempString += line;
            }
            reader.close();

        }catch(IOException e){
            e.printStackTrace();
        }
        return tempString;
    }
}
