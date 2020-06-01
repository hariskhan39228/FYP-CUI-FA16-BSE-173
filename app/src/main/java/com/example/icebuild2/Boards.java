package com.example.icebuild2;

import androidx.annotation.NonNull;

public class Boards {
    public String name, creator,image,boardClass;

    public Boards(){

    }

    public Boards(String name, String creator, String image, String boardClass) {
        this.name = name;
        this.creator = creator;
        this.image = image;
        this.boardClass = boardClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBoardClass() {
        return boardClass;
    }

    public void setBoardClass(String boardClass) {
        this.boardClass = boardClass;
    }
}
