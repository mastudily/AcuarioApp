/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.clases;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author Marcelo Astudillo
 */
public class HttpPost {

    private final String crlf = "\r\n";
    private URL url;
    private URLConnection urlConnection;
    private OutputStream outputStream;
    private InputStream inputStream;
    private String[] fileNames;
    private String output;
    private String boundary;
    private final int bufferSize = 4096;

    public HttpPost(URL argUrl) {
        url = argUrl;
        boundary = "---------------------------4664151417711";
    }

    public void setFileNames(String[] argFiles) {
        fileNames = argFiles;
    }

    public void post() {
        try {
            System.out.println("url:" + url);
            urlConnection = url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            String postData = "";
            String fileOrigen = fileNames[0];
            String fileDestino = fileNames[1];
            File f = new File(fileNames[0]);
            if (f.exists()) {
                InputStream fileInputStream = new FileInputStream(fileOrigen);

                byte[] fileData = new byte[fileInputStream.available()];
                fileInputStream.read(fileData);

                // ::::: PART 1 :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
                String part1 = "";
                part1 += "--" + boundary + crlf;

                fileOrigen = f.getName(); // we do not want the whole path, just the name
                part1 += "Content-Disposition: form-data; name=\""+fileDestino+"\"; filename=\"" + fileOrigen + "\""
                        + crlf;

            // CONTENT-TYPE
                // TODO: add proper MIME support here
                if (fileOrigen.endsWith("png")) {
                    part1 += "Content-Type: image/png" + crlf;
                } else if (fileOrigen.endsWith("jpg")) {
                    part1 += "Content-Type: image/jpeg" + crlf;
                } else if (fileOrigen.endsWith("pdf")) {
                    part1 += "Content-Type: application/pdf" + crlf;
                } else if (fileOrigen.endsWith("gif")) {
                    part1 += "Content-Type: image/jpeg" + crlf;
                }

                part1 += crlf;
                //System.out.println(part1);
            // File's binary data will be sent after this part

                // ::::: PART 2 :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
                String part2 = crlf + "--" + boundary + "--" + crlf;

                //System.out.println("Content-Length"
                //        + String.valueOf(part1.length() + part2.length() + fileData.length));
                urlConnection.setRequestProperty("Content-Length",
                        String.valueOf(part1.length() + part2.length() + fileData.length));

                // ::::: File send ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
                outputStream = urlConnection.getOutputStream();
                outputStream.write(part1.getBytes());

                int index = 0;
                int size = bufferSize;
                do {
                    System.out.println("wrote " + index + "b");
                    if ((index + size) > fileData.length) {
                        size = fileData.length - index;
                    }
                    outputStream.write(fileData, index, size);
                    index += size;
                } while (index < fileData.length);
                System.out.println("wrote " + index + "b");

                System.out.println(part2);
                outputStream.write(part2.getBytes());
                outputStream.flush();

                // ::::: Download result into the 'output' String :::::::::::::::::::::::::::::::::::::::::::::::
                inputStream = urlConnection.getInputStream();
                StringBuilder sb = new StringBuilder();
                char buff = 512;
                int len;
                byte[] data = new byte[buff];
                do {

                    len = inputStream.read(data);

                    if (len > 0) {
                        sb.append(new String(data, 0, len));
                    }
                } while (len > 0);
                output = sb.toString();

                System.out.println("DONE");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Close connection");
            try {
                outputStream.close();
            } catch (Exception e) {
                System.out.println(e);
            }
            try {
                inputStream.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    } // post() method

    public String getOutput() {
        return output;
    }
}
