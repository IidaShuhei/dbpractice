package com.example.service;

import java.io.IOException;
import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.LoginUser;
import com.example.domain.RamenShop;
import com.example.domain.RamenShopTime;
import com.example.form.RamenShopRegisterForm;
import com.example.repository.RamenImageRepository;
import com.example.repository.RamenShopRepository;
import com.example.repository.RamenShopTimeRepository;

/**
 * ラーメン店舗を管理するサービス.
 * 
 * @author iidashuhei
 *
 */
@Service
@Transactional
public class RamenShopRegisterService {

	@Autowired
	public RamenShopRepository ramenShopRepository;

	@Autowired
	public RamenShopTimeRepository ramenShopTimeRepository;

	@Autowired
	public RamenImageRepository ramenImageRepository;


	/**
	 * ラーメン屋を登録する.
	 * 
	 * @param articleRegisterForm 記事登録フォーム
	 * @throws IOException
	 */
	public void insert(RamenShopRegisterForm ramenShopRegisterForm,@AuthenticationPrincipal LoginUser loginUser){
		RamenShop ramenShop = new RamenShop();
		ramenShop.setShopName(ramenShopRegisterForm.getShopName());
		ramenShop.setZipcode(ramenShopRegisterForm.getZipcode());
		ramenShop.setPrefecture(ramenShopRegisterForm.getPrefecture());
		ramenShop.setCity(ramenShopRegisterForm.getCity());
		ramenShop.setOther(ramenShopRegisterForm.getOther());
		ramenShop.setHolidays(ramenShopRegisterForm.getHolidays());
		ramenShop.setCreatedBy(loginUser.getUser().getUserName());
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		ramenShop.setCreatedAt(timestamp);
		ramenShopRepository.insert(ramenShop);

		for(RamenShopTime ramenShopTime : ramenShopRegisterForm.getRamenShopTimeList()) {
			ramenShopTime.setDays(ramenShopTime.getDays());
			ramenShopTime.setNoonStartTime(ramenShopTime.getNoonStartTime());
			ramenShopTime.setNoonEndTime(ramenShopTime.getNoonEndTime());
			ramenShopTime.setNightStartTime(ramenShopTime.getNightStartTime());
			ramenShopTime.setNightEndTime(ramenShopTime.getNightEndTime());
			ramenShopTime.setOtherTime(ramenShopTime.getOtherTime());
			ramenShopTime.setShopId(ramenShop.getShopId());
			ramenShopTimeRepository.insert(ramenShopTime);
			ramenShopTimeRepository.delete(ramenShopTime);
		}
	}
}