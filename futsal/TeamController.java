package com.ai.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ai.domain.FieldDTO;
import com.ai.domain.MemberDTO;
import com.ai.domain.TeamDTO;
import com.ai.service.MemberService;
import com.ai.service.TeamService;

@Controller
@RequestMapping(path = "/team")
public class TeamController {
   @Autowired
   TeamService service;
   @Autowired
   MemberService mService;
   @Autowired
   TeamService tService;
   
  
   
   @RequestMapping(value = "/insert", method = RequestMethod.POST) 
   public ModelAndView insertTeam(@RequestParam("name")String tName,@RequestParam("location1")String location1,
		   @RequestParam("location2")String location2,
         @RequestParam("birth")String birth,@RequestParam("uniform")String uniform,
         @RequestParam("total")int total,@RequestParam("age")String tAge,
         @RequestParam("manner")int tManner,@RequestParam("message")String TeamInfo, HttpSession session) {
      TeamDTO insertTeam = new TeamDTO();
      ModelAndView mav = new ModelAndView(); 
      String userId = (String)session.getAttribute("userId");
      String _id = mService.findByid(userId).getNickName();
      insertTeam.set_id(_id); 
      insertTeam.setTName(tName);
      insertTeam.setTArea(location1 + " " + location2);
      insertTeam.setFoundingDate(birth);
      insertTeam.setUniform(uniform);
      insertTeam.setTTotal(total);
      insertTeam.setTAge(tAge);
      insertTeam.setTManner(tManner);
      insertTeam.setTeamInfo(TeamInfo);
      MemberDTO member = mService.findByid(userId);
      member.setTName(tName);
      mService.save(member);
      TeamDTO insertedTeam = service.insert(insertTeam);
      mav.addObject("insertedTeam", insertedTeam); 
      mav.setViewName("/main");
      
      System.out.println("insert tName : " + insertedTeam.getTName());
      System.out.println("insert tArea : " + insertedTeam.getTArea());
      System.out.println("insert fDate : " + insertedTeam.getFoundingDate());
      System.out.println("insert tUniform : " + insertedTeam.getUniform());
      System.out.println("insert tTotal : " + insertedTeam.getTTotal());
      System.out.println("insert tAge : " + insertedTeam.getTAge());
      System.out.println("insert tManner : " + insertedTeam.getTManner());
      System.out.println("insert tInfo : " + insertedTeam.getTeamInfo());
      
      return mav;
   }
   
   @RequestMapping(value = "/myteamtest")
   public ModelAndView moveToTeam(@RequestParam("tName") String tName, HttpSession session) { 
      TeamDTO myteam = service.findBytName(tName);
      ModelAndView mav = new ModelAndView();
      mav.addObject("myteam", myteam);
      mav.setViewName("/myteamtest");
      return mav;
   }
   
   @RequestMapping(value = "/search", method = RequestMethod.POST)
   public ModelAndView search(@RequestParam("tName")String tName, HttpSession session) {
	   ModelAndView mav = new ModelAndView();
	   System.out.println("찾는 팀이름 : " + tName);
	   ArrayList<TeamDTO> team = service.findByTNameRegex(tName);
	   mav.addObject("sTeamList", team);
	   System.out.println("========= SEARCHED TEAM NAME =========");
	   for(int i = 0; i < team.size(); i++) {
		   System.out.println("검색 된 팀 이름 : " + team.get(i).getTName());
	   }

	   if (mService.findByid((String)session.getAttribute("userId")).equals("null")){
		   String _id = (String) (session.getAttribute("userId")); 
		   MemberDTO member = new MemberDTO();
		   member = mService.findByid(_id);
		   mav.addObject("member", member);
		   ArrayList<TeamDTO> tList = tService.findAll();
		   mav.addObject("tList", tList);
	   }else {
		   mav.addObject("team", tService.findBytName(mService.findByid((String)session.getAttribute("userId")).getTName()));
		   String _id = (String) (session.getAttribute("userId")); 
		   MemberDTO member = new MemberDTO();
		   member = mService.findByid(_id);
		   mav.addObject("member", member);
		   ArrayList<TeamDTO> tList = tService.findAll();
		   mav.addObject("tList", tList);
	   }

	   mav.setViewName("/searchTeam");     
	   return mav;
   }
}