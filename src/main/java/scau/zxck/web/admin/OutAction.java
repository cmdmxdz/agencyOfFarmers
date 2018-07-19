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
import scau.zxck.dao.market.GoodsInfoDao;
import scau.zxck.entity.market.GoodsInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.List;

@Controller
@RequestMapping("/")
public class OutAction {


  @RequestMapping(value = "out", method = RequestMethod.POST)
  public String OutAction(String jsonStr) throws Exception {
    JSONObject data = JSONObject.parseObject(jsonStr);
    String r = "";
    HttpServletRequest request =
        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    HttpSession session = request.getSession();
    if(session.getAttribute("isAdmin")!=null){
      if((boolean)session.getAttribute("isAdmin")){
        session.removeAttribute("Admin_PK");
      }
      else{
        session.removeAttribute("User_PK");
      }
    }else{

    }
    session.removeAttribute("isAdmin");
    return null;
  }
}