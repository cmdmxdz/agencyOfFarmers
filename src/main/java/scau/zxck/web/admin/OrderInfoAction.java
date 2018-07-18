package scau.zxck.web.admin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.xml.internal.rngom.parse.host.Base;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import scau.zxck.base.dao.mybatis.Conditions;
import scau.zxck.base.exception.BaseException;
import scau.zxck.entity.market.GoodsInfo;
import scau.zxck.entity.market.GoodsLog;
import scau.zxck.entity.market.OrderInfo;
import scau.zxck.entity.sys.UserInfo;
import scau.zxck.service.market.IGoodsInfoService;
import scau.zxck.service.market.IOrderInfoService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Controller
@RequestMapping("/")
public class OrderInfoAction {
  @Autowired
  private IOrderInfoService orderInfoService;
  @Autowired
  private IGoodsInfoService goodsInfoService;

  @RequestMapping(value = "getUserOrderListPaging", method = RequestMethod.POST)
  public String getUserOrderListPaging(String jsonStr) throws BaseException {
    String r = "";
    HttpServletRequest request =
        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    HttpSession session = request.getSession();
    JSONObject pageInfo = JSONObject.parseObject(jsonStr);
    if (session.getAttribute("User_PK") != null) {
      pageInfo.put("User_PK", (int) session.getAttribute("User_PK"));
    } else {
      pageInfo.put("User_PK", "");
    }
    JSONArray jsonarr = new JSONArray();
    Conditions conditions = new Conditions();
    List list =
        orderInfoService.list(conditions.eq("user_info_id", pageInfo.get("User_PK").toString()));
    for (Iterator iter = ((java.util.List) list).iterator(); iter.hasNext();) {
      JSONObject temp = new JSONObject();
      OrderInfo order = (OrderInfo) iter.next();

      temp.put("Order_PK", order.getId());
      temp.put("User_PK", order.getUser_info_id());
      temp.put("Order_ID", order.getOrder_id());
      temp.put("Order_No", order.getOrder_no());
      temp.put("Goods_List", order.getGoods_list());
      temp.put("Goods_Num", order.getGoods_num());
      temp.put("Goods_Prices", order.getGoods_prices());
      temp.put("Order_Time", order.getOrder_time().toLocaleString());
      temp.put("Order_IsPay", order.isOrder_ispay());
      if (order.getOrder_paytime().toLocaleString().equals(new String("0001-1-1 1:01:01"))) {
        temp.put("Order_PayTime", "");
      } else {
        temp.put("Order_PayTime", order.getOrder_paytime().toLocaleString());
      }
      temp.put("Order_PayPrice", order.getOrder_payprice());
      temp.put("Order_State", order.getOrder_state());
      temp.put("Order_TrackNum", order.getOrder_tracknum());
      temp.put("Order_Company", order.getOrder_company());
      temp.put("Order_Website", order.getOrder_website());
      temp.put("Order_Aftersale", order.getOrder_aftersale());
      temp.put("Order_Reserve_1", order.getOrder_reserve_1());

      jsonarr.add(temp);
    }
    JSONArray temparr = JSONArrayPaging(jsonarr, pageInfo);
    r = temparr.toString();
    return "success";
  }

