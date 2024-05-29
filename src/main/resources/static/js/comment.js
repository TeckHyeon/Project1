$(document).ready(function() {
	$('.comment-box').keydown(function(e) {
		const isLoggedIn = $('#isLoggedIn').val() == 'true';
		const button = $(this);
		const post_idx = button.data('post-idx');
		const post_id = button.data('post-id');
		const message = button.data('message-number');
		const user_id = $('#user_id').val();

		// 엔터키가 눌렸을 때
		if (e.keyCode == 13) {
			if (!isLoggedIn) {
				// 비 로그인 상태일 경우 로그인 페이지로 리다이렉트
				window.location.href = '/login';
				return; // 이후의 코드 실행을 막기 위해 함수를 여기서 종료
			}
			// Shift키가 함께 눌렸는지 확인
			if (!e.shiftKey) {
				// Shift키가 눌리지 않았다면 댓글 전송
				e.preventDefault(); // 기본 동작(줄바꿈) 방지
				var comment = $(this).val(); // 댓글 내용을 가져옴
				console.log(comment); // 댓글 내용 확인용(실제로는 댓글을 추가하는 로직을 구현)
				const commentData = {
					comment_content: comment,
					to_post_idx: post_idx
				}
				$.ajax({
					url: '/CommentInsert',
					type: 'POST',
					contentType: 'application/json', // 이 줄을 추가해야 합니다.
					data: JSON.stringify(commentData), // 객체를 JSON 문자열로 변환
					success: function(data) {
						loadComments(post_idx);
            stompClient.send("/app/sendNotification", {}, JSON.stringify({
                post_id: post_id,
                user_id: user_id,
                post_idx: post_idx,
                message: message
            }));
					},
					error: function(error) {
						console.error('Error:', error);
					}
				});
				$(this).val(''); // 텍스트 에어리어 초기화
			}
			// Shift키와 함께 눌렸다면, 기본 동작인 줄바꿈을 수행
		}
	});

	function loadComments(post_idx) {
		$.ajax({
			url: '/LoadComments',
			type: 'GET',
			data: { "post_idx": post_idx },
			success: function(response) {
				console.log(post_idx);
				$(`#comments${post_idx}`).html(response);
			},
			error: function() {
				alert('댓글을 불러오는 데 실패했습니다. 다시 시도해주세요.');
			}
		});
	}


});