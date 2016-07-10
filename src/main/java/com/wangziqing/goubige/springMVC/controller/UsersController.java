package com.wangziqing.goubige.springMVC.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wangziqing.goubige.model.Users;
import com.wangziqing.goubige.springMVC.service.UsersService;

@Controller
@RequestMapping("/user")
public class UsersController {
	@Autowired
	UsersService usersService;

	@RequestMapping(value ="/authorizations", method = RequestMethod.POST)
	public void login(@RequestBody String userJson, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		System.out.println("登录");
		JSONObject jsonObject = JSON.parseObject(userJson);
		Users user = JSON.parseObject(jsonObject.get("user").toString(),
				Users.class);
		user = usersService.getByPhoneAndEmail(user.getTelePhone(),
				user.getEmail(), user.getPassWord());
		if(null==user){
			jsonObject.put("IsSuccess", false);
		}else jsonObject.put("IsSuccess", true);
		//jsonObject.replace("user", user);
		jsonObject.put("method", "authorizations");
		System.out.println(jsonObject.toJSONString());
		response.setHeader("Content-type", "text/html;charset=UTF-8");
		response.getWriter().print(jsonObject.toString());
	}

	// userJsonString 作为请求的jsonString
	@RequestMapping(value = "", method = RequestMethod.POST)
	public void register(@RequestBody String userJson,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		System.out.println("注册");
		JSONObject jsonObject = JSON.parseObject(userJson);
		Users user = JSON.parseObject(jsonObject.get("user").toString(),
				Users.class);
		if (null != usersService.getIDByPhoneAndEmail(user.getTelePhone(),
				user.getEmail())) {
			//已存在
			//jsonObject.replace("user", null);
			jsonObject.put("IsSuccess", false);
		} else {
			// 注册服务
			usersService.register(user);
			// 返回结果
			user = usersService.getByPhoneAndEmail(user.getTelePhone(),
					user.getEmail(), user.getPassWord());
			//jsonObject.replace("user", user);
			jsonObject.put("IsSuccess", true);
		}
		System.out.println(jsonObject.toString());
		response.setHeader("Content-type", "text/html;charset=UTF-8");
		response.getWriter().print(jsonObject.toString());
	}

	@RequestMapping(value = "/{userID}", method = RequestMethod.POST)
	public void update(@PathVariable int userID, @RequestBody String userJson,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		// 注册服务
		System.out.println("修改");
		System.out.println(userJson);
		JSONObject jsonObject = JSON.parseObject(userJson);
		Users user = JSON.parseObject(jsonObject.get("user").toString(),
				Users.class);
		user.setID(userID);
		usersService.update(user);
	//	jsonObject.replace("user", user);
		response.setHeader("Content-type", "text/html;charset=UTF-8");
		response.getWriter().print(jsonObject.toString());
	}

	@RequestMapping(value = "/userImg", method = RequestMethod.POST)
	public void uploadHeader(
			@RequestParam(value = "userImg", required = false) MultipartFile file,
			@RequestParam(value = "ID") int ID, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		// 调用上传文件
		System.out.println(file.getName());
		System.out.println(ID);
		usersService.updateheader(file.getInputStream(), ID);
	}
	@RequestMapping(value = "/{userID}", method = RequestMethod.GET)
	public void getUserDetailsByID(
			@PathVariable int userID,
			HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		Users user=usersService.getByID(userID);
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("user", user);
		jsonObject.put("method", "getUserDetailsByID");
		System.out.println(jsonObject.toString());
		response.setHeader("Content-type", "text/html;charset=UTF-8");
		response.getWriter().print(jsonObject.toString());
	}

	@RequestMapping(value="",method=RequestMethod.GET)
	public void getUsers(HttpServletRequest request,HttpServletResponse response) throws IOException{
		int pageSize,pageNum;
		pageSize=Integer.parseInt(request.getParameter("pageSize"));
		pageNum=Integer.parseInt(request.getParameter("pageNum"));
		String tag= request.getParameter("tag");
		System.out.println(pageNum+"-"+pageSize);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("pageNum", pageNum);
		jsonObject.put("pageSize", pageSize);
		jsonObject.put("users",usersService.getUserByPage(pageSize,pageNum));
		jsonObject.put("method","getUsers");
		jsonObject.put("tag",tag);
		response.setHeader("Content-type", "text/html;charset=UTF-8");
		response.getWriter().print(jsonObject.toString());
	}

}
