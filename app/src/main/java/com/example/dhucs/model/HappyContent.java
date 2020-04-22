package com.example.dhucs.model;

import java.io.Serializable;

public class HappyContent implements Serializable
{
    // 唯一的id
    private int id;
    // 活动内容
    private String content;
    private String title;
    private String image;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getImage()
    {
        return image;
    }

    public void setImage(String image)
    {
        this.image = image;
    }

}
