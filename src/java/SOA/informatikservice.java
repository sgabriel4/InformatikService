package SOA;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.xml.ws.WebServiceException;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

@WebService(serviceName = "informatikservice")
public class informatikservice implements FileTransfer{
    
    PDFParser parser = null;
    PDDocument pdDoc = null;
    COSDocument cosDoc = null;
    PDFTextStripper pdfStripper;
    //Carpeta donde se almacenaran los archivos subidos al soap
    String folderUploads = "C:\\textconverter/";
    String parsedText;

    /**
     * Metodo que al recibir un nombre de archivo y sus bytes, le extrae el texto si este es un PDF
     * Hace uso de la api de apache: PDF BOX
     * @param fileName nombre del archivo PDF a obtener texto
     * @param fileBytes bytes del archivo PDF a obtener texto
     * @return texto del archivo PDF
     */
    @WebMethod(operationName = "getTextPDF")
    public String getTextPDF(@WebParam(name = "fileName") String fileName, @WebParam(name = "file") byte[] fileBytes) {
        
        upload(fileName, fileBytes);
        File file = new File(folderUploads + fileName);

        try {
            parser = new PDFParser(new RandomAccessFile(file,"r"));
            parser.parse();
            cosDoc = parser.getDocument();
            pdfStripper = new PDFTextStripper();
            pdDoc = new PDDocument(cosDoc);
            parsedText = pdfStripper.getText(pdDoc);
            file.delete();
            return parsedText;
        } catch (IOException e) {
            try {
                if (cosDoc != null) {
                    cosDoc.close();
                }
                if (pdDoc != null) {
                    pdDoc.close();
                }
            } catch (IOException e1) {
                return "ERROR";
            }
            return "ERROR";
        }
    }
    
    /**
     * Metodo que al recibir un nombre de archivo y sus bytes, le extrae el texto si este es un DOC , DOCX
     * Hace uso de la api de apache: POI
     * @param fileName nombre del archivo DOC,DOCX a obtener texto
     * @param fileBytes bytes del archivo DOC,DOX a obtener texto
     * @return texto del archivo DOC,DOCX
     */
    @WebMethod(operationName = "getTextDoc")
    public String getTextDOC(@WebParam(name = "fileName") String fileName, @WebParam(name = "file") byte[] fileBytes) {
        
        upload(fileName, fileBytes);
        File file = new File(folderUploads + fileName);
        try {
            XWPFDocument docx = new XWPFDocument(new FileInputStream(folderUploads + fileName));
            
            XWPFWordExtractor we = new XWPFWordExtractor(docx);
            file.delete();
            return we.getText();
        } catch (Exception ex) {
            Logger.getLogger(informatikservice.class.getName()).log(Level.SEVERE, null, ex);
            return "ERROR";
        }
        
    }
    
    /**
     * Metodo que al recibir una URL y un atributo html, obtiene  el texto que este en todo ese atributo html
     * Hace uso de la api de JSOUP
     * @param url URL de la pagina web en formato http://www.example.com
     * @param match atributo html en donde se encuentra el texto interesado
     * @return texto de la web en ese atributo
     */
    @WebMethod(operationName = "getTextHTML")
    public String getTextHTML(@WebParam(name = "url") String url, @WebParam(name = "match") String match) {
       
        try {
            Document doc = Jsoup.connect(url).get();
            Elements ps = doc.select(match);
            return ps.text();
        } catch (IOException ex) {
            Logger.getLogger(informatikservice.class.getName()).log(Level.SEVERE, null, ex);
            return "ERROR";
        } catch (Exception ex) {
            Logger.getLogger(informatikservice.class.getName()).log(Level.SEVERE, null, ex);
            return "ERROR";
        }
        
    }
    
    /**
     * Metodo que al recibir un nombre de archivo y sus bytes, le extrae el texto si este es un texto plano
     * @param fileName nombre del archivo de texto a obtener texto
     * @param fileBytes bytes del archivo de texto a obtener texto
     * @return texto del archivo texto plano
     */
    @WebMethod(operationName = "getTextTXT")
    public String getTextTXT(@WebParam(name = "fileName") String fileName, @WebParam(name = "file") byte[] fileBytes) {
        
            upload(fileName, fileBytes);
            File file = new File(folderUploads + fileName);
        try {
            
            String cadena;
            String textoCompleto="";
            FileReader f = new FileReader(file);
            BufferedReader b = new BufferedReader(f);
            while ((cadena = b.readLine()) != null) {
                textoCompleto+=cadena+"\n";
            }
            b.close();
            file.delete();
            return textoCompleto;
        } catch (Exception ex) {
            Logger.getLogger(informatikservice.class.getName()).log(Level.SEVERE, null, ex);
            return "ERROR";
        }
    }
    
    @Override
    public void upload(String fileName, byte[] fileBytes){
        try {
            FileOutputStream fos = new FileOutputStream(folderUploads+fileName);
            BufferedOutputStream outputStream = new BufferedOutputStream(fos);
            outputStream.write(fileBytes);
            outputStream.close();
                          
        } catch (IOException ex) {
            System.err.println(ex);
            throw new WebServiceException(ex);
        }
    }


    @Override
    public byte[] download(String fileName) {
        return null;
    }

}

