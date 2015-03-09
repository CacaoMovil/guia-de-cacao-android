package kronos.comkronoscodecomandroid.activity.constant;

/**
 * Created by miguelb on 7/2/14.
 */
public enum Action {
    ADD(0), EDIT(1), DELETE(2), BATCH_DELETE(3), ;
    private int mId;
    public static String ACTION_KEY = "kronos.comkronoscodecomandroid.constant.ACTION_KEY";
    Action(int id){
        mId = id;
    }

    public int getId(){
        return mId;
    }
}