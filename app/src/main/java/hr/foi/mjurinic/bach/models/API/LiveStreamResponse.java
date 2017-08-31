package hr.foi.mjurinic.bach.models.API;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LiveStreamResponse implements Serializable {

    @SerializedName("avatar")
    private String avatar;

    @SerializedName("name")
    private String name;

    @SerializedName("nickname")
    private String nickname;

    public LiveStreamResponse(String avatar, String name, String nickname) {
        this.avatar = avatar;
        this.name = name;
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
