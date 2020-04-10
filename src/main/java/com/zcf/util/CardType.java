package com.zcf.util;

import com.zcf.game_bean.RoomBean;
import com.zcf.game_bean.UserBean;

import java.util.Arrays;

/**
 * writer: ZQ Time: 2018-11-28 Intent: 牌型计算工具类
 */
public class CardType {

    // public static void main(String[] args) {
    // RoomsParam room = new RoomsParam();
    // String[] str = new String[]{"3"};
    // room.setSpecialcard(str);
    // Map<String, int> map = new HashMap<String, int>();
    // map.put("double", 2);
    // room.setRoomType(map);
    // List<int> zj = new ArrayList<int>();
    // zj.add(58);
    // zj.add(44);
    // zj.add(14);
    // zj.add(65);
    // zj.add(54);
    // // int [] nnn = new int[]{
    // // 1,2,3,4
    // // };
    // // System.out.println(nnn.toString());
    // System.out.println(getCardType(zj,room));
    // // String[]str = new String[]{"1","2"};
    // System.out.println(getOdds(zj, room));
    //// System.out.println(PkCard(zj, xj, room));
    // }
    static int brandtype = 0;
    private static String[] brandcount = new String[]{"0-1-2", "0-1-3", "0-1-4", "0-2-3", "0-2-4", "0-3-4", "1-2-3",
            "1-3-4", "1-2-4", "2-3-4"};
    private static String[] type = new String[]{"0-1", "0-2", "0-3", "1-2", "1-3", "2-3"};

    /*
     * @Author:ZhaoQi
     *
     * @methodName:比较闲家是否胜利
     *
     * @Params:
     *
     * @Description:
     *
     * @Return:
     *
     * @Date:2019/3/14
     */
    public static Boolean PkCard(int[] zj, int[] xj, RoomBean myRoom) {
        // 获取牌型进行比拼大小 牌型相同在比点数
        // 倍率：至尊5倍，豹子4倍，二八杠3倍，8点到9点半2倍，0点到7点半1倍。
        int zhuang = 0;
        int xian = 0;
        int xjk1 = GodenFlower_D(xj);// 没牛返回最大值
        int zjk2 = GodenFlower_D(zj);
        int zjCardType = getCardType(zj, myRoom);// 庄家牌型
        int xjCardType = getCardType(xj, myRoom);// 闲家牌型

        System.out.println(zjCardType);
        System.out.println(xjCardType);
        if (xjCardType > zjCardType) {
            return true;
        } else if (xjCardType < zjCardType) {
            return false;
        } else if (xjCardType == zjCardType) {// 牌型相同判断牌值
            if (xjCardType > 900) {// 同花顺
                zhuang = lang(zj);// 判断花色
                xian = lang(xj);
                if (xian > zhuang) {
                    return true;
                } else {
                    return false;
                }
            }
            if ((xjCardType > 800 && xjCardType < 900) || (xjCardType > 600 && xjCardType < 700)
                    || (xjCardType > 500 && xjCardType < 600) || xjCardType > 300 && xjCardType < 400) {// 五小牛

                if (xjk1 > zjk2) {// 根据最大值比较
                    return true;
                } else {
                    return false;
                }

            }
            if (xjCardType > 400 && xjCardType < 500) {// 同花牛

                zhuang = lang(zj);
                xian = lang(xj);
                if (xian > zhuang) {
                    return true;
                } else {
                    return false;
                }

            }
            if (xjCardType > 100 && xjCardType < 150) {

                if (xjk1 > zjk2) {
                    return true;
                } else {
                    return false;
                }
            }

        }
        return null;
    }

    /*
     * @Author:ZhaoQi
     *
     * @methodName:获取牌型
     *
     * @Params:
     *
     * @Description:
     *
     * @Return:
     *
     * @Date:2019/3/14
     */
    public static int getCardType(int[] brand, RoomBean myRoom) {
        Leaflet(brand);
        if (GodenFlower_L(brand) != -1) {// 同花顺
            return GodenFlower_L(brand);
        }
        if (GodenFlower_A(brand) != -1) {// 炸弹牛
            return GodenFlower_A(brand);
        }
        if (GodenFlower_F(brand) != -1) {// 五小牛
            return GodenFlower_F(brand);
        }
        if (GodenFlower_I(brand) != -1) {// 同花牛
            return GodenFlower_I(brand);
        }
        if (GodenFlower_E(brand) != -1) {// 葫芦牛
            return GodenFlower_E(brand);
        }
        if (GodenFlower_G(brand) != -1) {// 顺子牛
            return GodenFlower_G(brand);
        }
        if (GodenFlower_B(brand) != -1) {// 五花牛
            return GodenFlower_B(brand);
        }
        if (GodenFlower_H(brand) != -1) {// 四花牛
            return GodenFlower_H(brand);
        }
        if (GodenFlower_C(brand) != -1) {// 牛1~9
            return GodenFlower_C(brand);
        }
        if (GodenFlower_D(brand) != -1) {// 没牛
            return GodenFlower_D(brand);
        }
        return -1;

    }

