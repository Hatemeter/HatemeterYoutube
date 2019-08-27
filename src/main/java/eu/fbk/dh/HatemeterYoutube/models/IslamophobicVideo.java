package eu.fbk.dh.HatemeterYoutube.models;

public class IslamophobicVideo {
    private String videoId;
    private String videoTitle;
    private String uploadDate;
    private String videoUrl;
    private int viewCount;

    public IslamophobicVideo(){}

    public IslamophobicVideo(String videoId,String videoTitle,String uploadDate,String videoUrl,int viewCount){
        this.videoId=videoId;
        this.videoTitle=videoTitle;
        this.uploadDate=uploadDate;
        this.videoUrl=videoUrl;
        this.viewCount=viewCount;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    @Override
    public String toString() {
        return "IslamophobicVideo{" +
                "videoId='" + videoId + '\'' +
                ", videoTitle='" + videoTitle + '\'' +
                ", uploadDate='" + uploadDate + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", viewCount=" + viewCount +
                '}';
    }
}
