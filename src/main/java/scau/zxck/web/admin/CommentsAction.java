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
import scau.zxck.entity.market.UserComments;
import scau.zxck.entity.sys.UserInfo;
import scau.zxck.service.market.IGoodsInfoService;
import scau.zxck.service.market.IUserCommentsService;
import sun.awt.SunHints;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Controller
@RequestMapping("/")
public class CommentsAction {
  @Autowired
  private IGoodsInfoService goodsInfoService;
  @Autowired
  private IUserCommentsService userCommentsService;

  @RequestMapping(value = "getGoodsComments", method = RequestMethod.POST)
  public String getGoodsComments(String jsonStr) throws BaseException {
    String r = "";
    JSONObject data = JSONObject.parseObject(jsonStr);
    JSONArray jsonArray = new JSONArray();
    Conditions conditions = new Conditions();
    List list =
        goodsInfoService.list(conditions.eq("goods_info_id", data.get("Goods_PK").toString()));
    if (!list.isEmpty()) {
      for (Iterator iter = ((java.util.List) list).iterator(); iter.hasNext();) {
        JSONObject temp = new JSONObject();
        UserComments comm = (UserComments) iter.next();

        temp.put("Comm_PK", comm.getId());
        temp.put("User_PK", comm.getUser_info_id());
        temp.put("Goods_PK", comm.getGoods_info_id());
        temp.put("Comm_Rank", comm.getComm_rank());
        temp.put("Comm_Text", comm.getComm_text());
        temp.put("Comm_Time", comm.getComm_time().toLocaleString());


        jsonArray.add(temp);
      }
    }
    r = jsonArray.toString();
    return null;
  }

  @RequestMapping(value = "addComments", method = RequestMethod.POST)
  public String addComments(String jsonStr) throws BaseException {
    String r = "";
    JSONObject data = JSONObject.parseObject(jsonStr);
    HttpServletRequest request =
        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    HttpSession session = request.getSession();
    if (session.getAttribute("User_PK") != null) {
      data.put("User_PK", session.getAttribute("User_PK"));
    } else {
      data.put("User_PK", "");
    }
    data.put("Comm_Time", (new SimpleDateFormat("yyyy-MM-dd HH:MM:ss")).format(new Date()));
    UserComments temp = new UserComments();
    temp.setUser_info_id(data.get("User_PK").toString());
    temp.setGoods_info_id(data.get("Goods_PK").toString());
    temp.setComm_rank((int) Integer.parseInt(data.get("Comm_Rank").toString()));
    temp.setComm_text(data.get("Comm_Text").toString());
    temp.setComm_time(Timestamp.valueOf(data.get("Comm_Time").toString()));

    temp.setUserinfo(new UserInfo());
    temp.setGoodsinfo(new GoodsInfo());
    try {
      userCommentsService.add(temp);
      r = "{\"status\":1}";
    } catch (Exception e) {
      e.printStackTrace();
      r = "{\"status\":0}";
    }
    return null;
  }

  @RequestMapping(value = "getUserGoodsComments", method = RequestMethod.POST)
  public String getUserGoodsComments(String jsonStr) throws BaseException {
    Conditions conditions = new Conditions();
    JSONObject temp=new JSONObject();
    String r = "";
    JSONObject data = JSONObject.parseObject(jsonStr);
    HttpServletRequest request =
        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    HttpSession session = request.getSession();
    if (session.getAttribute("User_PK") != null) {
      data.put("User_PK", session.getAttribute("User_PK"));
    } else {
      data.put("User_PK", "");
    }
    List list =
        userCommentsService.list(conditions.eq("goods_info_id", data.get("Goods_PK").toString())
            .and().eq("user_info_id", data.get("User_PK").toString()));
      if(!list.isEmpty()){
          UserComments comm =(UserComments)list.get(0);

          temp.put("Comm_PK", comm.getId());
          temp.put("User_PK", comm.getUser_info_id());
          temp.put("Goods_PK", comm.getGoods_info_id());
          temp.put("Comm_Rank", comm.getComm_rank());
          temp.put("Comm_Text", comm.getComm_text());
          temp.put("Comm_Time", comm.getComm_time().toLocaleString());
      }
      r=temp.toString();
      return null;
  }
  @RequestMapping(value = "deleteComments",method = RequestMethod.POST)
  public String deleteComments(String jsonStr) throws BaseException{
    String r="";
    JSONObject data=JSONObject.parseObject(jsonStr);
    try {
      userCommentsService.deleteByIds(data.get("Comm_PK").toString());
      r="{\"status\":1}";
    }catch (Exception e){
        e.printStackTrace();
      r="{\"status\":0}";
    }
    return "success";
  }
}