    private static int GodenFlower_H(int[] brand) {
        int count = fourniu(brand);
        switch (count) {
            case 54:
                return 292;// 大小王五花牛
            case 53:
                return 291;// 王五花牛
            case 52:
                return 290;// 黑桃K五花牛
            case 51:
                return 285;// 红桃K五花牛
            case 50:
                return 280;// 梅花K五花牛
            case 49:
                return 275;// 方块K五花牛
            case 48:
                return 270;// 黑桃Q五花牛
            case 47:
                return 265;// 红桃Q五花牛
            case 46:
                return 260;// 梅花Q五花牛
            case 45:
                return 255;// 方块Q五花牛
            case 44:
                return 250;// 黑桃J五花牛
            case 43:
                return 245;// 红桃J五花牛
            case 42:
                return 240;// 梅花J五花牛
            case 41:
                return 235;// 方块J五花牛
        }
        // 不成五花牛
        return -1;
    }

    private static int fourniu(int[] brand) {
        int count = 0;
        int brandnum = 0;

        for (int i = 0; i < brand.length; i++) {
            if (i + 1 < brand.length) {
                if (brand[i + 1] % 13 + 1 >= 11) {
                    count++;
                }
                if (count == 4) {
                    if (brand[0] % 13 + 1 == 10) {
                        int d = brand[brand.length - 1] % 13 + 1 + brand[brand.length - 1] / 13
                                + (brand[brand.length - 1] % 13) * 3 > brand[brand.length - 2] % 13 + 1
                                + brand[brand.length - 2] / 13 + (brand[brand.length - 2] % 13) * 3
                                ? brand[brand.length - 1] % 13 + 1 + brand[brand.length - 1] / 13
                                + (brand[brand.length - 1] % 13) * 3
                                : brand[brand.length - 2] % 13 + 1 + brand[brand.length - 2] / 13
                                + (brand[brand.length - 2] % 13) * 3;
                        int c = d > brand[brand.length - 3] % 13 + 1 + brand[brand.length - 3] / 13
                                + (brand[brand.length - 3] % 13) * 3 ? d
                                : brand[brand.length - 3] % 13 + 1 + brand[brand.length - 3] / 13
                                + (brand[brand.length - 3] % 13) * 3;
                        brandnum = c;
                    }
                }
            }
        }

        return brandnum;
    }

    /**
     * 是否为同花顺子牛
     *
     * @param brand
     * @param key
     * @return
     */
    public static int GodenFlower_L(int[] brand) {
        int count = straightf(brand);
        if (count != 0) {
            switch (count) {
                case 5:
                    return 950;// 同花顺5
                case 6:
                    return 955;// 同花顺6
                case 7:
                    return 960;// 同花顺7
                case 8:
                    return 965;// 同花顺8
                case 9:
                    return 970;// 同花顺9
                case 10:
                    return 975;// 同花顺10
                case 11:
                    return 980;// 同花顺J
                case 12:
                    return 985;// 同花顺Q
                case 13:
                    return 990;// 同花顺K
            }
        }
        return -1;
    }

    /**
     * 是否为同花顺子牛
     *
     * @param brand
     * @param key
     * @return
     */
    public static int straightf(int[] brand) {
        int count = 0;
        int max = 0;

        for (int i = 0; i < brand.length; i++) {
            if (i + 1 < brand.length) {
                if (brand[i] % 13 + 1 + 1 == brand[i + 1] % 13 + 1) {
                    if (brand[i] / 13 == brand[i + 1] / 13) {
                        count++;
                    }
                }
                if (count == 4) {
                    max = brand[brand.length - 1] % 13 + 1;
                }
            }
        }

        return max;
    }

