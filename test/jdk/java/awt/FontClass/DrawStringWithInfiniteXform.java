/*
 * Copyright (c) 2015, 2022, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
/*
 * @test
 * @bug 8023213
 * @summary Font/Text APIs should not crash/takes long time
 *          if transform includes INFINITY
 * @run main DrawStringWithInfiniteXform
 */
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

public class DrawStringWithInfiniteXform {

    volatile Timer timer;
    volatile boolean done;

    class ScheduleTask extends TimerTask {
        public void run() {
            System.out.println("Task running at " + System.currentTimeMillis());
            System.out.flush();
            synchronized (DrawStringWithInfiniteXform.class) {
               System.out.println(
                   "Checking done at " + System.currentTimeMillis());
               System.out.flush();
                if (!done) {
                    throw new RuntimeException(
                       "drawString with InfiniteXform transform takes long time");
                }
            }
        }
    }
    public DrawStringWithInfiniteXform() {
        timer = new Timer();
        timer.schedule(new ScheduleTask(), 30000);
    }

    public static void main(String [] args) {
        DrawStringWithInfiniteXform test = new DrawStringWithInfiniteXform();
        test.start();
    }

    private void start() {
        System.out.println("start at " + System.currentTimeMillis());
        System.out.flush();
        float[] vals = new float[6];
        for (int i=0; i<6; i++) {
            vals[i] = Float.POSITIVE_INFINITY;
        }
        AffineTransform nanTX = new AffineTransform(vals);

        BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bi.createGraphics();

        g2d.rotate(Float.POSITIVE_INFINITY);
        Font font = g2d.getFont();
        Font xfiniteFont;
        for (int i=0; i<2000; i++) {
            xfiniteFont = font.deriveFont(Float.POSITIVE_INFINITY);
            g2d.setFont(xfiniteFont);
            g2d.drawString("abc", 20, 20);
        }
        System.out.println("Loop done at " + System.currentTimeMillis());
        System.out.flush();
        synchronized (DrawStringWithInfiniteXform.class) {
            done = true;
            timer.cancel();
        }
        System.out.println("Test passed");
    }
}
