package hr.foi.mjurinic.bach.network.protocol;

import android.util.Pair;

import java.util.List;

public class ProtoStreamInfo extends ProtoMessage {

    List<Pair<Integer, Integer>> resolutions;

    public ProtoStreamInfo(List<Pair<Integer, Integer>> resolutions) {
        super.id = ProtoMessageType.STREAM_INFO_RESPONSE;
        this.resolutions = resolutions;
    }

    public List<Pair<Integer, Integer>> getResolutions() {
        return resolutions;
    }

    public void setResolutions(List<Pair<Integer, Integer>> resolutions) {
        this.resolutions = resolutions;
    }
}