    /**
     * 同花牛
     *
     * @param brand
     * @param key
     */
    private static int GodenFlower_I(int[] brand) {
        int count = flowers(brand);
        if (count != 0) {
            switch (count) {
                case 6:
                    return 555;// 同花牛6
                case 7:
                    return 560;// 同花7
                case 8:
                    return 565;// 同花8
                case 9:
                    return 570;// 同花9
                case 10:
                    return 575;// 同花10
                case 11:
                    return 580;// 同花J
                case 12:
                    return 585;// 同花Q
                case 13:
                    return 590;// 同花K
                case 14:
                    return 591;// 王同花牛
                case 15:
                    return 592;// 大小王同花牛
            }
        }
        return -1;
    }

    /**
     * 是否为同花牛
     *
     * @param brand
     * @param key
     * @return
     */
    public static int flowers(int[] brand) {
        int count = 0;
        int max = 0;
        for (int i = 0; i < brand.length; i++) {
            if (i + 1 < brand.length) {
                if (brand[i] / 13 == brand[i + 1] / 13) {
                    count++;
                }
                if (count == 4) {
                    max = brand[brand.length - 1] % 13 + 1;
                }
            }
        }

        return max;
    }

    /**
     * 顺子牛
     *
     * @param brand
     * @param key
     */
    private static int GodenFlower_G(int[] brand) {
        int count = straight(brand);
        if (count != 0) {
            switch (count) {
                case 5:
                    return 350;// 顺子5牛
                case 6:
                    return 355;// 顺子6牛
                case 7:
                    return 360;// 顺子7牛
                case 8:
                    return 365;// 顺子8牛
                case 9:
                    return 370;// 顺子9牛
                case 10:
                    return 375;// 顺子10牛
                case 11:
                    return 380;// 顺子J牛
                case 12:
                    return 385;// 顺子Q牛
                case 13:
                    return 390;// 顺子K牛
                case 14:
                    return 395;
            }
        }
        return -1;
    }

    /**
     * 是否为顺子牛
     *
     * @param brand
     * @param key
     * @return
     */
    public static int straight(int[] brand) {
        int count = 0;
        int max = 0;

        for (int i = 0; i < brand.length; i++) {
            if (i + 1 < brand.length) {
                if (brand[i] % 13 + 1 + 1 == brand[i + 1] % 13 + 1) {
                    count++;
                }
                if (count == 4) {
                    max = brand[brand.length - 1] % 13 + 1;
                }
            }
        }

        return max;
    }

    /**
     * 五小牛
     *
     * @param brand
     * @param key
     */
    private static int GodenFlower_F(int[] brand) {
        int brandMax = five(brand);
        if (brandMax != 0) {
            switch (brandMax) {
                case 9:
                    return 855;// 方块3
                case 10:
                    return 860;// 梅花3
                case 11:
                    return 865;// 红桃3
                case 12:
                    return 870;// 黑桃3
                case 13:
                    return 875;// 方块4
                case 14:
                    return 880;// 梅花4
                case 15:
                    return 885;// 红桃4
                case 16:
                    return 890;// 黑桃4
            }
        }
        return -1;
    }

    /**
     * 是否为5小牛
     *
     * @param brand
     * @param key
     * @return
     */

    public static int five(int[] brand) {
        int count = 0;
        int brandMax = 0;

        for (int i = 0; i < brand.length; i++) {
            if (brand[i] % 13 + 1 < 5) {
                count++;
            }
        }
        if (count == 5) {
            int counts = (brand[0] % 13 + 1) + (brand[1] % 13 + 1) + (brand[2] % 13 + 1) + (brand[3] % 13 + 1)
                    + (brand[brand.length - 1] % 13 + 1);
            if (counts <= 10) {
                brandMax = brand[brand.length - 1] % 13 + 1 + brand[brand.length - 1] / 13
                        + (brand[brand.length - 1] % 13) * 3;
            }
        }

        return brandMax;
    }

    /**
     * 葫芦牛
     *
     * @param brand
     * @param key
     * @return
     */
    private static int GodenFlower_E(int[] brand) {
        int count = gourd(brand);
        if (count != 0) {
            switch (count) {
                case 1:// AAA
                    return 601;
                case 2:// 222
                    return 605;
                case 3:// 333
                    return 610;
                case 4:// 444
                    return 615;
                case 5:// 555
                    return 620;
                case 6:// 666
                    return 625;
                case 7:// 777
                    return 630;
                case 8:// 888
                    return 635;
                case 9:// 999
                    return 640;
                case 10:// 101010
                    return 645;
                case 11:// jjj
                    return 650;
                case 12:// qqq
                    return 655;
                case 13:// kkk
                    return 660;
            }
        }
        return -1;
    }

