package edu.teco.serverless.test.model.servicelayer.lambdaruntime.images;

import edu.teco.serverless.model.servicelayer.lambdaruntime.images.ImageManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by steffen on 23.01.17.
 */
public class ImageManagerTest {
    private ImageManager manager;

    @Before
    public void setUp() {
        manager = ImageManager.getInstance();

        /**
        try {
            manager = ImageManager.getInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
*/
    }

    @Test
    public void init() throws Exception {
        assertNotEquals(0, manager.getFactories().size());
    }

    @Test
    public void getInstance() throws Exception {
        assertNotEquals(null, manager);
    }

}