  @RequestMapping(value = "getUStateOrderPaging", method = RequestMethod.POST)
  public String getUStateOrderPaging(String jsonStr, String jsonStr2) throws BaseException {
    String r = "";
    HttpServletRequest request =
        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    HttpSession session = request.getSession();
    JSONObject data = JSONObject.parseObject(jsonStr);
    JSONObject pageInfo = JSONObject.parseObject(jsonStr);
    if (session.getAttribute("User_PK") != null) {
      data.put("User_PK", (int) session.getAttribute("User_PK"));
    } else {
      data.put("User_PK", "");
    }
    JSONArray jsonarr = new JSONArray();
    Conditions conditions = new Conditions();
    List list = orderInfoService.list(conditions.eq("user_info_id", data.get("User_PK").toString())
        .and().eq("order_state", data.get("Order_State").toString()));
    for (Iterator iter = ((java.util.List) list).iterator(); iter.hasNext();) {
      JSONObject temp = new JSONObject();
      OrderInfo order = (OrderInfo) iter.next();

      temp.put("Order_PK", order.getId());
      temp.put("User_PK", order.getUser_info_id());
      temp.put("Order_ID", order.getOrder_id());
      temp.put("Order_No", order.getOrder_no());
      temp.put("Goods_List", order.getGoods_list());
      temp.put("Goods_Num", order.getGoods_num());
      temp.put("Goods_Prices", order.getGoods_prices());
      temp.put("Order_Time", order.getOrder_time().toLocaleString());
      temp.put("Order_IsPay", order.isOrder_ispay());
      if (order.getOrder_paytime().toLocaleString().equals(new String("0001-1-1 1:01:01"))) {
        temp.put("Order_PayTime", "");
      } else {
        temp.put("Order_PayTime", order.getOrder_paytime().toLocaleString());
      }
      temp.put("Order_PayPrice", order.getOrder_payprice());
      temp.put("Order_State", order.getOrder_state());
      temp.put("Order_TrackNum", order.getOrder_tracknum());
      temp.put("Order_Company", order.getOrder_company());
      temp.put("Order_Website", order.getOrder_website());
      temp.put("Order_Aftersale", order.getOrder_aftersale());
      temp.put("Order_Reserve_1", order.getOrder_reserve_1());

      jsonarr.add(temp);
    }
    JSONArray temparr = JSONArrayPaging(jsonarr, pageInfo);
    r = temparr.toString();
    return "success";
  }

