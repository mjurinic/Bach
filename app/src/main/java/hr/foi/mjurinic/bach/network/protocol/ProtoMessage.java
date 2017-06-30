package hr.foi.mjurinic.bach.network.protocol;

import java.io.Serializable;

public class ProtoMessage implements Serializable {

    protected int id;

    protected ProtoMessage() {}

    /**
     * Create a new ProtoMessage instance.
     *
     * @param id ProtoMessageType
     */
    public ProtoMessage(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
