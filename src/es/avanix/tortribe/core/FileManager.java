
package es.avanix.tortribe.core;

import es.avanix.tortribe.crypto.SHAHash;
import es.avanix.tortribe.main.Tortribe;
import java.io.File;
import java.util.HashMap;

/**
 *
 * @author juanjo
 */
public class FileManager {
    
    private static HashMap<byte[],File> localFiles;
    
    public static void init(String folder){
        localFiles = new HashMap<byte[],File>();
        File directory = new File(folder);
       if (directory.exists() && directory.canRead() && directory.canWrite()){
           File[] files = directory.listFiles((File dir, String name) -> !name.endsWith(".downloading"));
            for (File file : files) {
                localFiles.put(SHAHash.getSHA256fromFile(file), file);
            }
       }else{
           //ERROR
       }
       
    }
    
    public static HashMap<byte[], File> getLocalFiles(){
        return localFiles;
    }
    
    public static void populate(){
        localFiles.forEach((t, u) -> {
            String[] filetoadd = {u.getName(), new String(t)};
            Tortribe.myFiles_tableView.getItems().add(filetoadd);
        });
       
    }
    
    
}