  @RequestMapping(value = "addOrder", method = RequestMethod.POST)
  public String addOrder(String jsonStr) throws BaseException {
    String r = "";
    HttpServletRequest request =
        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    HttpSession session = request.getSession();
    JSONObject data = JSONObject.parseObject(jsonStr);
    String[] goodslist = ((String) data.get("Goods_List")).split("#");
    String[] goodsnum = ((String) data.get("Goods_Num")).split("#");
    for (int i = 0; i < goodslist.length; i++) {// 循环操作订单中的每一项商品
      JSONObject log = new JSONObject();
      log.put("Goods_PK", goodslist[i]);
      log.put("Goods_In", 0);
      log.put("Goods_Out", goodsnum[i]);
      log.put("Goods_PriceChange", 0);
      log.put("GL_Time", (new SimpleDateFormat("yyyy-MM-dd HH:MM:ss")).format(new Date()));
      GoodsLog tempg = new GoodsLog();
      tempg.setGoods_info_id(log.get("Goods_PK").toString());
      tempg.setGoods_in((int) Integer.parseInt(log.get("Goods_In").toString()));
      tempg.setGoods_out((int) Integer.parseInt(log.get("Goods_Out").toString()));
      tempg.setGoods_pricechange((float) Float.parseFloat(log.get("Goods_PriceChange").toString()));
      tempg.setGl_time(Timestamp.valueOf(log.get("GL_Time").toString()));
      JSONObject goodspk = new JSONObject();
      goodspk.put("Goods_PK", goodslist[i]);
      JSONObject temp = new JSONObject();
      Conditions conditions = new Conditions();
      GoodsInfo list = goodsInfoService.findById(goodspk.get("Goods_PK").toString());
      if (list != null) {
        GoodsInfo goods = list;

        temp.put("Goods_PK", goods.getId());
        temp.put("Goods_Name", goods.getGoods_name());
        temp.put("Goods_Type", goods.getGoods_type());
        temp.put("Goods_Num", goods.getGoods_num());
        temp.put("Goods_Price", goods.getGoods_price());
        temp.put("Goods_Mark", goods.getGoods_mark());
        temp.put("Goods_Show", goods.getGoods_show());
        temp.put("Goods_Picture", goods.getGoods_picture());
        temp.put("Goods_Season", goods.getGoods_season());
        temp.put("Goods_Blossom", goods.getGoods_blossom());
        temp.put("Goods_Fruit", goods.getGoods_fruit());
        temp.put("Goods_Mature", goods.getGoods_mature());
        temp.put("Goods_Expiration", goods.getGoods_expiration());
        temp.put("Goods_Reserve_1", goods.getGoods_reserve_1());
        temp.put("Goods_Reserve_2", goods.getGoods_reserve_2());
      }
      int num = (int) temp.get("Goods_Num") - Integer.parseInt(goodsnum[i]);
      temp.put("Goods_Num", num);
      // access.updateGoodsInfo(goods);// 减去库存
      GoodsInfo temp1 = goodsInfoService.findById(temp.get("Goods_PK").toString());
      temp1.setGoods_name(temp.get("Goods_Name").toString());
      temp1.setGoods_type((int) Integer.parseInt(temp.get("Goods_Type").toString()));
      temp1.setGoods_num((int) Integer.parseInt(temp.get("Goods_Num").toString()));
      temp1.setGoods_price((float) Float.parseFloat(temp.get("Goods_Price").toString()));
      temp1.setGoods_mark(temp.get("Goods_Mark").toString().charAt(0));
      temp1.setGoods_show(temp.get("Goods_Show").toString().charAt(0));
      temp1.setGoods_picture(temp.get("Goods_Picture").toString());
      temp1.setGoods_season((int) Integer.parseInt(temp.get("Goods_Season").toString()));
      temp1.setGoods_blossom(temp.get("Goods_Blossom").toString());
      temp1.setGoods_fruit(temp.get("Goods_Fruit").toString());
      temp1.setGoods_mature(temp.get("Goods_Mature").toString());
      temp1.setGoods_expiration(temp.get("Goods_Expiration").toString());
      goodsInfoService.updateById(temp1);
    }
    // Date date = new Date();
    // String id = (new SimpleDateFormat("yyyyMMddHHMMss")).format(date);
    // System.out.println(id);
    if (session.getAttribute("User_PK") != null) {
      String pk = String.valueOf(session.getAttribute("User_PK"));
      data.put("User_PK", session.getAttribute("User_PK"));
      // id += pk.substring(pk.length() - 6, pk.length());
    } else {
      data.put("User_PK", "");
      // id += "000000";
    }
    // data.put("Order_ID", id);
    // data.put("Order_Time", (new SimpleDateFormat("yyyy-MM-dd HH:MM:ss")).format(date));
    // String date1 = "0001-01-01 01:01:01";
    // try {
    // Date strD = (new SimpleDateFormat("yyyy-MM-dd HH:MM:ss")).parse(date1);
    // data.put("Order_PayTime", (new SimpleDateFormat("yyyy-MM-dd HH:MM:ss")).format(strD));
    // } catch (ParseException e) {
    // e.printStackTrace();
    // }
    OrderInfo orderInfo = new OrderInfo();
    orderInfo.setUser_info_id(data.get("User_PK").toString());
    orderInfo.setGoods_list(data.get("Goods_List").toString());
    orderInfo.setGoods_num(data.get("Goods_Num").toString());
    orderInfo.setGoods_prices(data.get("Goods_Price").toString());
    orderInfo.setOrder_aftersale((int) Integer.parseInt(data.get("Order_Aftersale").toString()));
    orderInfo.setOrder_company(data.get("Order_Company").toString());
    orderInfo.setOrder_id(data.get("Order_ID").toString());
    orderInfo.setOrder_ispay((boolean) data.get("Order_IsPay"));
    orderInfo.setOrder_no(data.get("Order_No").toString());
    orderInfo.setOrder_payprice((float) Float.parseFloat(data.get("Order_PayPrice").toString()));
    orderInfo.setOrder_paytime(Timestamp.valueOf(data.get("Order_PayTime").toString()));
    orderInfo.setOrder_reserve_1(data.get("Order_Reserve_1").toString());
    orderInfo.setOrder_state((int) Integer.parseInt(data.get("Order_State").toString()));
    orderInfo.setOrder_time(Timestamp.valueOf(data.get("Order_Time").toString()));
    orderInfo.setOrder_tracknum(data.get("Order_Tracknum").toString());
    orderInfo.setOrder_website(data.get("Order_Website").toString());
    orderInfo.setUserInfo(new UserInfo());
    orderInfoService.add(orderInfo);
    Conditions conditions = new Conditions();
    OrderInfo orderInfo1 =
        orderInfoService.find(conditions.eq("order_id", data.get("Order_Id").toString()));
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("Order_PK", orderInfo1.getId());
    jsonObject.put("User_PK", orderInfo1.getUser_info_id());
    jsonObject.put("Order_ID", orderInfo1.getOrder_id());
    jsonObject.put("Order_No", orderInfo1.getOrder_no());
    jsonObject.put("Goods_List", orderInfo1.getGoods_list());
    jsonObject.put("Goods_Num", orderInfo1.getGoods_num());
    jsonObject.put("Goods_Prices", orderInfo1.getGoods_prices());
    jsonObject.put("Order_Time", orderInfo1.getOrder_time().toLocaleString());
    jsonObject.put("Order_IsPay", orderInfo1.isOrder_ispay());
    if (orderInfo1.getOrder_paytime().toLocaleString().equals(new String("0001-1-1 1:01:01"))) {
      jsonObject.put("Order_PayTime", "");
    } else {
      jsonObject.put("Order_PayTime", orderInfo1.getOrder_paytime().toLocaleString());
    }
    jsonObject.put("Order_PayPrice", orderInfo1.getOrder_payprice());
    jsonObject.put("Order_State", orderInfo1.getOrder_state());
    jsonObject.put("Order_TrackNum", orderInfo1.getOrder_tracknum());
    jsonObject.put("Order_Company", orderInfo1.getOrder_company());
    jsonObject.put("Order_Website", orderInfo1.getOrder_website());
    jsonObject.put("Order_Aftersale", orderInfo1.getOrder_aftersale());
    jsonObject.put("Order_Reserve_1", orderInfo1.getOrder_reserve_1());
    r = jsonObject.toString();
    return "success";
  }

