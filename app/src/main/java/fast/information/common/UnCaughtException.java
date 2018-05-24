package fast.information.common;

import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import fast.information.BuildConfig;

/**
 * Created by xiaqibo on 2015/7/28.
 */
public class UnCaughtException implements Thread.UncaughtExceptionHandler {


    public UnCaughtException(){
        super();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        ex.printStackTrace();
        String info = null;
        FileOutputStream fos = null ;
        ByteArrayOutputStream baos = null;
        PrintStream printStream = null;
        try {
            File file = new File(MyApplication.instance.getExternalCacheDir() , "Error.txt") ;
            if(!file.exists())
                file.createNewFile() ;
            fos = new FileOutputStream(file) ;
            for(int i = 0 ; i < ex.getStackTrace().length ; i++){
                info += ex.getStackTrace()[i].toString() ;
            }
            info += ex.toString() ;
            fos.write(info.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try{
                if (fos != null) {
                    fos.close();
                }
                fos = null ;}catch (Throwable t){t.printStackTrace();}
            if(!BuildConfig.DEBUG){
                System.exit(0);
            }else{
                ClipboardManager clipboardManager = (ClipboardManager) MyApplication.instance.getSystemService(Context.CLIPBOARD_SERVICE);
                clipboardManager.setText(info) ;
            }
        }
    }
}
