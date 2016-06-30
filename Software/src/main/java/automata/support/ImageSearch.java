package automata.support;

import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ImageSearch {

    private final int cores = Runtime.getRuntime().availableProcessors();

    private int min = 0b11111111_11111111_11111111;
    private int max = 0b00000000_00000000_00000000;
    private int tWidth, maxX = 0, maxY = 0;
    private int tHeight, minX = 0, minY = 0;
    private BufferedImage t;

    public ImageSearch(BufferedImage bt) {
        storeTarget(bt);
    }

    public final void storeTarget(BufferedImage t) {
        this.t = t; int rgb; tWidth = t.getWidth(); tHeight = t.getHeight();
        for(int x = 0; x < tWidth; x++) {
            for (int y = 0; y < tHeight; y++) {
                rgb = t.getRGB(x, y);
                if (Integer.compareUnsigned(rgb, max)>0) { max = rgb; maxX = x; maxY = y; }
                if (Integer.compareUnsigned(rgb, max)<0) { min = rgb; minX = x; minY = y; }
            }
        }
    }

    public final int[] search(BufferedImage i) {

        int iWidth, iHeight, point[] = new int[2];
        point[0] = -1; point[1] = -1;
        int startX[] = new int[cores], endX[] = new int[cores];
        int startY[] = new int[cores], endY[] = new int[cores];

        iWidth = i.getWidth()-tWidth; iHeight = i.getHeight()-tHeight;
        ExecutorService executor = Executors.newFixedThreadPool(cores);

        for (int q = 0 ; q < cores ; q++){
            startX[q] = (iWidth/cores)*q; startY[q] = 0;
            endX[q] = (iWidth/cores)*(q+1); endY[q] = iHeight;
            int fQuad = q;

            executor.execute(() -> {
                Thread.currentThread().setName("thread" + fQuad);
                int difference = 100; int rgbC;
                for (int x = startX[fQuad]; x < endX[fQuad]; x++) {
                    for (int y = startY[fQuad]; y < endY[fQuad]; y++) {
                        rgbC = i.getRGB(x + maxX, y + maxY);
                        if (rgbC == max) {
                            rgbC = i.getRGB(x + minX, y + minY);
                            if (rgbC == min) {
                                difference = 0;
                                for (int ty = 0; ty < tHeight; ty = ty +3) {
                                    for (int tx = 0; tx < tWidth; tx = tx +3) {
                                        difference += t.getRGB(tx, ty) - i.getRGB(x+tx, y+ty);
                                    }
                                }
                                if (difference == 0) {
                                    point[0] = x; point[1] = y;
                                    executor.shutdownNow();
                                }
                            }
                        }
                        if (difference == 0) break;
                    }
                    if (difference == 0) break;
                }
            });
        }
        try {
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return point;
    }
}