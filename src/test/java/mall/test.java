package mall;

import com.zcf.game_bean.RoomBean;
import com.zcf.util.CardType;

/**
 * Created with IDEA
 * author:ZhaoQ
 * className:
 * Date:2020/4/2
 * Time:15:47
 */
public class test {
    public static void main(String[] args){
        int[] cards = {12,25,11,2,6};
        int i = CardType.backPatterns(cards, new RoomBean());
        System.out.println(i);
    }
}
