package com.ai.controller;

import com.ai.domain.*;
import com.ai.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.time.format.DateTimeFormatter;



@Slf4j
@Controller
@RequestMapping(value = "/field")
public class FieldController {
   @Autowired
   FieldService service;
   @Autowired
   TeamService tService;
   @Autowired
   ReserveService rService;
   @Autowired
   ReserveListService rlService;
   @Autowired
   MemberService mService;
   @Autowired
   FieldReviewService frService;

   @RequestMapping(value = "/click")
   public String moveToField(@RequestParam("fName") String fName) {
      FieldDTO field = service.findByfName(fName);
      return "redirect:/field/"+field.getId();
   }

   @RequestMapping(path = "/{id}")
   public ModelAndView searchField(@PathVariable String id, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
      ModelAndView mav = new ModelAndView();
      FieldDTO field = service.findByid(id);
      ArrayList<FieldDTO> fList = service.findAll();
      ArrayList<String> fNList = new ArrayList<String>();
      ArrayList<String> latList = new ArrayList<String>();
      ArrayList<String> lonList = new ArrayList<String>();
      ArrayList<ReserveDTO> reserveList = new ArrayList<ReserveDTO>();
      String date = null;
      String time = null;
      ReserveDTO reserve = new ReserveDTO();
      TeamDTO homeInfo = new TeamDTO();
      for(int i = 0; i < fList.size(); i++) {
         fNList.add(fList.get(i).getFName());
         latList.add(fList.get(i).getLatitude());
         lonList.add(fList.get(i).getLongitude());
      }
      LinkedHashMap <String, String> timeMap = new LinkedHashMap <String, String>();
      Cookie[] cookies = request.getCookies();
      for(Cookie cookie : cookies) {
         if(cookie.getName().equals("date")) {
            date = cookie.getValue();
            System.out.println("현재 선택된 날짜 : " + date);
         }
         if(cookie.getName().equals("time")) {
            time = cookie.getValue();
            System.out.println("현재 선택된 시간 : " + time);
         }
      }
      Date today = new Date();
      int hours = today.getHours();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      String now = sdf.format(today);
      System.out.println("now : " + now);
      System.out.println("hours : " + hours);
      for(int j = 0; j < 12; j++) {
         timeMap.put(String.format("%d시 - %d시", 2*j, (2*j)+2), "null");
      }
      String[] timeArray = timeMap.keySet().toArray(new String[timeMap.size()]);

      try {
         reserveList = rService.findByField(field.getFName());
         for(int i = 0; i < reserveList.size(); i++) {
            if(reserveList.get(i).getDate().equals(date)) {
               try {
                  if(reserveList.get(i).getType().equalsIgnoreCase("part")) {
                     timeMap.replace(reserveList.get(i).getTime(), reserveList.get(i).getState());
                  }
                  if(reserveList.get(i).getType().equalsIgnoreCase("all") || reserveList.get(i).getState().equalsIgnoreCase("B")) {
                     timeMap.replace(reserveList.get(i).getTime(), "full");
                  }
               } catch (Exception e) {
                  System.out.println("Field Controller Error : " + e.getMessage());
               }
            }
         }
      } catch (Exception e) {
         System.out.println("해당 구장의 예약 정보가 없음!");
      }
      if(now.equals(date)) {
         System.out.println("현재 날짜와 선택된 날짜가 같음!");
         for(int p = 0; p < timeArray.length; p++) {
            String[] timeArray2 = timeArray[p].split("시 - ");
            String[] timeArray3 = timeArray2[1].split("시");
            if(hours >= Integer.parseInt(timeArray3[0].toString())) {
               timeMap.replace(timeArray[p].toString(), "full");
            }
         }
         System.out.println("hours : " + hours);
      }

      // 구장의 이름, 선택된 시간, 날짜 정보 조회
      try {
         reserve = rService.findReserve(field.getFName(), date, time);
         homeInfo = tService.findBytName(reserve.getNameA());
      } catch(Exception e) {
         reserve = null;
         homeInfo = null;
      }
      System.out.println("homeInfo : " + homeInfo);
      MemberDTO member = new MemberDTO();
      try {
         member = mService.findByid((String)session.getAttribute("userId"));
         TeamDTO myteam = tService.findBytName(member.getTName());

         mav.addObject("member", member);
         mav.addObject("userTeamInfo", member.getTName());
         mav.addObject("myteam", myteam);
      } catch (Exception e){
         member = null;
         mav.addObject("userTeamInfo", null);
      }

      ArrayList<FieldReviewVO> reviewList = new ArrayList<FieldReviewVO>();
      FieldReviewDTO fieldreivew = new FieldReviewDTO();
      try{
         reviewList = frService.findByid(field.getFName()).getReviews(); // 해당구장리뷰

      }catch(Exception e){
         log.info(field.getFName() + "에 리뷰가 없습니다");
         reviewList = null;
      }
      log.info("해당 구장 리뷰 : " + reviewList);
      // 해당 구장에 이미 좋아요를 눌렀으면 true, 반대면 false
      boolean alreadylikefield = false;
      try{
         if(member.getLikeFieldList().contains(field.getFName()) == true){
            alreadylikefield = true;
         }
      }catch(Exception e){
         log.info("해당 회원은 찜한 구장이 없습니다.");
         alreadylikefield = false;
      }

      mav.addObject("alreadylikefield", alreadylikefield);
      mav.addObject("reviewList", reviewList);
      mav.addObject("homeInfo", homeInfo);
      mav.addObject("timeMap", timeMap);
      mav.addObject("fNList", fNList);
      mav.addObject("latList", latList);
      mav.addObject("lonList", lonList);
      mav.addObject("field", field);
      mav.setViewName("/field");
      return mav;
   }

