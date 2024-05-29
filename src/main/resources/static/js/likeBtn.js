$(document).ready(function() {
	const user_idx = $('#user_idx').val();
	const user_id = $('#user_id').val();
	const csrfToken = $('meta[name="_csrf"]').attr('content');
	const csrfHeader = $('meta[name="_csrf_header"]').attr('content');

	console.log("CSRF Token:", csrfToken);
	console.log("CSRF Header:", csrfHeader);

	if (!csrfToken || !csrfHeader) {
		console.error("CSRF token or header is missing.");
		return;
	}

	function updateScrapButtons() {
		$('.like_btn').each(function() {
			const button = $(this);
			const post_idx = button.data('post-idx');
			const svg = button.find('svg');

			$.ajax({
				url: `/CheckLike?post_idx=` + post_idx + '&user_idx=' + user_idx,
				type: 'GET',
				dataType: 'json',
				success: function(isLiked) {
					button.data('liked', isLiked);
					if (isLiked) {
						svg.attr('fill', 'red');
					} else {
						svg.attr('fill', 'none');
					}
				},
				error: function(error) {
					console.error('Error:', error);
				}
			});
		});
	}

	updateScrapButtons();

	$('.like_btn').click(function() {
		const button = $(this);
		const post_idx = button.data('post-idx');
		const post_id = button.data('post-id');
		const isLiked = button.data('liked');
		const message = button.data('message-number');

		if (isLiked) {
			$.ajax({
				url: '/LikeDelete',
				type: 'DELETE',
				contentType: 'application/json',
				data: JSON.stringify({
					post_idx: post_idx,
					user_idx: user_idx
				}),
				beforeSend: function(xhr) {
					xhr.setRequestHeader(csrfHeader, csrfToken);
				},
				success: function(response) {
					updateScrapButtons();
					$.ajax({
						url: '/LoadLikes',
						type: 'POST',
						data: { "post_idx": post_idx, "user_idx": user_idx },
						beforeSend: function(xhr) {
							xhr.setRequestHeader(csrfHeader, csrfToken);
						},
						success: function(loadlikes) {
							$('#post_likes' + post_idx).text(loadlikes);
						},
						error: function() {
							alert('오류가 발생했습니다. 다시 시도해주세요.');
						}
					});
				},
				error: function(error) {
					console.error('Error:', error);
					alert('오류가 발생했습니다. 다시 시도해주세요.');
				}
			});
		} else {
			$.ajax({
				url: '/LikeAdd',
				type: 'POST',
				contentType: 'application/json',
				data: JSON.stringify({
					post_idx: post_idx,
					user_idx: user_idx
				}),
				beforeSend: function(xhr) {
					xhr.setRequestHeader(csrfHeader, csrfToken);
				},
				success: function(response) {
					updateScrapButtons();
					$.ajax({
						url: '/LoadLikes',
						type: 'POST',
						data: { "post_idx": post_idx, "user_idx": user_idx },
						beforeSend: function(xhr) {
							xhr.setRequestHeader(csrfHeader, csrfToken);
						},
						success: function(loadlikes) {
							$('#post_likes' + post_idx).text(loadlikes);
						},
						error: function() {
							alert('오류가 발생했습니다. 다시 시도해주세요.');
						}
					});
					$.ajax({
						url: '/AddNoti',
						type: 'POST',
						data: { "post_id": post_id, "user_id": user_id, "post_idx": post_idx, "message": message },
						beforeSend: function(xhr) {
							xhr.setRequestHeader(csrfHeader, csrfToken);
						},
						success: function(response) {
							console.log('Add 성공');
						},
						error: function() {
							alert('오류가 발생했습니다. 다시 시도해주세요.');
						}
					});
				},
				error: function(error) {
					console.error('Error:', error);
					alert('오류가 발생했습니다. 다시 시도해주세요.');
				}
			});
		}
	});
});
