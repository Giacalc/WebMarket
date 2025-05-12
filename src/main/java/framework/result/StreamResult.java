/*
 * StreamResult.java
 * 
 * Questa classe permette di inviare file locali e altri stream al browser sotto forma
 * di oggetti da scaricare (da non renderizzare nel browser)
 * 
 * This class supports the transmission of files and binary streams to the browser
 * (as downloadable files)
 * 
 */
package framework.result;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Giuseppe Della Penna
 */
public class StreamResult {

    protected ServletContext context;
    protected InputStream resource;
    protected String resourceName;
    protected long resourceSize;
    private String resourceType;

    public StreamResult(ServletContext context) {
        this.context = context;
        this.resource = null;
        this.resourceName = "";
        this.resourceSize = 0;
        this.resourceType = "";
    }

    public void setResource(File file) throws IOException {
        this.resource = new FileInputStream(file);
        this.resourceSize = file.length();
        this.resourceName = file.getName();
    }

    public void setResource(URL resource) throws IOException {
        URLConnection connection = resource.openConnection();
        String url = resource.toString();
        this.resource = connection.getInputStream();
        this.resourceSize = connection.getContentLength();
        this.resourceName = url.substring(url.lastIndexOf('/') + 1);
        this.resourceType = connection.getContentType();

    }

    public void setResource(InputStream data, long resourceSize, String resourceName) {
        this.resource = data;
        this.resourceSize = resourceSize;
        this.resourceName = resourceName;
    }

    public void activate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        OutputStream out = null;
        if (resource != null) {
            try {
                //disabilitiamo tutte le forme di caching...
                //disable caching...
                response.setHeader("Pragma", "");
                response.setHeader("Cache-Control", "");

                String contentType = (String) request.getAttribute("contentType");
                if (contentType == null) {
                    if (resourceType != null) {
                        contentType = resourceType;
                    } else {
                        contentType = "application/octet-stream";
                    }
                }
                response.setContentType(contentType);
                response.setContentLength((int) this.resourceSize);

                String contentDisposition = (String) request.getAttribute("contentDisposition");
                if (contentDisposition == null) {
                    contentDisposition = "attachment";
                }
                contentDisposition += "; filename=\"" + this.resourceName + "\"";
                response.setHeader("Content-Disposition", contentDisposition);

                //copiamo lo stream in output
                //copy the stream to the output
                out = response.getOutputStream();
                byte[] buffer = new byte[1024];
                int read;
                while ((read = this.resource.read(buffer)) > 0) {
                    out.write(buffer, 0, read);
                }
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException ex) {
                    //ingoriamo altri errori nel finally
                    //ignore errors in finally clause
                }
            }
        }
    }
}
