package eu.fbk.dh.HatemeterYoutube.models;

public class IslamophobicChannel {
    private String channelId;
    private String channelName;
    private String channelUrl;
    private String channelLocation;
    private String islamophobicVideos;
    private int nbOfSubscribers;
    private int nbOfIslamophobicVideos;

    public IslamophobicChannel(String channelId, String channelName, String channelUrl, String channelLocation, String islamophobicVideos, int nbOfSubscribers, int nbOfIslamophobicVideos) {
        this.channelId = channelId;
        this.channelName = channelName;
        this.channelUrl = channelUrl;
        this.channelLocation = channelLocation;
        this.islamophobicVideos = islamophobicVideos;
        this.nbOfSubscribers = nbOfSubscribers;
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

    public String getChannelLocation() {
        return channelLocation;
    }

    public void setChannelLocation(String channelLocation) {
        this.channelLocation = channelLocation;
    }

    public String getIslamophobicVideos() {
        return islamophobicVideos;
    }

    public void setIslamophobicVideos(String islamophobicVideos) {
        this.islamophobicVideos = islamophobicVideos;
    }

    public int getNbOfSubscribers() {
        return nbOfSubscribers;
    }

    public void setNbOfSubscribers(int nbOfSubscribers) {
        this.nbOfSubscribers = nbOfSubscribers;
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
                ", channelLocation='" + channelLocation + '\'' +
                ", islamophobicVideos='" + islamophobicVideos + '\'' +
                ", nbOfSubscribers=" + nbOfSubscribers +
                ", nbOfIslamophobicVideos=" + nbOfIslamophobicVideos +
                '}';
    }
}
