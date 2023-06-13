package infra.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class ImagePanel extends JLabel {

    public ImagePanel(ImageIcon src) {
        super(src);
    }

    public ImagePanel(Image src) {
        super((Icon) src);
    }

    public void setImage(ImageIcon src) {
        super.setIcon(src);
    }

    public void removeImage() {
        super.setIcon(new ImageIcon(""));
    }
}