  @RequestMapping(value = "updateOrder", method = RequestMethod.POST)
  public String updateOrder(String jsonStr) throws BaseException {
    String r = "";
    JSONObject json = JSONObject.parseObject(jsonStr);
    OrderInfo temp = orderInfoService.findById(json.get("Order_PK").toString());
    temp.setUser_info_id(json.get("User_PK").toString());
    temp.setOrder_id(json.get("Order_ID").toString());
    temp.setOrder_no(json.get("Order_No").toString());
    temp.setGoods_list(json.get("Goods_List").toString());
    temp.setGoods_num(json.get("Goods_Num").toString());
    temp.setGoods_prices(json.get("Goods_Prices").toString());
    temp.setOrder_time(Timestamp.valueOf(json.get("Order_Time").toString()));
    temp.setOrder_ispay((boolean) json.get("Order_IsPay"));
    if (!json.get("Order_PayTime").equals(new String(""))) {
      temp.setOrder_paytime(Timestamp.valueOf(json.get("Order_PayTime").toString()));
    }
    /*
     * else {
     * 
     * try { String date1 = "0001-01-01 01:01:01"; Date strD = (Date) (new
     * SimpleDateFormat("yyyy-MM-dd HH:MM:ss")).parse(date1);
     * temp.setOrder_paytime(Timestamp.valueOf((new
     * SimpleDateFormat("yyyy-MM-dd HH:MM:ss")).format(strD))); } catch (ParseException e) {
     * e.printStackTrace(); } }
     */
    temp.setOrder_payprice((float) Float.parseFloat(json.get("Order_PayPrice").toString()));
    temp.setOrder_state((int) Integer.parseInt(json.get("Order_State").toString()));
    temp.setOrder_tracknum(json.get("Order_TrackNum").toString());
    temp.setOrder_company(json.get("Order_Company").toString());
    temp.setOrder_website(json.get("Order_Website").toString());
    temp.setOrder_aftersale((int) Integer.parseInt(json.get("Order_Aftersale").toString()));
    temp.setOrder_reserve_1(json.get("Order_Reserve_1").toString());
    try {
      orderInfoService.updateById(temp);
      r = "{\"status\":1}";
    } catch (Exception e) {
      e.printStackTrace();
      r = "{\"status\":0}";
    }
    return "success";
  }

