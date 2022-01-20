package Bili;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;

import Bili.model.BiliBiliResult;

import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @Author: 栗少
 * @Date: 2021/11/23
 */
public class Createmd {

    public static void main(String[] args) {
        while (true) {
            System.out.println("请输入视频的BV号");
            Scanner scanner = new Scanner(System.in);
            String bvLink = scanner.nextLine();

            System.out.println("请输入放置的文件夹路径");
            Scanner target = new Scanner(System.in);
            String targetSrc = target.nextLine();

            String biliResult = HttpUtil.get("https://api.bilibili.com/x/player/pagelist?bvid=" + bvLink + "&jsonp=jsonp");
            BiliBiliResult biliBiliResult = JSONUtil.toBean(biliResult, BiliBiliResult.class);
            List<BiliBiliResult.DataBean> data = biliBiliResult.getData();

            for (BiliBiliResult.DataBean datum : data) {
                String part = datum.getPart();
                int page = datum.getPage();
                if (Character.isDigit(part.substring(0,1).charAt(0))) {
                    // 如果是数字说明不需要拼接
                    String string = targetSrc +"/"+ part +"("+ datum.getDuration() / 60 +"分钟).md";
                    String newStr = RemoveSpecialStr(string);
                    FileUtil.touch(newStr);
                } else {
                    try {
                        String string = targetSrc +"/"+page+"."+ part +"("+ datum.getDuration() / 60 +"分钟).md";
                        // 字符串处理
                        String newStr = RemoveSpecialStr(string);
                        FileUtil.touch(newStr);
                    } catch (IORuntimeException e) {
                        System.out.println("文件并没有生成,可能路径内有元字符" + part);
                    }
                }
            }
        }
    }

    private static String RemoveSpecialStr(String string) {
        String aa = "";
        String regEx="[\n`~!@#$%^*+=|';',\\[\\]<>?~！@#￥%&*（）——+|‘；：”“’。， 、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(string);
        return m.replaceAll(aa).trim();
    }


}
