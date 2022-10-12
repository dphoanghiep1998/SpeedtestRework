package com.example.speedtest_rework.core.download;


import com.example.speedtest_rework.core.base.Connection;

import java.io.InputStream;

public abstract class Downloader extends Thread{
    private Connection c;
    private String path;
    private int ckSize;
    private boolean stopASAP=false, resetASAP=false;
    private long totDownloaded=0;

    public Downloader(Connection c, String path, int ckSize){
        this.c=c;
        this.path=path;
        this.ckSize=ckSize<1?1:ckSize;
        start();
    }

    private static final int BUFFER_SIZE=10240;
    public void run(){
        try{
            String s=path;
            s+= "random4000x4000.jpg";
            long lastProgressEvent=System.currentTimeMillis();
            long ckBytes=ckSize*1048576, newRequestThreshold=ckBytes/4;
            long bytesLeft=0;
            InputStream in=c.getInputStream();
            byte[] buf=new byte[BUFFER_SIZE];
            while(!stopASAP){
                if(bytesLeft<=newRequestThreshold){
                    c.GET(s, true);
                    bytesLeft+=ckBytes;
                }
                int l=in.read(buf);
                bytesLeft-=l;
                if(resetASAP){
                    totDownloaded=0;
                    resetASAP=false;
                }
                totDownloaded+=l;
                if(System.currentTimeMillis()-lastProgressEvent>200){
                    lastProgressEvent=System.currentTimeMillis();
                    onProgress(totDownloaded);
                }
            }
            c.close();
        }catch(Throwable t){
            try{c.close();}catch(Throwable t1){}
            onError(t.toString());
        }
    }

    public void stopASAP(){
        this.stopASAP=true;
    }

    public abstract void onProgress(long downloaded);
    public abstract void onError(String err);

    public void resetDownloadCounter(){
        resetASAP=true;
    }

    public long getDownloaded() {
        return resetASAP?0:totDownloaded;
    }
}