    /**
     * 判断是否为葫芦牛
     *
     * @param brand
     * @param key
     * @return
     */
    public static int gourd(int[] brand) {
        int count = 0;
        int gourd = 0;

        for (int i = 0; i < brand.length; i++) {
            if (i + 1 < brand.length) {
                if (brand[i] % 13 + 1 == brand[i + 1] % 13 + 1) {
                    count++;
                    if (count == 3) {
                        gourd = brand[i - 1] % 13 + 1;
                    }
                }
            }
        }

        return gourd;
    }

    /**
     * 检测炸弹牛牌型
     *
     * @param brand
     * @param key
     * @param brand
     * @return 相同牌的数值
     */
    public static int GodenFlower_A(int[] brand) {
        int identical = GofenFlower(brand);// 相同牌值
        // 炸弹(4张牌值相同)
        if (identical != 0) {
            // 查询出一张除去炸弹以外的牌的真实牌值
            // for (int i = 0; i < brand.length; i++) {
            // if (brand[i]%13+1 != brandtype && brand[i]%13+1 > 9) {
            switch (identical) {
                case 1:// AAAA
                    return 730;
                case 2:// 2222
                    return 735;
                case 3:// 3333
                    return 740;
                case 4:// 4444
                    return 745;
                case 5:// 5555
                    return 750;
                case 6:// 6666
                    return 755;
                case 7:// 7777
                    return 760;
                case 8:// 8888
                    return 765;
                case 9:// 9999
                    return 770;
                case 10:// 10101010
                    return 775;
                case 11:// JJJJ
                    return 780;
                case 12:// QQQQ
                    return 785;
                case 13:// KKKK
                    return 790;
            }
            // }

            // }
        }
        // 不成炸弹
        return -1;
    }

    /**
     * 检测牌型是否相同
     *
     * @param brand
     * @param key
     * @return
     */
    private static int GofenFlower(int[] brand) {
        int identical = 0;
        int GofenFlower = 0;// 相同牌值

        // 检测炸弹
        for (int i = 0; i < brand.length; i++) {
            if ((i + 1) < brand.length) {
                // 检测相同牌值
                if (brand[i] % 13 + 1 == brand[i + 1] % 13 + 1 && brand[0] % 13 + 1 == brand[brand.length - 1] % 13 + 1
                        || brand[1] % 13 + 1 == brand[brand.length - 1] % 13 + 1) {
                    // 同牌型得数量
                    identical++;
                    if (identical == 3) {
                        // 同牌型得牌值
                        GofenFlower = brand[i] % 13 + 1;
                    }
                }
            }
        }

        return GofenFlower;
    }

    /***
     * 检测牛一到牛九
     *
     * @param key
     *
     * @return
     */
    public static int GodenFlower_C(int[] brand) {
        int brands = ISBrandIndex(brand);
        switch (brands) {
            case 0:
                return 120;// 牛牛
            case 1:
                return 111;// 牛一
            case 2:
                return 112;// 牛二
            case 3:
                return 113;// 牛三
            case 4:
                return 114;// 牛四
            case 5:
                return 115;// 牛五
            case 6:
                return 116;// 牛六
            case 7:
                return 117;// 牛七
            case 8:
                return 118;// 牛八
            case 9:
                return 119;// 牛九
        }
        return -1; // 没牛
    }

    /**
     * @param
     * @return @throws
     */
    public static int ISBrandIndex(int[] brand) {
        int brandnum = -1;
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < brand.length; i++) {
            for (int j = 0; j < brandcount.length; j++) {
                String[] arr = brandcount[j].split("-");
                int a = brand[Integer.valueOf(arr[0])];
                int b = brand[Integer.valueOf(arr[1])];
                int c = brand[Integer.valueOf(arr[2])];
                int d = brand[Integer.valueOf(arr[0])] % 13 + 1;
                int e = brand[Integer.valueOf(arr[1])] % 13 + 1;
                int f = brand[Integer.valueOf(arr[2])] % 13 + 1;
                if (d > 9) {
                    d = 10;
                }
                if (e > 9) {
                    e = 10;
                }
                if (f > 9) {
                    f = 10;
                }
                if ((d + e + f) % 10 == 0) {
                    for (int k = 0; k < brand.length; k++) {
                        for (int k2 = k + 1; k2 < brand.length; k2++) {
                            if (brand[k] != a && brand[k] != b && brand[k] != c && brand[k2] != a && brand[k2] != b
                                    && brand[k2] != c) {
                                if (!sb.toString().equals(""))
                                    sb.append("-");
                                sb.append(brand[k]);
                                sb.append(",");
                                sb.append(brand[k2]);
                                int x = brand[k] % 13 + 1;
                                int z = brand[k2] % 13 + 1;
                                if (x > 9) {
                                    x = 10;
                                }
                                if (z > 9) {
                                    z = 10;
                                }
                                //String[] brand_index = sb.toString().split(",");

                                brandnum = (x + z) % 10;
                                return brandnum;
                            }
                        }
                    }
                }
            }
        }

