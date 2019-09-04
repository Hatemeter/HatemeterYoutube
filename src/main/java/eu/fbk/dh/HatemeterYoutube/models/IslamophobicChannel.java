package eu.fbk.dh.HatemeterYoutube.models;

public class IslamophobicChannel {
    private String channelId;
    private String channelName;
    private String channelUrl;
    private String islamophobicVideos;
    private int nbOfIslamophobicVideos;

    public IslamophobicChannel(String channelId, String channelName, String channelUrl, String islamophobicVideos, int nbOfIslamophobicVideos) {
        this.channelId = channelId;
        this.channelName = channelName;
        this.channelUrl = channelUrl;
        this.islamophobicVideos = islamophobicVideos;
        this.nbOfIslamophobicVideos = nbOfIslamophobicVideos;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelUrl() {
        return channelUrl;
    }

    public void setChannelUrl(String channelUrl) {
        this.channelUrl = channelUrl;
    }

    public int getNbOfIslamophobicVideos() {
        return nbOfIslamophobicVideos;
    }

    public void setNbOfIslamophobicVideos(int nbOfIslamophobicVideos) {
        this.nbOfIslamophobicVideos = nbOfIslamophobicVideos;
    }

    @Override
    public String toString() {
        return "IslamophobicChannel{" +
                "channelId='" + channelId + '\'' +
                ", channelName='" + channelName + '\'' +
                ", channelUrl='" + channelUrl + '\'' +
                ", islamophobicVideos='" + islamophobicVideos + '\'' +
                ", nbOfIslamophobicVideos=" + nbOfIslamophobicVideos +
                '}';
    }
}
