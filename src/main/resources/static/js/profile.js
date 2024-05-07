const delete_btn = $('.delete_post');
delete_btn.click(function() {
	const button = $(this); // 현재 반복하고 있는 개별 버튼에 대한 참조를 생성합니다.
	const user_id = button.data('profile-id'); // 'btn'을 사용하여 현재 버튼의 데이터를 가져옵니다.
	const post_idx = button.data('post-idx'); // 'btn'을 사용하여 현재 버튼의 데이터를 가져옵니다.
	$.ajax({
		url: '/DeletePost/' + post_idx,
		type: 'DELETE',
		contentType: 'application/json',
		dataType: 'json',
		data: JSON.stringify({
			post_idx: post_idx
		}),
		success: function(response) {
			window.location.reload();
		},
		error: function(error) {
			console.error('Error:', error);
		}
	});
});
const edit_btn = $('.edit_btn');
edit_btn.click(function() {
	const button = $(this);
	const post_idx = button.data('post-idx');
	window.location.href = `/UpdatePost/${post_idx}`;
});
$(document).ready(
	function() {
		const login_user = $('#user_id').val();
		$(".profile-img").click(function() {
			$("#profileInput").click();
		});
		$("#profileInput").change(
			function() {
				if (confirm("프로필 사진을 변경하겠습니까?")) {
					// FormData 객체를 생성하고 파일을 추가
					var formData = new FormData();
					formData.append('file',
						$('#profileInput')[0].files[0]);

					// AJAX를 통해 서버에 파일 전송
					$.ajax({
						url: '/updateProfile', // 프로필 업데이트를 처리하는 서버의 URL
						type: 'POST',
						data: formData,
						processData: false, // jQuery가 데이터를 처리하지 못하도록 설정
						contentType: false, // jQuery가 contentType을 설정하지 못하도록 설정
						success: function(data) {
							alert("프로필 사진이 변경되었습니다.");
							location.reload(); // 페이지를 새로고침하여 변경된 사진을 보여줌
						},
						error: function(e) {
							alert("오류가 발생했습니다.");
						}
					});
				}
			});

		const follow_btn = $('.follow_btn');

		function updateFollowBtn() {
			var isLoggedIn = $('#isLoggedIn').val() === 'true';
			if (isLoggedIn) {
				follow_btn.each(function() {
					const button = $(this); // 현재 반복하고 있는 개별 버튼에 대한 참조를 생성합니다.
					const user_id = button.data('user-id'); // 'btn'을 사용하여 현재 버튼의 데이터를 가져옵니다.
					$.ajax({
						url: `/CheckFollow/` + user_id + `/`
							+ login_user,
						type: 'GET',
						dataType: 'json',
						success: function(following) {
							button.data('following', following); // 'btn'을 사용해 현재 버튼의 데이터를 업데이트합니다.
							button
								.toggleClass('btn-primary',
									following).toggleClass(
										'btn-outline-secondary',
										!following).val(
											following ? '팔로잉' : '팔로우');
						},
						error: function(error) {
							console.error('Error:', error);
							alert('오류가 발생했습니다. 다시 시도해주세요.');
						}
					});
				});
			}
		}
		updateFollowBtn();
		follow_btn.click(function() {
			const button = $(this);
			const user_id = button.data('user-id');
			const following = button.data('following');
			var isLoggedIn = $('#isLoggedIn').val() === 'true';
			if (isLoggedIn) {
			if (following) {
				$.ajax({
					url: `/DeleteFollow/${user_id}/${login_user}`,
					type: 'DELETE',
					success: function(response) {
						alert('팔로우가 해제되었습니다.');
						updateFollowBtn(); // 모든 팔로우 버튼 상태 갱신
					},
					error: function(error) {
						console.error('Error:', error);
						alert('오류가 발생했습니다. 다시 시도해주세요.');
					}
				});
			} else {
				$.ajax({
					url: `/InsertFollow/${user_id}/${login_user}`,
					type: 'POST',
					success: function(response) {
						alert('팔로우가 추가되었습니다.');
						updateFollowBtn(); // 모든 팔로우 버튼 상태 갱신
					},
					error: function(error) {
						console.error('Error:', error);
						alert('오류가 발생했습니다. 다시 시도해주세요.');
					}
				});
				;
			}
				}
				else {
					window.location.href = '/login';
				}
		});

	});