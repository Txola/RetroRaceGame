package entity;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import main.Utils;

/**
 *
 * @author txola
 */
public class ResourceManager {
    private static final ResourceManager instance = new ResourceManager();
    public static ResourceManager instance(){
        return instance;
    }
    
    private final ArrayList<Image> images;
    
    public ResourceManager() {
        images = new ArrayList<>();
        images.add(new Image("src/resources/car2.png", (float) 0.45, 1));//player
        images.add(new Image("src/resources/mercedes.png", 1, 1));
        images.add(new Image("src/resources/subaruGris.png", (float) 2.3, 1));
        images.add(new Image("src/resources/azul.png", (float) 0.65, 1));
        images.add(new Image("src/resources/audi.png", (float) 0.55, 1));
        images.add(new Image("src/resources/kia.png", (float) 0.7, 1));
        images.add(new Image("src/resources/escarabajo.png", (float) 1.25, 1));
        images.add(new Image("src/resources/bmwm5.png", (float) 0.9, 1));
        images.add(new Image("src/resources/van.png", (float) 2.8, 1));
        images.add(new Image("src/resources/camioneta.png", (float) 0.6, 1));
        
        
        
        
        
        
        
        images.add(new Image("src/resources/Tree.png", 10, (float) 0.1));
        
        
        images.add(new Image("src/resources/finishFlag.png", (float) 1.7, 0));
    }
    
    public int getIndexOf(Image image) {
        return images.indexOf(image);
    }

    public Image getRandomVehicleImage() {
        return images.get((int) Utils.uniform(1, 10));
    }
    
    public Image get(int index){
        return images.get(index);
    }
}
