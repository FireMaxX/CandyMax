package edu.uci.github.candymax;

/**
 * Created by fxuyi on 2017/4/29.
 */

import java.util.Random;

public class Fruit {
    int iconindex;
    int x;
    int y;
    boolean status;

    public Fruit(int i, int j) {
        Random ran = new Random();

        this.x = i;
        this.y = j;
        this.status = true;
        this.iconindex = ran.nextInt(5);
        System.out.println(iconindex);
    }
    //Remove candy and reset its status
    public void reset(int origin_iconindex){
        Random ran = new Random();

        this.status=false;
        this.y=0;
        this.iconindex = ran.nextInt(5);
        while (this.iconindex==origin_iconindex){   //Pseudo Random, to avoid new fruit to be the same as vanished one
            this.iconindex = ran.nextInt(5);        //Otherwise, it will cause false illusion that candy was not killed
        }
    }

}