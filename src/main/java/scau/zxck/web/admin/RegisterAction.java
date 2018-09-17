package scau.zxck.web.admin;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import scau.zxck.base.dao.mybatis.Conditions;
import scau.zxck.entity.market.CartInfo;
import scau.zxck.entity.market.DeliveryAddress;
import scau.zxck.entity.sys.UserInfo;
import scau.zxck.service.market.ICartInfoService;
import scau.zxck.service.market.IDeliveryAddressService;
import scau.zxck.service.sys.IUserRegisterService;
import scau.zxck.utils.FlushWriteUtil;
import scau.zxck.utils.ReadJSONUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration("classpath:config/spring/spring.xml")
@Controller
@RequestMapping("/")
public class RegisterAction {
    @Autowired
    private IUserRegisterService userRegisterService;
    @Autowired
    private ICartInfoService cartInfoService;
    @Autowired
    private IDeliveryAddressService deliveryAddressService;
    @Autowired
    private HttpServletRequest request;

    @RequestMapping(value = "register", method = RequestMethod.POST)
//  @Test
    public void register(HttpServletResponse response) throws Exception {
        JSONObject data= ReadJSONUtil.readJSONStr(request);
        JSONObject temp = new JSONObject();
        data.put("User_RegTime", (new SimpleDateFormat("yyyy-MM-dd HH:MM:ss").format(new Date())).toString());
        boolean flag;
        UserInfo userInfo = new UserInfo();
        userInfo.setUser_password(data.get("User_Password").toString());
        userInfo.setUser_name(data.get("User_Name").toString());
        userInfo.setUser_cell(data.get("User_Cell").toString());
        userInfo.setUser_email(data.get("User_Email").toString());
        userInfo.setUser_sex((int) Integer.parseInt(data.get("User_Sex").toString()));
        userInfo.setUser_regtime(Timestamp.valueOf(data.get("User_RegTime").toString()).toString());
        userInfo.setUser_realname(data.get("User_Realname").toString());
        userInfo.setUser_id(data.get("User_ID").toString());
        userInfo.setCart(new CartInfo());
        userInfo.setDeliveryaddress(new DeliveryAddress());
        try {
            userRegisterService.add(userInfo);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        }
        temp.put("isSuccess", flag);
        Conditions conditions = new Conditions();
        List list =
                userRegisterService.list(conditions.eq("user_name", data.get("User_name").toString()).or()

                        .eq("user_cell", data.get("User_Cell").toString()).or()
                        .eq("user_email", data.get("User_Email").toString()).and()
                        .eq("user_password", data.get("User_Password").toString()));
        if (list.isEmpty()) {
//      return null;// 原来是return temp
        } else {
            UserInfo user = (UserInfo) list.get(0);
            temp.put("User_PK", user.getId());
            data.put("Cart_PK", temp.get("User_PK").toString());
            CartInfo cartInfo = new CartInfo();
            cartInfo.setId(data.get("Cart_PK").toString());
            cartInfo.setGoods_list("");
            cartInfo.setGoods_num("");
            cartInfoService.add(cartInfo);
            DeliveryAddress deliveryAddress = new DeliveryAddress();
            data.put("Deliv_PK", temp.get("User_PK").toString());
            deliveryAddress.setId(data.get("Deliv_PK").toString());
            deliveryAddress.setDeliv_cell("");
            deliveryAddress.setDeliv_name("");
            deliveryAddress.setDeliv_address("");
            deliveryAddress.setDeliv_zipcode("");
            deliveryAddressService.add(deliveryAddress);
        }
    }

    @RequestMapping(value = "validateAccount", method = RequestMethod.POST)
    public void validateAccount( HttpServletResponse response) throws Exception {
        String r = "";
        JSONObject data= ReadJSONUtil.readJSONStr(request);
        Conditions conditions = new Conditions();
        if (!data.get("User_Name").toString().equals("")) {
            List list =
                    userRegisterService.list(conditions.eq("user_name", data.get("User_Name").toString()));
            if (list == null) {
                r = "{\"status\":0}";
            } else
                r = "{\"status\":1}";
        }
        if (!data.get("User_Cell").toString().equals("")) {
            List list =
                    userRegisterService.list(conditions.eq("user_cell", data.get("User_Cell").toString()));
            if (list == null) {
                r = "{\"status\":0}";
            } else
                r = "{\"status\":1}";
        }
        if (!data.get("User_Email").toString().equals("")) {
            List list =
                    userRegisterService.list(conditions.eq("user_email", data.get("User_Email").toString()));
            if (list == null) {
                r = "{\"status\":0}";
            } else
                r = "{\"status\":1}";
        }
        FlushWriteUtil.flushWrite(response,r);
    }
}
