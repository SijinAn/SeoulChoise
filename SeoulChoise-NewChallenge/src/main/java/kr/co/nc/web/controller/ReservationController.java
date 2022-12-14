package kr.co.nc.web.controller;




import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.siot.IamportRestClient.exception.IamportResponseException;

import kr.co.nc.annotation.LoginUser;
import kr.co.nc.criteria.RoomCriteria;
import kr.co.nc.service.AccommodationService;
import kr.co.nc.service.LoginedUserService;
import kr.co.nc.service.ReservationService;
import kr.co.nc.vo.User;
import kr.co.nc.web.form.PaymentRequest;

@Controller
public class ReservationController {

	@Autowired
	LoginedUserService loginedUserService;
	@Autowired
	ReservationService reservationService;
	@Autowired
	AccommodationService accommodationService;
	/*
	 * 예약페이지 요청처리 
	 * 요청URI : /reservation
	 * 뷰 페이지 : /WEB-INF/views/home.jsp
	 */
	@GetMapping(path = "/reservation")
	public String reservation(@LoginUser User user,RoomCriteria criteria, @RequestParam(name ="id")Integer accoId,@RequestParam(name ="duration")Integer duration, @RequestParam(name="roomno")Integer roomNo, @RequestParam(name ="checkin" ,required=false) String checkIn, @RequestParam(name ="checkout") String checkOut ,
	Model model, PaymentRequest paymentRequest) {
		model.addAttribute("user", user);
		model.addAttribute("id", accoId);
		model.addAttribute("roomno", roomNo);
		model.addAttribute("checkin", checkIn);
		model.addAttribute("checkout", checkOut);
		model.addAttribute("duration", duration);
		
		//상세페이지에서 온 해당 id를 가지고 숙소정보 가져오기.
		model.addAttribute("acco", accommodationService.getAccommodationDetailById(accoId));
		model.addAttribute("room", reservationService.getRoomDetailByroomNo(roomNo));
		// 체크인 체크아웃 데이터 가져오기...?
		
		return "reservation/reservation";
	}
	/*
	 * 예약완료페이지 요청
	 * 요청URI : /myreservation/complete
	 * 뷰 페이지 : /WEB-INF/views/reservation.jsp
	 */
	@PostMapping("/reservation/complete")
	public String paymentinfo(PaymentRequest paymentRequest, @LoginUser User user) throws IamportResponseException, IOException{
		reservationService.insertReservate(paymentRequest, user);
		
		return "redirect:/user/information?cat=CAT_002";
	}
	/*
	 * 예약취소/환불 페이지 요청
	 * 요청URI : /myreservation/refund
	 * 뷰 페이지 : /WEB-INF/views/myreservation.jsp
	 */
	@GetMapping("/reservation/refund")
	public String reservationRefund(@LoginUser User user, @RequestParam(name="reservationNo") String reservationNo) {
		reservationService.updatePaymentStatus(reservationNo);
		
		return "redirect:/user/information?cat=CAT_002";
	}
	
	/*
	 * 상세페이지 요청
	 * 요청URI : /myreservation
	 * 뷰 페이지 : /WEB-INF/views/myreservation.jsp
	 */
	@GetMapping(path = "/myreservation")
	public String myReservation(@LoginUser User user ,@RequestParam(name="reservationNo")String reservationNo ,Model model) {
		
		model.addAttribute("payment", reservationService.getPaymentInfo(reservationNo));
		return "reservation/myreservation";
	}

	
}
