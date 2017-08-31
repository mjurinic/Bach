package hr.foi.mjurinic.bach.network.protocol;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ProtoMultimedia extends ProtoMessage {

    private byte[] rawFrame;
    private boolean isFrontCameraFrame;

    public ProtoMultimedia(byte[] rawFrame, boolean isFrontCameraFrame) {
        super.id = ProtoMessageType.MULTIMEDIA;
        this.rawFrame = rawFrame;
        this.isFrontCameraFrame = isFrontCameraFrame;
    }

    public byte[] getRawFrame() {
        return rawFrame;
    }

    public Bitmap getFrameAsBitmap() {
        return BitmapFactory.decodeByteArray(rawFrame, 0, rawFrame.length);
    }

    public boolean isFrontCameraFrame() {
        return isFrontCameraFrame;
    }
}