  @RequestMapping(value = "payOrder", method = RequestMethod.POST)
  public String payOrder(String jsonStr) throws BaseException {
    JSONObject data = JSONObject.parseObject(jsonStr);
    String r = "";
    JSONObject temp = new JSONObject();
    OrderInfo order = orderInfoService.findById(data.get("Order_ID").toString());

    temp.put("Order_PK", order.getId());
    temp.put("User_PK", order.getUser_info_id());
    temp.put("Order_ID", order.getOrder_id());
    temp.put("Order_No", order.getOrder_no());
    temp.put("Goods_List", order.getGoods_list());
    temp.put("Goods_Num", order.getGoods_num());
    temp.put("Goods_Prices", order.getGoods_prices());
    temp.put("Order_Time", order.getOrder_time().toLocaleString());
    temp.put("Order_IsPay", order.isOrder_ispay());
    if (order.getOrder_paytime().toLocaleString().equals(new String("0001-1-1 1:01:01"))) {
      temp.put("Order_PayTime", "");
    } else {
      temp.put("Order_PayTime", order.getOrder_paytime().toLocaleString());
    }
    temp.put("Order_PayPrice", order.getOrder_payprice());
    temp.put("Order_State", order.getOrder_state());
    temp.put("Order_TrackNum", order.getOrder_tracknum());
    temp.put("Order_Company", order.getOrder_company());
    temp.put("Order_Website", order.getOrder_website());
    temp.put("Order_Aftersale", order.getOrder_aftersale());
    temp.put("Order_Reserve_1", order.getOrder_reserve_1());
    Date date = new Date();
    temp.put("Order_PayTime", (new SimpleDateFormat("yyyy-MM-dd HH:MM:ss").format(date)));
    temp.put("Order_IsPay", true);
    temp.put("Order_State", "6");
    OrderInfo tempx = orderInfoService.findById(temp.get("Order_PK").toString());
    tempx.setUser_info_id(temp.get("User_PK").toString());
    tempx.setOrder_id(temp.get("Order_ID").toString());
    tempx.setOrder_no(temp.get("Order_No").toString());
    tempx.setGoods_list(temp.get("Goods_List").toString());
    tempx.setGoods_num(temp.get("Goods_Num").toString());
    tempx.setGoods_prices(temp.get("Goods_Prices").toString());
    tempx.setOrder_time(Timestamp.valueOf(temp.get("Order_Time").toString()));
    tempx.setOrder_ispay((boolean) temp.get("Order_IsPay"));
    if (!temp.get("Order_PayTime").equals(new String(""))) {
      tempx.setOrder_paytime(Timestamp.valueOf(temp.get("Order_PayTime").toString()));
    }
    /*
     * else {
     *
     * try { String date1 = "0001-01-01 01:01:01"; Date strD = (Date) (new
     * SimpleDateFormat("yyyy-MM-dd HH:MM:ss")).parse(date1);
     * temp.setOrder_paytime(Timestamp.valueOf((new
     * SimpleDateFormat("yyyy-MM-dd HH:MM:ss")).format(strD))); } catch (ParseException e) {
     * e.printStackTrace(); } }
     */
    tempx.setOrder_payprice((float) Float.parseFloat(temp.get("Order_PayPrice").toString()));
    tempx.setOrder_state((int) Integer.parseInt(temp.get("Order_State").toString()));
    tempx.setOrder_tracknum(temp.get("Order_TrackNum").toString());
    tempx.setOrder_company(temp.get("Order_Company").toString());
    tempx.setOrder_website(temp.get("Order_Website").toString());
    tempx.setOrder_aftersale((int) Integer.parseInt(temp.get("Order_Aftersale").toString()));
    tempx.setOrder_reserve_1(temp.get("Order_Reserve_1").toString());
    try {
      orderInfoService.updateById(tempx);
      r = "{\"status\":1}";
    } catch (Exception e) {
      e.printStackTrace();
      r = "{\"status\":0}";
    }
    return "success";
  }