   @RequestMapping(value = "/search", method = RequestMethod.POST)
   @ResponseBody
   public SearchMainDTO searchFields(@RequestParam("searchField") String fName, Model model) throws IOException {
      log.info("fName : " + fName);
      ArrayList<FieldDTO> Nfields = service.findByFNameRegex(fName);
      ArrayList<FieldDTO> Afields = service.findByFAddressRegex(fName);
      List<FieldDTO> NAfields = new ArrayList<>(Nfields);
      NAfields.removeAll(Afields);
      NAfields.addAll(Afields);
      SearchMainDTO searchMain = new SearchMainDTO();
      searchMain.setNAfields(NAfields);

      return searchMain;
   }

   @RequestMapping(value = "/getHomeInfoToAjax", method = RequestMethod.POST)
   @ResponseBody
   public TeamDTO searchHomeTeam(@RequestParam("field") String field, @RequestParam("date") String date, @RequestParam("time") String time){
      log.info("searchHomeTeam에 들어옴");
      TeamDTO homeTeam = new TeamDTO();
      try {
         //log.info("홈팀명 : " + rService.findReserve(field, date, time).getNameA());
         homeTeam = tService.findBytName(rService.findReserve(field, date, time).getNameA());
         //log.info("홈팀객체자체 : " + tService.findBytName(rService.findReserve(field, date, time).getNameA()));
      } catch (Exception e){
         log.info(e.getMessage());
      }
      //log.info("homeTeam 로고패스 + 로고네임 : " + homeTeam.getLogoPath() + homeTeam.getLogoName());
      return homeTeam;
   }

   @RequestMapping(value = "/insertReview", method = RequestMethod.POST)
   @ResponseBody
   public ArrayList<FieldReviewVO> insertReviewAjax(@RequestParam("field") String field, @RequestParam("name") String name, @RequestParam("review") String review) throws Exception {
      ModelAndView mav = new ModelAndView();
      log.info("InsertReview컨트롤러에 들어옴");

      log.info("구장명 : " + field);
      log.info("작성자 : " + name);
      log.info("리뷰내용 : " + review);
      LocalDateTime curDateTime = LocalDateTime.now(); //현재시간
      String parsedcurDateTime = curDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); //현재시간을 String으로 파씽
      // 구장명으로 해당하는 getreviews  -> VO객체
      // 그 VO객체를 구장명에 해당하는 DTO객체에 add
      // DTO객체가 null 이면 첫번째 인덱스에 set
      // else - 그 DTO객체에 add
      // 그 DTO객체 save
      FieldReviewVO vo = new FieldReviewVO(name, review, parsedcurDateTime); // VO객체에 담기
      FieldReviewDTO fieldreview = new FieldReviewDTO(); //빈 DTO객체 생성
      ArrayList<FieldReviewVO> volist = null; // 빈 vo객체리스트생성

