/**
* Web worker: an object of this class executes in its own new thread
* to receive and respond to a single HTTP request. After the constructor
* the object executes on its "run" method, and leaves when it is done.
*
* One WebWorker object is only responsible for one client connection. 
* This code uses Java threads to parallelize the handling of clients:
* each WebWorker runs in its own thread. This means that you can essentially
* just think about what is happening on one client at a time, ignoring 
* the fact that the entirety of the webserver execution might be handling
* other clients, too. 
*
* This WebWorker class (i.e., an object of this class) is where all the
* client interaction is done. The "run()" method is the beginning -- think
* of it as the "main()" for a client interaction. It does three things in
* a row, invoking three methods in this class: it reads the incoming HTTP
* request; it writes out an HTTP header to begin its response, and then it
* writes out some HTML content for the response content. HTTP requests and
* responses are just lines of text (in a very particular format). 
*
**/

import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.lang.Runnable;
import java.io.*;
import java.util.Date;
import java.text.DateFormat;
import java.util.TimeZone;
//import javax.imageio;

public class WebWorker implements Runnable
{

private Socket socket;
private Path webAddressFile;  //The Path to the file trying to be accessed
private String fileType;      //The filetype can be html, jpeg, gif, png
private String contentType;   //The contentype is image/fileType or text/fileType

/**
* Constructor: must have a valid open socket
**/
public WebWorker(Socket s)
{
   socket = s;
}

/**
* Worker thread starting point. Each worker handles just one HTTP 
* request and then returns, which destroys the thread. This method
* assumes that whoever created the worker created it with a valid
* open socket object.
**/
public void run()
{
   System.err.println("Handling connection...");
   try {
      InputStream  is = socket.getInputStream();
      OutputStream os = socket.getOutputStream();
      readHTTPRequest(is);
      
      writeHTTPHeader(os, contentType); //contentType is established in readHTTPRequest
      
      writeContent(os);
      os.flush();
      socket.close();
   } catch (Exception e) {
      System.err.println("Output error: "+e);
   }
   System.err.println("Done handling connection.");
   return;
}

/**
* Read the HTTP request header.
**/
private void readHTTPRequest(InputStream is)
{
   String line;
   BufferedReader r = new BufferedReader(new InputStreamReader(is));
   while (true) {
      try {
         while (!r.ready()) Thread.sleep(1);
         line = r.readLine();
         if(line.length() > 9)
         {
        	//Looks at the line with the directory path and removes the GET and HTTP/ 1.1-----------this should end in jpeg, png, gif
	         if(line.substring(0,3).equals("GET")) 
	         {
	        	//The file should just be the directory path now
	        	 webAddressFile = Paths.get(line.substring(5, line.length() - 9));
	        	 System.err.println(webAddressFile);
	        	 
	        	 fileType = line.substring(line.indexOf('.') + 1, line.length() - 9);
	        	 //System.err.println(fileType); 
	             switch(fileType) 
	             { 
	                 case "jpeg": 
	                     System.err.println("jpeg"); 
	                     contentType = "image/jpeg";
	                     break; 
	                 case "gif": 
	                     System.err.println("gif");
	                     contentType = "image/gif";
	                     break; 
	                 case "png": 
	                     System.err.println("png"); 
	                     contentType = "image/png";
	                     break; 
	                 default:                                     //Default file type is html and content type is text/html
	                     System.err.println("unknown file type"); 
	                     contentType = "text/html";
	             } 
	         }//End if
         }//End if
         System.err.println("Request line: ("+line+")");
         if (line.length()==0) break;
      } catch (Exception e) {
         System.err.println("Request error: "+e);
         break;
      }
   }
   return;
}

/**
* Write the HTTP header lines to the client network connection.
* @param os is the OutputStream object to write to
* @param contentType is the string MIME content type (e.g. "text/html")
**/
private void writeHTTPHeader(OutputStream os, String contentType) throws Exception
{
   Date d = new Date();
   DateFormat df = DateFormat.getDateTimeInstance();
   df.setTimeZone(TimeZone.getTimeZone("GMT"));
   try  //If the File exists returns a header with "200 OK" 
   {
	  //String s1 = Files.readString(webAddressFile);
	  Files.exists(webAddressFile);
	  os.write("HTTP/1.1 200 OK\n".getBytes());
   }
   catch(Exception e) //If the File doesn't exist returns an HTTP header with "400 Not Found" 
   {
	  os.write("HTTP/1.1 404 Not Found\n".getBytes());
   }//End try-catch
   os.write("Date: ".getBytes());
   os.write((df.format(d)).getBytes());
   os.write("\n".getBytes());
   os.write("Server: Test Server\n".getBytes());
   //os.write("Last-Modified: Wed, 08 Jan 2003 23:11:55 GMT\n".getBytes());
   //os.write("Content-Length: 438\n".getBytes()); 
   os.write("Connection: close\n".getBytes());
   os.write("Content-Type: ".getBytes());
   os.write(contentType.getBytes());
   os.write("\n\n".getBytes()); // HTTP header ends with 2 newlines
   return;
}

/**
* Write the data content to the client network connection. This MUST
* be done after the HTTP header has been written out.
* @param os is the OutputStream object to write to
**/
private void writeContent(OutputStream os) throws Exception
{
	if(contentType.substring(0,4).equals("text"))
	{
		try //Writes the File if the file exists
		{
			//Stores the File into a string and calls exception if it doesn't exist
			Files.exists(webAddressFile);
			String s1 = Files.readString(webAddressFile);
			
			//Creates the date and time in Mountain Time
			Date d = new Date();
			DateFormat df = DateFormat.getDateTimeInstance();
			df.setTimeZone(TimeZone.getTimeZone("America/Denver"));
			
			//Turns the date into string
			String dateString = df.format(d);
			
			//Replaces all the iterations of certain tags with their replacements
			String s2 = s1.replaceAll("<cs371date>", dateString);
			String s3 = s2.replaceAll("<cs371server>", "Test Server");
			
			//Outputs to the os after replacements are done
			os.write(s3.getBytes());
		}
		catch (Exception e) //If the file does not exists displays a 404 error
		{
			System.err.println(webAddressFile +" is not found");
	        //System.err.println("404 error");
	        os.write("404 Not Found".getBytes());
		}//End try-catch
	}//End if
	else //If the content type starts with image/
	{
		try //Writes the File if the file exists
		{
		
			//Files.exists(webAddressFile);
			byte[] fileContents =  Files.readAllBytes(webAddressFile);
			/*
			System.err.println();
			for(int i = 0; i < fileContents.length; i++)
			{
				System.err.print(fileContents[i] + " "); 
			}
			System.err.println();
			*/
			os.write(fileContents);
		}
		catch (Exception e) //If the file does not exists displays a 404 error
		{
	        //System.err.println("404 error");
			System.err.println(webAddressFile +" is not found"); 
	        os.write("404 Not Found".getBytes());
		}//End try-catch
	}//End else
}//End writeContent

} // end class
