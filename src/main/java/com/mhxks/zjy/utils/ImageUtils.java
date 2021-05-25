package com.mhxks.zjy.utils;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
public class ImageUtils {

    public static BufferedImage removeBackground(byte[] bs){
        //定义一个临界阈值
        int threshold = 500;




            ImageIcon buf = new ImageIcon(bs);

            BufferedImage img = new BufferedImage(161,41,BufferedImage.TYPE_INT_RGB);
            Graphics2D g = (Graphics2D)img.getGraphics();
            g.drawImage(buf.getImage(),0,0,161,41,null);
            g.dispose();


            int width = img.getWidth();
            int height = img.getHeight();






            for(int i = 1;i < width;i++){
                for (int x = 0; x < width; x++){
                    for (int y = 0; y < height; y++){
                        Color color = new Color(img.getRGB(x, y));
                       // System.out.println("red:"+color.getRed()+" | green:"+color.getGreen()+" | blue:"+color.getBlue());
                        int num = color.getRed()+color.getGreen()+color.getBlue();
                        if(num >= threshold){
                            img.setRGB(x, y, Color.WHITE.getRGB());
                        }

                    }
                }
            }
            for(int i = 1;i<width;i++){
                Color color1 = new Color(img.getRGB(i, 1));
                int num1 = color1.getRed()+color1.getGreen()+color1.getBlue();
                for (int x = 0; x < width; x++)
                {
                    for (int y = 0; y < height; y++)
                    {
                        Color color = new Color(img.getRGB(x, y));

                        int num = color.getRed()+color.getGreen()+color.getBlue();
                        if(num==num1){
                            img.setRGB(x, y, Color.BLACK.getRGB());
                        }else{
                            img.setRGB(x, y, Color.WHITE.getRGB());
                        }
                    }
                }
            }

            for (int i = 0; i < width; i++) {
                img.setRGB(i,0,Color.BLACK.getRGB());
                img.setRGB(i,height-1,Color.BLACK.getRGB());
            }
            for (int i = 0; i < height; i++) {
                img.setRGB(0,i,Color.BLACK.getRGB());
                img.setRGB(width-1,i,Color.BLACK.getRGB());
            }

            for (int i = 0; i < width; i++) {
                for (int i1 = 0; i1 < height; i1++) {


                    int color = img.getRGB(i,i1);
                    if(isWhite(color)){
                        List<ColorPostion> colorPostions = new ArrayList<ColorPostion>();
                        List<ColorPostion> colorPostions2 = new ArrayList<ColorPostion>();
                        colorPostions.add(new ColorPostion(i,i1,new Color(color)));
                        boolean hasWhite;
                        do {
                            hasWhite = false;
                            for (ColorPostion colorPostion : colorPostions) {
                                int colorleft = img.getRGB(colorPostion.x - 1, colorPostion.y);
                                if (isWhite(colorleft) && !conPostion(colorPostion.x - 1, colorPostion.y, colorPostions)&& !conPostion(colorPostion.x - 1, colorPostion.y,colorPostions2)) {
                                    colorPostions2.add(new ColorPostion(colorPostion.x - 1, colorPostion.y, new Color(colorleft)));
                                    hasWhite = true;

                                }
                                int colorright = img.getRGB(colorPostion.x + 1, colorPostion.y);
                                if (isWhite(colorright) && !conPostion(colorPostion.x + 1, colorPostion.y, colorPostions)&& !conPostion(colorPostion.x + 1, colorPostion.y, colorPostions2)) {
                                    colorPostions2.add(new ColorPostion(colorPostion.x + 1, colorPostion.y, new Color(colorright)));
                                    hasWhite = true;

                                }
                                int colortop = img.getRGB(colorPostion.x, colorPostion.y + 1);
                                if (isWhite(colortop) && !conPostion(colorPostion.x, colorPostion.y + 1, colorPostions)&& !conPostion(colorPostion.x, colorPostion.y + 1, colorPostions2)) {
                                    colorPostions2.add(new ColorPostion(colorPostion.x, colorPostion.y + 1, new Color(colortop)));
                                    hasWhite = true;

                                }
                                int colorbotton = img.getRGB(colorPostion.x, colorPostion.y - 1);
                                if (isWhite(colorbotton) && !conPostion(colorPostion.x, colorPostion.y - 1, colorPostions)&& !conPostion(colorPostion.x, colorPostion.y - 1, colorPostions2)) {
                                    colorPostions2.add(new ColorPostion(colorPostion.x, colorPostion.y - 1, new Color(colorbotton)));
                                    hasWhite = true;
                                }
                            }
                            colorPostions.addAll(colorPostions2);
                            colorPostions2.clear();

                        }while (hasWhite==true);

                        if(colorPostions.size()<10){
                            for (ColorPostion colorPostion : colorPostions) {
                                img.setRGB(colorPostion.x,colorPostion.y,Color.BLACK.getRGB());
                            }
                        }
                    }

                }
            }



            return img;

    }
/*
    public static void cuttingImg(String imgUrl){
        try{
            File newfile=new File(imgUrl);
            BufferedImage bufferedimage=ImageIO.read(newfile);
            int width = bufferedimage.getWidth();
            int height = bufferedimage.getHeight();
            if (width > 52) {
                bufferedimage=ImgUtils.cropImage(bufferedimage,(int) ((width - 52) / 2),0,(int) (width - (width-52) / 2),(int) (height));
                if (height > 16) {
                    bufferedimage=ImgUtils.cropImage(bufferedimage,0,(int) ((height - 16) / 2),52,(int) (height - (height - 16) / 2));
                }
            }else{
                if (height > 16) {
                    bufferedimage=ImgUtils.cropImage(bufferedimage,0,(int) ((height - 16) / 2),(int) (width),(int) (height - (height - 16) / 2));
                }
            }
            ImageIO.write(bufferedimage, "jpg", new File(imgUrl));
        }catch (IOException e){
            e.printStackTrace();
        }
    }*/
public static BufferedImage toBufferedImage(Image image) {


    // This code ensures that all the pixels in the image are loaded
    image = new ImageIcon(image).getImage();
    BufferedImage bimage = null;
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    try {
        int transparency = Transparency.OPAQUE;
        GraphicsDevice gs = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gs.getDefaultConfiguration();
        bimage = gc.createCompatibleImage(image.getWidth(null),
                image.getHeight(null), transparency);
    } catch (HeadlessException e) {
        // The system does not have a screen
    }
    if (bimage == null) {
        // Create a buffered image using the default color model
        int type = BufferedImage.TYPE_INT_RGB;
        bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
    }
    // Copy image to buffered image
    Graphics2D g = bimage.createGraphics();

    // 增加透明度解决png透明图片会变黑的问题
    bimage = g.getDeviceConfiguration().createCompatibleImage(image.getWidth(null), image.getHeight(null), Transparency.TRANSLUCENT);
    g = bimage.createGraphics();

    // Paint the image onto the buffered image
    g.drawImage(image, 0, 0, null);
    g.dispose();
    return bimage;
}

    public static boolean isWhite(int rgb){
        Color color = new Color(rgb);
        int he = color.getRed()+color.getGreen()+color.getBlue();
        return color.getRed()==color.getGreen()&&color.getRed()==color.getBlue()&&(he>(240*3));
    }

    public static class ColorPostion{
        public int x;
        public int y;
        public Color color;
        public ColorPostion(int x,int y,Color color){
            this.x = x;
            this.y =  y;
            this.color = color;
        }
    }
    public static boolean conPostion(int x,int y,List<ColorPostion> colorPostions){
        for (ColorPostion colorPostion : colorPostions) {
            if(colorPostion.x==x&&colorPostion.y==y){
                return true;
            }
        }
        return false;
    }
}
