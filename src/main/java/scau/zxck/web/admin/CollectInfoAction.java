package scau.zxck.web.admin;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import scau.zxck.base.dao.mybatis.Conditions;
import scau.zxck.base.exception.BaseException;
import scau.zxck.entity.market.GoodsInfo;
import scau.zxck.entity.market.UserCollection;
import scau.zxck.entity.sys.UserInfo;
import scau.zxck.service.market.IUserCollectService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Controller
@RequestMapping("/")
public class CollectInfoAction {
    @Autowired
    private IUserCollectService userCollectService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpSession session;

    @RequestMapping(value = "addCollect", method = RequestMethod.POST)
    public void addCollection( HttpServletResponse response) throws Exception {
      String r="";
      BufferedReader br = request.getReader();
      String str, wholeStr = "";
      while((str = br.readLine()) != null){
        wholeStr += str;
      }
      String jsonStr=wholeStr;

        JSONObject data = JSONObject.parseObject(jsonStr);
//        HttpServletRequest request =
//                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//        HttpSession session = request.getSession();
        if (session.getAttribute("User_PK") != null) {
            data.put("User_PK", session.getAttribute("User_PK"));
        } else {
            data.put("User_PK", "");
        }
        data.put("Collect_Time", (new SimpleDateFormat("yyyy-MM-dd HH:MM:ss")).format(new Date()));
        UserCollection userCollection = new UserCollection();
        userCollection.setUser_info_id(data.get("User_PK").toString());
        userCollection.setGoods_info_id(data.get("Goods_PK").toString());
        userCollection.setCollect_time(Timestamp.valueOf(data.get("Collect_Time").toString()).toString());
        userCollection.setUserinfo(new UserInfo());
        userCollection.setGoodsinfo(new GoodsInfo());
        try {
            userCollectService.add(userCollection);
            r = "{\"status\":1}";
        } catch (Exception e) {
            e.printStackTrace();
            r = "{\"status\":0}";
        }
        PrintWriter out = response.getWriter();
        out.flush();
        out.write(r);
        out.flush();
    }


    @RequestMapping(value = "removeCollect", method = RequestMethod.POST)
    public void removeCollect( HttpServletResponse response) throws Exception {
      String r="";
      BufferedReader br = request.getReader();
      String str, wholeStr = "";
      while((str = br.readLine()) != null){
        wholeStr += str;
      }
      String jsonStr=wholeStr;
        JSONObject data = JSONObject.parseObject(jsonStr);
//        HttpServletRequest request =
//                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//        HttpSession session = request.getSession();
        if (session.getAttribute("User_PK") != null) {
            data.put("User_PK", session.getAttribute("User_PK"));
        } else {
            data.put("User_PK", "");
        }
        try {
            userCollectService.deleteByIds(data.get("Collect_PK").toString());
            r = "{\"status\":1}";
        } catch (BaseException e) {
            e.printStackTrace();
            r = "{\"status\":0}";
        }
        PrintWriter out = response.getWriter();
        out.flush();
        out.write(r);
        out.flush();
    }

    @RequestMapping(value = "getCollect", method = RequestMethod.POST)
    public void getCollect( HttpServletResponse response) throws Exception {
      String r="";
      BufferedReader br = request.getReader();
      String str, wholeStr = "";
      while((str = br.readLine()) != null){
        wholeStr += str;
      }
      String jsonStr=wholeStr;
      JSONObject data = JSONObject.parseObject(jsonStr);

        if (session.getAttribute("User_PK") != null) {
            data.put("User_PK", session.getAttribute("User_PK"));
        } else {
            data.put("User_PK", "");
        }
        JSONArray jsonarr = new JSONArray();
        Conditions conditions = new Conditions();
        List list =
                userCollectService.list(conditions.eq("user_info_id", data.get("User_PK").toString()));
        // DataSearch.collectByID((int)Integer.parseInt(json.get("User_PK").toString()));

        for (Iterator iter = ((java.util.List) list).iterator(); iter.hasNext(); ) {
            JSONObject temp = new JSONObject();
            UserCollection collect = (UserCollection) iter.next();
            temp.put("Collect_PK", collect.getId());
            temp.put("User_PK", collect.getUser_info_id());
            temp.put("Goods_PK", collect.getGoods_info_id());
            temp.put("Collect_Time", collect.getCollect_time().toString());
            jsonarr.add(temp);
        }
       r = jsonarr.toString();
        PrintWriter out = response.getWriter();
        out.flush();
        out.write(r);
        out.flush();
    }
}
