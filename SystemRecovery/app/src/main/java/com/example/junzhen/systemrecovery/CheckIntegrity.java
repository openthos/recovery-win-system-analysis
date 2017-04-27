package com.example.junzhen.systemrecovery;

import android.os.Handler;
import android.os.Message;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by junzhen on 2015/10/29.
 */
public class CheckIntegrity extends Thread {
    private Handler handler;
    private String file;
    private String sha1_stardard;

    public CheckIntegrity(Handler handler,String file,String sha1_stardard){
        this.handler = handler;
        this.file = file;
        this.sha1_stardard = sha1_stardard;
    }
    public void run()
    {
        check();
    }
    public boolean check() {
        File file = new File(this.file);
        if (file.exists()) {
            try {
                if (sha1_stardard.equals(getFileSHA(file))) {
                    sendMsg(FileUtil.fileRight);
                    return true;
                } else {
                    sendMsg(FileUtil.fileWrong);
                    return false;
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            sendMsg(FileUtil.fileNotExist);
            return false;
        }

    }
    private static String getFileSHA(File file) throws NoSuchAlgorithmException, IOException {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest;
        FileInputStream in;
        byte buffer[] = new byte[1024];
        int len;
        digest = MessageDigest.getInstance("SHA-1");
        in = new FileInputStream(file);
        while ((len = in.read(buffer, 0, 1024)) != -1) {
            digest.update(buffer, 0, len);
        }
        in.close();
        BigInteger bigInt = new BigInteger(1, digest.digest());
        String result = bigInt.toString(16);
        if(result.length() != 40)
            result = "0" + result;
        return result;
    }

    private void sendMsg(int what) {
        Message msg = new Message();
        msg.what = what;
        handler.sendMessage(msg);
    }
}
