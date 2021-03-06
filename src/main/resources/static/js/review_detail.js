/**
 * 
 */
$(function() {
	$("#del").on('click', function() {
		var reviewUserId = $('#reviewUserId').val();
		var userId = $('#userId').val();
		if (reviewUserId != userId) {
			alert('投稿した人だけが削除できます');
			return false;
		} else {
			var result = window.confirm('本当に削除しますか？');
			if(result) {
				return true;
			} else {
				return false;
			}
		}
	});
	//いいねボタン
	$("#good").on('click',function(){
		var reviewId = $('#review').val();
		var userId = $('#user').val();
		
		$.ajax({
			type : 'POST',
			url : '/good',
			data : {
				reviewId : reviewId,
				userId : userId
			},
			dataType : 'json'
		}).done(function(data){
			console.log(data)
			var id = $('#review').val();
			$.ajax({
				type : 'POST',
				url : '/countGood',
				data : {
					reviewId : reviewId
				},
				dataType : 'json'
			}).done(function(data){
				$("#textGood").html("<span>" + data + "<span>");
			})
		})
	});
	
	//お気に入りボタン
	$("#favorite-btn").on('click',function(){
		var reviewId = $('#review').val();
		$.ajax({
			type : 'POST',
			url : '/favorite',
			data : {
				reviewId : reviewId,
			},
			dataType : 'json'
		}).done(function(data){
			console.log("wow")
			if($('.heart').css('background','red')){
				$('.heart').css('background','black')
			} else {
				$('.heart').css('background','red')
			}
		})
	});
});