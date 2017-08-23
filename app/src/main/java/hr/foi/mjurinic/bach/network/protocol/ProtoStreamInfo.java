package hr.foi.mjurinic.bach.network.protocol;

import java.io.Serializable;
import java.util.List;

import hr.foi.mjurinic.bach.models.CameraSize;

public class ProtoStreamInfo extends ProtoMessage {

    private CameraInfo frontCameraInfo;
    private CameraInfo backCameraInfo;

    public ProtoStreamInfo(CameraInfo frontCameraInfo, CameraInfo backCameraInfo) {
        super.id = ProtoMessageType.STREAM_INFO_RESPONSE;
        this.frontCameraInfo = frontCameraInfo;
        this.backCameraInfo = backCameraInfo;
    }

    public CameraInfo getFrontCameraInfo() {
        return frontCameraInfo;
    }

    public CameraInfo getBackCameraInfo() {
        return backCameraInfo;
    }

    public static class CameraInfo implements Serializable {
        private List<CameraSize> resolutions;
        private List<int[]> supportedFpsRange;

        public CameraInfo(List<CameraSize> resolutions, List<int[]> supportedFpsRange) {
            this.resolutions = resolutions;
            this.supportedFpsRange = supportedFpsRange;
        }

        public List<CameraSize> getResolutions() {
            return resolutions;
        }

        public List<int[]> getSupportedFpsRange() {
            return supportedFpsRange;
        }
    }
}
