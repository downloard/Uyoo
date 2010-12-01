package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;


public class FileHelper {
	
	public static void copyFile(String target, String source) throws IOException {
        // Create channel on the source
        FileChannel srcChannel = new FileInputStream(source).getChannel();
    
        // Create channel on the destination
        FileChannel dstChannel = new FileOutputStream(target).getChannel();
    
        // Copy file contents from source to destination
        dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
    
        // Close the channels
        srcChannel.close();
        dstChannel.close();
	}
	
	public static void copyFile(String target, InputStream source) throws IOException {
        FileOutputStream dstChannel = new FileOutputStream(target);
        byte[] buffer = new byte[1];
        
        while (source.read(buffer) != -1) {
            dstChannel.write(buffer);
        }
        
        dstChannel.close();
	}
	
	public static void copyFile(File dst, File src) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);
    
        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
}