  @RequestMapping(value = "getNoPayOrder", method = RequestMethod.POST)
  public String getNoPayOrder(String jsonStr) throws BaseException {
    JSONObject pageInfo = JSONObject.parseObject(jsonStr);
    String r = "";
    JSONArray jsonarr = new JSONArray();
    Conditions conditions = new Conditions();
    List list = orderInfoService.list(conditions.eq("order_isPay", false));


    for (Iterator iter = ((java.util.List) list).iterator(); iter.hasNext();) {
      JSONObject temp = new JSONObject();

      OrderInfo order = (OrderInfo) iter.next();

      temp.put("Order_PK", order.getId());
      temp.put("User_PK", order.getUser_info_id());
      temp.put("Order_ID", order.getOrder_id());
      temp.put("Order_No", order.getOrder_no());
      temp.put("Goods_List", order.getGoods_list());
      temp.put("Goods_Num", order.getGoods_num());
      temp.put("Goods_Prices", order.getGoods_prices());
      temp.put("Order_Time", order.getOrder_time().toLocaleString());
      temp.put("Order_IsPay", order.isOrder_ispay());
      if (order.getOrder_paytime().toLocaleString().equals(new String("0001-1-1 1:01:01"))) {
        temp.put("Order_PayTime", "");
      } else {
        temp.put("Order_PayTime", order.getOrder_paytime().toLocaleString());
      }
      temp.put("Order_PayPrice", order.getOrder_payprice());
      temp.put("Order_State", order.getOrder_state());
      temp.put("Order_TrackNum", order.getOrder_tracknum());
      temp.put("Order_Company", order.getOrder_company());
      temp.put("Order_Website", order.getOrder_website());
      temp.put("Order_Aftersale", order.getOrder_aftersale());
      temp.put("Order_Reserve_1", order.getOrder_reserve_1());
      jsonarr.add(temp);
    }
    r = JSONArrayPaging(jsonarr, pageInfo).toString();
    return "success";
  }

  @RequestMapping(value = "getIDOrder", method = RequestMethod.POST)
  public String getIDOrder(String jsonStr) throws BaseException {
    JSONObject data = JSONObject.parseObject(jsonStr);
    JSONObject temp = new JSONObject();
    Conditions conditions = new Conditions();
    List list = orderInfoService.list(conditions.eq("order_id", data.get("Order_ID").toString()));
    if (!list.isEmpty()) {
      OrderInfo order = (OrderInfo) list.get(0);

      temp.put("Order_PK", order.getId());
      temp.put("User_PK", order.getUser_info_id());
      temp.put("Order_ID", order.getOrder_id());
      temp.put("Order_No", order.getOrder_no());
      temp.put("Goods_List", order.getGoods_list());
      temp.put("Goods_Num", order.getGoods_num());
      temp.put("Goods_Prices", order.getGoods_prices());
      temp.put("Order_Time", order.getOrder_time().toLocaleString());
      temp.put("Order_IsPay", order.isOrder_ispay());
      if (order.getOrder_paytime().toLocaleString().equals(new String("0001-1-1 1:01:01"))) {
        temp.put("Order_PayTime", "");
      } else {
        temp.put("Order_PayTime", order.getOrder_paytime().toLocaleString());
      }
      temp.put("Order_PayPrice", order.getOrder_payprice());
      temp.put("Order_State", order.getOrder_state());
      temp.put("Order_TrackNum", order.getOrder_tracknum());
      temp.put("Order_Company", order.getOrder_company());
      temp.put("Order_Website", order.getOrder_website());
      temp.put("Order_Aftersale", order.getOrder_aftersale());
      temp.put("Order_Reserve_1", order.getOrder_reserve_1());
    }
    String r = temp.toString();
    return "success";
  }

