/*
 * 文 件 名:  TranslateTest.java
 * 描    述:  <描述>
 * 修 改 人:   袁立位
 * 修改时间:  2015年11月23日
 */

import java.util.Map;

import org.apache.http.util.TextUtils;
import org.junit.Test;

import com.ylw.gen.common.digest.MD5;
import com.ylw.gen.common.log.Log;
import com.ylw.gen.logic.http.HttpParamers;
import com.ylw.gen.logic.http.HttpUtils;

/**
 * @description 百度翻译API调用测试<br/>
 * 
 *              <pre>
 * 加密规范
 *  
 * 为保证接口调用安全，接口采用IP限制和MD5加密签名验证， 签名的计算方法如下：
 * 1、将请求参数 APPID (appid), 翻译query(q), 随机数(salt), 按照 appid q salt的顺序拼接得到串1。
 * 2、在串1后拼接由平台分配的私钥(secret key) 得到串2。
 * 3、对串2做md5，得到sign。
 * 
 * 例：将hello从英文翻译成中文：
 * 平台申请的appid：2015063000000001，平台分配的私钥(sec_key)：12345678，翻译请求：
 * http://api.fanyi.baidu.com/api/trans/vip/translate?q=hello&appid=2015063000000001&salt=1435577028&from=en&to=zh
 * 
 * sign的计算方法：
 * >拼接串1
 * 拼接平台申请的appid=2015063000000001 和随机数salt=1435660288 翻译原文q=hello
 * 串1=2015063000000001hello1435660288
 * 
 * >拼接串2（平台分配的私钥为12345678）
 * 串2=2015063000000001hello143566028812345678
 * 
 * >计算签名sign（对串2做md5加密）
 * sign=md5(2015063000000001hello143566028812345678)
 * sign=2f7b6bfb034a64a978707bd303d20cce
 * 
 * 完整请求为：
 * 
 * http://api.fanyi.baidu.com/api/trans/vip/translate?q=hello&appid=2015063000000001&salt=1435660288&from=en&to=zh&sign=2f7b6bfb034a64a978707bd303d20cce
 * </pre>
 * @author 袁立位
 * @date 2015年11月23日 上午9:31:41
 */
public class TranslateTest {
    private String appid = "20151123000006145";
    private String privateKey = "";// 私钥
    private String query;// 翻译原文
    private String salt;// 随机数
    private String sign;// 签名

    @Test
    public void testQuery() {
        query = "The team edited down the recordings, leaving only the part where the students picked up the phone and said hello.";
        salt = String.valueOf(Math.random());
        String str1 = appid + query + salt;
        String str2 = str1 + privateKey;
        sign = MD5.md5(str2);

        String url = "http://api.fanyi.baidu.com/api/trans/vip/translate";

        HttpUtils.getString(url, new HttpParamers() {
            @Override
            public void initParams(Map<String, String> params) {
                params.put("q", query);
                params.put("appid", appid);
                params.put("salt", salt);
                params.put("from", "en");
                params.put("to", "zh");
                params.put("sign", sign);
            }

            @Override
            public void onRespone(String content) {
                Log.debug(ascii2native(content));
            }
        });
    }

    public String ascii2native(String sAscii) {
        if (TextUtils.isEmpty(sAscii))
            return "";
        StringBuilder sb = new StringBuilder();
        String[] words = sAscii.split("\\\\u");
        sb.append(words[0]);
        for (int i = 1; i < words.length; i++) {
            String word = words[i];
            sb.append((char) Integer.parseInt(word.substring(0, 4), 16));
            if (word.length() > 4) {
                sb.append(word.substring(4));
            }
        }
        return sb.toString();
    }
}
