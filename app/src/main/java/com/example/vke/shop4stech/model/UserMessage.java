package com.example.vke.shop4stech.model;


/**
 * Created by vke on 2016/5/16.
 */
public class UserMessage {
    private String Content;
    private String Author;
    private String TimeStamp;
    private String Index;

    public UserMessage(){

    }

    public UserMessage(String content, String author, String timeStamp, String index){
        Content = content;
        Author = author;
        TimeStamp = timeStamp;
        Index = index;
    }

    public String getAuthor() {
        return Author;
    }

    public String getContent() {
        return Content;
    }

    public String getIndex() {
        return Index;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setIndex(String index) {
        Index = index;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public void setContent(String content) {
        Content = content;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }
}