  @RequestMapping(value = "getSumOfPayedNoFinishedOrder", method = RequestMethod.POST)
  public String getSumOfPayedNoFinishedOrder(String jsonStr) throws BaseException {
    JSONObject data = JSONObject.parseObject(jsonStr);
    Conditions conditions = new Conditions();
    List list =
        orderInfoService.list(conditions.eq("order_state", 1).and().eq("order_isPay", true));
    int sum = list.size();
    String r = r = "{\"sum\":" + sum + "}";
    return "success";
  }

  @RequestMapping(value = "getPayedNoFinishedOrder", method = RequestMethod.POST)
  public String getPayedNoFinishedOrder(String jsonStr) throws BaseException {
    JSONObject pageInfo = JSONObject.parseObject(jsonStr);
    JSONArray jsonarr = new JSONArray();
    Conditions conditions = new Conditions();
    List list =
        orderInfoService.list(conditions.eq("order_state", 1).and().eq("order_isPay", true));
    for (Iterator iter = ((java.util.List) list).iterator(); iter.hasNext();) {
      JSONObject temp = new JSONObject();

      OrderInfo order = (OrderInfo) iter.next();

      temp.put("Order_PK", order.getId());
      temp.put("User_PK", order.getUser_info_id());
      temp.put("Order_ID", order.getOrder_id());
      temp.put("Order_No", order.getOrder_no());
      temp.put("Goods_List", order.getGoods_list());
      temp.put("Goods_Num", order.getGoods_num());
      temp.put("Goods_Prices", order.getGoods_prices());
      temp.put("Order_Time", order.getOrder_time().toLocaleString());
      temp.put("Order_IsPay", order.isOrder_ispay());
      if (order.getOrder_paytime().toLocaleString().equals(new String("0001-1-1 1:01:01"))) {
        temp.put("Order_PayTime", "");
      } else {
        temp.put("Order_PayTime", order.getOrder_paytime().toLocaleString());
      }
      temp.put("Order_PayPrice", order.getOrder_payprice());
      temp.put("Order_State", order.getOrder_state());
      temp.put("Order_TrackNum", order.getOrder_tracknum());
      temp.put("Order_Company", order.getOrder_company());
      temp.put("Order_Website", order.getOrder_website());
      temp.put("Order_Aftersale", order.getOrder_aftersale());
      temp.put("Order_Reserve_1", order.getOrder_reserve_1());
      jsonarr.add(temp);
    }
    String r=JSONArrayPaging(jsonarr,pageInfo).toString();
    return "success";
  }

  public JSONArray JSONArrayPaging(JSONArray arr, JSONObject json) {
    JSONArray temparr = new JSONArray();
    JSONObject firstjson = new JSONObject();

    firstjson.put("Size", arr.size());

    if (arr.size() < json.getIntValue("NumPerPage")) {
      firstjson.put("PageNum", 1);
    } else {
      if (arr.size() % json.getIntValue("NumPerPage") == 0) {
        firstjson.put("PageNum", arr.size() / json.getIntValue("NumPerPage"));
      } else {
        firstjson.put("PageNum", (arr.size() / json.getIntValue("NumPerPage")) + 1);
      }
    }
    firstjson.put("NowPage", json.getIntValue("Page"));
    firstjson.put("NumPerPage", json.getIntValue("NumPerPage"));

    temparr.add(firstjson);
    for (int i = (json.getInteger("Page") - 1) * json.getIntValue("NumPerPage"); i < arr
        .size(); i++) {
      temparr.add(arr.get(i));
      if (i >= json.getIntValue("Page") * json.getIntValue("NumPerPage") - 1)
        break;
    }

    return temparr;
  }
}