        return brandnum;
    }

    /**
     * 取牛牛左三右二分牌
     *
     * @param brand
     * @param userBean
     * @return
     * @throws
     */
    public static int BrandIndex(int[] brand, UserBean userBean) {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < brand.length; i++) {
            for (int j = 0; j < brandcount.length; j++) {
                String[] arr = brandcount[j].split("-");
                int a = brand[Integer.valueOf(arr[0])];
                int b = brand[Integer.valueOf(arr[1])];
                int c = brand[Integer.valueOf(arr[2])];
                int d = brand[Integer.valueOf(arr[0])] % 13 + 1;
                int e = brand[Integer.valueOf(arr[1])] % 13 + 1;
                int f = brand[Integer.valueOf(arr[2])] % 13 + 1;
                if (d > 9) {
                    d = 10;
                }
                if (e > 9) {
                    e = 10;
                }
                if (f > 9) {
                    f = 10;
                }
                if ((d + e + f) % 10 == 0) {
                    for (int k = 0; k < brand.length; k++) {
                        for (int k2 = k + 1; k2 < brand.length; k2++) {
                            if (brand[k] != a && brand[k] != b && brand[k] != c && brand[k2] != a && brand[k2] != b
                                    && brand[k2] != c) {
                                if (!sb.toString().equals(""))
                                    sb.append("-");
                                sb.append(brand[k]);
                                sb.append(",");
                                sb.append(brand[k2]);
                                String brand_index = sb.toString();
                                userBean.setBrand_index(brand_index);
                                return 0;
                            }
                        }
                    }
                }
            }
        }
        return -1;
    }

    /****
     *
     * 检测五花牛
     *
     * @param brand
     * @param key
     *
     * @return -1不是五花牛
     */
    public static int GodenFlower_B(int[] brand) {
        // 检测花牌
        int brand1 = 0;
        int brandnum = 0;
        for (int i = 0; i < brand.length; i++) {
            if (brand[i] % 13 + 1 >= 11) {
                brand1++;
            }
        }
        if (brand1 == 5) {
            int d = brand[brand.length - 1] % 13 + 1 + brand[brand.length - 1] / 13
                    + (brand[brand.length - 1] % 13) * 3 > brand[brand.length - 2] % 13 + 1
                    + brand[brand.length - 2] / 13 + (brand[brand.length - 2] % 13) * 3
                    ? brand[brand.length - 1] % 13 + 1 + brand[brand.length - 1] / 13
                    + (brand[brand.length - 1] % 13) * 3
                    : brand[brand.length - 2] % 13 + 1 + brand[brand.length - 2] / 13
                    + (brand[brand.length - 2] % 13) * 3;
            int c = d > brand[brand.length - 3] % 13 + 1 + brand[brand.length - 3] / 13
                    + (brand[brand.length - 3] % 13) * 3 ? d
                    : brand[brand.length - 3] % 13 + 1 + brand[brand.length - 3] / 13
                    + (brand[brand.length - 3] % 13) * 3;
            brandnum = c;
        }

        switch (brandnum) {
            case 54:
                return 492;// 大小王五花牛
            case 53:
                return 491;// 王五花牛
            case 52:
                return 490;// 黑桃K五花牛
            case 51:
                return 485;// 红桃K五花牛
            case 50:
                return 480;// 梅花K五花牛
            case 49:
                return 475;// 方块K五花牛
            case 48:
                return 470;// 黑桃Q五花牛
            case 47:
                return 465;// 红桃Q五花牛
            case 46:
                return 460;// 梅花Q五花牛
            case 45:
                return 455;// 方块Q五花牛
            case 44:
                return 450;// 黑桃J五花牛
            case 43:
                return 445;// 红桃J五花牛
            case 42:
                return 440;// 梅花J五花牛
            case 41:
                return 435;// 方块J五花牛
        }
        // 不成五花牛
        return -1;
    }

    /**
     * 冒泡根据牌值从小到大
     *
     * @param brand
     * @return
     */
    private static int[] Leaflet(int[] brand) {
        for (int i = 0; i < brand.length - 1; i++) {
            for (int j = 0; j < brand.length - 1 - i; j++) {
                if (brand[j] % 13 + 1 > brand[j + 1] % 13 + 1) {
                    int tmp = brand[j + 1];
                    brand[j + 1] = brand[j];
                    brand[j] = tmp;
                }
            }
        }
        return brand;
    }

    public static int reodds(int odds) {
        switch (odds) {
            case 1003:
                odds = 3;
                break;
            case 1004:
                odds = 4;
                break;
            case 1010:
                odds = 10;
                break;
            case 2004:
                odds = 4;
                break;
            case 2005:
                odds = 5;
                break;
            case 2006:
                odds = 6;
                break;
            case 2007:
                odds = 7;
                break;
            case 2008:
                odds = 8;
                break;
            case 2009:
                odds = 9;
                break;
            case 2010:
                odds = 10;
                break;
        }

        return odds;
    }

    /*
     * @Author:ZhaoQi
     *
     * @methodName:获取赔率
     *
     * @Params:
     *
     * @Description:
     *
     * @Return:
     *
     * @Date:2019/3/14
     */
    public static int getOdds(int[] brand, RoomBean myRoom) {
        /**
         * 翻倍规则：0.牛牛*3 、牛九*2、牛八*2 1.牛牛*4 、牛九*3、牛八*2、牛七*2 2.【疯狂加倍】牛牛*10~牛一*1
         * 、无牛*1
         */
        int integer = 1;
        int odd = 1;
        int odds = getCardType(brand, myRoom);
        switch (integer) {
            case 0:
                if (odds == 120) {
                    return 3;
                }
                if (odds == 119) {
                    return 2;
                }
                if (odds == 118) {
                    return 1;
                }
                break;
            case 1:
                if (odds == 120) {
                    return 4;
                }
                if (odds == 119) {
                    return 3;
                }
                if (odds == 118) {
                    return 2;
                }
                if (odds == 117) {
                    return 2;
                }
                break;
        }

        /**
         * 特殊牌型:顺子牛（5倍）、同花牛（6倍）、五花牛（5倍）、葫芦牛（7倍）、炸弹牛（8倍）、五小牛（9倍）、同花顺（10倍）
         */
        if (500 > odds && odds > 400) {
            return 6;
        }
        if (600 > odds && odds > 500) {
            return 6;
        }
        if (300 > odds && odds > 200) {
            return 4;
        }
        if (400 > odds && odds > 300)
            return 5;
        if (700 > odds && odds > 600)
            return 7;
        if (900 > odds && odds > 800)
            return 8;
        if (800 > odds && odds > 700)
            return 8;
        if (odds > 900)
            return 10;
        return odd;
    }

    /**
     * 获取牌型
     *
     * @param brand
     * @return
     */
    public static int backPatterns(int[] brand, RoomBean myRoom) {
        int num = getCardType(brand, myRoom);
        if (num > 900) {
            return 950;
        }
        if (900 > num && num > 800) {
            return 850;
        }
        if (800 > num && num > 700) {
            return 750;
        }
        if (700 > num && num > 600) {
            return 650;
        }
        if (600 > num && num > 500) {
            return 550;
        }
        if (500 > num && num > 400) {
            return 450;
        }
        if (400 > num && num > 300) {
            return 350;
        }
        if (300 > num && num > 200) {
            return 250;
        }
        if (num < 100) {
            return 50;
        }
        return num;
    }

    /**
     * 没牛返回最大牌值
     *
     * @param number
     * @return @throws
     */
    public static int GodenFlower_D(int[] brand) {

        Leaflet(brand);
        for (int i = brand.length - 1; i < brand.length; i--) {
            if (brand[i] <= 51) {
                // 获取牌组里最大牌得下标
                int brandMax = brand[i] % 13 + 1 + brand[i] / 13 + (brand[i] % 13) * 3;
                return brandMax;
            }
        }

        return 0;
    }

    /**
     * 花色
     *
     * @param brand
     * @return
     */
    public static int lang(int[] brand) {
        int n = -1;
        if (brand[0] != 65 && brand[1] != 66) {
            n = brand[0] / 13;
        }
        if (n == 4) {
            n = -1;
        }
        return n;
    }

}
