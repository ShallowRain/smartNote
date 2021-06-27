package cn.rainss.smartNote.utils;

import cn.rainss.smartNote.adapter.entity.NewInfo;
import com.xuexiang.xaop.annotation.MemoryCache;
import com.xuexiang.xui.widget.banner.widget.banner.BannerItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 演示数据
 *
 *
 * @since 2018/11/23 下午5:52
 */
public class DemoDataProvider {

    public static String[] titles = new String[]{
            "AndroidSystem",
            "Android",
    };

    public static String[] urls = new String[]{//640*360 360/640=0.5625
            "https://bkimg.cdn.bcebos.com/pic/95eef01f3a292df58921b9d3b2315c6035a87311",
            "https://bkimg.cdn.bcebos.com/pic/78310a55b319ebc433cd2ac18d26cffc1e171634"
    };

    @MemoryCache
    public static List<BannerItem> getBannerList() {
        List<BannerItem> list = new ArrayList<>();
        for (int i = 0; i < urls.length; i++) {
            BannerItem item = new BannerItem();
            item.setImgUrl(urls[i]);
            item.setTitle(titles[i]);

            list.add(item);
        }
        return list;
    }

    /**
     * 用于占位的空信息
     *
     * @return
     */
    @MemoryCache
    public static List<NewInfo> getDemoNewInfos() {
        List<NewInfo> list = new ArrayList<>();
        list.add(new NewInfo("Android 系统","Android一词的本义指“机器人”，同时也是Google于2007年11月5日Android  logo相关图片Android logo相关图片(33张)宣布的基于Linux平台的开源手机操作系统的名称，该平台由操作系统、中间件、用户界面和应用软件组成。")
                .setSummary("安卓是一种基于Linux内核（不包含GNU组件）的自由及开放源代码的操作系统。主要使用于移动设备，如智能手机和平板电脑，由美国Google公司和开放手机联盟领导及开发。Android操...\n")
                .setDetailUrl("https://baidu.com")
                .setImageUrl("https://rainss.cn/usr/themes/Volcano/thumb/4.png"));

        list.add(new NewInfo("消息", "谷歌被迫“挥刀”华为：迸裂的安卓生态")
                .setSummary("曾经，有实力的厂商都曾推出过自己的手机操作系统，比如三星的自有系统Tizen，微软的Windows Phone，以及塞班、黑莓等等。最终，因为安卓开放的生态以及对谷歌的信任，如今除苹果外几乎所有的手机制造商都在全力支持安卓。")
                .setDetailUrl("https://imgsrc.baidu.com/")
                .setImageUrl("https://rainss.cn/usr/themes/Volcano/thumb/1.png"));

        list.add(new NewInfo("Android/HTTP", "XHttp2 一个功能强悍的网络请求库，使用RxJava2 + Retrofit2 + OKHttp进行组装")
                .setSummary("一个功能强悍的网络请求库，使用RxJava2 + Retrofit2 + OKHttp组合进行封装。还不赶紧点击使用说明文档，体验一下吧！")
                .setDetailUrl("https://juejin.im/post/5b6b9b49e51d4576b828978d")
                .setImageUrl("https://user-gold-cdn.xitu.io/2018/8/9/1651c568a7e30e02?imageView2/0/w/1280/h/960/format/webp/ignore-error/1"));
        return list;
    }


}
