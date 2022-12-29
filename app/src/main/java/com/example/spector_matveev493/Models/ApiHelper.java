package com.example.spector_matveev493.Models;

import android.app.Activity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ApiHelper {
    Activity ctx;

    public ApiHelper(Activity ctx)
    {
        this.ctx = ctx;
    }

    public void on_ready(String res){}

    String http_get(String req, String body)
    {

        try {
            URL url = new URL(req);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("accept", "application/json");
            con.setRequestMethod("POST");

            con.setDoOutput(true);
            BufferedOutputStream out = new BufferedOutputStream(con.getOutputStream());
            out.write(body.getBytes());
            out.flush();

            BufferedInputStream inp = new BufferedInputStream(con.getInputStream());

            byte[] buf = new byte[512];
            String res = "";

            while (true)
            {
                int num = inp.read(buf);
                if (num < 0) break;

                res += new String(buf, 0, num);
            }
            con.disconnect();

            return res;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "fail";
    }

    public class NetOp implements Runnable
    {
        public String req;
        public String body;

        public void run() {
            try {
                final String res = http_get(req, body);

                ctx.runOnUiThread(new Runnable() {
                    public void run() {
                        on_ready(res);
                    }
                });
            }
            catch (Exception ex)
            {}
        }
    }

    public void send(String req, String body)
    {
        NetOp nop = new NetOp();
        nop.body = body;
        nop.req = req;

        Thread th = new Thread(nop);
        th.start();
    }
}
