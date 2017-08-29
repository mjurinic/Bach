package hr.foi.mjurinic.bach.network.protocol;

public class ProtoMessageType {

    /**
     * Client check if host is on-line.
     */
    public static final int HELLO_REQUEST = 110;

    /**
     * Host response to clients HELLO_REQUEST.
     */
    public static final int HELLO_RESPONSE = 210;

    /**
     * Request supported stream configuration.
     */
    public static final int STREAM_INFO_REQUEST = 120;

    /**
     * Supported stream configuration.
     */
    public static final int STREAM_INFO_RESPONSE = 220;

    /**
     * Check if stream configuration is supported.
     */
    public static final int STREAM_CONFIG_REQUEST = 121;

    /**
     * Response for correct stream configuration request.
     */
    public static final int STREAM_CONFIG_RESPONSE_OK = 221;

    /**
     * Response for wrong stream configuration request.
     */
    public static final int STREAM_CONFIG_RESPONSE_ERROR = 222;

    /**
     * Client device is ready.
     */
    public static final int CLIENT_READY = 300;

    /**
     * Stream chunk data.
     */
    public static final int MULTIMEDIA = 301;

    /**
     * Notify others the stream is closing.
     */
    public static final int STREAM_CLOSE = 400;
}
