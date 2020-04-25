package mall;

import com.zcf.game_bean.RoomBean;
import com.zcf.util.CardType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created with IDEA
 * author:ZhaoQ
 * className:
 * Date:2020/4/2
 * Time:15:47
 */
public class test {
    public static void main(String[] args) {
        int[] cards = {0,21,30,52,53};
        ArrayList<Integer> list = new ArrayList<>();
        RoomBean roomBean = new RoomBean();
        List<Integer> ints = Arrays.stream(cards).boxed().collect(Collectors.toList());
        int index_s;//小王的小标
        int index_S;//大王的下标
        if (ints.contains(52) && ints.contains(53)) {
            index_s = ints.indexOf(52);
            index_S = ints.indexOf(53);
            for (int i = 0; i < 52; i++) {
                int[] card_copy = new int[5];
                System.arraycopy(cards, 0, card_copy, 0, 5);
                card_copy[index_s] = i;
                for (int j = i; j < 52; j++) {
                    System.arraycopy(cards, 0, card_copy, 0, 5);
                    card_copy[index_s] = i;
                    card_copy[index_S] = j;
                    int cardType = CardType.getCardType(card_copy, roomBean);
                    list.add(cardType);
                }
            }
        } else if (ints.contains(52)) {
            index_s = ints.indexOf(52);
            for (int i = 0; i < 52; i++) {
                int[] card_copy = new int[5];
                System.arraycopy(cards, 0, card_copy, 0, 5);
                card_copy[index_s] = i;
                int cardType = CardType.getCardType(card_copy, roomBean);
                list.add(cardType);
            }
        } else if (ints.contains(53)) {
            index_S = ints.indexOf(53);
            for (int j = 0; j < 52; j++) {
                int[] card_copy = new int[5];
                System.arraycopy(cards, 0, card_copy, 0, 5);
                card_copy[index_S] = j;
                int cardType = CardType.getCardType(card_copy, roomBean);
                list.add(cardType);
            }
        } else {
            int cardType = CardType.getCardType(cards, roomBean);
            list.add(cardType);
        }
        Collections.sort(list);
        System.out.println(list.get(list.size() - 1));
    }
}
