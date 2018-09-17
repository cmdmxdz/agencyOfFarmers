//package scau.zxck.web.admin;
//
//import org.apache.shiro.web.session.HttpServletSession;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import javax.servlet.http.Cookie;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//import java.util.Enumeration;
//import java.util.HashMap;
//import java.util.Map;
//
//@Controller
//@RequestMapping("/")
//public class TestAction {
//  @Autowired
//  private HttpServletRequest request;
//
//  @RequestMapping(value = "test", method = RequestMethod.POST)
//  @ResponseBody
//  public String s() throws Exception{
//      Enumeration<String> headerNames = request.getHeaderNames();
//      String sss="";
//      while(headerNames.hasMoreElements())
//      {
//          String headerName = headerNames.nextElement();
//          // 获取每个请求、及其对应的值
//                 sss+= headerName + "-->" + request.getHeader(headerName) + "<br/>";
//      }
//// 设置解码方式，对于简体中文，使用GBK解码
//      request.setCharacterEncoding("GBK");   // ①
//// 下面依次获取表单域的值
//      String name = request.getParameter("name");
//      String gender = request.getParameter("gender");
//// 如果某个请求参数有多个值，将使用该方法获取多个值
//      String[] color = request.getParameterValues("color");
//      String national = request.getParameter("country");
//      return "name:"+name+"\n"+"gender:"+gender+"\n"+"colors:"+color.toString()+"national:"+national;
//  }
//}

package scau.zxck.web.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import scau.zxck.base.dao.mybatis.Conditions;
import scau.zxck.service.market.ILoginService;
import scau.zxck.service.sys.IUserLoginService;
import scau.zxck.utils.CodeUtil;
import scau.zxck.utils.SendEmail2Util;
import scau.zxck.utils.SendEmailUtil;
import scau.zxck.web.listener.UserSessionListener;


import javax.servlet.http.*;

@Controller
@RequestMapping("/")
public class TestAction {
    private static int count=0;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpSession session;
    @Autowired
    private IUserLoginService userLoginService;
    @RequestMapping(value = "test", method = RequestMethod.POST)
    public String s(String email,String from,String count,String accreditCode) throws Exception {
        for(int i=0;i<Integer.parseInt(count);i++) {
            SendEmailUtil sendEmailUtil = new SendEmailUtil(email, CodeUtil.generateUniqueCode());
            sendEmailUtil.setFrom(from);
            sendEmailUtil.setAccreditCode(accreditCode);
            new Thread(sendEmailUtil).start();
        }
        return "success";
    }
    @RequestMapping(value = "admin", method = RequestMethod.POST)
    public String test2() throws Exception {
        System.out.println("test2");
        return "success";
    }
  @RequestMapping(value = "testEmail", method = RequestMethod.POST)
  public String test3(String from,String to,String accreditCode) throws Exception {
    SendEmail2Util sendEmail2Util=new SendEmail2Util();
    sendEmail2Util.setAccreditCode(accreditCode);
    sendEmail2Util.setFrom(from);
    sendEmail2Util.setTo(to);
    new Thread(sendEmail2Util).start();
    return "success";
  }

}
