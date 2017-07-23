package hr.foi.mjurinic.bach.models;

import java.io.Serializable;

public class CameraSize implements Serializable {

    int width;
    int height;

    /**
     * Picture resolution in pixels.
     *
     * @param width in pixels
     * @param height in pixels
     */
    public CameraSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CameraSize && width == ((CameraSize) obj).width && height == ((CameraSize) obj).height;
    }
}
