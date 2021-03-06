package com.example.controller;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.domain.Comment;
import com.example.domain.Favorite;
import com.example.domain.LoginUser;
import com.example.domain.Review;
import com.example.domain.User;
import com.example.form.CommentForm;
import com.example.repository.CommentRepository;
import com.example.repository.FavoriteRepository;
import com.example.repository.GoodRepository;
import com.example.repository.ReviewRepository;
import com.example.service.ReviewService;
import com.example.service.UserService;

/**
 * レビュー詳細を管理するコントローラー.
 * 
 * @author iidashuhei
 *
 */
@Controller
@RequestMapping("/detail")
public class DetailReviewController {

	@Autowired
	private ReviewService reviewService;

	@Autowired
	public ReviewRepository reviewRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private FavoriteRepository favoriteRepository;

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private GoodRepository goodRepository;

	/**
	 * レビューIDからレビュー詳細を表示する.
	 * 
	 * @param reviewId レビューID
	 * @param model    モデル
	 * @return レビュー詳細画面
	 */
	@RequestMapping("")
	public String load(Integer reviewId, Model model, @AuthenticationPrincipal LoginUser loginUser) {
		List<Review> reviewList = reviewService.load(reviewId);
		for (Review review : reviewList) {
			List<Comment> commentList = commentRepository.findByReviewId(reviewId);
			review.setCommentList(commentList);
			model.addAttribute("review", review);
			model.addAttribute("good",goodRepository.good(reviewId));
		}
		
		List<Favorite> favoriteList = favoriteRepository.findByUserIdAndReviewId(loginUser.getUser().getUserId(),reviewId);
		if (favoriteList.size() == 0) {
			model.addAttribute("favorite", "favorite");
		} else {
			model.addAttribute("nofavorite", "nofavorite");
		}

		User user = userService.findByUserId(loginUser.getUser().getUserId());
		model.addAttribute("user", user);
		return "review_detail";
	}

	/**
	 * レビューID、ユーザーIDからレビューを削除する.
	 * 
	 * @param reviewId  レビューID
	 * @param userId    ユーザーID
	 * @param loginUser ログインユーザー
	 * @return
	 */
	@RequestMapping("/delete")
	public String delete(Integer reviewId, Integer userId, @AuthenticationPrincipal LoginUser loginUser) {
		Review review = new Review();
		review.setReviewId(reviewId);
		review.setUserId(userId);
		review.setDeletedBy(loginUser.getUser().getUserName());
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		review.setDeletedAt(timestamp);
		reviewService.delete(review);
		commentRepository.deleteByReviewId(reviewId);
		return "redirect:/userDetail/toUserReviewList";
	}

	/**
	 * コメントを登録する.
	 * 
	 * @param commentForm コメントフォーム
	 * @return トップページへリダイレクト
	 */
	@RequestMapping("/register")
	public String insert(CommentForm commentForm, Integer reviewId, @AuthenticationPrincipal LoginUser loginUser) {
		Comment comment = new Comment();
		comment.setCommentName(loginUser.getUser().getUserName());
		comment.setUserId(loginUser.getUser().getUserId());
		comment.setContent(commentForm.getContent());
		comment.setReviewId(commentForm.getReviewId());
		commentRepository.insert(comment);
		return "redirect:/detail?revi" + "ewId=" + reviewId;
	}

	
}