      // 기존의 dto객체중 id가 field랑 같은게 존재하면 해당dto객체의 vo리스트에 vo객체 add 그리고 save
      // 존재하지 않으면 빈 dto 객체에 set 인덱스 0번째의 vo리스트에 vo객체 그리고 save
      try{
         volist = frService.findByid(field).getReviews(); // 해당구장에 리뷰가 있으면 담아라

      }catch(Exception e){ //해당구장에 리뷰가 없으면
         log.info("해당 구장에 리뷰는 존재하지 않아요");
         volist = new ArrayList<FieldReviewVO>();
      }
      volist.add(vo);
      fieldreview.setId(field);
      fieldreview.setReviews(volist);
      frService.save(fieldreview);

      return volist;
   }

   @RequestMapping(value = "/deleteReviewAjax", method = RequestMethod.POST)
   @ResponseBody
   public ArrayList<FieldReviewVO> deleteReview(@RequestParam("field") String field, @RequestParam("regDate") String regDate, @RequestParam("nickName") String nickName){
      log.info("deleteReviewAjax 컨트롤러 들어옴");

      FieldReviewDTO fr = frService.findByid(field);
      FieldReviewVO vo = new FieldReviewVO();
      ArrayList<FieldReviewVO> volist = new ArrayList<>();

      for(int i=0; i<fr.getReviews().size(); i++){ //해당구장의리뷰다가져온걸 하나씩 돌려서 리뷰리스트안에 vo리스트안에 인덱스i번째것들의 작성자와 작성일자가 같은 인덱스i번째놈들을 vo리스트에서 지우고 세이브
         if(fr.getReviews().get(i).getNickName().equals(nickName) && fr.getReviews().get(i).getRegDate().equals(regDate)){
            log.info("삭제 하는 해당 vo 객체 : " + fr.getReviews().get(i));
            fr.getReviews().remove(i);
            frService.save(fr);
            log.info("삭제 성공 후 로그찍어요 ");
         }
      }
      volist = fr.getReviews();
      return volist;
   }

   @RequestMapping(value = "/modifyReviewAjax", method = RequestMethod.POST)
   @ResponseBody
   public ArrayList<FieldReviewVO> modifyReview(@RequestParam("field") String field, @RequestParam("name") String name,
                                                @RequestParam("review") String review, @RequestParam("bfmodifyreview") String bfmodifyreview){
      log.info("modifyReviewAjax 컨트롤러 들어옴");
      ModelAndView mav = new ModelAndView();

      log.info("구장명 : " + field);
      log.info("작성자 : " + name);
      log.info("수정전리뷰내용 : " + bfmodifyreview);
      log.info("수정후리뷰내용 : " + review);

      FieldReviewDTO fr = frService.findByid(field);
      ArrayList<FieldReviewVO> volist = new ArrayList<FieldReviewVO>();
      LocalDateTime curDateTime = LocalDateTime.now(); //현재시간
      String newRegDate = curDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); //현재시간을 String으로 파씽

      for(int i=0; i<fr.getReviews().size(); i++){
         if(fr.getReviews().get(i).getNickName().equals(name) && fr.getReviews().get(i).getReview().equals(bfmodifyreview)){

            log.info("수정 하는 해당 vo 객체 : " + fr.getReviews().get(i));
            fr.getReviews().get(i).setReview(review);
            fr.getReviews().get(i).setRegDate(newRegDate);
            frService.save(fr);
         }
      }
      volist = fr.getReviews();
      return volist;
   }


}