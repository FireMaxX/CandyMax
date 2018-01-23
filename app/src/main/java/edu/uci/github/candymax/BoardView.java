package edu.uci.github.candymax;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.graphics.Rect;
import java.util.List;


/**
 * Created by YifanXu on 2017/4/29.
 */

public class BoardView extends SurfaceView implements SurfaceHolder.Callback {
    private Bitmap icons[];
    private int prevX;
    private int prevY;
    private List<Integer> indices;
    private Fruit candy[];
    final int AmountofColumn=9;
    final int AmountofRow=11;
    private int startRowNum;
    private int startColNum;
    private int candyamount=0;
    private int index[][]=new int[AmountofColumn][AmountofRow];
    private int score=0;


    public BoardView(Context context) {
        super(context);
        getHolder().addCallback(this);
        setFocusable(true);
        System.out.println("In Constructor");

        icons = new Bitmap[6];
        candy = new Fruit[AmountofColumn*AmountofRow];
        prevX = 0;
        prevY = 0;
        startRowNum = 0;
        startColNum = 0;

        // Initialize candies
        for(int i = 0; i < AmountofColumn; ++i) {
            for(int j = 0; j < AmountofRow; ++j) {
                candy[candyamount]=new Fruit(i,j);
                index[i][j]=candyamount;
                candyamount++;
            }
        }
        // Vanish Three-Candy-In-Line before game start
        if (findThree()){
            System.out.println("Find new Elimination after initialization");
        }
        score=0;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        System.out.println("Touch event occured...");

        int currX;
        int currY;
        int endRowNum = 0;
        int endColNum = 0;
        int width = getWidth();
        int height = getHeight();

        int rowHeight = height / (AmountofRow+1);
        int columnWidth = width / AmountofColumn;

        if(event.getAction() == MotionEvent.ACTION_DOWN) {  //Code from TA, Thanks
            prevX = (int) event.getX();
            prevY = (int) event.getY();

            startRowNum = prevY / rowHeight;
            startColNum = prevX / columnWidth;
            //Check bottom border
            if (startRowNum>=AmountofRow){
                startRowNum=AmountofRow-1;
            }
        }

        else if(event.getAction() == MotionEvent.ACTION_UP) {
            currX = (int) event.getX();
            currY = (int) event.getY();

            endRowNum = currY / rowHeight;
            endColNum = currX / columnWidth;
            //Check bottom border
            if (endRowNum>=AmountofRow){
                endRowNum=AmountofRow-1;
            }

            System.out.println("StartColNum: " + startColNum + " StartRowNum : " + startRowNum);
            System.out.println("EndColNum: " + endColNum + " EndRowNum: " + endRowNum);


            if(startRowNum == endRowNum) {
                if(startColNum > endColNum) {
                    System.out.println("Right to Left");
                    swapImages(startRowNum, startColNum, endRowNum, startColNum-1);
                }
                else if(startColNum < endColNum) {
                    System.out.println("Left to Right");
                    swapImages(startRowNum, startColNum, endRowNum, startColNum+1);
                }
                else {
                    System.out.println("Unrecognized action");
                }
            }
            else if(startColNum == endColNum) {
                if(startRowNum < endRowNum) {
                    System.out.println("Top to bottom");
                    swapImages(startRowNum, startColNum, startRowNum+1, endColNum);
                }
                else if(startRowNum > endRowNum) {
                    System.out.println("Bottom to top");
                    swapImages(startRowNum, startColNum, startRowNum-1, endColNum);
                }
                else {
                    System.out.println("Unrecognized action");
                }
            }
            else {
                System.out.println("Invalid move");
            }
            invalidate();
        }
        return true;
    }

    //Swap two adjacent fruits
    void swapImages(int startRow, int startCol, int endRow, int endCol){
        int StartIndex,EndIndex;

        if ((startRow<0)|(startCol<0)|(endRow<0)|(endCol<0)){
            return;
        }

        StartIndex=index[startCol][startRow];
        EndIndex=index[endCol][endRow];

        candy[StartIndex].x=endCol;
        candy[StartIndex].y=endRow;
        candy[EndIndex].x=startCol;
        candy[EndIndex].y=startRow;

        index[startCol][startRow]=EndIndex;
        index[endCol][endRow]=StartIndex;

        invalidate();

        //Swap back if not vaild(no three-in-line appear on canvas after swap)
        if (!(check(StartIndex)|check(EndIndex)|(findThree()))){
            StartIndex=index[startCol][startRow];
            EndIndex=index[endCol][endRow];

            candy[StartIndex].x=endCol;
            candy[StartIndex].y=endRow;
            candy[EndIndex].x=startCol;
            candy[EndIndex].y=startRow;

            index[startCol][startRow]=EndIndex;
            index[endCol][endRow]=StartIndex;
            System.out.println("Not Valid, Swapped back");
            invalidate();
        }
    }

