package hr.foi.mjurinic.bach.network.protocol;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ProtoMultimedia extends ProtoMessage {

    private byte[] rawFrame;

    public ProtoMultimedia(byte[] rawFrame) {
        super.id = ProtoMessageType.MULTIMEDIA;
        this.rawFrame = rawFrame;
    }

    public byte[] getRawFrame() {
        return rawFrame;
    }

    public Bitmap getFrameAsBitmap() {
        return BitmapFactory.decodeByteArray(rawFrame, 0, rawFrame.length);
    }
}
