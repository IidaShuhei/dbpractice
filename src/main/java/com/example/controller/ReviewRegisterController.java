package com.example.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import com.example.domain.LoginUser;
import com.example.domain.RamenImage;
import com.example.domain.RamenShop;
import com.example.domain.Review;
import com.example.domain.User;
import com.example.form.ReviewRegisterForm;
import com.example.repository.RamenImageRepository;
import com.example.repository.RamenShopTimeRepository;
import com.example.repository.ReviewRepository;
import com.example.service.RamenShopService;
import com.example.service.UserService;

/**
 * レビューを登録するコントローラー.
 * 
 * @author iidashuhei
 *
 */
@Controller
@RequestMapping("/review")
public class ReviewRegisterController {
	
	@Autowired
	private ReviewRepository reviewRepository;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	public RamenImageRepository ramenImageRepository;

	@Autowired
	public RamenShopTimeRepository ramenShopTimeRepository;
	
	@Autowired
	public RamenShopService ramenShopService;
	
	@ModelAttribute
	public ReviewRegisterForm setUpForm() {
		return new ReviewRegisterForm();
	}

	/**
	 * レビュー登録画面へ遷移.
	 * 
	 * @return 記事投稿画面
	 */
	@RequestMapping("/toInsert")
	public String toInsert(Integer shopId, Model model, @AuthenticationPrincipal LoginUser loginUser) {
		if(shopId != null) {
			RamenShop ramenShop = ramenShopService.load(shopId);
			model.addAttribute("ramenShop", ramenShop);
		}
		
		User user = userService.findByUserId(loginUser.getUser().getUserId());
		model.addAttribute("user", user);
		return "register_review";
	}
	
	/**
	 * レビューを登録する.
	 * 
	 * @param articleRegisterForm 記事登録フォーム
	 * @param result 結果
	 * @param model モデル
	 * @return トップページへリダイレクト
	 * @throws IOException
	 */
	@RequestMapping("/insert")
	public String insert(@Validated ReviewRegisterForm reviewRegisterForm, BindingResult result, @AuthenticationPrincipal LoginUser loginUser, Model model) throws IOException {
	
		String str = reviewRegisterForm.getRamenPrice();
		Pattern p = Pattern.compile("^\\d{3,4}$");
		Matcher m = p.matcher(str);
		if(!m.find()) {
			result.rejectValue("ramenPrice", null, "半角数値3桁または4桁で入力してください");
		}
		
		// 画像ファイル形式チェック
		MultipartFile image = reviewRegisterForm.getRamenImage();
		String fileExtension = null;
		try {
			fileExtension = getExtension(image.getOriginalFilename());
			if (!"jpg".equals(fileExtension) && !"png".equals(fileExtension)) {
				result.rejectValue("ramenImage", "", "拡張子は.jpgか.pngのみに対応しています");
			}
		} catch (Exception e) {
			result.rejectValue("ramenImage", "", "画像を選択してください");
		}

		RamenImage ramenImage = new RamenImage();
		// 画像ファイルをBase64形式にエンコード
		String base64FileString = Base64.getEncoder().encodeToString(image.getBytes());
		if ("jpg".equals(fileExtension)) {
			base64FileString = "data:image/jpeg;base64," + base64FileString;
		} else if ("png".equals(fileExtension)) {
			base64FileString = "data:image/png;base64," + base64FileString;
		}
		ramenImage.setImagePath(base64FileString);
		ramenImageRepository.insert(ramenImage);
		
		if (result.hasErrors()) {
			return toInsert(reviewRegisterForm.getShopId(),model, loginUser);
		}
		
		Review review = new Review();
		review.setShopId(reviewRegisterForm.getShopId());
		review.setUserId(loginUser.getUser().getUserId());
		review.setRamenName(reviewRegisterForm.getRamenName());
		review.setRamenPrice(Integer.parseInt(reviewRegisterForm.getRamenPrice()));
		review.setComments(reviewRegisterForm.getComments());
		review.setRamenImagePathId(ramenImage.getImageId());
		review.setCreatedBy(loginUser.getUser().getUserName());
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		review.setCreatedAt(timestamp);
		
		reviewRepository.insert(review);
		return "redirect:/showReview";
	}

	/*
	 * ファイル名から拡張子を返します.
	 * 
	 * @param originalFileName ファイル名
	 * 
	 * @return .を除いたファイルの拡張子
	 */
	private String getExtension(String originalFileName) throws Exception {
		if (originalFileName == null) {
			throw new FileNotFoundException();
		}
		int point = originalFileName.lastIndexOf(".");
		if (point == -1) {
			throw new FileNotFoundException();
		}
		return originalFileName.substring(point + 1);
	}
}