    //Check a single object candy[i] to see if three-in-line exist around
    boolean check(int i){
        int x,y; //Center Point
        int left=0,right=0,up=0,down=0;
        int k=1;
        int counter=0;

        x=candy[i].x;
        y=candy[i].y;
        //up
        k=1;
        up=0;
        while((y-k>=0)){
            if (candy[index[x][y-k]].iconindex==candy[i].iconindex){
                up++;
                k++;
            }
            else break;
        }
        //down
        down=0;
        k=1;
        while((y+k<AmountofRow)){
            if (candy[index[x][y+k]].iconindex==candy[i].iconindex){
                down++;
                k++;
            }
            else break;
        }
        //left
        left=0;
        k=1;
        while((x-k>=0)){
            if (candy[index[x-k][y]].iconindex==candy[i].iconindex){
                left++;
                k++;
            }
            else break;
        }
        //right
        k=1;
        right=0;
        while((x+k<AmountofColumn)){
            if (candy[index[x+k][y]].iconindex==candy[i].iconindex){
                right++;
                k++;
            }
            else break;
        }
        //Eliminate Three-In-Line
        if (((up+down+1)>=3)|((left+right+1)>=3)){
            System.out.println("Around("+candy[i].x+","+candy[i].y+")");
            System.out.println("Find-Up:"+up+" Down:"+down+" Left:"+left+" Right:"+right);
            //Avoid L elimination
            if ((left+right)<2){ //Only Vertical
                left=0;
                right=0;
            }
            else if ((up+down)<2){ //Only Horizontal
                up=0;
                down=0;
            }
            System.out.println("Kill-Up:"+up+" Down:"+down+" Left:"+left+" Right:"+right);
            eliminateFruit(up,down,left,right,i);
            score+=(up+down+left+right+1);    //Total Score
            return true;
        }
        else return false;
    }

    //Find out if three or more fruits are in line on canvas
    boolean findThree(){
        int counter=0;

        for(int i=0;i<AmountofColumn*AmountofRow;i++){
            if (check(i)){
                counter++;
            }
        }
        if (counter>0){
            return true;
        }
        else{
            return false;
        }
    }

    //Drop all candies above (x,y) down 1 unit
    void drop(int x,int y){
        int originIndex=0;
        for (int j=y-1;j>=0;j--){
            originIndex=index[x][j];
            candy[originIndex].y++;
            index[x][j+1]=originIndex;
        }
        invalidate();
    }

    //Eliminate a candy
    void eliminateFruit(int up, int down, int left, int right, int ind){
        int i,k;
        int x=candy[ind].x;
        int y=candy[ind].y;
        int tempIndex;

        //up, only eliminate candy-(x,y-1) but for "up" times
        for(i=0;i<up;i++){
            tempIndex=index[x][y-1];
            candy[tempIndex].reset(candy[ind].iconindex);
            invalidate();
            index[x][y-1]=-1; //Safe Code
            drop(x,y-1);
            index[x][0]=tempIndex;
            candy[tempIndex].status=true;
        }
        //left
        for(i=0;i<left;i++){
            tempIndex=index[x-i-1][y];
            candy[tempIndex].reset(candy[ind].iconindex);
            invalidate();
            index[x-i-1][y]=-1; //Safe Code
            drop(x-i-1,y);
            index[x-i-1][0]=tempIndex;
            candy[tempIndex].status=true;
        }
        //right
        for(i=0;i<right;i++){
            tempIndex=index[x+i+1][y];
            candy[tempIndex].reset(candy[ind].iconindex);
            invalidate();
            index[x+i+1][y]=-1; //Safe Code
            drop(x+i+1,y);
            index[x+i+1][0]=tempIndex;
            candy[tempIndex].status=true;
        }
        //down & self
        for(i=0;i<(down+1);i++) {
            tempIndex = index[x][y + i];
            candy[tempIndex].reset(candy[ind].iconindex);
            invalidate();
            index[x][y+i]=-1; //Safe Code
            drop(x,y+i);
            index[x][0]=tempIndex;
            candy[tempIndex].status=true;
        }
        while(findThree()){
            System.out.println("Find new Elimination");
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        System.out.println("OnDraw called");
        canvas.drawColor(Color.WHITE);
        Rect rect = new Rect();
        int width = getWidth();
        int height = getHeight();

        int rowHeight = height / (AmountofRow+1);
        int columnWidth = width / AmountofColumn;
        String message;
        int location_factor;
        int i,j;
        //Display Candies
        for(int k = 0; k < AmountofColumn*AmountofRow; ++k) {
            if (candy[k].status){   //Only draw alive candies
                i=candy[k].x;
                j=candy[k].y;
                rect.set(i * columnWidth, j * rowHeight, (i + 1) * columnWidth, (j + 1) * rowHeight);
                canvas.drawBitmap(icons[candy[k].iconindex], null, rect, null);
            }
            else{
                i=candy[k].x;
                j=candy[k].y;
                rect.set(i * columnWidth, j * rowHeight, (i + 1) * columnWidth, (j + 1) * rowHeight);
                canvas.drawBitmap(icons[9], null, rect, null);
            }
        }
        //Display background of Score
        rect.set(0, AmountofRow * rowHeight, width, height);
        canvas.drawBitmap(icons[5], null, rect, null);
        //Dispay Score
        Paint p = new Paint(Color.GREEN);
        p.setTextSize(60);  // Set text size
        if (score>150){
            message = "~You Win!";
            p.setColor(Color.RED);
        }
        else {
            message = "Score: "+ score;  // The text content
        }
        p.setTextAlign(Paint.Align.CENTER);
        p.setFakeBoldText(true);
        canvas.drawText(message, width/2,(height/(AmountofRow+1)*(AmountofRow)+height/60),p);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        System.out.println("Surface created");
        setWillNotDraw(false);

        icons[0] = BitmapFactory.decodeResource(getResources(), R.drawable.watermellon1);
        icons[1] = BitmapFactory.decodeResource(getResources(), R.drawable.wiki2);
        icons[2] = BitmapFactory.decodeResource(getResources(), R.drawable.orange3);
        icons[3] = BitmapFactory.decodeResource(getResources(), R.drawable.dragon4);
        icons[4] = BitmapFactory.decodeResource(getResources(), R.drawable.apple5);
        icons[5] = BitmapFactory.decodeResource(getResources(), R.drawable.empty);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }


}