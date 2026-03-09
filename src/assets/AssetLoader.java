package assets;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class AssetLoader {

    public static BufferedImage loadImage(String path){

        try{
            return ImageIO.read(AssetLoader.class.getResource(path));
        }
        catch(IOException e){
            e.printStackTrace();
            return null;
        }

    }

}