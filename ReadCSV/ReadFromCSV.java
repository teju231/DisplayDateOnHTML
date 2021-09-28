import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import javax.script.ScriptException;
import java.awt.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadFromCSV {
    //Input files
    public static String sampleHtmlFile = "HTML_sample.html";
    public static String sourceCSV = "SourceCSV.csv";

    public static void main(String args[]) {
        try {
            readHTML(sampleHtmlFile);
            Desktop.getDesktop().browse(new File(sampleHtmlFile).toURI());
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void readHTML(String fileName) throws ScriptException {
        Document htmlFile = null;
        String usrName = null;
        try {
            htmlFile = Jsoup.parse(new File(fileName), "ISO-8859-1");
            String htmlFileString = htmlFile.toString();
            Boolean iframeFlag = false;
            String iframeFile = null;
            if (htmlFileString.contains("src")) {
                iframeFlag = true;
                Pattern p = Pattern.compile("src=\"(.*?)\"");
                Matcher m = p.matcher(htmlFileString);
                if (m.find()) {
                    iframeFile = m.group(1).toString();
                }
            } else {
                usrName = htmlFile.getElementById("usrName").text();
            }
            if (iframeFlag == true) {
                readHTML(iframeFile);
            }else{
                String dueDate = getDateFromCsv(usrName);
                Element expDate = htmlFile.getElementById("expiryData");
                expDate.attr("value", dueDate);
                String allHtml = htmlFile.html();
                BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
                bw.write(allHtml);
                bw.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getDateFromCsv(String usrName) {
        String line = "";
        String splitBy = ",";
        String actualDueDate = null;
        try {
            //parsing a CSV file into BufferedReader class constructor
            if(usrName != null){
                BufferedReader br = new BufferedReader(new FileReader(sourceCSV));
                String dueDate = null;
                while ((line = br.readLine()) != null)
                {
                    String[] users = line.split(splitBy);
                    //use comma as separator
                    if(users[4].equals(usrName)){
                        dueDate = users[2];
                    }
                }
                br.close();
                Date date1=new SimpleDateFormat("dd/MM/yyyy").parse(dueDate);
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy");
                Calendar cal = Calendar.getInstance();
                cal.setTime(date1);
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                cal.add(Calendar.YEAR, 3);
                Date actualDue = cal.getTime();
                //System.out.println("date is "+sdf.format(actualDue));
                actualDueDate = sdf.format(actualDue);
            }

        }
        catch(IOException | ParseException e) {
            e.printStackTrace();
        }
        return actualDueDate;
    }


}


