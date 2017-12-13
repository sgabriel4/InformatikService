package SOA;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * Interface con los parametros de 2 metodos
 * @author Sergio
 */
@WebService
public interface FileTransfer {
    /**
     * 
     * Metodo que sube al servidor SOAP un archivo
     * @param fileName nombre del archivo a subir
     * @param fileBytes bytes del archivo
     */
    @WebMethod
    public void upload(String fileName, byte[] fileBytes);
     
    /**
     * Metodo que descarga o envia un archivo
     * @param fileName el nombre del archivo a descargar
     * @return bytes del archivo descargar
     */
    @WebMethod
    public byte[] download(String fileName);  
}
